package com.game.project.auth.dtos;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String email;
    private String newPassword;
}
