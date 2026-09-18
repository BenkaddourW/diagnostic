package com.softwaymedical.diagnostic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.softwaymedical.diagnostic.exception.InvalidHealthIndexException;
import org.junit.jupiter.api.Test;

class DiagnosticServiceTest {

  private final DiagnosticService diagnosticService = new DiagnosticService();

  @Test
  void shouldReturnCardiologyWhenHealthIndexIsDivisibleByThree() {
    int healthIndex = 33;

    String actualResult = diagnosticService.diagnose(healthIndex);

    assertThat(actualResult).isEqualTo("Cardiologie");
  }

  @Test
  void shouldReturnTraumatologyWhenHealthIndexIsDivisibleByFive() {
    int healthIndex = 55;

    String actualResult = diagnosticService.diagnose(healthIndex);

    assertThat(actualResult).isEqualTo("Traumatologie");
  }

  @Test
  void shouldReturnBothMedicalUnitsWhenHealthIndexIsDivisibleByThreeAndFive() {
    int healthIndex = 15;

    String actualResult = diagnosticService.diagnose(healthIndex);

    assertThat(actualResult).isEqualTo("Cardiologie, Traumatologie");
  }

  @Test
  void shouldReturnEmptyStringWhenNoMedicalUnitMatches() {
    int healthIndex = 7;

    String actualResult = diagnosticService.diagnose(healthIndex);

    assertThat(actualResult).isEmpty();
  }

  @Test
  void shouldRejectZeroHealthIndex() {

    assertThatThrownBy(() -> diagnosticService.diagnose(0))
        .isInstanceOf(InvalidHealthIndexException.class)
        .hasMessage("Health index must be a positive integer.");
  }

  @Test
  void shouldRejectNegativeHealthIndex() {

    assertThatThrownBy(() -> diagnosticService.diagnose(-15))
        .isInstanceOf(InvalidHealthIndexException.class)
        .hasMessage("Health index must be a positive integer.");
  }
}
