/**
 * ErrorResponse provides a consistent JSON error structure for API responses.
 *
 * All exceptions are caught by the global exception handler and returned
 * in this standardized format to the client.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("status")
    private int status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("path")
    private String path;
}


