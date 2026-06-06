package org.informatics.winterolympics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ski_slaloms")
public class SkiSlalom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "olympic_game_id")
    private OlympicGame olympicGame;

    @Column(nullable = false)
    private String name;

    private String sex;

    private int minimalAge;

    private int numberOfAthletes = 0;

    private LocalDateTime timeOfEvent;

    @OneToMany(mappedBy = "skiSlalom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkiSlalomRun> runs = new ArrayList<>();

    public SkiSlalom() {}

    public SkiSlalom(OlympicGame olympicGame, String name, String sex, int minimalAge) {
        this.olympicGame = olympicGame;
        this.name = name;
        this.sex = sex;
        this.minimalAge = minimalAge;
    }

    public Long getId() { return id; }

    public OlympicGame getOlympicGame() { return olympicGame; }
    public void setOlympicGame(OlympicGame olympicGame) { this.olympicGame = olympicGame; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public int getMinimalAge() { return minimalAge; }
    public void setMinimalAge(int minimalAge) { this.minimalAge = minimalAge; }

    public LocalDateTime getTimeOfEvent() { return timeOfEvent; }
    public void setTimeOfEvent(LocalDateTime timeOfEvent) { this.timeOfEvent = timeOfEvent; }

    public List<SkiSlalomRun> getRuns() { return runs; }
}
