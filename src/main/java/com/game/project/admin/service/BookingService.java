package com.game.project.admin.service;
import com.game.project.admin.entity.Auditorium;
import com.game.project.admin.entity.Booking;
import com.game.project.admin.entity.BookingDTO;
import com.game.project.admin.entity.BookingStatus;
import com.game.project.admin.exception.ConflictException;
import com.game.project.admin.exception.ResourceNotFoundException;
import com.game.project.admin.exception.UnauthorizedException;
import com.game.project.admin.repository.AuditoriumRepository;
import com.game.project.admin.repository.BookingRepository;
import com.game.project.auth.models.User;
import com.game.project.auth.repositories.UserRepository;
import com.game.project.auth.services.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ResponseEntity<Booking> createBooking(BookingDTO bookingDTO, Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Auditorium auditorium = auditoriumRepository.findById(bookingDTO.getAuditoriumId())
                .orElseThrow(() -> new ResourceNotFoundException("Auditorium not found"));

        // check for conflicts
        List<Booking> conflicts = bookingRepository
                .findByAuditoriumAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        auditorium,
                        List.of(BookingStatus.PENDING, BookingStatus.APPROVED),
                        bookingDTO.getEnd(),
                        bookingDTO.getStart()
                );

        if (!conflicts.isEmpty()) {
            throw new ConflictException("Auditorium already booked during this time slot");
        }


        if (!conflicts.isEmpty()) {
            throw new ConflictException("Auditorium already booked during this time slot");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setAuditorium(auditorium);
        booking.setStartTime(bookingDTO.getStart());
        booking.setEndTime(bookingDTO.getEnd());
        booking.setPurpose(bookingDTO.getPurpose());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        String date = bookingDTO.getStart().toLocalDate().format(dateFmt);
        String fromTime = bookingDTO.getStart().toLocalTime().format(timeFmt);
        String toTime   = bookingDTO.getEnd().toLocalTime().format(timeFmt);

        Duration duration = Duration.between(bookingDTO.getStart(), bookingDTO.getEnd());
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart(); // Java 9+

        String durationStr = (hours > 0 ? hours + " hr " : "")
                + (minutes > 0 ? minutes + " min" : "");
        if (durationStr.isBlank()) durationStr = "0 min";

        String subject = "Booking Created: " + auditorium.getName();
        String body = String.format(
                "Hello %s,\n\nYour booking for auditorium '%s' is PENDING approval.\n" +
                        "Date: %s\nTime: %s – %s\nDuration: %s\nPurpose: %s\n\nRegards,\nAuditorium Team",
                user.getUsername(),
                auditorium.getName(),
                date,
                fromTime,
                toTime,
                durationStr,
                bookingDTO.getPurpose()
        );
        emailService.sendEmail(username, subject, body);

        return ResponseEntity.ok(savedBooking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserId(user.getId());
    }

    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Transactional
    public Booking updatePendingBooking(Long bookingId,
                                        BookingDTO dto,
                                        String userEmail) {

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // ✅ Only the owner can edit
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You cannot edit someone else’s booking");
        }

        // ✅ Only while still pending
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking already approved or cancelled");
        }

        Auditorium auditorium = auditoriumRepository.findById(dto.getAuditoriumId())
                .orElseThrow(() -> new ResourceNotFoundException("Auditorium not found"));

        // ✅ check conflicts excluding this booking
        List<Booking> conflicts =
                bookingRepository.findByAuditoriumAndStatusInAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        auditorium,
                        List.of(BookingStatus.PENDING, BookingStatus.APPROVED),
                        booking.getId(),
                        dto.getEnd(),
                        dto.getStart());

        if (!conflicts.isEmpty()) {
            throw new ConflictException("Auditorium already booked during this time slot");
        }

        // ✅ Update fields
        booking.setAuditorium(auditorium);
        booking.setStartTime(dto.getStart());
        booking.setEndTime(dto.getEnd());
        booking.setPurpose(dto.getPurpose());

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking updateBookingStatusByAdmin(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(status);
        Booking updated = bookingRepository.save(booking);

        // ✅ Send email notification to the user
        String subject = "Booking Status Updated : " + booking.getAuditorium().getName();
        String body = String.format(
                "Hello %s,\n\nYour booking for auditorium '%s' has been updated.\n" +
                        "New Status: %s\nStart: %s\nEnd: %s\n\nRegards,\nAuditorium Team",
                booking.getUser().getUsername(),
                booking.getAuditorium().getName(),
                status,
                booking.getStartTime(),
                booking.getEndTime()
        );
        emailService.sendEmail(booking.getUser().getEmail(), subject, body);

        return updated;
    }

}



