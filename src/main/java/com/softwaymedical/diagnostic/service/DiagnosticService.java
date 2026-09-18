package com.softwaymedical.diagnostic.service;

import com.softwaymedical.diagnostic.domain.DiagnosticRuleCatalog;
import com.softwaymedical.diagnostic.domain.MedicalUnit;
import com.softwaymedical.diagnostic.exception.InvalidHealthIndexException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** Validates indexes, evaluates the fixed rule catalogue and formats the selected units. */
@Service
public class DiagnosticService {

  /**
   * Determines the medical units associated with a health index. Each unit appears at most once, in
   * the order of its first matching catalogue rule. Strict positivity is an application design
   * assumption.
   *
   * @param healthIndex the strictly positive health index to evaluate
   * @return the French unit names separated by {@code ", "}, or an empty string when no rule
   *     matches
   * @throws InvalidHealthIndexException if the index is not positive
   */
  public String diagnose(int healthIndex) {

    if (healthIndex <= 0) {
      throw new InvalidHealthIndexException("Health index must be a positive integer.");
    }

    return DiagnosticRuleCatalog.rules().stream()
        .filter(rule -> rule.matches(healthIndex))
        .map(rule -> rule.medicalUnit())
        .distinct()
        .map(MedicalUnit::getDisplayName)
        .collect(Collectors.joining(", "));
  }
}
