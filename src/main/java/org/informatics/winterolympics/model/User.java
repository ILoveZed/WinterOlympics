package org.informatics.winterolympics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false, unique = true)
    private String keycloakUserId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "athlete_id")
    private Athlete athlete;

    public User(){

    }

    public User(String username, String keycloakUserId, Athlete athlete) {
        this.username = username;
        this.keycloakUserId = keycloakUserId;
        this.athlete = athlete;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public String getKeycloakUserId() {
        return keycloakUserId;
    }
}
