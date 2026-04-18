// Sightmis.java (corrected mapping)
package com.alkamel.missingloved.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Sightmis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_code")
    private String missingcode;

    @Column(name = "sightmiscode")
    private String sightmiscode;

    private String mseencountry;
    private String mseengov;
    private String mseencity;
    private String mseendistrict;
    private String mseenaddress;
    private LocalDate mseendate;
    private String mseentime;
    private LocalDate reportdate;
    private Boolean msightrepagree;

    private String mseenreportedby;
    private String mseencreatedby;
    private String mseencreatedip;
    private LocalDateTime mseencreatedat;

    private String mseenactivatedby;
    private String mseenactivatedip;
    private LocalDateTime mseenactivatedat;

    private String mseendeactivatedby;
    private String mseendeactivatedip;
    private LocalDateTime mseendeactivatedat;


    @Column(name = "mseenactivationcode")
    private String mseenactivationcode;

    public void setMseenactivationcode(String mseenactivationcode) {
        this.mseenactivationcode = mseenactivationcode;
    }

    public void setMseencountry(String mseencountry) {
        this.mseencountry = mseencountry;
    }


    public String getMseenactivationcode() {
        return this.mseenactivationcode;
    }
}

