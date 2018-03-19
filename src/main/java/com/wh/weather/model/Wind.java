package com.wh.weather.model;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Wind implements Serializable {

  private static final long serialVersionUID = 1L;

  private Float speed;
  private Float direction;

  public Wind() {
    //
  }

  public Float getSpeed() {
    return speed;
  }

  public void setSpeed(Float speed) {
    this.speed = speed;
  }

  public Float getDirection() {
    return direction;
  }

  public void setDirection(Float direction) {
    this.direction = direction;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("speed", speed)
        .append("direction", direction).toString();
  }
}
