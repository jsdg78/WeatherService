package com.wh.weather;

import com.wh.weather.model.Wind;

/**
 * A service for getting weather-related information.
 */
public interface WeatherService {

  /**
   * Returns current wind data given the zip code.
   */
  Wind getWind(String zipCode);
}
