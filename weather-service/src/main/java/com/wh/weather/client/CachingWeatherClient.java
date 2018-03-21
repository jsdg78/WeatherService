package com.wh.weather.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.wh.cache.Cache;
import com.wh.weather.model.Weather;

/**
 * A weather client that supports caching features.
 * 
 * It uses the Gang-of-Four Decorator design pattern to "decorate" or enhance the functionality of
 * the underlying weather client by adding caching features to prevent potentially expensive calls.
 */
public class CachingWeatherClient extends WeatherClientDecorator {

  private static final Logger LOG = LoggerFactory.getLogger(CachingWeatherClient.class);

  @Autowired
  private Cache<String, Weather> cache;

  public CachingWeatherClient(WeatherClient weatherClient) {
    super(weatherClient);
  }

  @Override
  public Weather getWeather(String zipCode) {
    // If the zip code is found in the cache,
    // then return the cached information.
    if (cache.containsKey(zipCode)) {
      LOG.info("Found weather data in cache.");
      return cache.get(zipCode);
    }
    // Otherwise, get the information from
    // the client and cache the results.
    Weather weather = super.getWeather(zipCode);
    cache.put(zipCode, weather);
    return weather;
  }
}
