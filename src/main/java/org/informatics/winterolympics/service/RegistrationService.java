package org.informatics.winterolympics.service;

import org.informatics.winterolympics.dto.RegistrationDto;
import org.informatics.winterolympics.model.*;
import org.informatics.winterolympics.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
public class RegistrationService {

    private final CompetitionRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final SkiSlalomRepository slalomRepository;
    private final BiathlonRepository biathlonRepository;

    public RegistrationService(CompetitionRegistrationRepository registrationRepository,
                               UserRepository userRepository,
                               SkiSlalomRepository slalomRepository,
                               BiathlonRepository biathlonRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.slalomRepository = slalomRepository;
        this.biathlonRepository = biathlonRepository;
    }

    public void applyForSlalom(String keycloakUserId, Long slalomId) {
        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        SkiSlalom slalom = slalomRepository.findById(slalomId)
                .orElseThrow(() -> new RuntimeException("Competition not found"));

        if (registrationRepository.existsByUserAndSkiSlalom(user, slalom)) {
            throw new IllegalStateException("Already applied for this competition");
        }

        validateEligibility(user.getAthlete(), slalom.getSex(), slalom.getMinimalAge());

        CompetitionRegistration reg = new CompetitionRegistration();
        reg.setUser(user);
        reg.setSkiSlalom(slalom);
        registrationRepository.save(reg);
    }

    public void applyForBiathlon(String keycloakUserId, Long biathlonId) {
        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Biathlon biathlon = biathlonRepository.findById(biathlonId)
                .orElseThrow(() -> new RuntimeException("Competition not found"));

        if (registrationRepository.existsByUserAndBiathlon(user, biathlon)) {
            throw new IllegalStateException("Already applied for this competition");
        }

        validateEligibility(user.getAthlete(), biathlon.getSex(), biathlon.getMinimalAge());

        CompetitionRegistration reg = new CompetitionRegistration();
        reg.setUser(user);
        reg.setBiathlon(biathlon);
        registrationRepository.save(reg);
    }

    @Transactional(readOnly = true)
    public List<RegistrationDto> getMyRegistrations(String keycloakUserId) {
        return registrationRepository.findByUserKeycloakUserId(keycloakUserId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<RegistrationDto> getAllRegistrations() {
        return registrationRepository.findAll().stream().map(this::toDto).toList();
    }

    public void accept(Long id) {
        CompetitionRegistration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setStatus(RegistrationStatus.ACCEPTED);
    }

    public void reject(Long id) {
        CompetitionRegistration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setStatus(RegistrationStatus.REJECTED);
    }

    private void validateEligibility(Athlete athlete, String requiredSex, int minAge) {
        if (athlete == null) return;
        if (athlete.getSex() != null && !athlete.getSex().isEmpty()
                && !athlete.getSex().equalsIgnoreCase(requiredSex)) {
            throw new IllegalArgumentException(
                    "Your sex does not match this competition's requirement (" + requiredSex + ")");
        }
        if (athlete.getDateOfBirth() != null) {
            int age = Period.between(athlete.getDateOfBirth().toLocalDate(), LocalDate.now()).getYears();
            if (age < minAge) {
                throw new IllegalArgumentException("Minimum age for this competition is " + minAge);
            }
        }
    }

    private RegistrationDto toDto(CompetitionRegistration r) {
        boolean isSlalom = r.getSkiSlalom() != null;
        String type = isSlalom ? "SLALOM" : "BIATHLON";
        String compName = isSlalom ? r.getSkiSlalom().getName() : r.getBiathlon().getName();
        Long compId = isSlalom ? r.getSkiSlalom().getId() : r.getBiathlon().getId();
        String gameName = isSlalom
                ? r.getSkiSlalom().getOlympicGame().getName()
                : r.getBiathlon().getOlympicGame().getName();
        String username = r.getUser().getUsername();
        String athleteName = r.getUser().getAthlete() != null
                ? r.getUser().getAthlete().getName() : username;
        return new RegistrationDto(r.getId(), type, compName, compId, gameName,
                username, athleteName, r.getStatus().name(), r.getAppliedAt().toString());
    }
}
