package com.game.project.auth.controllers;

import com.game.project.auth.dtos.*;
import com.game.project.auth.models.User;
import com.game.project.auth.repositories.UserRepository;
import com.game.project.auth.services.AuthService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import com.game.project.auth.services.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name="Authentication",description = "Handles signup,OTP,login,logout flows")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    private final UserRepository  userRepository;

    private final CustomUserDetailsService userDetailsService;


    @PostMapping("/register")
    @Operation(summary = "Register User",description = "Saves user and sends OTP to email")
    public ResponseEntity<?> register(@RequestBody SignupRequest request){
        return authService.register(request);
    }

    @PostMapping("/verify-otp")
    @Operation(
            summary = "Verify Email OTP",
            description = "Verifies the OTP and returns a JWT token if successful")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request){
        return authService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    @Operation(
            summary = "Resend OTP",description = "Resends a new OTP if the previous one is expired")
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest request){
        return authService.resendOtp(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login",description = "Logs in a user (verified) and returns a JWT token on success")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return authService.login(request);

//        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/forgot/password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request){
        authService.getOtp(request.getEmail());
        return ResponseEntity.ok("OTP is sent to the email if account exists");
    }

    @PostMapping("/verify/password-otp")
    public ResponseEntity<String> verifyPasswordOtp(@RequestBody OtpVerificationRequest request){
        boolean valid = authService.verifyPasswordOtp(request.getEmail(),request.getOtp());
        if(valid){
            return ResponseEntity.ok("OTP has been verified");
        }
        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    @PostMapping("/reset/password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request){
        authService.resetPassword(request.getEmail(),request.getNewPassword());
        return ResponseEntity.ok("Password successfully reset");
    }


    }

