package com.softwaymedical.diagnostic.domain;

/** Defines the supported medical units and the French labels used in diagnostic responses. */
public enum MedicalUnit {
  CARDIOLOGY("Cardiologie"),
  TRAUMATOLOGY("Traumatologie");

  private final String displayName;

  MedicalUnit(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Returns the label used in the public text response.
   *
   * @return the French medical unit name
   */
  public String getDisplayName() {
    return displayName;
  }
}
