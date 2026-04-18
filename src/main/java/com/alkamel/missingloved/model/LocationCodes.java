package com.alkamel.missingloved.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "location_codes")
public class LocationCodes {

    @Id
    @Column(name = "code", length = 8, nullable = false)
    private String code;

    @Column(name = "gov", length = 2)
    private String gov;

    @Column(name = "pol", length = 2)
    private String pol;

    @Column(name = "zon1", length = 2)
    private String zon1;

    @Column(name = "zon2", length = 2)
    private String zon2;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "type", columnDefinition = "enum('LOCAL','INTERNATIONAL')")
    private String type;

    // --- Getters and Setters ---
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getGov() { return gov; }
    public void setGov(String gov) { this.gov = gov; }

    public String getPol() { return pol; }
    public void setPol(String pol) { this.pol = pol; }

    public String getZon1() { return zon1; }
    public void setZon1(String zon1) { this.zon1 = zon1; }

    public String getZon2() { return zon2; }
    public void setZon2(String zon2) { this.zon2 = zon2; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
