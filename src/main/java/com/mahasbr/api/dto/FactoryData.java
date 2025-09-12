package com.mahasbr.api.dto;

import lombok.Data;

@Data
public class FactoryData {
	private int SrNo;
	private String nameOfEstablishmentOrOwner;
	private String streetName;
	private String locality;
	private int pinCode;
	private String telephoneMobNumber;
	private String emailAddress;
	private String headOfficeHouseNo;
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
