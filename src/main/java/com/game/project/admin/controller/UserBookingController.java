package com.game.project.admin.controller;

import com.game.project.admin.entity.Auditorium;
import com.game.project.admin.entity.Booking;
import com.game.project.admin.entity.BookingDTO;
import com.game.project.admin.service.AuditoriumService;
import com.game.project.admin.service.BookingService;
import com.game.project.auth.models.User;
import com.game.project.auth.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user/bookings")
@RequiredArgsConstructor
public class UserBookingController {

    private final BookingService bookingService;
    private  final CustomUserDetailsService userDetailsService;
    private final AuditoriumService auditoriumService;

   @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO,Authentication authentication){
       return bookingService.createBooking(bookingDTO,authentication);
   }

   @GetMapping("/getAll")
   public List<Booking> getBookingsByUser(Authentication authentication){
       return bookingService.getBookingsByUser(authentication);

   }

   @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable("bookingId") Long bookingId,Authentication authentication){
       bookingService.cancelBooking(bookingId,authentication);
       return ResponseEntity.ok(Map.of(
               "success",true,
               "message","Booking cancelled successfully"
       ));
   }
    @PutMapping("/update/{bookingid}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long bookingid,
            @RequestBody BookingDTO dto,
            Authentication authentication) {

        // authentication.getName() gives the logged-in user’s email
        Booking updated = bookingService.updatePendingBooking(bookingid, dto, authentication.getName());
        return ResponseEntity.ok(updated);
    }
}
