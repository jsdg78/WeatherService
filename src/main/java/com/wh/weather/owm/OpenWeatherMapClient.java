package com.wh.weather.owm;

import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.wh.weather.WeatherClient;
import com.wh.weather.model.Wind;

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
  public Wind getWind(String zipCode) {
    Weather weather = getWeather(zipCode);
    Map<String, String> wind = weather.getWind();
    String speed = wind.get("speed");
    String direction = wind.get("deg");
    return new Wind(speed, direction);
  }

  private Weather getWeather(String zipCode) {
    ResponseEntity<Weather> response =
        restTemplate.getForEntity(getWeatherURI(zipCode), Weather.class);
    Weather weather = response.getBody();
    LOG.info("Current weather for {}: {}", zipCode, weather);
    return weather;
  }

  private URI getWeatherURI(String zipCode) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/weather")
        .queryParam("zip", zipCode).queryParam("APPID", apiKey);
    return builder.build().encode().toUri();
  }
}
