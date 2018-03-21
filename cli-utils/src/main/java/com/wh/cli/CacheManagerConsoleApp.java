package com.wh.cli;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 * A CLI utility to manage an application cache.
 * 
 * <pre>
 * Currently, the business is only asking for
 * the ability to bust the cache if necessary.
 * 
 * USAGE
 * =====
 * Using Java:
 * $ java -jar target\cli-utils-1.0.0-SNAPSHOT.jar --D http://localhost:8080/api/v1
 * 
 * Using Maven:
 * $ mvn spring-boot:run -Dspring-boot.run.arguments="--D,http://localhost:8080/api/v1"
 * </pre>
 */
@SpringBootApplication
public class CacheManagerConsoleApp implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(CacheManagerConsoleApp.class);

  private final RestTemplate restTemplate;

  public CacheManagerConsoleApp() {
    this.restTemplate = (new RestTemplateBuilder()).build();
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(CacheManagerConsoleApp.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
    System.exit(0);
  }

  @Override
  public void run(ApplicationArguments args) {
    String operation = args.getOptionNames().stream().findFirst().orElse(null);
    String url = args.getNonOptionArgs().stream().findFirst().orElse(null);
    if (("D".equalsIgnoreCase(operation) || "delete".equalsIgnoreCase(operation))
        && StringUtils.isNotBlank(url)) {
      // Delete the contents of the cache used by the WeatherService.
      restTemplate.delete(url.replace("/$", "") + "/cache/contents");
      LOG.info("***** Done *****");
      return;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("\n\nUsage:");
    sb.append("\njava -jar <PathToJARFile> --D,--delete <CacheURL>");
    sb.append("\n");
    sb.append("\nExample:");
    sb.append("\njava -jar target\\cli-utils-1.0.0-SNAPSHOT.jar --D http://localhost:8080/api/v1");
    LOG.info(sb.toString());
  }
}
