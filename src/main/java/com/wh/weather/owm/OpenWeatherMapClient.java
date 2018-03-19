package com.wh.weather.owm;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.wh.weather.WeatherClient;
import com.wh.weather.model.Location;
import com.wh.weather.model.Temperature;
import com.wh.weather.model.Weather;
import com.wh.weather.model.Wind;

@Component
public class OpenWeatherMapClient implements WeatherClient {

  private static final Logger LOG = LoggerFactory.getLogger(OpenWeatherMapClient.class);

  private final RestTemplate restTemplate;

  @Value("${openweathermap.api.url}")
  private String apiBaseUrl;

  @Value("${openweathermap.api.key}")
  private String apiKey;

  public OpenWeatherMapClient() {
    this.restTemplate = (new RestTemplateBuilder()).build();
  }

  @Override
  public Weather getWeather(String zipCode) {
    WeatherFeed feed = getWeatherFeed(zipCode);
    Weather weather = new Weather();

    List<Map<String, String>> list = feed.getWeather();
    weather.setDateTime(feed.getDt());
    weather.setCondition(list.get(0).get("main"));
    weather.setDescription(list.get(0).get("description"));

    Location location = new Location();
    Map<String, String> map = feed.getSys();
    location.setCity(feed.getName());
    location.setZipCode(zipCode);
    location.setCountry(map.get("country"));
    weather.setLocation(location);

    Temperature temperature = new Temperature();
    map = feed.getMain();
    temperature.setCurrent(Float.valueOf(map.get("temp")));
    temperature.setLow(Float.valueOf(map.get("temp_min")));
    temperature.setHigh(Float.valueOf(map.get("temp_max")));
    weather.setHumidity(Float.valueOf(map.get("humidity")));
    weather.setTemperature(temperature);

    Wind wind = new Wind();
    map = feed.getWind();
    wind.setSpeed(Float.valueOf(map.get("speed")));
    wind.setDirection(Float.valueOf(map.get("deg")));
    weather.setWind(wind);

    LOG.info("Current weather for {}: {}", zipCode, weather);
    return weather;
  }

  public WeatherFeed getWeatherFeed(String zipCode) {
    ResponseEntity<WeatherFeed> response =
        restTemplate.getForEntity(getWeatherURI(zipCode), WeatherFeed.class);
    return response.getBody();
  }

  private URI getWeatherURI(String zipCode) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/weather")
        .queryParam("zip", zipCode).queryParam("APPID", apiKey);
    return builder.build().encode().toUri();
  }
}
