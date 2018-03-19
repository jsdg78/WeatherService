package com.wh.weather;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wh.cache.Cache;
import com.wh.weather.model.Wind;

@Validated
@RestController
public class WeatherController {

  @Autowired
  private Cache<String, String> cache;

  @Autowired
  private WeatherService weatherService;

  @GetMapping("/api/v1/wind/{zipCode}")
  public Wind getWind(@Pattern(regexp = "\\d{5}",
      message = "{zipcode.pattern.invalid}") @PathVariable String zipCode) {
    return weatherService.getWind(zipCode);
  }

  @DeleteMapping("/api/v1/cache/contents")
  public boolean clearCache() {
    cache.clear();
    return (cache.size() == 0);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, List<String>> handle(ConstraintViolationException e) {
    return getErrors(e.getConstraintViolations());
  }

  private Map<String, List<String>> getErrors(Set<ConstraintViolation<?>> violations) {
    return Collections.singletonMap("errors",
        violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
  }
}
