package com.example.clothingstore.controller;

import com.example.clothingstore.config.mapper.LocationMapper;
import com.example.clothingstore.dto.ErrorResponse;
import com.example.clothingstore.model.CommuneEntity;
import com.example.clothingstore.model.DistrictEntity;
import com.example.clothingstore.model.ProvinceEntity;
import com.example.clothingstore.service.CommuneService;
import com.example.clothingstore.service.DistrictService;
import com.example.clothingstore.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LocationController {
    @Autowired
    ProvinceService provinceService;

    @Autowired
    DistrictService districtService;

    @Autowired
    CommuneService communeService;

    @GetMapping("/location/provinces")
    public Object getAllProvince()
    {
        List<ProvinceEntity> provinceEntities = provinceService.getAllProvince();

        List<LocationMapper> provinceMappers = new ArrayList<>();
        for (ProvinceEntity provinceEntity : provinceEntities) {
            LocationMapper locationMapper = new LocationMapper(provinceEntity.getId(), provinceEntity.getProvince());
            provinceMappers.add(locationMapper);
        }
        return ResponseEntity.ok(provinceMappers);
    }

    @GetMapping("/location/districts/{province_id}")
    public Object getDistrictByProvinceId(@PathVariable long province_id)
    {
        if (!provinceService.existById(province_id)) {
            return new ResponseEntity<>(new ErrorResponse("No districts found for province with id " + province_id),
                    HttpStatus.NOT_FOUND);
        }

        List<DistrictEntity> districtEntities = districtService.getDistrictByProvinceId(province_id);

        if (districtEntities.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("No districts found for province with id " + province_id),
                    HttpStatus.NOT_FOUND);

        }

        List<LocationMapper> districtMappers = new ArrayList<>();

        for (DistrictEntity districtEntity : districtEntities) {
            LocationMapper districtMapper = new LocationMapper(districtEntity.getId(), districtEntity.getDistrict());
            districtMappers.add(districtMapper);
        }

        return ResponseEntity.ok(districtMappers);
    }

    @GetMapping("/location/communes/{district_id}")
    public Object getCommuneByDistrictId(@PathVariable long district_id)
    {
        if (!districtService.existById(district_id)) {
            return new ResponseEntity<>(new ErrorResponse("No communes found for district with id " + district_id),
                    HttpStatus.NOT_FOUND);
        }

        List<CommuneEntity> communeEntities = communeService.getCommuneByDistrictId(district_id);

        if (communeEntities.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("No communes found for district with id " + district_id),
                    HttpStatus.NOT_FOUND);
        }

        List<LocationMapper> communeMappers = new ArrayList<>();

        for (CommuneEntity communeEntity : communeEntities) {
            LocationMapper communeMapper = new LocationMapper(communeEntity.getId(), communeEntity.getCommune());
            communeMappers.add(communeMapper);
        }

        return ResponseEntity.ok(communeMappers);
    }
}
