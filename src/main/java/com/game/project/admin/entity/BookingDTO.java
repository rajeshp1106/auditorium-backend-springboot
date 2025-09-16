package com.game.project.admin.entity;

import com.game.project.auth.models.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDTO {

    public Long userId;
    public Long auditoriumId;
    public LocalDateTime start;
    public LocalDateTime end;
    public String purpose;



}
