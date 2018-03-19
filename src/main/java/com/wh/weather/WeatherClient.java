package com.wh.weather;

import com.wh.weather.model.Weather;

public interface WeatherClient {

  Weather getWeather(String zipCode);
}
