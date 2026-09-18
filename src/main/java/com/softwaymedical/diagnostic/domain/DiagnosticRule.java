package com.softwaymedical.diagnostic.domain;

import java.util.Objects;

/**
 * Immutable association between a strictly positive divisor and a non-null medical unit.
 *
 * @param divisor the positive divisor used to evaluate the index
 * @param medicalUnit the medical unit associated with this rule
 */
public record DiagnosticRule(int divisor, MedicalUnit medicalUnit) {

  /**
   * Creates a rule whose divisor can safely be used in a remainder operation.
   *
   * @param divisor the strictly positive divisor
   * @param medicalUnit the non-null unit selected when the rule matches
   * @throws IllegalArgumentException if the divisor is zero or negative
   * @throws NullPointerException if the medical unit is null
   */
  public DiagnosticRule {
    if (divisor <= 0) {
      throw new IllegalArgumentException("Divisor must be positive.");
    }

    Objects.requireNonNull(medicalUnit);
  }

  /**
   * Checks whether the health index satisfies this rule. This arithmetic operation also accepts
   * zero and negative indexes; the service is responsible for enforcing the input positivity
   * policy.
   *
   * @param healthIndex the health index to evaluate
   * @return true if the index is divisible by this rule's divisor
   */
  public boolean matches(int healthIndex) {
    return healthIndex % divisor == 0;
  }
}
