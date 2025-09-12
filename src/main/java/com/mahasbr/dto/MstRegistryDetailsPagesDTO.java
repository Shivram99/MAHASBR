package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MstRegistryDetailsPagesDTO {

    private Long siNo;
    private String nameOfEstablishmentOrOwner;
    private String houseNo;
    private String streetName;
    private String locality;
    private Integer pinCode;
    private Long telephoneMobNumber;
    private String emailAddress;
    private String panNumber;
    private String tanNumber;
    private String headOfficeHouseNo;
    private String headOfficeStreetName;
    private String headOfficeLocality;
    private Integer headOfficePinCode;
    private Long headOfficeTelephoneMobNumber;
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
    private String locationCode;
    private String registrationStatus;
    private String townVillage;
    private String taluka;
    private String district;
    private String sector;
    private String wardNumber;
    private String nameOfAuthority;
    private String nameOfAct;

    // Always return dates as String formatted properly
    private String dateOfRegistration;
    private String dateOfDeregistrationExpiry;

    private String gstNumber;
    private String hsnCode;
    private String recordStatus;
    private String brnNo;
    private Long regUserId;
}

