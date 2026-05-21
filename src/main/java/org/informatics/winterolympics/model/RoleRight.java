package org.informatics.winterolympics.model;

import jakarta.persistence.*;

@Entity
public class RoleRight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "right_id")
    private Right right;
}
