package org.informatics.winterolympics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "olympic_games")
public class OlympicGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;

    private String city;

    private String year;
}
