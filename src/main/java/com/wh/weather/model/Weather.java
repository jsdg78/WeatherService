package com.wh.weather.model;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Model class that represents the weather.
 * 
 * A particular weather API provider might provide a lot of data but our company is only interested
 * in these few pieces of information.
 */
public class Weather implements Serializable {

  private static final long serialVersionUID = 1L;

  private String dateTime;
  private String city;
  private String zipCode;
  private String country;
  private String condition;
  private String description;
  private Float temperature;
  private Wind wind;

  public Weather() {
    //
  }

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Float getTemperature() {
    return temperature;
  }

  public void setTemperature(String temperature) {
    setTemperature(Float.valueOf(temperature));
  }

  public void setTemperature(Float temperature) {
    this.temperature = temperature;
  }

  public Wind getWind() {
    return wind;
  }

  public void setWind(Wind wind) {
    this.wind = wind;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("dateTime", dateTime)
        .append("city", city).append("zipCode", zipCode).append("country", country)
        .append("condition", condition).append("description", description)
        .append("temperature", temperature).append("wind", wind).toString();
  }
}
