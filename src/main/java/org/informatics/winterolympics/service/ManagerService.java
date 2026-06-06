package org.informatics.winterolympics.service;

import org.informatics.winterolympics.dto.*;
import org.informatics.winterolympics.model.*;
import org.informatics.winterolympics.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;

@Service
public class ManagerService {

    private final OlympicGameRepository gameRepository;
    private final SkiSlalomRepository slalomRepository;
    private final SkiSlalomRunRepository runRepository;
    private final BiathlonRepository biathlonRepository;

    public ManagerService(OlympicGameRepository gameRepository,
                          SkiSlalomRepository slalomRepository,
                          SkiSlalomRunRepository runRepository,
                          BiathlonRepository biathlonRepository) {
        this.gameRepository = gameRepository;
        this.slalomRepository = slalomRepository;
        this.runRepository = runRepository;
        this.biathlonRepository = biathlonRepository;
    }

    public GameDto createGame(CreateGameRequest req) {
        OlympicGame game = gameRepository.save(
                new OlympicGame(req.name(), req.country(), req.city(), req.year()));
        return toDto(game);
    }

    @Transactional(readOnly = true)
    public List<GameDto> getAllGames() {
        return gameRepository.findAll().stream().map(this::toDto).toList();
    }

    public SkiSlalomDto addSlalom(Long gameId, CreateSkiSlalomRequest req) {
        if (req.run2Competitors() >= req.run1Competitors()) {
            throw new IllegalArgumentException(
                    "Run 2 competitors (" + req.run2Competitors() + ") must be less than Run 1 competitors (" + req.run1Competitors() + ")");
        }
        OlympicGame game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found: " + gameId));

        SkiSlalom slalom = new SkiSlalom(game, req.name(), req.sex(), req.minAge());
        if (req.timeOfEvent() != null && !req.timeOfEvent().isBlank()) {
            slalom.setTimeOfEvent(LocalDateTime.parse(req.timeOfEvent()));
        }
        slalomRepository.save(slalom);

        runRepository.save(new SkiSlalomRun(slalom, 1, req.run1Competitors()));
        runRepository.save(new SkiSlalomRun(slalom, 2, req.run2Competitors()));

        return new SkiSlalomDto(slalom.getId(), slalom.getName(), slalom.getSex(),
                slalom.getMinimalAge(), req.run1Competitors(), req.run2Competitors(),
                formatDateTime(slalom.getTimeOfEvent()));
    }

    public BiathlonDto addBiathlon(Long gameId, CreateBiathlonRequest req) {
        OlympicGame game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found: " + gameId));

        Biathlon biathlon = new Biathlon(game, req.name(), req.sex(), req.minAge(),
                req.numCompetitors(), req.numLaps(), req.numShootings(),
                req.lapsBetweenShootings(), req.penaltyTimeSeconds());
        if (req.timeOfEvent() != null && !req.timeOfEvent().isBlank()) {
            biathlon.setTimeOfEvent(LocalDateTime.parse(req.timeOfEvent()));
        }
        biathlonRepository.save(biathlon);

        return toDto(biathlon);
    }

    public void deleteGame(Long gameId) {
        gameRepository.deleteById(gameId);
    }

    public void deleteSlalom(Long slalomId) {
        slalomRepository.deleteById(slalomId);
    }

    public void deleteBiathlon(Long biathlonId) {
        biathlonRepository.deleteById(biathlonId);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private GameDto toDto(OlympicGame game) {
        List<SkiSlalomDto> slaloms = game.getSkiSlaloms().stream()
                .map(this::toSlalomDto).toList();
        List<BiathlonDto> biathlons = game.getBiathlons().stream()
                .map(this::toDto).toList();
        return new GameDto(game.getId(), game.getName(), game.getCountry(),
                game.getCity(), game.getYear(), slaloms, biathlons);
    }

    private SkiSlalomDto toSlalomDto(SkiSlalom s) {
        int run1 = s.getRuns().stream().filter(r -> r.getRunNumber() == 1)
                .mapToInt(SkiSlalomRun::getNumberOfAthletes).findFirst().orElse(0);
        int run2 = s.getRuns().stream().filter(r -> r.getRunNumber() == 2)
                .mapToInt(SkiSlalomRun::getNumberOfAthletes).findFirst().orElse(0);
        return new SkiSlalomDto(s.getId(), s.getName(), s.getSex(), s.getMinimalAge(), run1, run2,
                formatDateTime(s.getTimeOfEvent()));
    }

    private BiathlonDto toDto(Biathlon b) {
        return new BiathlonDto(b.getId(), b.getName(), b.getSex(), b.getMinimalAge(),
                b.getNumberOfAthletes(), b.getNumberOfLaps(), b.getNumberOfShootings(),
                b.getNumberOfLapsBetweenShootings(), b.getPenaltyTimeSeconds(),
                formatDateTime(b.getTimeOfEvent()));
    }

    private String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.toString() : null;
    }
}
