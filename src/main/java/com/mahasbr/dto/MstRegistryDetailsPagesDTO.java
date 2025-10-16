package com.mahasbr.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MstRegistryDetailsPagesDTO {
	private String srNo;
    private String nameOfEstablishmentOrOwner;
    private String houseNo;
    private String streetName;
    private String locality;
    private Integer pinCode;
    private Long telephoneMobNumber;

    @Email
    private String emailAddress;

    private String panNumber;
    private String tanNumber;
    private String headOfficeHouseNo;
    private String headOfficeStreetName;
    private String headOfficeLocality;
    private Integer headOfficePinCode;
    private Long headOfficeTelephoneMobNumber;

    @Email
    private String headOfficeEmailAddress;

    private String headOfficePanNumber;
    private String headOfficeTanNumber;
    private String descriptionOfMajorActivity;
    private String nic2008ActivityCode;
    private String nic2008ActivityCodeDescription;
    private String yearOfStartOfOperation;
    private String ownershipCode;
    private Integer totalNumberOfPersonsWorking;
    private String actAuthorityRegistrationNumbers;
    private String remarks;

    private String locationCode; // optional, can be generated if missing
    private String registrationStatus;
    private String townVillage;
    private String taluka;
    private String district;
    private String sector;
    private String wardNumber;
    private String nameOfAuthority;
    private String nameOfAct;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfRegistration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfDeregistrationExpiry;

    private String gstNumber;
    private String hsnCode;
    private String recordStatus;

    private String brnNo; // optional, can be generated internally
}
