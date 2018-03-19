package com.wh.weather.model;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Wind implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String speed;
  private final String direction;

  public Wind(String speed, String direction) {
    this.speed = speed;
    this.direction = direction;
  }

  public String getSpeed() {
    return speed;
  }

  public String getDirection() {
    return direction;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("speed", speed)
        .append("direction", direction).toString();
  }
}
