package com.wh.weather.client.owm;

import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.wh.weather.client.WeatherClient;
import com.wh.weather.model.Weather;
import com.wh.weather.model.Wind;

/**
 * A weather client that uses the OpenWeatherMap API.
 */
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

  /**
   * Returns current weather data given the zip code.
   * 
   * This method calls the OpenWeatherMap API and translates the resulting WeatherFeed object
   * (vendor-specific) to our company's Weather object which can then be used in our interfaces.
   */
  @Override
  public Weather getWeather(String zipCode) {
    Weather weather = new Weather();
    weather.setZipCode(zipCode);

    WeatherFeed feed = getWeatherFeed(zipCode);
    // Use the Java 8.0 java.util.Optional class to provide
    // null-safety while traversing these nested properties.
    Optional<WeatherFeed> opt = Optional.ofNullable(feed);
    opt.map(WeatherFeed::getDt).ifPresent(weather::setDateTime);
    opt.map(WeatherFeed::getName).ifPresent(weather::setCity);
    opt.map(WeatherFeed::getSys).map(e -> e.get("country")).ifPresent(weather::setCountry);
    opt.map(WeatherFeed::getWeather).map(e -> e.get("main")).ifPresent(weather::setCondition);
    opt.map(WeatherFeed::getWeather).map(e -> e.get("description"))
        .ifPresent(weather::setDescription);
    opt.map(WeatherFeed::getMain).map(e -> e.get("temp")).ifPresent(weather::setTemperature);

    Wind wind = new Wind();
    opt.map(WeatherFeed::getWind).map(e -> e.get("speed")).ifPresent(wind::setSpeed);
    opt.map(WeatherFeed::getWind).map(e -> e.get("deg")).ifPresent(wind::setDirection);
    weather.setWind(wind);

    LOG.info("Current weather for {}: {}", zipCode, weather);
    return weather;
  }

  public WeatherFeed getWeatherFeed(String zipCode) {
    URI url = getWeatherURI(zipCode);
    LOG.info("Request: {}", url);

    // Call the OpenWeatherMap API and deserialize the JSON response to our WeatherFeed object.
    ResponseEntity<WeatherFeed> response = restTemplate.getForEntity(url, WeatherFeed.class);
    WeatherFeed feed = response.getBody();
    LOG.info("Response: {}", feed);
    return feed;
  }

  private URI getWeatherURI(String zipCode) {
    // Expand the base URL to include our query parameters.
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/weather")
        .queryParam("zip", zipCode).queryParam("APPID", apiKey);
    return builder.build().encode().toUri();
  }
}
