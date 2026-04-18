// ========== View.java ==========
package com.alkamel.missingloved.model;

import jakarta.persistence.*;

@Entity
public class View {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    // Getters and Setters
}
