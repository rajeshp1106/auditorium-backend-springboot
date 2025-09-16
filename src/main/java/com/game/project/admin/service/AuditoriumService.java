package com.game.project.admin.service;

import com.game.project.admin.entity.Auditorium;
import com.game.project.admin.repository.AuditoriumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditoriumService {

    private final AuditoriumRepository auditoriumRepository;

    public Auditorium save(Auditorium auditorium){
        return auditoriumRepository.save(auditorium);
    }

    public Optional<Auditorium> findByName(String name){
        return auditoriumRepository.findByName(name);
    }

    public List<Auditorium> getAllAuditorium(){
        return auditoriumRepository.findAll();
    }

    public List<Auditorium> getAll(){
        return auditoriumRepository.findByActiveTrue();
    }
    public Auditorium findAuditoriumById(Long id){
        return auditoriumRepository.findById(id).get();
    }

    public Auditorium updateAuditorium(Long id, Auditorium updated) {
        Auditorium updatedAuditorium = auditoriumRepository.findById(id).map(auditorium -> {
            auditorium.setName(updated.getName());
            auditorium.setLocation(updated.getLocation());
            auditorium.setCapacity(updated.getCapacity());
            auditorium.setHasProjector(updated.isHasProjector());
            auditorium.setHasSoundSystem(updated.isHasSoundSystem());
            auditorium.setHasAirConditioning(updated.isHasAirConditioning());
            auditorium.setHasStageLighting(updated.isHasStageLighting());
            auditorium.setHasWifi(updated.isHasWifi());
            auditorium.setHasWheelchairAccess(updated.isHasWheelchairAccess());
            auditorium.setHasGreenRoom(updated.isHasGreenRoom());
            auditorium.setHasParking(updated.isHasParking());
            auditorium.setHasPodium(updated.isHasPodium());
            auditorium.setHasVideoRecording(updated.isHasVideoRecording());
            auditorium.setActive(updated.isActive());
            return auditoriumRepository.save(auditorium);
        }).orElseThrow(() -> new RuntimeException("Auditorium not found"));

        return updatedAuditorium;
    }

    public void deleteAuditorium(Long id){
        Auditorium auditorium = auditoriumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auditorium not found with id: " + id));
        auditoriumRepository.delete(auditorium);
    }

}
