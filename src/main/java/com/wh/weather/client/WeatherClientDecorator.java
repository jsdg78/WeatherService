package com.wh.weather.client;

import com.wh.weather.model.Weather;

/**
 * Abstract base decorator for the weather client.
 */
public abstract class WeatherClientDecorator implements WeatherClient {

  /** The weather client being decorated. */
  protected WeatherClient weatherClient;

  public WeatherClientDecorator(WeatherClient weatherClient) {
    this.weatherClient = weatherClient;
  }

  @Override
  public Weather getWeather(String zipCode) {
    return weatherClient.getWeather(zipCode);
  }
}
