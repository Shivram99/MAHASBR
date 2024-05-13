package com.mahasbr.entity;

import com.mahasbr.model.TalukaMasterModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "taluka_master")
public class TalukaMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotBlank
	private Long talukaId;

	@NotBlank
	private String talukaName;
	@NotBlank
	private Integer censusTalukaCode;

	public TalukaMaster(TalukaMasterModel talukaMasterModel) {
		this.talukaName = talukaMasterModel.getTalukaName();
		this.censusTalukaCode = talukaMasterModel.getCensusTalukaCode();
	}
}
