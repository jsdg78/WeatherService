package com.wh.weather.client;

import com.wh.weather.model.Weather;

public class WeatherClientDecorator implements WeatherClient {

  protected WeatherClient weatherClient;

  public WeatherClientDecorator(WeatherClient weatherClient) {
    this.weatherClient = weatherClient;
  }

  @Override
  public Weather getWeather(String zipCode) {
    return weatherClient.getWeather(zipCode);
  }
}
