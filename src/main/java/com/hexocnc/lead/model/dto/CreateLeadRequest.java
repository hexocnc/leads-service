package com.hexocnc.lead.model.dto;

import com.hexocnc.domain.Domain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLeadRequest {

    @NotNull(message = "Lead domain is required")
    private Domain domain;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be 100 characters or fewer")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^\\+?[0-9\\s\\-()]{7,20}$",
            message = "Invalid phone number format"
    )
    private String phone;

    private String email;

    private String description;

}
