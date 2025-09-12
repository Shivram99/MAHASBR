package com.mahasbr.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopDataEntity {
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
    private String headOfficePinCode;
    private String headOfficeTelephoneMobNumber;
    private String headOfficeEmailAddress;
    private int totalNumberOfPersonsWorking;
    private String nameOfAuthority;
    private String dateOfRegistration;
    private String dateOfDeregistrationExpiry;
}
