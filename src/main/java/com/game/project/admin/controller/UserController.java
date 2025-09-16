package com.game.project.admin.controller;

import com.game.project.admin.entity.Auditorium;
import com.game.project.admin.service.AuditoriumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuditoriumService auditoriumService;


    @GetMapping("/auditoriums/getAll")
    public ResponseEntity<List<Auditorium>> getAllAuditoriums() {
        List<Auditorium> auditoriums = auditoriumService.getAll();
        return ResponseEntity.ok(auditoriums);
    }

    @GetMapping("/auditorium/get/{id}")
    public ResponseEntity<Auditorium> getAuditoriumById(@PathVariable Long id){
        Auditorium saved = auditoriumService.findAuditoriumById(id);
        return ResponseEntity.ok(saved);
    }
}
