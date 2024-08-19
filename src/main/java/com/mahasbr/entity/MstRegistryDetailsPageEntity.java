package com.mahasbr.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@Table(name = "mst_reg_details")
@ToString
public class MstRegistryDetailsPageEntity extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "siNo_generator")
	@SequenceGenerator(name = "siNo_generator", sequenceName = "siNo_sequence", allocationSize = 1)
	@Column(name = "siNo")
	private Long siNo;
		
	@Column(length = 100)
	private String nameOfEstablishmentOrOwner;

	@Column(length = 500)
	private String houseNo;

	@Column(length = 500)
	private String streetName;

	@Column(length = 500)
	private String locality;

	@Column(length = 6)
	private Integer pinCode;

	@Column(length = 10)
	private Long telephoneMobNumber;

	@Column(length = 320)
	private String emailAddress;

	@Column(length = 10)
	private String panNumber;

	@Column(length = 10)
	private String tanNumber;

	@Column(length = 500)
	private String headOfficeHouseNo;

	@Column(length = 500)
	private String headOfficeStreetName;

	@Column(length = 500)
	private String headOfficeLocality;

	@Column(length = 6)
	private Integer headOfficePinCode;

	@Column(length = 10)
	private Long headOfficeTelephoneMobNumber;

	@Column(length = 320)
	private String headOfficeEmailAddress;

	@Column(length = 10)
	private String headOfficePanNumber;

	@Column(length = 10)
	private String headOfficeTanNumber;

	@Column(length = 100)
	private String descriptionOfMajorActivity;

	@Column(length = 5)
	private Integer nic2008ActivityCode;

	@Column(length = 5)
	private String nic2008ActivityCodeDesicripton;

	@Column(length = 4)
	private Integer yearOfStartOfOperation;

	@Column(length = 1)
	private Integer ownershipCode;

	@Column(length = 7)
	private Integer totalNumberOfPersonsWorking;

	@Column(length = 100)
	private String actAuthorityRegistrationNumbers;

	@Column(length = 100)
	private String remarks;

	@Column(length = 16)
	private String locationCode;

	@Column(length = 1)
	private String registrationStatus;

	@Column(length = 100)
	private String townVillage;

	@Column(length = 100)
	private String taluka;

	@Column(length = 100)
	private String district;

	// (Rural / Urban)
	@Column(length = 5)
	private String sector;

	@Column(length = 10)
	private String wardNumber;

	@Column(length = 200)
	private String nameOfAuthority;

	@Column(length = 200)
	private String nameOfAct;

	@Column(length = 30)
	private LocalDate dateOfRegistration;

	@Column(length = 30)
	private LocalDate dateOfDeregistrationExpiry;

	@Column(length = 15)
	private String gstNumber;

	@Column(length = 10)
	private String hsnCode;

	private String recordStatus;

	@Column(name = "brn_number", unique = true, nullable = false)
	private String brnNo;

}
