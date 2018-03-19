package com.wh.weather.model;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Temperature implements Serializable {

  private static final long serialVersionUID = 1L;

  private Float current;
  private Float low;
  private Float high;

  public Temperature() {
    //
  }

  public Float getCurrent() {
    return current;
  }

  public void setCurrent(Float current) {
    this.current = current;
  }

  public Float getLow() {
    return low;
  }

  public void setLow(Float low) {
    this.low = low;
  }

  public Float getHigh() {
    return high;
  }

  public void setHigh(Float high) {
    this.high = high;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("current", current)
        .append("low", low).append("high", high).toString();
  }
}
