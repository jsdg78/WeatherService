package com.wh.weather.internal;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.wh.weather.WeatherService;
import com.wh.weather.client.WeatherClient;
import com.wh.weather.model.Weather;
import com.wh.weather.model.Wind;

/**
 * A service for getting weather-related information using a cache-enabled client.
 */
@Service
public class SimpleWeatherService implements WeatherService {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleWeatherService.class);

  @Autowired
  // To use the basic client instead of the caching client,
  // simply remove the Qualifier annotation and the basic
  // client will be injected into this bean instead.
  @Qualifier("cachingWeatherClient")
  private WeatherClient weatherClient;

  public SimpleWeatherService(WeatherClient weatherClient) {
    this.weatherClient = Validate.notNull(weatherClient, "weatherClient is null");
  }

  public WeatherClient getWeatherClient() {
    return weatherClient;
  }

  @Override
  public Wind getWind(String zipCode) {
    // Only purpose of the getter is to make it easier
    // for the client to be spied during unit testing.
    Weather weather = getWeatherClient().getWeather(zipCode);
    Wind wind = weather.getWind();
    LOG.info("Current wind for {}: {}", zipCode, wind);
    return weather.getWind();
  }
}
