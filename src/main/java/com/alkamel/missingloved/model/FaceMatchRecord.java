package com.alkamel.missingloved.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;
import jakarta.persistence.PrePersist;


@Entity
@Table(name = "face_match_record")

public class FaceMatchRecord {
    public FaceMatchRecord() {
        this.status = "Waiting";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ✅ Updated to ignore if missing record is deleted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missing_id", referencedColumnName = "id", nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private Missing missing;

    // ✅ Updated to ignore if found record is deleted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "found_id", referencedColumnName = "id", nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private Found found;

    private double similarity;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    private String status;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    private String reviewedIp;
    @PrePersist
    public void assignDefaultStatusIfUnreviewed() {
        if ((this.reviewedBy == null || this.reviewedBy.isBlank()) &&
                this.reviewedAt == null &&
                (this.reviewedIp == null || this.reviewedIp.isBlank())) {
            this.status = "Waiting";
        }
    }


    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Missing getMissing() {
        return missing;
    }

    public void setMissing(Missing missing) {
        this.missing = missing;
    }

    public Found getFound() {
        return found;
    }

    public void setFound(Found found) {
        this.found = found;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewedIp() {
        return reviewedIp;
    }

    public void setReviewedIp(String reviewedIp) {
        this.reviewedIp = reviewedIp;
    }

}

