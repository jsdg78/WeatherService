package com.wh.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.wh.weather.client.WeatherClient;
import com.wh.weather.model.Weather;
import com.wh.weather.model.Wind;

@Service
public class SimpleWeatherService implements WeatherService {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleWeatherService.class);

  @Autowired
  @Qualifier("cachingWeatherClient")
  private WeatherClient weatherClient;

  @Override
  public Wind getWind(String zipCode) {
    Weather weather = weatherClient.getWeather(zipCode);
    Wind wind = weather.getWind();
    LOG.info("Current wind for {}: {}", zipCode, wind);
    return weather.getWind();
  }
}
