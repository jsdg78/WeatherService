package com.wh.weather.client;

import com.wh.weather.model.Weather;

public interface WeatherClient {

  Weather getWeather(String zipCode);
}
