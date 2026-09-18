package com.softwaymedical.diagnostic.exception;

/**
 * Signals an index rejected by the service's strictly positive input policy. This policy is a
 * design assumption, not a confirmed requirement of the exercise.
 */
public class InvalidHealthIndexException extends IllegalArgumentException {

  /**
   * Creates an input error whose message is exposed as the HTTP problem detail.
   *
   * @param message a client-facing explanation without internal technical details
   */
  public InvalidHealthIndexException(String message) {
    super(message);
  }
}
