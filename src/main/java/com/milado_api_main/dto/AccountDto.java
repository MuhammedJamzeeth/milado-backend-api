package com.milado_api_main.dto;

import com.milado_api_main.entities.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link Account}
 */

@Getter
@Setter
@Schema(title = "AccountCreationRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class AccountDto implements Serializable{

    @NotNull(message = "ID cannot be null")
    Integer id;

    @NotBlank(message = "First name cannot be blank")
    String firstName;

    @NotBlank(message = "Last name cannot be blank")
    String lastName;

    @NotBlank(message = "Phone number cannot be blank")
    String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Password cannot be blank")
    String password;

    @NotNull(message = "Verification status for phone cannot be null")
    Integer isVerifiedPhone;

    @NotNull(message = "Verification status for Email cannot be null")
    Integer isVerifiedEmail;
}