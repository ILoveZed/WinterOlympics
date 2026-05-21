package org.informatics.winterolympics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rights")
public class Right {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
