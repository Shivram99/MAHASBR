package com.mahasbr.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactoryDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int srNo;
    private String nameOfEstablishmentOrOwner;
    private String streetName;
    private String locality;
    private int pinCode;
    private String telephoneMobNumber;
    private String emailAddress;
    private String headOfficeStreetName;
    private String headOfficeLocality;
    private int headOfficePinCode;
    private String headOfficeTelephoneMobNumber;
    private String headOfficeEmailAddress;
    private String yearOfStartOfOperation;
    private int totalNumberOfPersonsWorking;
    private String townVillage;
    private String taluka;
    private int district;
    private String addrLandmark;
    private String nameOfAuthority;
    private String approvalDate;
    private String dateOfDeregistrationExpiry;
}
