package org.informatics.winterolympics.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "olympic_games")
public class OlympicGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String country;

    private String city;

    private String year;

    @OneToMany(mappedBy = "olympicGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkiSlalom> skiSlaloms = new ArrayList<>();

    @OneToMany(mappedBy = "olympicGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Biathlon> biathlons = new ArrayList<>();

    public OlympicGame() {}

    public OlympicGame(String name, String country, String city, String year) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.year = year;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public List<SkiSlalom> getSkiSlaloms() { return skiSlaloms; }
    public List<Biathlon> getBiathlons() { return biathlons; }
}
