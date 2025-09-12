package com.mahasbr.api.dto;

import lombok.Data;

@Data
public class ShopData {
	private int SrNo;
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
	private String remarks;
	private String locationCode;
	private int registrationStatus;
	private int townVillage;
	private int taluka;
	private int district;
	private String institutionAddress;
	private String nameOfAuthority;
	private String dateOfRegistration;
	private String dateOfDeregistrationExpiry;
}
