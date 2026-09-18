package com.softwaymedical.diagnostic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configures the OpenAPI documentation exposed by the application. */
@Configuration
public class OpenApiConfig {

  /**
   * Supplies API metadata; the documented API version is separate from the Maven artifact version.
   *
   * @return metadata merged with the operations discovered by springdoc
   */
  @Bean
  public OpenAPI diagnosticOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Medical Diagnostic API")
                .description(
                    "API for determining medical units " + "associated with a health index.")
                .version("1.0.0"));
  }
}
