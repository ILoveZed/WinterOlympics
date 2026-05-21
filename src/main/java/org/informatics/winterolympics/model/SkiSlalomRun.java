package org.informatics.winterolympics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ski_slalom_runs")
public class SkiSlalomRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ski_slalom_id")
    private SkiSlalom skiSlalom;

    private int numberOfAthletes;


}
