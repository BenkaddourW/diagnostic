package com.softwaymedical.diagnostic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application Diagnostic et de sa configuration Spring Boot.
 *
 * @author Benkaddour Wafaa
 * @version 0.0.1-SNAPSHOT
 */
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
