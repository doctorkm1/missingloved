// ========== UserLog.java ==========
package com.alkamel.missingloved.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String actionType;
    private String description;
    private String ipAddress;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Getters and Setters
}
