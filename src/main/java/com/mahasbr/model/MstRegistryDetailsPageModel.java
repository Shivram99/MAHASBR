package com.mahasbr.model;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.mahasbr.entity.Auditable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MstRegistryDetailsPageModel extends Auditable  {
	
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

	private String nic2008ActivityCodeDesicripton;

	private Integer yearOfStartOfOperation;

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

	private String dateOfRegistration;

	private String dateOfDeregistrationExpiry;

	private String gstNumber;
	
	private String hsnCode;

	private String recordStatus;

	private String brnNo;
	
	private Long regUserId;
}
