package com.alkamel.missingloved.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import com.alkamel.missingloved.model.User;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "missing")
public class Missing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String reporterCode;
    @NotBlank
    private String firstName;

    @NotBlank
    private String secondName;

    @NotBlank
    private String familyName;

    @NotBlank
    private String gender;

    @NotBlank
    private String nationality;

    @NotBlank
    @Pattern(regexp = "\\d{14}")
    private String nationalId;

    public User getActivatedBy() { return activatedBy; }
    public void setActivatedBy(User activatedBy) { this.activatedBy = activatedBy; }

    public User getDeactivatedBy() { return deactivatedBy; }
    public void setDeactivatedBy(User deactivatedBy) { this.deactivatedBy = deactivatedBy; }


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate dob;

    @NotNull
    private Integer age;

    @NotBlank
    private String healthStatus;

    @NotBlank
    private String country;

    @NotBlank
    private String government;

    @NotBlank
    private String city;

    @NotBlank
    private String address;

    @NotBlank
    private String landline;

    @NotBlank
    private String reporterRelation;

    @NotBlank
    private String mobile;

    private String email;
    private String phone;

    private String district;
    private String lastSeenDistrict;

    private String createdIp;
    private String activatedIp;
    private String deactivatedIp;

    // Getters & Setters
    public String getCreatedIp() { return createdIp; }
    public void setCreatedIp(String createdIp) { this.createdIp = createdIp; }

    public String getActivatedIp() { return activatedIp; }
    public void setActivatedIp(String activatedIp) { this.activatedIp = activatedIp; }

    public String getDeactivatedIp() { return deactivatedIp; }
    public void setDeactivatedIp(String deactivatedIp) { this.deactivatedIp = deactivatedIp; }

    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLastSeenDistrict() {
        return lastSeenDistrict;
    }
    public void setLastSeenDistrict(String lastSeenDistrict) {
        this.lastSeenDistrict = lastSeenDistrict;
    }

/*    private String lastSeenDistrict;
    public String getlastSeenDistrict() {
        return lastSeenDistrict;
    }public void setlastSeenDistrict(String lastSeenDistrict) {
        this.lastSeenDistrict = lastSeenDistrict;
    }
*/
    @NotBlank
    private String lastSeenCountry;

    @NotBlank
    private String lastSeenGovernorate;

    @NotBlank
    private String lastSeenCity;

    @NotBlank
    private String lastSeenLocation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate lastSeenDate;

    private String photoUrl;
    private String photoFileName;
    private String officialDocPath;
    private String documentType;
    private String documentNumber;
    private String documentIssuedBy;
    private LocalDate documentDate;
    private LocalDate photoDate;

    private String caseCode;
    private String createdBy; // ✅ Needed for tracking user
    private LocalDateTime createdAt;

    private boolean activated;
    private String validatorCode;
    private LocalDateTime activatedAt;
/*    private String activationIp; */

    // ✅ Add getter and setter for reporterCode
    public String getReporterCode() {
        return reporterCode;
    }

    public void setReporterCode(String reporterCode) {
        this.reporterCode = reporterCode;
    }

    @ManyToOne
    @JoinColumn(name = "activated_by", referencedColumnName = "id")
    private User activatedBy;

    @ManyToOne
    @JoinColumn(name = "deactivated_by", referencedColumnName = "id")
    private User deactivatedBy;

    private String ipAddress;

    private LocalDateTime deactivatedAt;

    // ✅ New transient fields for photo and document upload
    @Transient
    private MultipartFile photo;

    @Transient
    private MultipartFile officialDoc;


}
