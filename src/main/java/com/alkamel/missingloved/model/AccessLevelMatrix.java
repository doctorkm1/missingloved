// ========== AccessLevelMatrix.java ==========
package com.alkamel.missingloved.model;

import jakarta.persistence.*;

@Entity
public class AccessLevelMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "view_id")
    private View view;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private Boolean allowInsert;
    private Boolean allowUpdate;
    private Boolean allowDelete;
    private Boolean allowView;

    // Getters and Setters
}

