package com.wh.weather;

import javax.validation.Validator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import com.wh.cache.Cache;
import com.wh.cache.internal.MapBackedCache;
import com.wh.weather.client.CachingWeatherClient;
import com.wh.weather.client.WeatherClient;
import com.wh.weather.client.owm.OpenWeatherMapClient;
import com.wh.weather.model.Weather;

/**
 * This is the entry point to our Spring Boot application. Note that the SpringBootApplication
 * annotation is equivalent to declaring Configuration, EnableAutoConfiguration and ComponentScan
 * annotations all at the same time.
 * 
 * <pre>
 * USAGE
 * =====
 * Using Java:
 * $ java -jar target\weather-service-1.0.0-SNAPSHOT.jar
 * 
 * Using Maven:
 * $ mvn spring-boot:run
 * </pre>
 */
@SpringBootApplication
public class WeatherApp {

  public static void main(String[] args) {
    SpringApplication.run(WeatherApp.class, args);
  }

  @Bean
  public Cache<String, Weather> cache() {
    return new MapBackedCache<>();
  }

  @Bean
  public WeatherClient weatherClient() {
    // Create a basic weather client.
    return new OpenWeatherMapClient();
  }

  @Bean
  public WeatherClient cachingWeatherClient() {
    // Decorate the basic client with caching features.
    return new CachingWeatherClient(weatherClient(), cache());
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    // Create BeanPostProcesor for performing validations on annotated methods.
    MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
    // Delegate to a JSR-303 (javax.validation) provider.
    processor.setValidator(validator());
    return processor;
  }

  @Bean
  public Validator validator() {
    // Bootstrap a javax.validation.ValidationFactory
    // - a factory that returns Validator instances.
    return new LocalValidatorFactoryBean();
  }
}
