package com.alkamel.missingloved.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SightUsr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String secondName;
    private String thirddName;
    private String familyName;

    private String sex;
    private String nationality;
    private String nationalId;

    private LocalDate birthDate;

    private String landline;
    private String mobile;

    private String country;
    private String government;
    private String city;
    private String address;

    private String email;

    private String userCode;
    private String password;
}
