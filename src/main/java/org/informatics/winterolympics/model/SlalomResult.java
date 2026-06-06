package org.informatics.winterolympics.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "slalom_results")
public class SlalomResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CompetitionRegistration registration;

    @Column(nullable = false)
    private double run1TimeSeconds;

    private Double run2TimeSeconds;

    public SlalomResult() {}

    public Long getId() { return id; }

    public CompetitionRegistration getRegistration() { return registration; }
    public void setRegistration(CompetitionRegistration registration) { this.registration = registration; }

    public double getRun1TimeSeconds() { return run1TimeSeconds; }
    public void setRun1TimeSeconds(double run1TimeSeconds) { this.run1TimeSeconds = run1TimeSeconds; }

    public Double getRun2TimeSeconds() { return run2TimeSeconds; }
    public void setRun2TimeSeconds(Double run2TimeSeconds) { this.run2TimeSeconds = run2TimeSeconds; }

    public Double getCombinedTime() {
        return run2TimeSeconds != null ? run1TimeSeconds + run2TimeSeconds : null;
    }
}
