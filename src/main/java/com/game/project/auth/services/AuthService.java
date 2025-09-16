package com.game.project.auth.services;

import com.game.project.auth.dtos.LoginRequest;
import com.game.project.auth.dtos.OtpVerificationRequest;
import com.game.project.auth.dtos.ResendOtpRequest;
import com.game.project.auth.dtos.SignupRequest;
import com.game.project.auth.models.Role;
import com.game.project.auth.models.User;
import com.game.project.auth.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> register(SignupRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if(optionalUser.isPresent()){
            User existingUser =  optionalUser.get();

            if(Boolean.TRUE.equals(existingUser.getIsVerified())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already registred and verified. Please proceed to login");
            }

            String newOtp = String.format("%06d",new Random().nextInt(999999));
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);

            existingUser.setUsername(request.getUsername());
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            existingUser.setOtp(newOtp);
            existingUser.setOtpExpiry(expiry);
            userRepository.save(existingUser);

            emailService.sendOtp(existingUser.getEmail(),newOtp);

            return ResponseEntity.ok("Registered again: OTP resent to your email");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);


        User newuser = new User(
                null,
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                false,
                otp,
                expiry,
                Role.USER
        );
        userRepository.save(newuser);
        emailService.sendOtp(newuser.getEmail(),otp);
        return ResponseEntity.ok("User registered. OTP sent to email");
    }

    public ResponseEntity<?> verifyOtp(OtpVerificationRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","User not found"));

        }

        User user = optionalUser.get();
        System.out.println("User found :"+" "+user);

        if(user.getOtp()==null || user.getOtpExpiry()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","No OTP generated or already verified"));
        }

        if(!user.getOtp().equals(request.getOtp())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Invalid OTP"));
        }

        if(user.getOtpExpiry().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","OTP expired"));
        }

        user.setIsVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().name());

        return ResponseEntity.ok(Map.of("token",token,"message","Email verified Succesfully. Now proceed to login"));
    }

    public ResponseEntity<?> resendOtp(ResendOtpRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","User not found"));
        }
        User user = optionalUser.get();

        if(Boolean.TRUE.equals(user.getIsVerified())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "User already verified. Please log in."));
        }

        if (user.getOtp() != null && user.getOtpExpiry() != null && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
            long secondsLeft = Duration.between(LocalDateTime.now(),user.getOtpExpiry()).getSeconds();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "OTP already sent. Please wait " + secondsLeft + " seconds."));
        }

        String newOtp = String.format("%06d",new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);
        user.setOtp(newOtp);
        user.setOtpExpiry(expiry);
        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), newOtp);

        return ResponseEntity.ok(Map.of("message", "New OTP sent to email"));


    }

    public ResponseEntity<?> login(LoginRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","User not found"));
        }

        User user = optionalUser.get();

        if(!Boolean.TRUE.equals(user.getIsVerified())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Email is not verified. Please complete the OTP verification."));
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid password"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of("token", token, "message", "Login successful"));


    }

    @PostConstruct
    public void createAdminAccount(){
        User adminAccount =  userRepository.findByRole(Role.ADMIN);
        if(null== adminAccount){
            User user = new User();
            user.setRole(Role.ADMIN);
            user.setEmail("admin@test.com");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setUsername("admin");
            user.setIsVerified(true);
            userRepository.save(user);
        }
    }

    public void getOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsVerified()) {
            throw new RuntimeException("User email is not verified. Please verify before resetting password.");
        }

        String otp = String.format("%06d",new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);
        user.setOtp(otp);
        user.setOtpExpiry(expiry);
        userRepository.save(user);
        emailService.sendEmail(user.getEmail(),"Forgot Password OTP","Password Reset OTP, Your OTP is:"+otp);
    }

    public boolean verifyPasswordOtp(String email, String otp) {
        User  user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsVerified()) {
            throw new RuntimeException("User email is not verified.");
        }
        return user.getOtp()!=null && user.getOtp().equals(otp) && user.getOtpExpiry().isAfter(LocalDateTime.now());
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getIsVerified()) {
            throw new RuntimeException("User email is not verified.");

        }

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

    }
}
