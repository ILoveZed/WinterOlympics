package org.informatics.winterolympics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ski_slalom_runs")
public class SkiSlalomRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ski_slalom_id", nullable = false)
    private SkiSlalom skiSlalom;

    @Column(nullable = false)
    private int runNumber;

    @Column(nullable = false)
    private int numberOfAthletes;

    public SkiSlalomRun() {}

    public SkiSlalomRun(SkiSlalom skiSlalom, int runNumber, int numberOfAthletes) {
        this.skiSlalom = skiSlalom;
        this.runNumber = runNumber;
        this.numberOfAthletes = numberOfAthletes;
    }

    public Long getId() { return id; }

    public SkiSlalom getSkiSlalom() { return skiSlalom; }
    public void setSkiSlalom(SkiSlalom skiSlalom) { this.skiSlalom = skiSlalom; }

    public int getRunNumber() { return runNumber; }
    public void setRunNumber(int runNumber) { this.runNumber = runNumber; }

    public int getNumberOfAthletes() { return numberOfAthletes; }
    public void setNumberOfAthletes(int numberOfAthletes) { this.numberOfAthletes = numberOfAthletes; }
}
