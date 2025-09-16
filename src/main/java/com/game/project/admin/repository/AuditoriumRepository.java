package com.game.project.admin.repository;

import com.game.project.admin.entity.Auditorium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
    Optional<Auditorium> findByName(String name);

    List<Auditorium> findByActiveTrue();
}
