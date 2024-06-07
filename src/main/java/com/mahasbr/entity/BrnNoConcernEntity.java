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

@Entity
@Data
@NoArgsConstructor
@Table(name = "brn_no_concern_dtls")
public class BrnNoConcernEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brn_no_concern_dtls_seq_generator")
	@SequenceGenerator(name = "brn_no_concern_dtls_seq_generator", sequenceName = "brn_no_concern_dtls_seq", allocationSize = 1)
	@Column(length = 7)
	private Long slNo;
	@NotBlank
	@Column(length = 500)
	String nameOfEstateOwner;
	
	@NotBlank
	@Column(length = 500)
	String houseNo;
	
	@NotBlank
	@Column(length = 500)
	String StreetName;
	
	@NotBlank
	@Column(length = 500)
	String locality;
	
	@Column(length = 7)
	int pincode;
	
	@Column( length = 10)
	Long telMobNo;
	
	@Column(length = 320)
	String emailAddress;
	
	@Column(length = 10)
	String panNo;
	
	@Column( length = 10)
	String tanNo;
	
	@Column(length = 500)
	String hohouseNo;
	
	@Column(length = 500)
	String hoStreetName;
	
	@Column(length = 500)
	String hoLocality;
	
	@Column(length = 6)
	int hoPincode;
	
	@Column(length = 10)
	Long hoTelMobNo;
	
	@Column(length = 320)
	String hoEmailAddress;
	
	@Column(length = 10)
	String hoPanNo;
	
	@Column(length = 10)
	String hoTanNo;
	
	@Column(length = 100)
	String majarActOfEst;
	
	@Column(length = 3)
	int nicActCode;
	
	@Column(length = 4)
	LocalDate  opCurStartDate;
	
	@Column(length = 1)
	int ownCode;
	
	@Column( length = 7)
	int noOfWorkers;
	
	@Column(length = 100)
	String actRegNo;
	
	@Column(length = 100)
	String remarks;
	
	@Column( length = 16)
	String loccode;
	
	@Column(length = 17)
	String busRegNo;
	
	@Column(length = 12)
	long aadharNo;
	
	@Column(length = 1)
	String regstatus;
	@Column(length = 100)
	String townVillage;
	
	@Column(length = 100)
	String Taluka;
	
	@Column(length = 100)
	String district;
	
	@Column(length = 100)
	String sector;
	
	@Column(length = 200)
	String nameofAuth;
	
	@Column(length = 200)
	String NameofAct;
	
	@Column(length = 30)
	String dateOfReg;
	
	@Column(length = 30)
	String dateOfExpiry;

	

	@Column(length = 100)
	String remark;
	
	
	

}
