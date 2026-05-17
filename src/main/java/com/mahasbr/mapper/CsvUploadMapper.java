package com.mahasbr.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.validator.CsvUploadValidator;

@Component
public class CsvUploadMapper {

	public MstRegistryDetailsPageEntity toEntity(Map<String, String> rowData) {
		MstRegistryDetailsPageEntity entity = new MstRegistryDetailsPageEntity();
		entity.setNameOfEstablishmentOrOwner(value(rowData, CsvUploadValidator.ESTABLISHMENT_NAME));
		entity.setHouseNo(value(rowData, CsvUploadValidator.HOUSE_NO));
		entity.setStreetName(value(rowData, CsvUploadValidator.STREET_NAME));
		entity.setLocality(value(rowData, CsvUploadValidator.LOCALITY));
		entity.setTownVillage(value(rowData, CsvUploadValidator.TOWN_VILLAGE));
		entity.setTaluka(value(rowData, CsvUploadValidator.TALUKA));
		entity.setDistrict(value(rowData, CsvUploadValidator.DISTRICT));
		entity.setSector(value(rowData, CsvUploadValidator.SECTOR));
		entity.setActAuthorityRegistrationNumbers(value(rowData, CsvUploadValidator.ACT_REGISTRATION_NO));
		entity.setNameOfAct(value(rowData, CsvUploadValidator.NAME_OF_ACT));
		entity.setNameOfAuthority(value(rowData, CsvUploadValidator.NAME_OF_AUTHORITY));
		entity.setTelephoneMobNumber(value(rowData, CsvUploadValidator.MOBILE_NO));
		entity.setEmailAddress(value(rowData, CsvUploadValidator.EMAIL));
		entity.setPanNumber(value(rowData, CsvUploadValidator.PAN).toUpperCase());
		entity.setTanNumber(value(rowData, CsvUploadValidator.TAN).toUpperCase());
		entity.setWardNumber(value(rowData, CsvUploadValidator.WARD_NUMBER));
		entity.setGstNumber(value(rowData, CsvUploadValidator.GST_NUMBER).toUpperCase());
		entity.setNic2008ActivityCode(value(rowData, CsvUploadValidator.NIC_CODE));
		entity.setPinCode(parseInteger(value(rowData, CsvUploadValidator.PIN_CODE)));
		return entity;
	}

	private Integer parseInteger(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return Integer.parseInt(value.contains(".") ? value.substring(0, value.indexOf('.')) : value);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	private String value(Map<String, String> rowData, String key) {
		return rowData.getOrDefault(key, "").trim();
	}
}
