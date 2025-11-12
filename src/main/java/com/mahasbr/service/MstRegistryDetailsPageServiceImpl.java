package com.mahasbr.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.entity.CensusEntity;
import com.mahasbr.entity.ConcernRegistryDetailsPageEntity;
import com.mahasbr.entity.DuplicateRegistryDetailsPageEntity;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.User;
import com.mahasbr.filter.AuthTokenFilter;
import com.mahasbr.model.BRNGenartionRemark;
import com.mahasbr.model.BRNGenerationRecordCount;
import com.mahasbr.model.MstRegistryDetailsPageModel;
import com.mahasbr.repository.CensusEntityRepository;
import com.mahasbr.repository.ConcernRegistryDetailsPageRepository;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.DuplicateRegistryDetailsPageRepository;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.util.BRNGenerator;
import com.mahasbr.util.JwtUtils;
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
	private final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

	AuthTokenFilter authTokenFilter = new AuthTokenFilter();

	@Autowired
	UserRepository userRepository;

	@Autowired
	private JwtUtils jwtUtils;

	Long userId = 0L;

	public Long getLoginUsernameId() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			userId = user.get().getRegistry().getId();
		}
		return userId;
	}

	public Collection<? extends GrantedAuthority> getUsersRole() {

		Collection<? extends GrantedAuthority> roles = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();

		return roles;
	}

	public BRNGenerationRecordCount uploadRegiteryCSVFileForBRNGeneration(MultipartFile file) {
		StringUtils stringUtils = new StringUtils();
		int count = 0;
		Integer totalRecordCount = 0;
		Integer brnCount = 0;
		Integer concernCount = 0;
		Integer duplicateCount = 0;
		BRNGenerationRecordCount bRNGenerationRecordCount = new BRNGenerationRecordCount();
		StringBuilder errorMessages = new StringBuilder();

		final Set<String> REQUIRED_HEADERS = Set.of("NAME_OF_ESTABLISHMENT/OWNER", "PAN", "TAN", "EMAIL", "TEL/MOB_NO",
				"NIC_2008_ACTIVITY_CODE", "GST_NUMBER", "HOUSE_NO", "STREET_NAME", "LOCALITY", "TOWN_VILLAGE", "TALUKA",
				"DISTRICT", "SECTOR(RURAL/URBAN)", "PIN_CODE");
		Set<String> actualHeaders = new HashSet<>();
		Set<String> missingHeaders = new HashSet<>(REQUIRED_HEADERS);
		ObjectMapper objectMapper = new ObjectMapper();
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			for (Sheet sheet : workbook) {

				Row headerRow = sheet.getRow(0);
				// Assuming the first row is the header
				int numberOfCells = headerRow.getPhysicalNumberOfCells();
				for (int j = 0; j < numberOfCells; j++) {
					actualHeaders.add(stringUtils.safeUpperCase(headerRow.getCell(j).getStringCellValue()));
				}
				missingHeaders.removeAll(actualHeaders);
				if (!missingHeaders.isEmpty()) {
					bRNGenerationRecordCount.setMissingHeaders(missingHeaders);
					return bRNGenerationRecordCount;
				}
				for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from the second row
					Row row = sheet.getRow(i);
					MstRegistryDetailsPageModel mstRegistryDetailsPageModel = new MstRegistryDetailsPageModel();
					totalRecordCount++;
					mstRegistryDetailsPageModel.setRegUserId(userId);
					for (int j = 0; j < numberOfCells; j++) {
						Cell cell = row.getCell(j);
						String header = stringUtils.safeUpperCase(headerRow.getCell(j).getStringCellValue());
						String value = getCellValue(cell);

						switch (header) {
						case "NAME_OF_ESTABLISHMENT/OWNER":
							mstRegistryDetailsPageModel.setNameOfEstablishmentOrOwner(
									stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "HOUSE_NO":
							mstRegistryDetailsPageModel
									.setHouseNo(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "STREET_NAME":
							mstRegistryDetailsPageModel
									.setStreetName(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "LOCALITY":
							mstRegistryDetailsPageModel
									.setLocality(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "PIN_CODE":
							mstRegistryDetailsPageModel.setPinCode(parseIntCellValue(cell));
							break;
						case "TEL/MOB_NO":
							mstRegistryDetailsPageModel.setTelephoneMobNumber(parseLongCellValue(cell));
							break;
						case "EMAIL":
							mstRegistryDetailsPageModel
									.setEmailAddress(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "PAN":
							mstRegistryDetailsPageModel
									.setPanNumber(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "TAN":
							mstRegistryDetailsPageModel
									.setTanNumber(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "HEAD_OFFICE_HOUSE_NO":
							mstRegistryDetailsPageModel.setHeadOfficeHouseNo(
									stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "HEAD_OFFICE_STREET_NAME":
							mstRegistryDetailsPageModel.setHeadOfficeStreetName(
									stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "HEAD_OFFICE_LOCALITY":
							mstRegistryDetailsPageModel.setHeadOfficeLocality(
									stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "HEAD_OFFICE_PIN_CODE":
							mstRegistryDetailsPageModel.setHeadOfficePinCode(parseIntCellValue(cell));
							break;
						case "HEAD_OFFICE_TEL/MOB_NUMBER":
							mstRegistryDetailsPageModel.setHeadOfficeTelephoneMobNumber(parseLongCellValue(cell));
							break;
						case "DES_MAJOR_ACTIVITY_OF_ESTABLISHMENT":
							mstRegistryDetailsPageModel.setDescriptionOfMajorActivity(
									stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "NIC_2008_ACTIVITY_CODE":
							mstRegistryDetailsPageModel.setNic2008ActivityCode(parseIntCellValue(cell));
							break;
						case "YEAR_START_OF_OPERATION_UNDER_CURRENT_OWNERSHIP":
							mstRegistryDetailsPageModel.setYearOfStartOfOperation(parseIntCellValue(cell));
							break;
						case "OWNERSHIP_CODE":
							mstRegistryDetailsPageModel.setOwnershipCode(parseIntCellValue(cell));
							break;
						case "TOTAL_NUMBER_OF_PERSONS_WORKING":
							mstRegistryDetailsPageModel.setTotalNumberOfPersonsWorking(parseIntCellValue(cell));
							break;
						case "ACT/AUTHORITY_REGISTRATION_NO":
							mstRegistryDetailsPageModel.setActAuthorityRegistrationNumbers(
									stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "REMARKS":
							mstRegistryDetailsPageModel
									.setRemarks(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "LOCATION_CODE":
							mstRegistryDetailsPageModel
									.setLocationCode(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "REGISTRATION_STATUS":
							mstRegistryDetailsPageModel.setRegistrationStatus(
									stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "TOWN_VILLAGE":
							mstRegistryDetailsPageModel
									.setTownVillage(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "TALUKA":
							mstRegistryDetailsPageModel
									.setTaluka(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "DISTRICT":
							mstRegistryDetailsPageModel
									.setDistrict(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "SECTOR(RURAL/URBAN)":
							mstRegistryDetailsPageModel
									.setSector(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "WARD_NUMBER":
							mstRegistryDetailsPageModel
									.setWardNumber(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "NAME_OF_AUTHORITY":
							mstRegistryDetailsPageModel
									.setNameOfAuthority(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "NAME_OF_ACT":
							mstRegistryDetailsPageModel
									.setNameOfAct(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "DATE_OF_REGISTRATION":
//							mstRegistryDetailsPageModel.setDateOfRegistration(value);
							break;
						case "DATE_OF_EXPIRY":
//							mstRegistryDetailsPageModel.setDateOfDeregistrationExpiry(value);
							break;
						case "GST_NUMBER":
							mstRegistryDetailsPageModel
									.setGstNumber(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						case "HSN_CODE":
							mstRegistryDetailsPageModel
									.setHsnCode(stringUtils.safeUpperCase(getCellValue(cell)).isEmpty() ? "N/A"
											: getCellValue(cell));
							break;
						default:
							// Handle any unexpected headers if necessary
							break;
						}
					}

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

					logger.info(" header  " + mstRegistryDetailsPageModel.toString());

					if (!Objects.equals(bRNGenartionRemark.getLocationCode(), "NA")) {
						MstRegistryDetailsPageEntity entity = objectMapper.convertValue(mstRegistryDetailsPageModel,
								MstRegistryDetailsPageEntity.class);
						// ADD THE LOCATION CODE
						mstRegistryDetailsPageModel.setLocationCode(bRNGenartionRemark.getLocationCode());
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
								logger.info(" Sheet  no " + count + " row no " + row.getRowNum() + " Duplicate data "
										+ duplicate.toString());
								duplicateRegistryDetailsPageRepository.save(duplicate);
								duplicateCount++;
							} catch (Exception e) {

							}

						} else {
							// save the new record
							MstRegistryDetailsPageEntity mstRegistryDetailsPageEntity = objectMapper
									.convertValue(mstRegistryDetailsPageModel, MstRegistryDetailsPageEntity.class);
							mstRegistryDetailsPageEntity.setBrnNo(bRNGenerator.getBRNNumber());
							try {
								logger.info(" Sheet  no " + count + " row no " + row.getRowNum() + " BRN Data Info "
										+ mstRegistryDetailsPageEntity.toString());
								mstRegistryDetailsPageRepository.save(mstRegistryDetailsPageEntity);
								brnCount++;
							} catch (Exception e) {
								logger.error(" Sheet  no " + count + " row no " + row.getRowNum() + "BRN Data Info ",
										e.getMessage());
							}

						}

					} else {
						// save the concern record
						ConcernRegistryDetailsPageEntity concernRegistryDetailsPageEntity = objectMapper
								.convertValue(mstRegistryDetailsPageModel, ConcernRegistryDetailsPageEntity.class);
						try {
							concernRegistryDetailsPageEntity.setRemarks(bRNGenartionRemark.getRemark());
							logger.info(" Sheet  no " + count + " row no " + row.getRowNum() + " concern Data Info "
									+ concernRegistryDetailsPageEntity.toString());
							concernRegistryDetailsPageRepository.save(concernRegistryDetailsPageEntity);
							concernCount++;
						} catch (Exception e) {
							logger.info(" Sheet  no " + count + " row no " + row.getRowNum() + "concerson Info"
									+ e.getMessage());
						}
					}
				}
			}

		} catch (IOException e) {
			logger.info("OUTER Info", e.getMessage());
		}
		bRNGenerationRecordCount.setTotalBRNGeneretion(brnCount);
		bRNGenerationRecordCount.setTotalGeneration(totalRecordCount);
		bRNGenerationRecordCount.setDuplicateGeneration(duplicateCount);
		bRNGenerationRecordCount.setConcernData(concernCount);
		return bRNGenerationRecordCount;
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

	private long parseLongCellValue(Cell cell) {
		if (cell == null || cell.getCellType() != CellType.NUMERIC) {
			return 0L; // Default value if cell is null or not numeric
		}
		return (long) cell.getNumericCellValue();
	}

	public String getLocationCode(String stateName, String districtName, String talukaName, String villageName) {

		BRNGenartionRemark locRemark = new BRNGenartionRemark();
		StringBuilder sb = new StringBuilder();
		String censusStateCode = "";

		String locationCode = "";
		String censusDistrictCode = "";
		String subDistrictCode = "";

		String villegeCode = "";

		logger.info("location mandatory data : stateName " + stateName);
		logger.info("location mandatory data : districtName " + districtName);
		logger.info("location mandatory data : talukaName " + talukaName);
		logger.info("location mandatory data : villageName " + villageName);

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

		logger.info("location mandatory data : stateName " + stateName);
		logger.info("location mandatory data : district " + district);
		logger.info("location mandatory data : taluka " + taluka);
		logger.info("location mandatory data : townVillage " + townVillage);
		logger.info("location mandatory data : nameOfEstablishmentOrOwner " + nameOfEstablishmentOrOwner);
		logger.info("location mandatory data : panNumber " + tanNumber);
		logger.info("location mandatory data : emailAddress " + emailAddress);
		logger.info("location mandatory data : telephoneMobNumber " + telephoneMobNumber);
		logger.info("location mandatory data : gstNumber " + gstNumber);
		logger.info("location mandatory data : nic2008ActivityCode " + nic2008ActivityCode);
		logger.info("location mandatory data : nic2008ActivityCodeDesicripton " + nic2008ActivityCodeDesicripton);
		logger.info("location mandatory data : houseNo " + houseNo);
		logger.info("location mandatory data : streetName " + streetName);
		logger.info("location mandatory data : locality " + locality);
		logger.info("location mandatory data : pinCode " + pinCode);
		BRNGenartionRemark locRemark = new BRNGenartionRemark();
		StringBuilder sb = new StringBuilder();

		String censusStateCode = "";

		String locationCode = "";
		String censusDistrictCode = "";
		String subDistrictCode = "";

		String villegeCode = "";

		if (stateName == null || stateName.isEmpty() || stateName.equalsIgnoreCase("")) {
			sb.append(" state,");
		}

		if (district == null || district.isEmpty() || district.equalsIgnoreCase("")) {
			sb.append(" district,");
		}

		if (taluka == null || taluka.isEmpty() || taluka.equalsIgnoreCase("")) {
			sb.append(" taluka,");
		}

		if (townVillage == null || townVillage.isEmpty()) {
			sb.append(" townVillage,");
		}
		if (nameOfEstablishmentOrOwner == null || nameOfEstablishmentOrOwner.isEmpty()
				|| nameOfEstablishmentOrOwner.equalsIgnoreCase("")) {
			sb.append(" NameOfEstablishmentOrOwner,");
		}
		if (panNumber == null || panNumber.isEmpty() || panNumber.equalsIgnoreCase("")) {
			sb.append(" Pan,");
		}
		if (tanNumber == null || tanNumber.isEmpty() || tanNumber.equalsIgnoreCase("")) {
			sb.append(" tan,");
		}
		if (emailAddress == null || emailAddress.isEmpty() || emailAddress.equalsIgnoreCase("")) {
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

		if (stateName != null && !stateName.isEmpty() && !stateName.equalsIgnoreCase("N/A") && district != null
				&& !district.isEmpty() && !district.equalsIgnoreCase("N/A") && taluka != null && !taluka.isEmpty()
				&& !taluka.equalsIgnoreCase("N/A") && townVillage != null && !townVillage.isEmpty()
				&& !townVillage.equalsIgnoreCase("N/A")) {
			try {
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
			} catch (Exception e) {
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

	public List<MstRegistryDetailsPageEntity> getsearchBRNAndEstablishmentDetails(String district, String brnNo,
			String establishment) {
		List<MstRegistryDetailsPageEntity> searchBRNAndEstablishmentDetails = new ArrayList<>();
		searchBRNAndEstablishmentDetails = mstRegistryDetailsPageRepository
				.findByDistrictAndBrnNoOrNameOfEstablishmentOrOwner(district, brnNo, establishment);

		return searchBRNAndEstablishmentDetails;
	}

	@Override
	public Page<MstRegistryDetailsPageEntity> getAllRegistoryDetails(Pageable pageable) {
		Collection<? extends GrantedAuthority> userRoles = getUsersRole();
		String role = userRoles.stream().map(GrantedAuthority::getAuthority).findFirst().orElse("UNKNOWN");

		switch (role) {
		case "ROLE_REG_AUTH_API":
			return mstRegistryDetailsPageRepository.findAllByRegUserId(getLoginUsernameId(), pageable);

		case "ROLE_REG_AUTH_CSV":
			return mstRegistryDetailsPageRepository.findAllByRegUserId(getLoginUsernameId(), pageable);

		case "ROLE_DES_DISTRICT":
			return getDistrictWiseRegistryDetails(pageable);

		case "ROLE_DES_REGION":
			return getRegionWiseRegistryDetails(pageable);

		case "ROLE_DES_STATE":
			return mstRegistryDetailsPageRepository.findAll(pageable);

		default:
			return Page.empty(pageable);
		}

	}

	private void writeErrorLog(String errorMessages) {
		if (!errorMessages.isEmpty()) {
			try (FileWriter fileWriter = new FileWriter("error_log.txt")) {
				fileWriter.write(errorMessages);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String validateExceldata() {
		return null;

	}

	@Override
	public Optional<MstRegistryDetailsPageEntity> getBRNDetails(String brnno) {
		return mstRegistryDetailsPageRepository.findByBrnNo(brnno);
	}

	@Override
	public Page<MstRegistryDetailsPageEntity> getPostLoginDashboardData(Pageable pageable,
			List<Long> selectedDistrictIds, List<Long> selectedTalukaIds, String registerDateFrom,
			String registerDateTo) {
		List<String> formattedCodes = selectedTalukaIds.stream().map(code -> String.format("%05d", code))
				.collect(Collectors.toList());

		List<String> talukaName = talukaMasterRepository.findTalukaNameByCensusTalukaCode(formattedCodes);
		List<String> districtName = districtMasterRepository
				.findDistrictNamesByCensusDistrictCodes(selectedDistrictIds);
		List<String> districtsLower = districtName.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String> talukaNameLower = talukaName.stream().map(String::toLowerCase).collect(Collectors.toList());

//		if (!talukaName.isEmpty()) {
//			Page<MstRegistryDetailsPageEntity> mstRegistoryData = mstRegistryDetailsPageRepository
//					.findByTalukasAndDistrictsAndRegUserId(talukaNameLower, districtsLower, getLoginUsernameId(), pageable);
//			return mstRegistoryData;
//		}

//		Page<MstRegistryDetailsPageEntity> mstRegistoryData = mstRegistryDetailsPageRepository
//				.findByDistrictsAndRegUserId(districtsLower, getLoginUsernameId(), pageable);

		Collection<? extends GrantedAuthority> userRoles = getUsersRole();
		String role = userRoles.stream().map(GrantedAuthority::getAuthority).findFirst().orElse("UNKNOWN");

		switch (role) {
		case "ROLE_REG_AUTH_API":
			if (!talukaName.isEmpty())
				return mstRegistryDetailsPageRepository.findByTalukasAndDistrictsAndRegUserId(talukaNameLower,
						districtsLower, getLoginUsernameId(), pageable);
			else if (!districtName.isEmpty())
				return mstRegistryDetailsPageRepository.findByDistrictsAndRegUserId(districtsLower,
						getLoginUsernameId(), pageable);
			else
				return mstRegistryDetailsPageRepository.findAllByRegUserId(getLoginUsernameId(), pageable);

		case "ROLE_REG_AUTH_CSV":
			if (!talukaName.isEmpty())
				return mstRegistryDetailsPageRepository.findByTalukasAndDistrictsAndRegUserId(talukaNameLower,
						districtsLower, getLoginUsernameId(), pageable);
			else if (!districtName.isEmpty())
				return mstRegistryDetailsPageRepository.findByDistrictsAndRegUserId(districtsLower,
						getLoginUsernameId(), pageable);
			else
				return mstRegistryDetailsPageRepository.findAllByRegUserId(getLoginUsernameId(), pageable);

		case "ROLE_DES_DISTRICT":
			return getDistrictWiseRegistryDetails(pageable);

		case "ROLE_DES_REGION":
			return getRegionWiseRegistryDetailsBasedOnfilter(selectedDistrictIds, selectedTalukaIds, registerDateFrom,
					registerDateTo, pageable);

		case "ROLE_DES_STATE":
			return getStateDetailsBasedOnfilter(selectedDistrictIds, selectedTalukaIds, registerDateFrom,
					registerDateTo, pageable);

		default:
			return Page.empty(pageable);
		}

	}

	@Override
	public Page<MstRegistryDetailsPageEntity> getBRNData(String brn, Pageable pageable) {

		return mstRegistryDetailsPageRepository.findAllByBrnNoAndRegUserId(brn, pageable);
	}

	private Page<MstRegistryDetailsPageEntity> getRegionWiseRegistryDetails(Pageable pageable) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		// 2. Fetch all district codes under this divisio
		List<String> districtName = districtMasterRepository
				.findDistrictNamesByDivisionCode(user.get().getDivisionCode());
		List<String> lowercaseDistricts = districtName.stream().map(String::toLowerCase).collect(Collectors.toList());

		if (districtName.isEmpty()) {
			return Page.empty(pageable);
		}
		return mstRegistryDetailsPageRepository.findByDistricts(lowercaseDistricts, pageable);
	}

	private Page<MstRegistryDetailsPageEntity> getDistrictWiseRegistryDetails(Pageable pageable) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		// 2. Fetch all district codes under this divisio
		Optional<String> districtName = districtMasterRepository
				.findDistrictNameById(user.get().getDistrict().getDistrictId());

		if (districtName.isEmpty()) {
			return Page.empty(pageable);
		}
		return mstRegistryDetailsPageRepository.findByDistrictName(districtName.get(), pageable);
	}

	private Page<MstRegistryDetailsPageEntity> getRegionWiseRegistryDetailsBasedOnfilter(List<Long> selectedDistrictIds,
			List<Long> selectedTalukaIds, String registerDateFrom, String registerDateTo, Pageable pageable) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		List<String> formattedCodes = selectedTalukaIds.stream().map(code -> String.format("%05d", code))
				.collect(Collectors.toList());

		List<String> talukaName = talukaMasterRepository.findTalukaNameByCensusTalukaCode(formattedCodes);

		List<String> districtName = districtMasterRepository
				.findDistrictNamesByCensusDistrictCodes(selectedDistrictIds);

		List<String> districtsLower = districtName.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String> talukaNameLower = talukaName.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String> lowercaseDistricts = districtName.stream().map(String::toLowerCase).collect(Collectors.toList());

		if (!talukaName.isEmpty()) {
			return mstRegistryDetailsPageRepository.findByTalukasAndDistricts(talukaNameLower, districtsLower,
					pageable);
		}
		if (districtName.isEmpty()) {
			return Page.empty(pageable);
		}
		return mstRegistryDetailsPageRepository.findByDistricts(lowercaseDistricts, pageable);

	}

	private Page<MstRegistryDetailsPageEntity> getStateDetailsBasedOnfilter(List<Long> selectedDistrictIds,
			List<Long> selectedTalukaIds, String registerDateFrom, String registerDateTo, Pageable pageable) {

//		String username = SecurityContextHolder.getContext().getAuthentication().getName();
//		Optional<User> user = userRepository.findByUsername(username);

		List<String> formattedCodes = selectedTalukaIds.stream().map(code -> String.format("%05d", code))
				.collect(Collectors.toList());

		List<String> talukaName = talukaMasterRepository.findTalukaNameByCensusTalukaCode(formattedCodes);

		List<String> districtName = districtMasterRepository
				.findDistrictNamesByCensusDistrictCodes(selectedDistrictIds);

		List<String> districtsLower = districtName.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String> talukaNameLower = talukaName.stream().map(String::toLowerCase).collect(Collectors.toList());

		List<String> lowercaseDistricts = districtName.stream().map(String::toLowerCase).collect(Collectors.toList());

		if (!talukaName.isEmpty() && !districtName.isEmpty()) {
			return mstRegistryDetailsPageRepository.findByTalukasAndDistricts(talukaNameLower, districtsLower,
					pageable);
		} else if (!districtName.isEmpty()) {
			return mstRegistryDetailsPageRepository.findByDistricts(lowercaseDistricts, pageable);
		} else {
			return mstRegistryDetailsPageRepository.findAll(pageable);
		}

	}
}
