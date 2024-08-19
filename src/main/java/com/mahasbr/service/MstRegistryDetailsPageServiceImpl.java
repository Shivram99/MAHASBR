package com.mahasbr.service;

import java.io.IOException;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.entity.CensusEntity;
import com.mahasbr.entity.ConcernRegistryDetailsPageEntity;
import com.mahasbr.entity.DuplicateRegistryDetailsPageEntity;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.model.BRNGenartionRemark;
import com.mahasbr.model.MstRegistryDetailsPageModel;
import com.mahasbr.repository.CensusEntityRepository;
import com.mahasbr.repository.ConcernRegistryDetailsPageRepository;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.DuplicateRegistryDetailsPageRepository;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.util.BRNGenerator;
import com.mahasbr.util.StringUtils;

@Service
public class MstRegistryDetailsPageServiceImpl implements MstRegistryDetailsPageService {

	private static final Logger logger = LoggerFactory.getLogger(MstRegistryDetailsPageServiceImpl.class);

	@Autowired
	MstRegistryDetailsPageRepository mstRegistryDetailsPageRepository;

	@Autowired
	BRNGenerator bRNGenerator;

	@Autowired
	StatesMasterService statesMasterService;

	@Autowired
	TalukaMasterService talukaMasterService;

	@Autowired
	VillageMasterService villageMasterService;

	@Autowired
	DistrictMasterRepository districtMasterRepository;

	@Autowired
	TalukaMasterRepository talukaMasterRepository;

	@Autowired
	ConcernRegistryDetailsPageRepository concernRegistryDetailsPageRepository;

	@Autowired
	CensusEntityRepository censusEntityRepository;

	@Autowired
	DuplicateRegistryDetailsPageRepository duplicateRegistryDetailsPageRepository;

