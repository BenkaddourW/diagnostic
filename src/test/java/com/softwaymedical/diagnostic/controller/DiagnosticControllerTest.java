package com.softwaymedical.diagnostic.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.softwaymedical.diagnostic.exception.DiagnosticExceptionHandler;
import com.softwaymedical.diagnostic.service.DiagnosticService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(DiagnosticController.class)
@Import({DiagnosticService.class, DiagnosticExceptionHandler.class})
class DiagnosticControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnCardiologyWhenHealthIndexIs33() throws Exception {

    mockMvc
        .perform(get("/api/v1/diagnostics/33"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Cardiologie"));
  }

  @Test
  void shouldReturnBothMedicalUnitsWhenHealthIndexIs15() throws Exception {

    mockMvc
        .perform(get("/api/v1/diagnostics/15"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Cardiologie, Traumatologie"));
  }

  @Test
  void shouldReturnBadRequestWhenHealthIndexIsNotNumeric() throws Exception {

    expectInvalidHealthIndex(
        "abc", "Health index must be a whole number between 1 and 2147483647.");
  }

  @Test
  void shouldReturnBadRequestWhenHealthIndexIsZero() throws Exception {

    mockMvc
        .perform(get("/api/v1/diagnostics/0"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.instance").value("/api/v1/diagnostics/0"))
        .andExpect(jsonPath("$.title").value("Invalid health index"))
        .andExpect(jsonPath("$.detail").value("Health index must be a positive integer."));
  }

  @Test
  void shouldReturnBadRequestWhenHealthIndexIsNegative() throws Exception {

    mockMvc
        .perform(get("/api/v1/diagnostics/-15"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.instance").value("/api/v1/diagnostics/-15"))
        .andExpect(jsonPath("$.title").value("Invalid health index"))
        .andExpect(jsonPath("$.detail").value("Health index must be a positive integer."));
  }

  @Test
  void shouldReturnTraumatologyWhenHealthIndexIs55() throws Exception {
    mockMvc
        .perform(get("/api/v1/diagnostics/55").accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Traumatologie"));
  }

  @Test
  void shouldReturnEmptyStringWhenNoMedicalUnitMatches() throws Exception {
    mockMvc
        .perform(get("/api/v1/diagnostics/7").accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturnBadRequestWhenHealthIndexIsDecimal() throws Exception {
    expectInvalidHealthIndex(
        "1.5", "Health index must be a whole number between 1 and 2147483647.");
  }

  @Test
  void shouldReturnBadRequestWhenHealthIndexExceedsIntegerRange() throws Exception {
    expectInvalidHealthIndex(
        "2147483648", "Health index must be a whole number between 1 and 2147483647.");
  }

  @ParameterizedTest
  @CsvSource({
    "0, Health index must be a positive integer.",
    "-15, Health index must be a positive integer.",
    "abc, Health index must be a whole number between 1 and 2147483647.",
    "1.5, Health index must be a whole number between 1 and 2147483647.",
    "2147483648, Health index must be a whole number between 1 and 2147483647."
  })
  void shouldReturnProblemDetailWhenClientAcceptsPlainText(String healthIndex, String detail)
      throws Exception {
    expectProblemDetail(
        mockMvc.perform(
            get("/api/v1/diagnostics/{healthIndex}", healthIndex).accept(MediaType.TEXT_PLAIN)),
        healthIndex,
        detail);
  }

  private void expectInvalidHealthIndex(String healthIndex, String detail) throws Exception {
    expectProblemDetail(
        mockMvc.perform(get("/api/v1/diagnostics/{healthIndex}", healthIndex)),
        healthIndex,
        detail);
  }

  private void expectProblemDetail(ResultActions response, String healthIndex, String detail)
      throws Exception {
    response
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Invalid health index"))
        .andExpect(jsonPath("$.detail").value(detail))
        .andExpect(jsonPath("$.instance").value("/api/v1/diagnostics/" + healthIndex));
  }
}
