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

    private int numberOfAthletes;

    private int numberOfLaps;

    private int numberOfShootings;

    private int numberOfLapsBetweenShootings;

    private String sex;

    private int minimalAge;

    private LocalDateTime timeOfEvent;

    public Biathlon() {
    }

    public Biathlon(OlympicGame olympicGame,
                    int numberOfAthletes,
                    int numberOfLaps,
                    int numberOfShootings,
                    int numberOfLapsBetweenShootings,
                    String sex,
                    int minimalAge,
                    LocalDateTime timeOfEvent) {
        this.olympicGame = olympicGame;
        this.numberOfAthletes = numberOfAthletes;
        this.numberOfLaps = numberOfLaps;
        this.numberOfShootings = numberOfShootings;
        this.numberOfLapsBetweenShootings = numberOfLapsBetweenShootings;
        this.sex = sex;
        this.minimalAge = minimalAge;
        this.timeOfEvent = timeOfEvent;
    }

    public OlympicGame getOlympicGame() {
        return olympicGame;
    }

    public void setOlympicGame(OlympicGame olympicGame) {
        this.olympicGame = olympicGame;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberOfAthletes() {
        return numberOfAthletes;
    }

    public void setNumberOfAthletes(int numberOfAthletes) {
        this.numberOfAthletes = numberOfAthletes;
    }

    public int getNumberOfLaps() {
        return numberOfLaps;
    }

    public void setNumberOfLaps(int numberOfLaps) {
        this.numberOfLaps = numberOfLaps;
    }

    public int getNumberOfShootings() {
        return numberOfShootings;
    }

    public void setNumberOfShootings(int numberOfShootings) {
        this.numberOfShootings = numberOfShootings;
    }

    public int getNumberOfLapsBetweenShootings() {
        return numberOfLapsBetweenShootings;
    }

    public void setNumberOfLapsBetweenShootings(int numberOfLapsBetweenShootings) {
        this.numberOfLapsBetweenShootings = numberOfLapsBetweenShootings;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getMinimalAge() {
        return minimalAge;
    }

    public void setMinimalAge(int minimalAge) {
        this.minimalAge = minimalAge;
    }

    public LocalDateTime getTimeOfEvent() {
        return timeOfEvent;
    }

    public void setTimeOfEvent(LocalDateTime date) {
        this.timeOfEvent = date;
    }
}