	public void uploadRegiteryCSVFileForBRNGeneration(MultipartFile file) {
		StringUtils stringUtils = new StringUtils();

		ObjectMapper objectMapper = new ObjectMapper();
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

			for (Sheet sheet : workbook) {
				for (Row row : sheet) {
					if (row.getRowNum() == 0) { // Skip header row if there's one
						continue;
					}
					try {

						MstRegistryDetailsPageModel mstRegistryDetailsPageModel = new MstRegistryDetailsPageModel();
						// read the excel

						StringBuilder sb = new StringBuilder();

						mstRegistryDetailsPageModel
								.setNameOfEstablishmentOrOwner(getCellValue(row.getCell(1)).toUpperCase());
						mstRegistryDetailsPageModel.setHouseNo(getCellValue(row.getCell(2)).toUpperCase());
						mstRegistryDetailsPageModel.setStreetName(getCellValue(row.getCell(3)).toUpperCase());
						mstRegistryDetailsPageModel.setLocality(getCellValue(row.getCell(4)).toUpperCase());
						mstRegistryDetailsPageModel.setTownVillage(getCellValue(row.getCell(5)).toUpperCase());
						mstRegistryDetailsPageModel.setTaluka(getCellValue(row.getCell(6)).toUpperCase());
						mstRegistryDetailsPageModel.setDistrict(getCellValue(row.getCell(7)).toUpperCase());
						mstRegistryDetailsPageModel.setPinCode(parseIntCellValue(row.getCell(8)));
						mstRegistryDetailsPageModel.setSector(getCellValue(row.getCell(9)).toUpperCase());
						mstRegistryDetailsPageModel.setNameOfAuthority(getCellValue(row.getCell(11)).toUpperCase());
						mstRegistryDetailsPageModel.setNameOfAct(getCellValue(row.getCell(12)).toUpperCase());

						BRNGenartionRemark bRNGenartionRemark = getLocationCodeAndCheckMandatoryDetails("MAHARASHTRA",
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getDistrict()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getTaluka()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getTownVillage()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getNameOfEstablishmentOrOwner()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getPanNumber()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getTanNumber()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getEmailAddress()),
								mstRegistryDetailsPageModel.getTelephoneMobNumber(),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getGstNumber()),
								mstRegistryDetailsPageModel.getNic2008ActivityCode(),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getNic2008ActivityCodeDesicripton()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getHouseNo()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getStreetName()),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getLocality()),
								mstRegistryDetailsPageModel.getPinCode(),
								stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getSector()));

						mstRegistryDetailsPageModel.setLocationCode(bRNGenartionRemark.getLocationCode());

						if (!bRNGenartionRemark.getLocationCode().equals("NA")) {
							MstRegistryDetailsPageEntity entity = objectMapper.convertValue(mstRegistryDetailsPageModel,
									MstRegistryDetailsPageEntity.class);
							// checking the Duplicate Data
							Optional<MstRegistryDetailsPageEntity> existing = mstRegistryDetailsPageRepository
									.findByRegistryDetails(
											stringUtils.safeUpperCase(
													mstRegistryDetailsPageModel.getNameOfEstablishmentOrOwner()),
											mstRegistryDetailsPageModel.getTelephoneMobNumber(),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getEmailAddress()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getPanNumber()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getTanNumber()),
											mstRegistryDetailsPageModel.getNic2008ActivityCode(),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getGstNumber()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getHouseNo()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getStreetName()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getLocality()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getTownVillage()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getTaluka()),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getDistrict()),
											mstRegistryDetailsPageModel.getPinCode(),
											stringUtils.safeUpperCase(mstRegistryDetailsPageModel.getSector()));

							if (existing.isPresent()) {
								// save the duplicate record
								DuplicateRegistryDetailsPageEntity duplicate = objectMapper.convertValue(
										mstRegistryDetailsPageModel, DuplicateRegistryDetailsPageEntity.class);
								duplicate.setBrnNo(existing.get().getBrnNo());
								duplicate.setRemarks("DUPLICATE");
								try {
									duplicateRegistryDetailsPageRepository.save(duplicate);
								} catch (Exception e) {

								}

							} else {
								// save the new record
								MstRegistryDetailsPageEntity mstRegistryDetailsPageEntity = objectMapper
										.convertValue(mstRegistryDetailsPageModel, MstRegistryDetailsPageEntity.class);
								mstRegistryDetailsPageEntity.setBrnNo(bRNGenerator.getBRNNumber());
								try {
									mstRegistryDetailsPageRepository.save(mstRegistryDetailsPageEntity);
								} catch (Exception e) {
									logger.info("BRN DATA Info" + e.getMessage());
								}

							}

						} else {
							// save the concern record
							ConcernRegistryDetailsPageEntity concernRegistryDetailsPageEntity = objectMapper
									.convertValue(mstRegistryDetailsPageModel, ConcernRegistryDetailsPageEntity.class);
							try {
								concernRegistryDetailsPageEntity.setRemarks(bRNGenartionRemark.getRemark());
								concernRegistryDetailsPageRepository.save(concernRegistryDetailsPageEntity);
							} catch (Exception e) {
								logger.info("concerson Info" + e.getMessage());
							}
						}

					} catch (Exception e) {
						logger.info("ABCD Info" + e.getMessage());
					}
				}
			}

		} catch (IOException e) {
			logger.info("concerson Info" + e.getStackTrace());
		}

	}

	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim().toUpperCase();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString().trim().toUpperCase();
			} else {
				return String.valueOf((int) cell.getNumericCellValue()).trim().toUpperCase();
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue()).trim().toUpperCase();
		case FORMULA:
			return cell.getCellFormula().trim().toUpperCase();
		case BLANK:
			return "";
		default:
			return "UNKNOWN CELL TYPE";
		}
	}

	private int parseIntCellValue(Cell cell) {
		if (cell == null || cell.getCellType() != CellType.NUMERIC) {
			return 0; // Default or error value
		}
		return (int) cell.getNumericCellValue();
	}

	public String getLocationCode(String stateName, String districtName, String talukaName, String villageName) {

		BRNGenartionRemark locRemark = new BRNGenartionRemark();
		StringBuilder sb = new StringBuilder();
		String censusStateCode = "";

		String locationCode = "";
		String censusDistrictCode = "";
		String subDistrictCode = "";

		String villegeCode = "";

		if (stateName == null || stateName.isEmpty()) {
			sb.append("STATE");
		}
		if (districtName == null || districtName.isEmpty()) {
			sb.append("DISTRICT");
		}
		if (districtName == null || districtName.isEmpty()) {
			sb.append("TALUKA");
		}
		if (districtName == null || districtName.isEmpty()) {
			sb.append("VILLAGE");
		}

		locRemark.setRemark(sb.toString());

		try {

			if (stateName != null && !stateName.isEmpty() && districtName != null && !districtName.isEmpty()
					&& talukaName != null && !talukaName.isEmpty() && villageName != null && !villageName.isEmpty()) {
				Optional<CensusEntity> censusEntity = censusEntityRepository
						.findByCensusStateNameAndCensusDistrictNameAndCensusTahsilNameAndCensusVillageName(stateName,
								districtName, talukaName, villageName);

				if (censusEntity.isPresent()) {
					censusStateCode = censusEntity.get().getCensusStateCode().toString();
					censusDistrictCode = censusEntity.get().getCensusDistrictCode().toString();
					subDistrictCode = censusEntity.get().getCensusTahsilCode().toString();
					villegeCode = censusEntity.get().getCensusVillageCode().toString();
				}

			}

//			StatesMaster state = statesMasterService.findByStateName("MAHARASHTRA");
//			censusStateCode = state.getCensusStateCode().toString();
//
//			Optional<DistrictMaster> district = districtMasterRepository
//					.findByCensusStateCodeAndDistrictName(state.getCensusStateCode(), districtName);
//
//			if (district.isPresent()) {
//				censusDistrictCode = district.get().getCensusDistrictCode().toString();
//			}
//			Optional<TalukaMaster> taluka = talukaMasterService
//					.findByCensusDistrictCodeAndTalukaName(censusDistrictCode, talukaName);
//
//			if (taluka.isPresent()) {
//				subDistrictCode = taluka.get().getCensusTalukaCode().toString();
//				if (subDistrictCode.length() <= 4) {
//					subDistrictCode = "0" + subDistrictCode;
//				}
//
//				Optional<VillageMaster> village = villageMasterService
//						.getVillagesByCensusTalukaCodeAndVillageName(taluka.get().getCensusTalukaCode(), villageName);
//				if (village.isPresent()) {
//					villegeCode = village.get().getCensusVillageCode().toString();
//				}
//
//			}

		} catch (Exception e) {

		}
		if (!censusStateCode.equals("") && !censusDistrictCode.equals("") && !subDistrictCode.equals("")
				&& !villegeCode.equals("")) {
			return censusStateCode + censusDistrictCode + subDistrictCode + villegeCode;
		}
		return "NA";

	}

	private BRNGenartionRemark getLocationCodeAndCheckMandatoryDetails(String stateName, String district, String taluka,
			String townVillage, String nameOfEstablishmentOrOwner, String panNumber, String tanNumber,
			String emailAddress, Long telephoneMobNumber, String gstNumber, Integer nic2008ActivityCode,
			String nic2008ActivityCodeDesicripton, String houseNo, String streetName, String locality, Integer pinCode,
			String sector) {

		BRNGenartionRemark locRemark = new BRNGenartionRemark();
		StringBuilder sb = new StringBuilder();

		String censusStateCode = "";

		String locationCode = "";
		String censusDistrictCode = "";
		String subDistrictCode = "";

		String villegeCode = "";

		if (stateName == null || stateName.isEmpty()) {
			sb.append(" state,");
		}

		if (district == null || district.isEmpty()) {
			sb.append(" district,");
		}

		if (taluka == null || taluka.isEmpty()) {
			sb.append(" taluka,");
		}

		if (townVillage == null || townVillage.isEmpty()) {
			sb.append(" townVillage,");
		}
		if (nameOfEstablishmentOrOwner == null || nameOfEstablishmentOrOwner.isEmpty()) {
			sb.append(" NameOfEstablishmentOrOwner,");
		}
		if (panNumber == null || panNumber.isEmpty()) {
			sb.append(" Pan,");
		}
		if (tanNumber == null || tanNumber.isEmpty()) {
			sb.append(" tan,");
		}
		if (emailAddress == null || emailAddress.isEmpty()) {
			sb.append(" email,");
		}
		if (telephoneMobNumber == null) {
			sb.append(" telephoneMobNumber,");
		}
		if (gstNumber == null || gstNumber.isEmpty()) {
			sb.append(" gstNumber,");
		}
		if (nic2008ActivityCode == null) {
			sb.append(" nic2008ActivityCode,");
		}
		if (nic2008ActivityCodeDesicripton == null || nic2008ActivityCodeDesicripton.isEmpty()) {
			sb.append(" nic2008ActivityCodeDesicripton,");
		}
		if (houseNo == null || houseNo.isEmpty()) {
			sb.append(" houseNo,");
		}
		if (streetName == null || streetName.isEmpty()) {
			sb.append(" streetName,");
		}
		if (locality == null || locality.isEmpty()) {
			sb.append(" locality,");
		}
		if (pinCode == null) {
			sb.append(" pinCode,");
		}
		if (sector == null || sector.isEmpty()) {
			sb.append(" sector,");
		}

		if (stateName != null && !stateName.isEmpty() && district != null && !district.isEmpty() && taluka != null
				&& !taluka.isEmpty() && townVillage != null && !townVillage.isEmpty()) {
			Optional<CensusEntity> censusEntity = censusEntityRepository
					.findByCensusStateNameAndCensusDistrictNameAndCensusTahsilNameAndCensusVillageName(stateName,
							district, taluka, townVillage);

			if (censusEntity.isPresent()) {
				censusStateCode = censusEntity.get().getCensusStateCode().toString();
				censusDistrictCode = censusEntity.get().getCensusDistrictCode().toString();
				subDistrictCode = censusEntity.get().getCensusTahsilCode().toString();
				villegeCode = censusEntity.get().getCensusVillageCode().toString();
			} else {
				locRemark.setLocationCode("NA");
			}

		}
		locRemark.setRemark(sb.toString());
		if (!censusStateCode.equals("") && !censusDistrictCode.equals("") && !subDistrictCode.equals("")
				&& !villegeCode.equals("")) {
			locRemark.setLocationCode(censusStateCode + censusDistrictCode + subDistrictCode + villegeCode);

		}
		return locRemark;

	}

}

