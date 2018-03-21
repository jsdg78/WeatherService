package com.wh.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wh.cache.Cache;
import com.wh.weather.client.CachingWeatherClient;
import com.wh.weather.client.WeatherClient;
import com.wh.weather.client.owm.OpenWeatherMapClient;
import com.wh.weather.client.owm.WeatherFeed;
import com.wh.weather.internal.SimpleWeatherService;
import com.wh.weather.model.Weather;

// Enable Mockito annotations.
@RunWith(MockitoJUnitRunner.class)
public class WeatherServiceTest {

  private static final String WEATHER_RESPONSE_200 =
      "{\"coord\":{\"lon\":-76.28,\"lat\":40.03},\"weather\":[{\"id\":600,\"main\":\"Snow\",\"description\":\"light snow\",\"icon\":\"13d\"},{\"id\":701,\"main\":\"Mist\",\"description\":\"mist\",\"icon\":\"50d\"},{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"},{\"id\":741,\"main\":\"Fog\",\"description\":\"fog\",\"icon\":\"50d\"}],\"base\":\"stations\",\"main\":{\"temp\":271.4,\"pressure\":1004,\"humidity\":92,\"temp_min\":271.15,\"temp_max\":272.15},\"visibility\":1207,\"wind\":{\"speed\":7.2,\"deg\":50},\"clouds\":{\"all\":90},\"dt\":1521632700,\"sys\":{\"type\":1,\"id\":2352,\"message\":0.0041,\"country\":\"US\",\"sunrise\":1521630363,\"sunset\":1521674324},\"id\":420031788,\"name\":\"Conestoga Gardens\",\"cod\":200}";

  private static final String WEATHER_RESPONSE_404 =
      "{\"cod\":\"404\",\"message\":\"city not found\"}";

  @Spy
  private WeatherClient weatherClient = new OpenWeatherMapClient();

  @Spy
  private WeatherService weatherService = new SimpleWeatherService(weatherClient);

  @Mock
  private Cache<String, Weather> cache;

  @InjectMocks
  private WeatherController weatherController;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    try {
      // The weather client is a spy which means we are using the real object in our test but only
      // stubbing off certain methods. In this case, we are stubbing off getWeatherFeed() as this
      // is where the external integration, i.e. the call to the remote API, is taking place.
      OpenWeatherMapClient openWeatherMapClient = (OpenWeatherMapClient) weatherClient;
      doReturn(getWeatherFeed(WEATHER_RESPONSE_200)).when(openWeatherMapClient)
          .getWeatherFeed("17602");
      doReturn(getWeatherFeed(WEATHER_RESPONSE_404)).when(openWeatherMapClient)
          .getWeatherFeed("00000");
      // Spy the weather service so that it also uses our spied weather client.
      SimpleWeatherService simpleWeatherService = (SimpleWeatherService) weatherService;
      doReturn(weatherClient).when(simpleWeatherService).getWeatherClient();
      mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    } catch (Exception e) {
      fail(ExceptionUtils.getRootCauseMessage(e));
    }
  }

  @Test
  public void testWeatherController() {
    try {
      // Test GET getWind (200) response.
      mockMvc.perform(get("/api/v1/wind/17602")).andExpect(MockMvcResultMatchers.status().isOk())
          // .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.speed", Matchers.is(7.2)))
          .andExpect(jsonPath("$.direction", Matchers.is(50.0))).andDo(print());
      // Test GET getWind (404) response.
      mockMvc.perform(get("/api/v1/wind/00000")).andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.speed", Matchers.isEmptyOrNullString()))
          .andExpect(jsonPath("$.direction", Matchers.isEmptyOrNullString())).andDo(print());
      // Test DELETE clearCache (200) response.
      mockMvc.perform(delete("/api/v1/cache/contents"))
          .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().string("true"))
          .andDo(print());
    } catch (Exception e) {
      fail(ExceptionUtils.getRootCauseMessage(e));
    }
  }

  @Test
  public void testOpenWeatherMapClient() {
    OpenWeatherMapClient openWeatherMapClient = (OpenWeatherMapClient) weatherClient;
    // Test "not found" (404) response.
    WeatherFeed feed = openWeatherMapClient.getWeatherFeed("00000");
    assertEquals("404", feed.getCod());
    assertEquals("city not found", feed.getMessage());
    assertNull(feed.getDt());
    assertNull(feed.getName());

    // Test successful (200) response.
    feed = openWeatherMapClient.getWeatherFeed("17602");
    assertEquals("200", feed.getCod());
    assertEquals("1521632700", feed.getDt());
    assertEquals("Conestoga Gardens", feed.getName());
    assertEquals("US", feed.getSys().get("country"));
    assertEquals("Snow", feed.getWeather().get("main"));
    assertEquals("light snow", feed.getWeather().get("description"));
    assertEquals("271.4", feed.getMain().get("temp"));
    assertEquals("7.2", feed.getWind().get("speed"));
    assertEquals("50", feed.getWind().get("deg"));
    assertNull(feed.getMessage());

    // Test that we are able to populate our model object.
    Weather weather = weatherClient.getWeather("17602");
    assertEquals("200", feed.getCod());
    assertEquals("1521632700", weather.getDateTime());
    assertEquals("Conestoga Gardens", weather.getCity());
    assertEquals("17602", weather.getZipCode());
    assertEquals("US", weather.getCountry());
    assertEquals("Snow", weather.getCondition());
    assertEquals("light snow", weather.getDescription());
    assertEquals(Float.valueOf(271.4F), weather.getTemperature());
    assertEquals(Float.valueOf(7.2F), weather.getWind().getSpeed());
    assertEquals(Float.valueOf(50F), weather.getWind().getDirection());
  }

  @Test
  public void testCachingWeatherClient() {
    WeatherClient cachingWeatherClient = new CachingWeatherClient(weatherClient, cache);
    // Newly created instance that will be cached for the first time.
    Weather weather = cachingWeatherClient.getWeather("17602");

    // 17602 should now be in the cache and...
    when(cache.containsKey("17602")).thenReturn(true);
    when(cache.get("17602")).thenReturn(weather);

    // retrieving it should give us the original instance.
    assertTrue(weather == cachingWeatherClient.getWeather("17602"));

    // Once 17602 expires - this will return false...
    when(cache.containsKey("17602")).thenReturn(false);

    // and retrieving it again should give us a new instance.
    assertFalse(weather == cachingWeatherClient.getWeather("17602"));
  }

  private static WeatherFeed getWeatherFeed(String jsonString) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    // Deserialize JSON string into a WeatherFeed object.
    return mapper.readValue(jsonString, WeatherFeed.class);
  }
}
