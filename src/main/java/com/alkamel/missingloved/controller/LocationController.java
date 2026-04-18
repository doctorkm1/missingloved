package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.LocationCodes;
import com.alkamel.missingloved.repository.LocationCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationCodesRepository repo;

    // Fetch all governorates (LOCAL only, top-level)
    @GetMapping("/governments")
    public List<LocationCodes> getGovernments() {
        return repo.findByPolAndZon1AndZon2AndType("00", "00", "00", "LOCAL");
    }

    // Fetch cities (exclude governorate, only rows with Zon1/Zon2 = "00")
    @GetMapping("/cities/{govCode}")
    public List<LocationCodes> getCities(@PathVariable String govCode) {
        return repo.findByGovAndPolNotAndZon1AndZon2AndType(
                govCode, "00", "00", "00", "LOCAL"
        );
    }

    // Fetch Zon1 (districts) for a given city (Zon1 != 00, Zon2 = 00)
// Get Zon1 districts for a selected city
    @GetMapping("/zon1/{cityCode}")
    public List<LocationCodes> getZon1(@PathVariable String cityCode) {
        // Split the city code into parts (gov, pol) to pass correctly
        String gov = cityCode.substring(0, 2);
        String pol = cityCode.substring(2, 4);
        return repo.findByGovAndPolAndZon1NotAndZon2AndType(
                gov, pol, "00", "00", "LOCAL"
        );
    }
}
