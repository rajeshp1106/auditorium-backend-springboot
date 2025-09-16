package com.game.project.admin.repository;

import com.game.project.admin.entity.Auditorium;
import com.game.project.admin.entity.Booking;
import com.game.project.admin.entity.BookingStatus;
import com.game.project.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatus(BookingStatus status);

    // To check for time clashes
    List<Booking> findByAuditoriumAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Auditorium auditorium,
            Collection<BookingStatus> statuses,
            LocalDateTime end,
            LocalDateTime start);


    List<Booking> findByUser(User user);

    List<Booking> findByAuditorium(Auditorium auditorium);

    List<Booking> findByUserId(long id);

    // Add this in BookingRepository
    List<Booking> findByAuditoriumAndStatusInAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Auditorium auditorium,
            Collection<BookingStatus> statuses,
            Long excludeId,
            LocalDateTime end,
            LocalDateTime start);

    long countByAuditorium(Auditorium a1);
}

