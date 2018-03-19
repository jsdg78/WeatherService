package com.wh.cache.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class CacheManagerApp implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(CacheManagerApp.class);

  /*public static void main(String[] args) throws Exception {
    SpringApplication app = new SpringApplication(CacheManagerApp.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
  }*/

  @Override
  public void run(ApplicationArguments args) throws Exception {
    LOG.info("Your application started with option names : {}", args.getOptionNames());
  }
}
