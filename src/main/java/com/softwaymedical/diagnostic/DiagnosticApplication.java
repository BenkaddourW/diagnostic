package com.softwaymedical.diagnostic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the diagnostic HTTP application and its Spring configuration. */
@SpringBootApplication
public class DiagnosticApplication {

  /**
   * Starts the application with the supplied Spring Boot command-line options.
   *
   * @param args application arguments, such as {@code --server.port=8081}
   */
  public static void main(String[] args) {
    SpringApplication.run(DiagnosticApplication.class, args);
  }
}
