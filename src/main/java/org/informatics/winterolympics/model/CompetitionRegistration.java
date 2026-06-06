package org.informatics.winterolympics.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "competition_registrations")
public class CompetitionRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ski_slalom_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SkiSlalom skiSlalom;

    @ManyToOne
    @JoinColumn(name = "biathlon_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Biathlon biathlon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    public CompetitionRegistration() {}

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SkiSlalom getSkiSlalom() { return skiSlalom; }
    public void setSkiSlalom(SkiSlalom skiSlalom) { this.skiSlalom = skiSlalom; }

    public Biathlon getBiathlon() { return biathlon; }
    public void setBiathlon(Biathlon biathlon) { this.biathlon = biathlon; }

    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
