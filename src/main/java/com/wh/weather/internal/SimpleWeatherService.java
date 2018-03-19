package com.wh.weather.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wh.weather.WeatherClient;
import com.wh.weather.WeatherService;
import com.wh.weather.model.Weather;
import com.wh.weather.model.Wind;

@Service
public class SimpleWeatherService implements WeatherService {

  @Autowired
  private WeatherClient weatherClient;

  @Override
  public Wind getWind(String zipCode) {
    Weather weather = weatherClient.getWeather(zipCode);
    return weather.getWind();
  }
}
