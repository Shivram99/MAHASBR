package com.mahasbr.mapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;

@Component
public class RegistryMapper {

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    public MstRegistryDetailsPageEntity toEntity(Map<String, String> row) {
        MstRegistryDetailsPageEntity e = new MstRegistryDetailsPageEntity();

        // Strings directly
        e.setNameOfEstablishmentOrOwner(s(row, "nameOfEstablishmentOrOwner"));
        e.setHouseNo(s(row, "houseNo"));
        e.setStreetName(s(row, "streetName"));
        e.setLocality(s(row, "locality"));
        e.setTelephoneMobNumber(s(row, "telephoneMobNumber"));
        e.setEmailAddress(s(row, "emailAddress"));
        e.setPanNumber(s(row, "PANNumber"));
        e.setTanNumber(s(row, "TANNumber"));
        e.setHeadOfficeHouseNo(s(row, "headOfficeHouseNo"));
        e.setHeadOfficeStreetName(s(row, "headOfficeStreetName"));
        e.setHeadOfficeLocality(s(row, "headOfficeLocality"));
        e.setHeadOfficeTelephoneMobNumber(s(row, "headOfficeTelephoneMobNumber"));
        e.setHeadOfficeEmailAddress(s(row, "headOfficeEmailAddress"));
        e.setHeadOfficePanNumber(s(row, "headOfficePANNumber"));
        e.setHeadOfficeTanNumber(s(row, "headOfficeTANNumber"));
        e.setDescriptionOfMajorActivity(s(row, "descriptionOfMajorActivity"));
        e.setNic2008ActivityCode(s(row, "nic2008ActivityCode"));
        e.setYearOfStartOfOperation(s(row, "yearOfStartOfOperation"));
        e.setOwnershipCode(s(row, "ownershipCode"));
        e.setActAuthorityRegistrationNumbers(s(row, "actAuthorityRegistrationNumbers"));
        e.setRemarks(s(row, "remarks"));
        e.setLocationCode(s(row, "locationCode"));
        e.setRegistrationStatus(s(row, "registrationStatus"));
        e.setTownVillage(s(row, "townVillage"));
        e.setTaluka(s(row, "taluka"));
        e.setDistrict(s(row, "district"));
        e.setSector(s(row, "sector"));
        e.setWardNumber(s(row, "wardNumber"));
        e.setNameOfAuthority(s(row, "nameOfAuthority"));
        e.setNameOfAct(s(row, "nameOfAct"));
        e.setGstNumber(s(row, "gstNumber"));
        e.setHsnCode(s(row, "hsnCode"));
        e.setRecordStatus(s(row, "recordStatus"));

        // Numeric conversions
        Integer pin = parseInteger(s(row, "pinCode"));
        e.setPinCode(pin);

        Integer headPin = parseInteger(s(row, "headOfficePinCode"));
        e.setHeadOfficePinCode(headPin);

        Integer totalPersons = parseInteger(s(row, "totalNumberOfPersonsWorking"));
        e.setTotalNumberOfPersonsWorking(totalPersons);

        Integer regUserId = parseInteger(s(row, "regUserId"));
        e.setRegUserId(regUserId);

        // Dates
        LocalDate regDate = parseDate(s(row, "dateOfRegistration"));
        e.setDateOfRegistration(regDate);

        LocalDate dereg = parseDate(s(row, "dateOfDeregistrationExpiry"));
        e.setDateOfDeregistrationExpiry(dereg);

        // brnNo not present in excel — either set null or generate elsewhere
        e.setBrnNo(s(row, "brnNo"));

        return e;
    }

    private String s(Map<String,String> row, String key) {
        return row.getOrDefault(key, "").trim();
    }

    private Integer parseInteger(String v) {
        if (v == null || v.isBlank()) return null;
        try {
            // Some Excel numeric values come as decimal string like "123.0"
            if (v.contains(".")) v = v.substring(0, v.indexOf('.'));
            return Integer.valueOf(v);
        } catch (Exception ex) {
            return null;
        }
    }

    private LocalDate parseDate(String v) {
        if (v == null || v.isBlank()) return null;
        for (DateTimeFormatter f : DATE_FORMATS) {
            try {
                return LocalDate.parse(v, f);
            } catch (Exception ignored) {}
        }
        // Try ISO fallback
        try {
            return LocalDate.parse(v);
        } catch (Exception ignored) {}
        return null;
    }
}
