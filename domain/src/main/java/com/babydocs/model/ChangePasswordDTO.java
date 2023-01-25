package com.babydocs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ChangePasswordDTO {

    @NotNull(message = "Username is required")
    public String username;
    @NotNull(message = "Current password is required")
    public String currentPassword;
    @NotNull(message = "New password is required")
    public String newPassword;
    @NotNull(message = "Confirm New password is required")
    public String confirmNewPassword;
}
