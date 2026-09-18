package com.softwaymedical.diagnostic.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DiagnosticRuleTest {

  @Test
  void shouldMatchWhenHealthIndexIsDivisibleByDivisor() {

    DiagnosticRule rule = new DiagnosticRule(3, MedicalUnit.CARDIOLOGY);

    assertThat(rule.matches(33)).isTrue();
  }

  @Test
  void shouldNotMatchWhenHealthIndexIsNotDivisibleByDivisor() {

    DiagnosticRule rule = new DiagnosticRule(3, MedicalUnit.CARDIOLOGY);

    assertThat(rule.matches(10)).isFalse();
  }

  @Test
  void shouldRejectZeroDivisor() {

    assertThatThrownBy(() -> new DiagnosticRule(0, MedicalUnit.CARDIOLOGY))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Divisor must be positive.");
  }

  @Test
  void shouldRejectNegativeDivisor() {

    assertThatThrownBy(() -> new DiagnosticRule(-3, MedicalUnit.CARDIOLOGY))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldRejectNullMedicalUnit() {

    assertThatThrownBy(() -> new DiagnosticRule(3, null)).isInstanceOf(NullPointerException.class);
  }
}
