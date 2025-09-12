package com.mahasbr.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(value = { "hourOfDay" }, ignoreUnknown = true)
@Entity
@Table(name = "mst_reg_details")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true)
public class MstRegistryDetailsPageEntity extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_reg_details_seq_gen")
	@SequenceGenerator(name = "mst_reg_details_seq_gen", sequenceName = "mst_reg_details_seq", allocationSize = 1)
	@Column(name = "si_no", nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Long siNo;

	@Size(max = 100)
	@Column(name = "establishment_or_owner_name", length = 100)
	private String nameOfEstablishmentOrOwner;

	@Size(max = 500)
	@Column(name = "house_no", length = 500)
	private String houseNo;

	@Size(max = 500)
	@Column(name = "street_name", length = 500)
	private String streetName;

	@Size(max = 500)
	@Column(name = "locality", length = 500)
	private String locality;

	@Column(name = "pin_code")
	private Integer pinCode;

	@Column(name = "telephone_mob_number")
	private Long telephoneMobNumber;

	@Email
	@Size(max = 320)
	@Column(name = "email_address", length = 320)
	private String emailAddress;

	@Size(max = 10)
	@Column(name = "pan_number", length = 10)
	private String panNumber;

	@Size(max = 10)
	@Column(name = "tan_number", length = 10)
	private String tanNumber;

	@Size(max = 500)
	@Column(name = "head_office_house_no", length = 500)
	private String headOfficeHouseNo;

	@Size(max = 500)
	@Column(name = "head_office_street_name", length = 500)
	private String headOfficeStreetName;

	@Size(max = 500)
	@Column(name = "head_office_locality", length = 500)
	private String headOfficeLocality;

	@Column(name = "head_office_pin_code")
	private Integer headOfficePinCode;

	@Column(name = "head_office_telephone_mob_number")
	private Long headOfficeTelephoneMobNumber;

	@Email
	@Size(max = 320)
	@Column(name = "head_office_email_address", length = 320)
	private String headOfficeEmailAddress;

	@Size(max = 10)
	@Column(name = "head_office_pan_number", length = 10)
	private String headOfficePanNumber;

	@Size(max = 10)
	@Column(name = "head_office_tan_number", length = 10)
	private String headOfficeTanNumber;

	@Size(max = 100)
	@Column(name = "major_activity_description", length = 100)
	private String descriptionOfMajorActivity;

	@Column(name = "nic2008_activity_code")
	private String nic2008ActivityCode;

	@Size(max = 5)
	@Column(name = "nic2008_activity_code_description", length = 5)
	private String nic2008ActivityCodeDescription;

	@Column(name = "operation_start_year")
	private String yearOfStartOfOperation;

	@Column(name = "ownership_code")
	private String ownershipCode;

	@Column(name = "total_persons_working")
	private Integer totalNumberOfPersonsWorking;

	@Size(max = 100)
	@Column(name = "act_authority_reg_numbers", length = 100)
	private String actAuthorityRegistrationNumbers;

	@Size(max = 100)
	@Column(name = "remarks")
	private String remarks;

	@Size(max = 16)
	@Column(name = "location_code", length = 16)
	private String locationCode;

	@Size(max = 1)
	@Column(name = "registration_status", length = 10)
	private String registrationStatus;

	@Size(max = 100)
	@Column(name = "town_village", length = 100)
	private String townVillage;

	@Size(max = 100)
	@Column(name = "taluka", length = 100)
	private String taluka;

	@Size(max = 100)
	@Column(name = "district", length = 100)
	private String district;

	@Size(max = 6)
	@Column(name = "sector", length = 6)
	private String sector;

	@Size(max = 10)
	@Column(name = "ward_number", length = 10)
	private String wardNumber;

	@Size(max = 200)
	@Column(name = "authority_name", length = 200)
	private String nameOfAuthority;

	@Size(max = 200)
	@Column(name = "act_name", length = 200)
	private String nameOfAct;

	@Column(name = "registration_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDate dateOfRegistration;

	@Column(name = "deregistration_expiry_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDate dateOfDeregistrationExpiry;

	@Size(max = 15)
	@Column(name = "gst_number", length = 15)
	private String gstNumber;

	@Size(max = 10)
	@Column(name = "hsn_code", length = 10)
	private String hsnCode;

	@Size(max = 20)
	@Column(name = "record_status", length = 20)
	private String recordStatus;

	@NotNull
	@Size(max = 50)
	@Column(name = "brn_number", nullable = true, length = 50)
	private String brnNo;

	@Column(name = "reg_user_id")
	private Long regUserId;
}
