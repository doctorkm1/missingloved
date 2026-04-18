package com.alkamel.missingloved.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Found {
    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String ReporterCode;

    @NotBlank(message = "الاسم الأول مطلوب")
    @Size(min = 2, message = "الاسم الأول يجب ألا يقل عن حرفين")
    private String firstName;

    @NotBlank(message = "الاسم الثاني مطلوب")
    @Size(min = 2, message = "الاسم الثاني يجب ألا يقل عن حرفين")
    private String secondName;

    @NotBlank(message = "الاسم الثالث مطلوب")
    @Size(min = 2, message = "الاسم الثالث يجب ألا يقل عن حرفين")
    private String familyName;

    @NotBlank(message = "نوع الاسم مطلوب")
    private String nameType;

    @NotBlank(message = "الجنس مطلوب")
    private String gender;

    @Pattern(regexp = "^$|\\d{14}", message = "إذا تم إدخاله، يجب أن يتكون الرقم القومي من 14 رقمًا")
    private String nationalId;

    @NotBlank(message = "الدولة مطلوبة")
    private String country;

    @NotBlank(message = "المحافظة مطلوبة")
    private String governorate;

    @NotBlank(message = "المدينة مطلوبة")
    private String city;

    @NotBlank(message = "العنوان مطلوب")
    private String address;

    @NotNull(message = "تاريخ العثور مطلوب")
    private LocalDate foundDate;

    @NotNull(message = "السن التقريبي مطلوب")
    private Integer approxAge;

    @NotBlank(message = "الحالة الصحية مطلوبة")
    private String healthStatus;

    // Document metadata
    private String documentType;
    private String documentNumber;
    private String documentIssuedBy;
    private LocalDate documentDate;

    // File paths
    private String photoUrl;
    private String photoFileName;

    private String officialDocPath;


    // System metadata
    private String caseCode;
    private String createdIp;

    private boolean activated;
    private String validatorCode;
    private LocalDateTime activatedAt;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "activated_by")
    private User activatedBy;

    // ✅ Admin-only deactivation fields
    @ManyToOne
    @JoinColumn(name = "deactivated_by")
    private User deactivatedBy;

    private LocalDateTime deactivatedAt;
    private String activatedIp;
    private String deactivatedIp;

    @Column(name = "district", nullable = true, length = 255)
    @Pattern(regexp = ".*", message = "")
    private String district;

    // --- Host location and host person fields ---
    private String hCountry;
    private String hGovernorate;
    private String hCity;
    private String hDistrict;
    private String hAddress;
    private String hHostName;
    private String hMobile;
    private String hLandline;
    private LocalDate photoDate;

    // Existing getters/setters
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }

    // --- Getters and Setters for Host Fields ---
    public String gethCountry() {
        return hCountry;
    }
    public void sethCountry(String hCountry) {
        this.hCountry = hCountry;
    }

    public String gethGovernorate() {
        return hGovernorate;
    }
    public void sethGovernorate(String hGovernorate) {
        this.hGovernorate = hGovernorate;
    }

    public String gethCity() {
        return hCity;
    }
    public void sethCity(String hCity) {
        this.hCity = hCity;
    }

    public String gethDistrict() {
        return hDistrict;
    }
    public void sethDistrict(String hDistrict) {
        this.hDistrict = hDistrict;
    }

    public String gethAddress() {
        return hAddress;
    }
    public void sethAddress(String hAddress) {
        this.hAddress = hAddress;
    }

    public String gethHostName() {
        return hHostName;
    }
    public void sethHostName(String hHostName) {
        this.hHostName = hHostName;
    }

    public String gethMobile() {
        return hMobile;
    }
    public void sethMobile(String hMobile) {
        this.hMobile = hMobile;
    }

    public String gethLandline() {
        return hLandline;
    }
    public void sethLandline(String hLandline) {
        this.hLandline = hLandline;
    }
}
