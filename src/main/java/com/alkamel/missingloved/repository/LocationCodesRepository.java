package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.LocationCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LocationCodesRepository extends JpaRepository<LocationCodes, String> {

    // Governorates (Pol/Zon1/Zon2 = 00, type LOCAL)
    List<LocationCodes> findByPolAndZon1AndZon2AndType(String pol, String zon1, String zon2, String type);

    // Cities (Pol != "00", Zon1 = "00", Zon2 = "00", type LOCAL)
    List<LocationCodes> findByGovAndPolNotAndZon1AndZon2AndType(String gov, String pol, String zon1, String zon2, String type);

    // Zon1 (districts) (Gov + Pol, Zon1 != "00", Zon2 = "00", type LOCAL)
    List<LocationCodes> findByGovAndPolAndZon1NotAndZon2AndType(String gov, String pol, String zon1, String zon2, String type);

}
