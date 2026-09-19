package com.spendsmart.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Message is required")
    @Size(max = 1000)
    private String message;

    private String severity;
}
