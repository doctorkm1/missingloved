package com.alkamel.missingloved.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "اسم المستخدم مطلوب")
    @Size(min = 4, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String userCode;

    @NotBlank(message = "كلمة السر مطلوبة")
    @Size(min = 6, max = 100)
    @Column(nullable = false, length = 100)
    private String password;

    @NotBlank(message = "الاسم الأول مطلوب")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "الاسم الثانى مطلوب")
    @Size(max = 50)
    private String secondName;

    @NotBlank(message = "الاسم الثالث مطلوب")
    @Size(max = 50)
    private String thirdName;

    @NotBlank(message = "الاسم الأخير مطلوب")
    @Size(max = 50)
    private String familyName;

    @NotBlank(message = "الجنس مطلوب")
    private String sex;

    @NotBlank(message = "الجنسية مطلوبة")
    private String nationality;

    @NotBlank(message = "الرقم القومي مطلوب")
    @Size(min = 14, max = 14, message = "يجب أن يكون الرقم القومي 14 رقماً")
    private String nationalId;

    @NotNull(message = "تاريخ الميلاد مطلوب")
    private String birthDate;

    @NotNull(message = "الرقم الأرضى مطلوب")
    private String landline;

    @NotBlank(message = "رقم الموبايل مطلوب")
    private String mobile;

    @NotBlank(message = "الدولة مطلوبة")
    private String country;

    @NotBlank(message = "المحافظة مطلوبة")
    private String government;

    @NotBlank(message = "المدينة مطلوبة")
    private String city;

    @NotBlank(message = "العنوان مطلوب")
    private String address;

    @NotBlank(message = "البريد الإلكتروني مطلوب")
    @Email(message = "البريد الإلكتروني غير صالح")
    private String email;

    private Integer authorityCode = 0;

    // ✅ NEW FIELD for login control
    private boolean activated;

    // ✅ Audit fields
    private String createdBy;
    private LocalDateTime createdAt;
    private String createdIp;

    private String activatedBy;
    private LocalDateTime activatedAt;
    private String activationIp;

    private String deactivatedBy;
    private LocalDateTime deactivatedAt;
    private String deactivationIp;

    // Getters and Setters


    public Long getId() { return id; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSecondName() { return secondName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }

    public String getThirdName() {
        return thirdName; }
    public void setthirdName(String thirdName) { this.thirdName = thirdName; }


    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

/*   public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
*/
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getLandline() { return landline; }
    public void setLandline(String landline) { this.landline = landline; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getGovernment() { return government; }
    public void setGovernment(String government) { this.government = government; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAuthorityCode() { return authorityCode; }
    public void setAuthorityCode(Integer authorityCode) { this.authorityCode = authorityCode; }

    public boolean isActivated() { return activated; }
    public void setActivated(boolean activated) { this.activated = activated; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

/*    public String getcreatedIp() { return createdIp; }  */
    public void setcreatedIp(String createdIp) { this.createdIp = createdIp; }

    public String getActivatedBy() { return activatedBy; }
    public void setActivatedBy(String activatedBy) { this.activatedBy = activatedBy; }

    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }

    public String getActivationIp() { return activationIp; }
    public void setActivationIp(String activationIp) { this.activationIp = activationIp; }

    public String getDeactivatedBy() { return deactivatedBy; }
    public void setDeactivatedBy(String deactivatedBy) { this.deactivatedBy = deactivatedBy; }

    public LocalDateTime getDeactivatedAt() { return deactivatedAt; }
    public void setDeactivatedAt(LocalDateTime deactivatedAt) { this.deactivatedAt = deactivatedAt; }

    public String getDeactivationIp() { return deactivationIp; }
    public void setDeactivationIp(String deactivationIp) { this.deactivationIp = deactivationIp; }

    public String getCreatedIp() {
        return createdIp;
    }
    public void setCreatedIp(String createdIp) {
        this.createdIp = createdIp;
    }
}

