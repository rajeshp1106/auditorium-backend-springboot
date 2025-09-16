package com.game.project.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "auditoriums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditorium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String location;

    private int capacity;

    private boolean hasProjector;

    private boolean hasSoundSystem;

    private boolean hasAirConditioning;

    private boolean hasStageLighting;

    private boolean hasWifi;

    private boolean hasWheelchairAccess;

    private boolean hasGreenRoom;

    private boolean hasParking;

    private boolean hasPodium;

    private boolean hasVideoRecording;

    private boolean active = true;

    private String imgUrl;

    @OneToMany(mappedBy = "auditorium", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonIgnore   // << add this
    private List<Booking> bookings;


}
