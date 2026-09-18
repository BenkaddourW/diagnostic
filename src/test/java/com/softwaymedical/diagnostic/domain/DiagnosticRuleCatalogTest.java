package com.softwaymedical.diagnostic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DiagnosticRuleCatalogTest {

  @Test
  void shouldContainOnlyTheCurrentMedicalRules() {

    assertThat(DiagnosticRuleCatalog.rules())
        .containsExactly(
            new DiagnosticRule(3, MedicalUnit.CARDIOLOGY),
            new DiagnosticRule(5, MedicalUnit.TRAUMATOLOGY));
  }
}
