package org.informatics.winterolympics.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ski_slaloms")
public class SkiSlalom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "olympic_game_id")
    private OlympicGame olympicGame;

    private int numberOfAthletes;

    private String sex;

    private int minimalAge;

    private LocalDateTime timeOfEvent;

    public SkiSlalom() {
    }

    public SkiSlalom(OlympicGame olympicGame,
                     int numberOfAthletes,
                     int minimalAge,
                     String sex) {
        this.olympicGame = olympicGame;
        this.numberOfAthletes = numberOfAthletes;
        this.minimalAge = minimalAge;
        this.sex = sex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OlympicGame getOlympicGame() {
        return olympicGame;
    }

    public void setOlympicGame(OlympicGame olympicGame) {
        this.olympicGame = olympicGame;
    }

    public int getNumberOfAthletes() {
        return numberOfAthletes;
    }

    public void setNumberOfAthletes(int numberOfAthletes) {
        this.numberOfAthletes = numberOfAthletes;
    }

    public int getMinimalAge() {
        return minimalAge;
    }

    public void setMinimalAge(int minimalAge) {
        this.minimalAge = minimalAge;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
