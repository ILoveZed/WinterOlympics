package org.informatics.winterolympics.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "biathlon_results")
public class BiathlonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CompetitionRegistration registration;

    @Column(nullable = false)
    private double raceTimeSeconds;

    @Column(nullable = false)
    private int numberOfMisses;

    public BiathlonResult() {}

    public Long getId() { return id; }

    public CompetitionRegistration getRegistration() { return registration; }
    public void setRegistration(CompetitionRegistration registration) { this.registration = registration; }

    public double getRaceTimeSeconds() { return raceTimeSeconds; }
    public void setRaceTimeSeconds(double raceTimeSeconds) { this.raceTimeSeconds = raceTimeSeconds; }

    public int getNumberOfMisses() { return numberOfMisses; }
    public void setNumberOfMisses(int numberOfMisses) { this.numberOfMisses = numberOfMisses; }
}
