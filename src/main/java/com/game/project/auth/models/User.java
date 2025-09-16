package com.game.project.auth.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    private String username;
    private String password;
    private Boolean isVerified = false;
    private String otp;
    private LocalDateTime otpExpiry;


    @Enumerated(EnumType.STRING)
    private Role role;

}
