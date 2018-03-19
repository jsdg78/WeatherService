package com.wh.weather.owm;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Weather implements Serializable {

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
  private Map<String, String> coord;

  @JsonProperty
  private Map<String, String> main;

  @JsonProperty
  private List<Map<String, String>> weather;

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

  public Map<String, String> getCoord() {
    return coord;
  }

  public Map<String, String> getMain() {
    return main;
  }

  public List<Map<String, String>> getWeather() {
    return weather;
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
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("id", id)
        .append("cod", cod).append("name", name).append("dt", dt).append("base", base)
        .append("visibility", visibility).append("coord", coord).append("main", main)
        .append("weather", weather).append("wind", wind).append("clouds", clouds).append("sys", sys)
        .toString();
  }
}
