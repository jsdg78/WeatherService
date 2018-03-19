package com.wh.weather;

import javax.validation.Validator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import com.wh.cache.Cache;
import com.wh.cache.internal.MapBackedCache;

@SpringBootApplication
public class WeatherApp {

  public static void main(String[] args) {
    SpringApplication.run(WeatherApp.class, args);
  }

  @Bean
  public Cache<String, String> cache() {
    return new MapBackedCache<String, String>();
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
    processor.setValidator(validator());
    return processor;
  }

  @Bean
  public Validator validator() {
    return new LocalValidatorFactoryBean();
  }
}
