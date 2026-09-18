package com.softwaymedical.diagnostic.controller;

import com.softwaymedical.diagnostic.service.DiagnosticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller exposing medical diagnostic operations. */
@RestController
@RequestMapping("/api/v1/diagnostics")
@Tag(name = "Diagnostics", description = "Operations related to health index diagnostics")
public class DiagnosticController {

  private final DiagnosticService diagnosticService;

  /**
   * Uses the supplied service for input validation and unit selection; the controller handles HTTP
   * exposure.
   *
   * @param diagnosticService the service providing the diagnostic text response
   */
  public DiagnosticController(DiagnosticService diagnosticService) {
    this.diagnosticService = diagnosticService;
  }

  /**
   * Determines the medical units associated with a health index.
   *
   * @param healthIndex the positive health index to evaluate
   * @return unique French unit names in catalogue order, separated by {@code ", "}, or an empty
   *     string when no rule matches
   * @throws com.softwaymedical.diagnostic.exception.InvalidHealthIndexException if the index is not
   *     positive; the advice maps this exception to HTTP 400
   */
  @Operation(
      summary = "Diagnose a health index",
      description =
          """
                    Determines the medical units associated with a health index.

                    Multiples of 3 return "Cardiologie" (for example, 33).
                    Multiples of 5 return "Traumatologie" (for example, 55).
                    Multiples of both return "Cardiologie, Traumatologie" (for example, 15), in that order.
                    Returns an empty string when no unit matches.

                    This implementation accepts only positive health indexes as a design assumption.
                    """)
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Diagnostic successfully calculated",
        content =
            @Content(
                mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = String.class),
                examples = {
                  @ExampleObject(name = "index33", value = "Cardiologie"),
                  @ExampleObject(name = "index55", value = "Traumatologie"),
                  @ExampleObject(name = "index15", value = "Cardiologie, Traumatologie")
                })),
    @ApiResponse(
        responseCode = "400",
        description =
            "Health index must be a positive whole number within the 32-bit integer range",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {
                  @ExampleObject(
                      name = "nonPositiveIndex",
                      value =
                          """
                                            {"status":400,"title":"Invalid health index",
                                             "detail":"Health index must be a positive integer.",
                                             "instance":"/api/v1/diagnostics/0"}
                                            """),
                  @ExampleObject(
                      name = "invalidInteger",
                      value =
                          """
                                            {"status":400,"title":"Invalid health index",
                                             "detail":"Health index must be a whole number between 1 and 2147483647.",
                                             "instance":"/api/v1/diagnostics/abc"}
                                            """)
                }))
  })
  @GetMapping(value = "/{healthIndex}", produces = MediaType.TEXT_PLAIN_VALUE)
  public String diagnose(
      @Parameter(
              description = "Health index from 1 to 2147483647; positivity is a design assumption",
              schema =
                  @Schema(
                      type = "integer",
                      format = "int32",
                      minimum = "1",
                      maximum = "2147483647"),
              example = "15",
              required = true)
          @PathVariable
          int healthIndex) {

    return diagnosticService.diagnose(healthIndex);
  }
}
