// ========== Recorder.java ==========
package com.alkamel.missingloved.model;

import jakarta.persistence.*;

@Entity
public class Recorder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String phone;
    private String email;
    private String address;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private RecorderCategory category;

    // Getters and Setters
}
