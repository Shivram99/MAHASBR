package com.mahasbr.api.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrustListResponseDTO {

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
	private Integer nic2008ActivityCode;
	private String nic2008ActivityCodeDescription;
	private Integer ownershipCode;
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
	private String gstNumber;
	private String hsnCode;
	private String recordStatus;
	private String brnNo;
	private Long regUserId;

	@JsonFormat(pattern = "EEE MMM dd HH:mm:ss z yyyy", locale = "en")
	private ZonedDateTime createdDate;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime yearOfStartOfOperation;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime dateOfRegistration;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime dateOfDeregistrationExpiry;

}
