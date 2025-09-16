package com.game.project.admin.service;

import com.game.project.admin.entity.Auditorium;
import com.game.project.admin.entity.Booking;
import com.game.project.admin.entity.BookingStatus;
import com.game.project.admin.repository.AuditoriumRepository;
import com.game.project.admin.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    private final AuditoriumRepository auditoriumRepository;
    private final BookingRepository bookingRepository;

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Auditorium> allAuditoriums = auditoriumRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();

        stats.put("totalAuditoriums", allAuditoriums.size());
        stats.put("activeAuditoriums", allAuditoriums.stream().filter(Auditorium::isActive).count());
        stats.put("inactiveAuditoriums", allAuditoriums.stream().filter(a -> !a.isActive()).count());

        stats.put("totalBookings", allBookings.size());
        stats.put("pendingBookings", allBookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count());
        stats.put("approvedBookings", allBookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).count());
        stats.put("cancelledBookings", allBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count());

        // Optional: Most booked auditorium
        Auditorium mostBooked = allAuditoriums.stream()
                .max((a1, a2) -> Long.compare(
                        bookingRepository.countByAuditorium(a1),
                        bookingRepository.countByAuditorium(a2)
                ))
                .orElse(null);

        stats.put("mostBookedAuditorium", mostBooked != null ? mostBooked.getName() : "N/A");

        return stats;
    }
}

