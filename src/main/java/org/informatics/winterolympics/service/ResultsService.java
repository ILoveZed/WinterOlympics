package org.informatics.winterolympics.service;

import org.informatics.winterolympics.dto.*;
import org.informatics.winterolympics.model.*;
import org.informatics.winterolympics.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultsService {

    private final SlalomResultRepository slalomResultRepository;
    private final BiathlonResultRepository biathlonResultRepository;
    private final CompetitionRegistrationRepository registrationRepository;
    private final SkiSlalomRepository slalomRepository;
    private final BiathlonRepository biathlonRepository;

    public ResultsService(SlalomResultRepository slalomResultRepository,
                          BiathlonResultRepository biathlonResultRepository,
                          CompetitionRegistrationRepository registrationRepository,
                          SkiSlalomRepository slalomRepository,
                          BiathlonRepository biathlonRepository) {
        this.slalomResultRepository = slalomResultRepository;
        this.biathlonResultRepository = biathlonResultRepository;
        this.registrationRepository = registrationRepository;
        this.slalomRepository = slalomRepository;
        this.biathlonRepository = biathlonRepository;
    }

    // ── Slalom ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public SlalomResultsDto getSlalomResults(Long slalomId) {
        SkiSlalom slalom = slalomRepository.findById(slalomId)
                .orElseThrow(() -> new RuntimeException("Competition not found: " + slalomId));

        int run2Qualifiers = slalom.getRuns().stream()
                .filter(r -> r.getRunNumber() == 2)
                .mapToInt(SkiSlalomRun::getNumberOfAthletes)
                .findFirst().orElse(0);

        List<CompetitionRegistration> accepted =
                registrationRepository.findBySkiSlalomIdAndStatus(slalomId, RegistrationStatus.ACCEPTED);

        Map<Long, SlalomResult> resultMap = slalomResultRepository
                .findByRegistrationSkiSlalomId(slalomId)
                .stream()
                .collect(Collectors.toMap(r -> r.getRegistration().getId(), r -> r));

        // Assign run1Rank: sort athletes with run1 result by run1 time
        List<CompetitionRegistration> withRun1 = accepted.stream()
                .filter(reg -> resultMap.containsKey(reg.getId()))
                .sorted(Comparator.comparingDouble(reg -> resultMap.get(reg.getId()).getRun1TimeSeconds()))
                .collect(Collectors.toList());

        Map<Long, Integer> run1RankMap = new HashMap<>();
        for (int i = 0; i < withRun1.size(); i++) {
            run1RankMap.put(withRun1.get(i).getId(), i + 1);
        }

        // Assign finalRank: qualified athletes with both runs, sorted by combined time
        List<CompetitionRegistration> withBothRuns = withRun1.stream()
                .filter(reg -> {
                    Integer r1Rank = run1RankMap.get(reg.getId());
                    return r1Rank != null && r1Rank <= run2Qualifiers
                            && resultMap.get(reg.getId()).getRun2TimeSeconds() != null;
                })
                .sorted(Comparator.comparingDouble(reg -> resultMap.get(reg.getId()).getCombinedTime()))
                .collect(Collectors.toList());

        Map<Long, Integer> finalRankMap = new HashMap<>();
        for (int i = 0; i < withBothRuns.size(); i++) {
            finalRankMap.put(withBothRuns.get(i).getId(), i + 1);
        }

        List<SlalomAthleteResultDto> athletes = new ArrayList<>();
        for (CompetitionRegistration reg : accepted) {
            SlalomResult r = resultMap.get(reg.getId());
            String name = reg.getUser().getAthlete() != null
                    ? reg.getUser().getAthlete().getName()
                    : reg.getUser().getUsername();
            Integer run1Rank = run1RankMap.get(reg.getId());
            boolean qualifiedForRun2 = run1Rank != null && run1Rank <= run2Qualifiers;
            athletes.add(new SlalomAthleteResultDto(
                    reg.getId(), name, reg.getUser().getUsername(),
                    r != null ? r.getRun1TimeSeconds() : null,
                    run1Rank,
                    qualifiedForRun2,
                    r != null ? r.getRun2TimeSeconds() : null,
                    r != null ? r.getCombinedTime() : null,
                    finalRankMap.get(reg.getId())));
        }

        return new SlalomResultsDto(slalomId, slalom.getName(),
                slalom.getOlympicGame().getName(), run2Qualifiers, athletes);
    }

    @Transactional
    public void saveSlalomRun1Results(Long slalomId, List<SlalomResultEntry> entries) {
        for (SlalomResultEntry entry : entries) {
            SlalomResult result = slalomResultRepository
                    .findByRegistrationId(entry.registrationId())
                    .orElseGet(() -> {
                        CompetitionRegistration reg = registrationRepository
                                .findById(entry.registrationId())
                                .orElseThrow(() -> new RuntimeException(
                                        "Registration not found: " + entry.registrationId()));
                        SlalomResult r = new SlalomResult();
                        r.setRegistration(reg);
                        return r;
                    });
            result.setRun1TimeSeconds(entry.run1TimeSeconds());
            slalomResultRepository.save(result);
        }
    }

    @Transactional
    public void saveSlalomRun2Results(Long slalomId, List<SlalomRun2Entry> entries) {
        for (SlalomRun2Entry entry : entries) {
            SlalomResult result = slalomResultRepository
                    .findByRegistrationId(entry.registrationId())
                    .orElseThrow(() -> new RuntimeException(
                            "No Run 1 result for registration: " + entry.registrationId()));
            result.setRun2TimeSeconds(entry.run2TimeSeconds());
            slalomResultRepository.save(result);
        }
    }

    // ── Biathlon ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public BiathlonResultsDto getBiathlonResults(Long biathlonId) {
        Biathlon biathlon = biathlonRepository.findById(biathlonId)
                .orElseThrow(() -> new RuntimeException("Competition not found: " + biathlonId));

        List<CompetitionRegistration> accepted =
                registrationRepository.findByBiathlonIdAndStatus(biathlonId, RegistrationStatus.ACCEPTED);

        Map<Long, BiathlonResult> resultMap = biathlonResultRepository
                .findByRegistrationBiathlonId(biathlonId)
                .stream()
                .collect(Collectors.toMap(r -> r.getRegistration().getId(), r -> r));

        List<BiathlonAthleteResultDto> withResults = new ArrayList<>();
        List<BiathlonAthleteResultDto> withoutResults = new ArrayList<>();

        for (CompetitionRegistration reg : accepted) {
            BiathlonResult result = resultMap.get(reg.getId());
            String name = reg.getUser().getAthlete() != null
                    ? reg.getUser().getAthlete().getName()
                    : reg.getUser().getUsername();
            if (result != null) {
                double finalTime = result.getRaceTimeSeconds()
                        + ((double) result.getNumberOfMisses() * biathlon.getPenaltyTimeSeconds());
                withResults.add(new BiathlonAthleteResultDto(
                        reg.getId(), name, reg.getUser().getUsername(),
                        result.getRaceTimeSeconds(), result.getNumberOfMisses(), finalTime, null));
            } else {
                withoutResults.add(new BiathlonAthleteResultDto(
                        reg.getId(), name, reg.getUser().getUsername(),
                        null, null, null, null));
            }
        }

        withResults.sort(Comparator.comparingDouble(BiathlonAthleteResultDto::finalTime));

        List<BiathlonAthleteResultDto> all = new ArrayList<>();
        for (int i = 0; i < withResults.size(); i++) {
            var e = withResults.get(i);
            all.add(new BiathlonAthleteResultDto(
                    e.registrationId(), e.athleteName(), e.username(),
                    e.raceTimeSeconds(), e.numberOfMisses(), e.finalTime(), i + 1));
        }
        all.addAll(withoutResults);

        return new BiathlonResultsDto(biathlonId, biathlon.getName(),
                biathlon.getOlympicGame().getName(), biathlon.getPenaltyTimeSeconds(), all);
    }

    @Transactional
    public void saveBiathlonResults(Long biathlonId, List<BiathlonResultEntry> entries) {
        for (BiathlonResultEntry entry : entries) {
            BiathlonResult result = biathlonResultRepository
                    .findByRegistrationId(entry.registrationId())
                    .orElseGet(() -> {
                        CompetitionRegistration reg = registrationRepository
                                .findById(entry.registrationId())
                                .orElseThrow(() -> new RuntimeException(
                                        "Registration not found: " + entry.registrationId()));
                        BiathlonResult r = new BiathlonResult();
                        r.setRegistration(reg);
                        return r;
                    });
            result.setRaceTimeSeconds(entry.raceTimeSeconds());
            result.setNumberOfMisses(entry.numberOfMisses());
            biathlonResultRepository.save(result);
        }
    }
}
