package org.informatics.winterolympics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "biathlons")
public class Biathlon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "olympic_game_id")
    private OlympicGame olympicGame;

    @Column(nullable = false)
    private String name;

    private int numberOfAthletes;

    private int numberOfLaps;

    private int numberOfShootings;

    private int numberOfLapsBetweenShootings;

    private String sex;

    private int minimalAge;

    @Column(nullable = false)
    private int penaltyTimeSeconds;

    private LocalDateTime timeOfEvent;

    public Biathlon() {}

    public Biathlon(OlympicGame olympicGame, String name, String sex, int minimalAge,
                    int numberOfAthletes, int numberOfLaps, int numberOfShootings,
                    int numberOfLapsBetweenShootings, int penaltyTimeSeconds) {
        this.olympicGame = olympicGame;
        this.name = name;
        this.sex = sex;
        this.minimalAge = minimalAge;
        this.numberOfAthletes = numberOfAthletes;
        this.numberOfLaps = numberOfLaps;
        this.numberOfShootings = numberOfShootings;
        this.numberOfLapsBetweenShootings = numberOfLapsBetweenShootings;
        this.penaltyTimeSeconds = penaltyTimeSeconds;
    }

    public Long getId() { return id; }

    public OlympicGame getOlympicGame() { return olympicGame; }
    public void setOlympicGame(OlympicGame olympicGame) { this.olympicGame = olympicGame; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getNumberOfAthletes() { return numberOfAthletes; }
    public void setNumberOfAthletes(int numberOfAthletes) { this.numberOfAthletes = numberOfAthletes; }

    public int getNumberOfLaps() { return numberOfLaps; }
    public void setNumberOfLaps(int numberOfLaps) { this.numberOfLaps = numberOfLaps; }

    public int getNumberOfShootings() { return numberOfShootings; }
    public void setNumberOfShootings(int numberOfShootings) { this.numberOfShootings = numberOfShootings; }

    public int getNumberOfLapsBetweenShootings() { return numberOfLapsBetweenShootings; }
    public void setNumberOfLapsBetweenShootings(int v) { this.numberOfLapsBetweenShootings = v; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public int getMinimalAge() { return minimalAge; }
    public void setMinimalAge(int minimalAge) { this.minimalAge = minimalAge; }

    public int getPenaltyTimeSeconds() { return penaltyTimeSeconds; }
    public void setPenaltyTimeSeconds(int penaltyTimeSeconds) { this.penaltyTimeSeconds = penaltyTimeSeconds; }

    public LocalDateTime getTimeOfEvent() { return timeOfEvent; }
    public void setTimeOfEvent(LocalDateTime timeOfEvent) { this.timeOfEvent = timeOfEvent; }
}