// start Backup code

/*
 * 
 * 
 * MstRegistryDetailsPageEntity mstRegistryDetailsPageEntity = new
 * MstRegistryDetailsPageEntity();
 * 
 * ConcernRegistryDetailsPageEntity concernRegistryDetailsPageEntity = new
 * ConcernRegistryDetailsPageEntity(); String locationId =
 * getLocationCode("MAHARASHTRA", getCellValue(row.getCell(7)).toUpperCase(),
 * getCellValue(row.getCell(6)).toUpperCase(),
 * getCellValue(row.getCell(5)).toUpperCase());
 * 
 * if (!locationId.equals("NA")) {
 * mstRegistryDetailsPageEntity.setLocationCode(locationId);
 * 
 * mstRegistryDetailsPageEntity
 * .setNameOfEstablishmentOrOwner(getCellValue(row.getCell(1)).toUpperCase());
 * mstRegistryDetailsPageEntity.setHouseNo(getCellValue(row.getCell(2)).
 * toUpperCase());
 * mstRegistryDetailsPageEntity.setStreetName(getCellValue(row.getCell(3)).
 * toUpperCase());
 * mstRegistryDetailsPageEntity.setLocality(getCellValue(row.getCell(4)).
 * toUpperCase());
 * mstRegistryDetailsPageEntity.setTownVillage(getCellValue(row.getCell(5)).
 * toUpperCase());
 * mstRegistryDetailsPageEntity.setTaluka(getCellValue(row.getCell(6)).
 * toUpperCase());
 * mstRegistryDetailsPageEntity.setDistrict(getCellValue(row.getCell(7)).
 * toUpperCase());
 * mstRegistryDetailsPageEntity.setPinCode(parseIntCellValue(row.getCell(8)));
 * mstRegistryDetailsPageEntity.setSector(getCellValue(row.getCell(9)).
 * toUpperCase()); mstRegistryDetailsPageEntity
 * .setNameOfAuthority(getCellValue(row.getCell(11)).toUpperCase());
 * mstRegistryDetailsPageEntity.setNameOfAct(getCellValue(row.getCell(12)).
 * toUpperCase());
 * 
 * Optional<MstRegistryDetailsPageEntity> existing =
 * mstRegistryDetailsPageRepository .findByRegistryDetails(
 * stringUtils.safeUpperCase(
 * mstRegistryDetailsPageEntity.getNameOfEstablishmentOrOwner()),
 * mstRegistryDetailsPageEntity.getTelephoneMobNumber(),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getEmailAddress()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getPanNumber()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getTanNumber()),
 * mstRegistryDetailsPageEntity.getNic2008ActivityCode(),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getGstNumber()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getHouseNo()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getStreetName()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getLocality()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getTownVillage()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getTaluka()),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getDistrict()),
 * mstRegistryDetailsPageEntity.getPinCode(),
 * stringUtils.safeUpperCase(mstRegistryDetailsPageEntity.getSector())); if
 * (existing.isPresent()) { System.out.println("Duplicate Record" +
 * existing.get()); } else {
 * mstRegistryDetailsPageEntity.setBrnNo(bRNGenerator.getBRNNumber());
 * mstRegistryDetailsPageRepository.save(mstRegistryDetailsPageEntity); }
 * System.out.println(mstRegistryDetailsPageEntity); //
 * if(!locationId.equals("NA"))
 * 
 * } else { // storing the data in which has missing the mandatory filed
 * concernRegistryDetailsPageEntity
 * .setNameOfEstablishmentOrOwner(getCellValue(row.getCell(1)).toUpperCase());
 * concernRegistryDetailsPageEntity.setHouseNo(getCellValue(row.getCell(2)).
 * toUpperCase());
 * concernRegistryDetailsPageEntity.setStreetName(getCellValue(row.getCell(3)).
 * toUpperCase());
 * concernRegistryDetailsPageEntity.setLocality(getCellValue(row.getCell(4)).
 * toUpperCase());
 * concernRegistryDetailsPageEntity.setTownVillage(getCellValue(row.getCell(5)).
 * toUpperCase());
 * concernRegistryDetailsPageEntity.setTaluka(getCellValue(row.getCell(6)).
 * toUpperCase());
 * concernRegistryDetailsPageEntity.setDistrict(getCellValue(row.getCell(7)).
 * toUpperCase());
 * concernRegistryDetailsPageEntity.setPinCode(parseIntCellValue(row.getCell(8))
 * ); concernRegistryDetailsPageEntity.setSector(getCellValue(row.getCell(9)).
 * toUpperCase()); concernRegistryDetailsPageEntity
 * .setNameOfAuthority(getCellValue(row.getCell(11)).toUpperCase());
 * concernRegistryDetailsPageEntity.setNameOfAct(getCellValue(row.getCell(12)).
 * toUpperCase());
 * concernRegistryDetailsPageEntity.setRemarks("MADATORY DATA NOT MATCH");
 * 
 * try {
 * concernRegistryDetailsPageRepository.save(concernRegistryDetailsPageEntity);
 * } catch (Exception e) { logger.info("concerson Info" + e.getMessage()); }
 * 
 * }
 */
//End Backup code
