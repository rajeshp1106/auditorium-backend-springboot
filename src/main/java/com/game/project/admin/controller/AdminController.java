package com.game.project.admin.controller;

import com.game.project.admin.entity.Auditorium;
import com.game.project.admin.entity.Booking;
import com.game.project.admin.entity.UpdateStatusDTO;
import com.game.project.admin.service.AdminStatisticsService;
import com.game.project.admin.service.AuditoriumService;
import com.game.project.admin.service.BookingService;
import com.game.project.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuditoriumService auditoriumService;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping("/auditorium/add")
    public ResponseEntity<?> createAuditorium(@RequestBody Auditorium auditorium){
        if(auditoriumService.findByName(auditorium.getName()).isPresent()){
            return ResponseEntity.badRequest().body("Auditorium with this name already exists");
        }
        Auditorium newAuditorium = auditoriumService.save(auditorium);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Auditorium Created Successfully");
        response.put("auditorium", newAuditorium);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auditoriums/getAll")
    public ResponseEntity<List<Auditorium>> getAllAuditorium() {
        List<Auditorium> auditoriums = auditoriumService.getAllAuditorium();
        return ResponseEntity.ok(auditoriums);
    }

    @PutMapping("/auditorium/update/{id}")
    public ResponseEntity<?> updateAuditorium(@PathVariable Long id,@RequestBody Auditorium auditorium){

        try{
            Auditorium existing = auditoriumService.updateAuditorium(id,auditorium);
            return ResponseEntity.ok(Map.of(
                    "message", "Auditorium updated successfully",
                    "auditorium", existing
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/auditorium/delete/{id}")
    public ResponseEntity<?> deleteAuditorium(@PathVariable Long id){
        auditoriumService.deleteAuditorium(id);
        return ResponseEntity.ok(Map.of("message", "Auditorium deleted successfully"));

    }

    @GetMapping("/bookings/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookingsForAdmin(){
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/status/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody UpdateStatusDTO dto) {

        Booking updated = bookingService.updateBookingStatusByAdmin(bookingId, dto.getBookingStatus());
        return ResponseEntity.ok(updated);
    }

    private final AdminStatisticsService statisticsService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(statisticsService.getDashboardStatistics());
    }

    @GetMapping("/users/getAll")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }





}
