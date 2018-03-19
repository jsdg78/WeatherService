package com.wh.weather;

import com.wh.weather.model.Wind;

public interface WeatherClient {

  Wind getWind(String zipCode);
}
