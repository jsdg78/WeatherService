package com.wh.weather.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.wh.cache.Cache;
import com.wh.weather.model.Weather;

public class CachingWeatherClient extends WeatherClientDecorator {

  private static final Logger LOG = LoggerFactory.getLogger(CachingWeatherClient.class);

  @Autowired
  private Cache<String, Weather> cache;

  public CachingWeatherClient(WeatherClient weatherClient) {
    super(weatherClient);
  }

  @Override
  public Weather getWeather(String zipCode) {
    if (cache.containsKey(zipCode)) {
      LOG.info("Found weather data in cache.");
      return cache.get(zipCode);
    }
    Weather weather = super.getWeather(zipCode);
    cache.put(zipCode, weather);
    return weather;
  }
}
