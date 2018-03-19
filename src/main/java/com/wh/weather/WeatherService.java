package com.wh.weather;

import com.wh.weather.model.Wind;

public interface WeatherService {

  Wind getWind(String zipCode);
}
