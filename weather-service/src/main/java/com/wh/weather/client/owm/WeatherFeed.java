package com.wh.weather.client.owm;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for the OpenWeatherMap API.
 */
public class WeatherFeed implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty
  private String id;

  @JsonProperty
  private String cod;

  @JsonProperty
  private String name;

  @JsonProperty
  private String dt;

  @JsonProperty
  private String base;

  @JsonProperty
  private String visibility;

  @JsonProperty
  private String message;

  @JsonProperty
  private Map<String, String> coord;

  @JsonProperty
  private Map<String, String> main;

  @JsonProperty("weather")
  private List<Map<String, String>> weathers;

  @JsonProperty
  private Map<String, String> wind;

  @JsonProperty
  private Map<String, String> clouds;

  @JsonProperty
  private Map<String, String> sys;

  public String getId() {
    return id;
  }

  public String getCod() {
    return cod;
  }

  public String getName() {
    return name;
  }

  public String getDt() {
    return dt;
  }

  public String getBase() {
    return base;
  }

  public String getVisibility() {
    return visibility;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, String> getCoord() {
    return coord;
  }

  public Map<String, String> getMain() {
    return main;
  }

  public Map<String, String> getWeather() {
    return (weathers == null || weathers.isEmpty() ? Collections.emptyMap() : weathers.get(0));
  }

  public Map<String, String> getWind() {
    return wind;
  }

  public Map<String, String> getClouds() {
    return clouds;
  }

  public Map<String, String> getSys() {
    return sys;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id)
        .append("cod", cod).append("name", name).append("dt", dt).append("base", base)
        .append("visibility", visibility).append("message", message).append("coord", coord)
        .append("main", main).append("weather", weathers).append("wind", wind)
        .append("clouds", clouds).append("sys", sys).toString();
  }
}
