package com.wh.weather.client;

import com.wh.weather.model.Weather;

/**
 * A client for getting weather-related information.
 */
public interface WeatherClient {

  /**
   * Returns current weather data given the zip code.
   */
  Weather getWeather(String zipCode);
}
