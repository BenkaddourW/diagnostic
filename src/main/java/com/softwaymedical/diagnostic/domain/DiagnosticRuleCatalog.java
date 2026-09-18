package com.softwaymedical.diagnostic.domain;

import java.util.List;

/** Provides the fixed, immutable rules in their diagnostic output order. */
public final class DiagnosticRuleCatalog {

  private static final List<DiagnosticRule> RULES =
      List.of(
          new DiagnosticRule(3, MedicalUnit.CARDIOLOGY),
          new DiagnosticRule(5, MedicalUnit.TRAUMATOLOGY));

  private DiagnosticRuleCatalog() {}

  /**
   * Returns the rules in evaluation order: cardiology before traumatology. The service retains the
   * first matching occurrence of each medical unit.
   *
   * @return an unmodifiable list of diagnostic rules
   */
  public static List<DiagnosticRule> rules() {
    return RULES;
  }
}
