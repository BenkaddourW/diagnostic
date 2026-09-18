package com.softwaymedical.diagnostic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DiagnosticApplicationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldDocumentSuccessfulDiagnosticAsPlainText() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath(
                    "$.paths['/api/v1/diagnostics/{healthIndex}'].get.responses['200'].content['text/plain'].schema.type")
                .value("string"));
  }

  @Test
  void shouldDocumentInvalidHealthIndexAsProblemDetail() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath(
                    "$.paths['/api/v1/diagnostics/{healthIndex}'].get.responses['400'].content['application/problem+json'].schema['$ref']")
                .value("#/components/schemas/ProblemDetail"))
        .andExpect(
            jsonPath(
                    "$.paths['/api/v1/diagnostics/{healthIndex}'].get.responses['400'].content['text/plain']")
                .doesNotExist())
        .andExpect(
            jsonPath("$.components.schemas.ProblemDetail.properties.status.type").value("integer"))
        .andExpect(
            jsonPath("$.components.schemas.ProblemDetail.properties.title.type").value("string"))
        .andExpect(
            jsonPath("$.components.schemas.ProblemDetail.properties.detail.type").value("string"))
        .andExpect(
            jsonPath("$.components.schemas.ProblemDetail.properties.instance.type")
                .value("string"));
  }
}
