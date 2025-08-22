package com.mahasbr.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.User;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.model.TopicModel;
import com.mahasbr.repository.CommonHomeMethodsRepo;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.repository.VillageMasterRepository;
import com.mahasbr.util.StringHelperUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommonServiceImpl implements CommonService{
	private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
	@Autowired
	UserRepository userRepository;

	@Autowired
	CommonHomeMethodsRepo commonHomeMethodsRepo;

	@Autowired
	DistrictMasterRepository districtMasterRepository;

	@Autowired
	TalukaMasterRepository talukaMasterRepository;

	@Autowired
	VillageMasterRepository villageMasterRepository;

	@Override
	public Optional<User> findByUsername(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		return user;
	}

// for Get All District List 
	@Override
	public List<DistrictMaster> getAllDistrict() {
		return districtMasterRepository.findAll();
	}

	// for Get All District List by Districtcode
	@Override
	public DistrictMaster getAllDistrictDistrictCode(String censusDistrictCode) throws Exception {
		Optional<DistrictMaster> districtCode = districtMasterRepository.findByCensusDistrictCode(censusDistrictCode);
		if (districtCode.isPresent())
			return districtCode.get();
		else {

			throw new Exception("*** Id is not present ***");
		}
	}

	@Override
	public List<TalukaMaster> getAllTalukaByDistrictCode(String censusDistrictCode) {
		List<TalukaMaster> taluka = talukaMasterRepository.findBycensusDistrictCode(censusDistrictCode);
		return taluka;
	}

	@Override
	public List<VillageMaster> getAllVillageTalukaCode(String censusTalukaCode) {
		List<VillageMaster> village = villageMasterRepository.findByCensusTalukaCode(censusTalukaCode);
		return village;
	}

//	@Override
//	public List<TopicModel> findMenuNameByRoleID(Long levelRoleVal) {
//
//		List<Object[]> lstprop = commonHomeMethodsRepo.findMenuNameByRoleID(levelRoleVal);
//		List<TopicModel> lstMenuObj = new ArrayList<>();
//		if (!lstprop.isEmpty()) {
//			for (Object[] objLst : lstprop) {
//				TopicModel obj = new TopicModel();
//				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
//				obj.setMenuName(StringHelperUtils.isNullString(objLst[1]));
//				// obj.setMenuName(StringHelperUtils.isNullString(objLst[2]));
//				lstMenuObj.add(obj);
//			}
//		}
//		return lstMenuObj;
//	}

//	@Override
//	public List<TopicModel> findSubMenuByRoleID(Long levelRoleVal) {
//		List<Object[]> lstprop = commonHomeMethodsRepo.findSubMenuByRoleID(levelRoleVal);
//		List<TopicModel> lstSubMenuObj = new ArrayList<>();
//		if (!lstprop.isEmpty()) {
//			for (Object[] objLst : lstprop) {
//				TopicModel obj = new TopicModel();
//				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
//				obj.setMenuKey(StringHelperUtils.isNullInt(objLst[1]));
//				obj.setRoleKey(StringHelperUtils.isNullInt(objLst[2]));
//				obj.setSubMenuName(StringHelperUtils.isNullString(objLst[3]));
//
//				obj.setControllerName(StringHelperUtils.isNullString(objLst[5]));
//				obj.setLinkName(StringHelperUtils.isNullString(objLst[6]));
//
//				lstSubMenuObj.add(obj);
//			}
//		}
//		return lstSubMenuObj;
//
//	}

	

//	// Helper method to convert MultipartFile to File
//	private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {
//		java.io.File convFile = new java.io.File(file.getOriginalFilename());
//		file.transferTo(convFile);
//		return convFile;
	// }

	
	public boolean processPdf(String pdfFilePath) throws IOException, DocumentException {
		PdfReader reader = null;
		try {
			reader = new PdfReader(pdfFilePath);
			// Your PDF processing logic here
			System.out.println("PDF processed successfully.");
			return true;

		} finally {
			if (reader != null) {
				reader.close();
			}
			return false;
		}
	}



	@Override
	public boolean isValidExcel(MultipartFile file) throws InvalidFormatException {
		 try (InputStream inputStream = file.getInputStream()) {
	            Workbook workbook = WorkbookFactory.create(inputStream);
	            // Check if the workbook has at least one sheet
	            return workbook.getNumberOfSheets() > 0;
	        } catch (IOException e) {
	            return false;
	        }
	    
	}

	@Override
	public String extractTextFromXlsx(InputStream inputStream) throws IOException {
		StringBuilder text = new StringBuilder();
	    try (Workbook workbook = new XSSFWorkbook(inputStream)) {
	        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
	            Sheet sheet = workbook.getSheetAt(i);
	            for (Row row : sheet) {
	                for (Cell cell : row) {
	                    text.append(cell.toString()).append(" ");
	                }
	            }
	        }
	    }
	    return text.toString();
	}
	

	@Override
	public boolean isValidCSV(MultipartFile file) {
		 try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
		         BufferedReader br = new BufferedReader(reader)) {
		        
		        // Check if the file has at least one line
		        String firstLine = br.readLine();
		        return firstLine != null && !firstLine.isEmpty();
		    } catch (IOException e) {
		        return false;
		    }
		}

	@Override
	public String extractTextFromCsv(InputStream inputStream) throws IOException{
		 StringBuilder text = new StringBuilder();
		    try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
		        String[] line;
		        while ((line = reader.readNext()) != null) {
		            for (String cell : line) {
		                text.append(cell).append(" ");
		            }
		        }
		    } catch (CsvValidationException e) {
		        e.printStackTrace();
		        throw new IOException("Error reading CSV file", e);
		    }
		    return text.toString();
		}

	@Override
	public boolean detectMaliciousContent(String content) {
		if (content.isEmpty()) {
		        return false;
		    }

		String[] suspiciousPatterns = { "eval", // Use of eval function
				"document.write", // Writing to document
				"unescape", // Unescaping strings (often used in obfuscation)
				"<script>", // Inline scripts
				"</script>", // Closing script tags
				"window.location", // Redirects
				"XMLHttpRequest", // AJAX requests
				"alert(", // Alerts
				"confirm(", // Confirm dialogs
				"prompt(", // Prompt dialogs
				"setTimeout(", // Timers
				"setInterval(", // Intervals
				"document.location", // Document location
				"location.href", // Location href
				"document.cookie", // Accessing cookies
				"document.domain" // Document domain
		};

		// Regular expressions for HTML tags
		String[] htmlTags = { "<[^>]*>", // Any HTML tag
				"</[^>]*>", // Closing HTML tag
				"<script[^>]*>", // Opening script tag
				"</script>", // Closing script tag
				"<iframe[^>]*>", // Opening iframe tag
				"</iframe>" // Closing iframe tag
		};
		for (String pattern : suspiciousPatterns) {
			if (content.contains(pattern)) {
				return true;
			}
		}
		// Check for HTML tags
		for (String tag : htmlTags) {
			if (content.matches("(?s).*" + tag + ".*")) {
				return true;
			}
		}

		    return false;
		}

	@Override
	public DistrictMaster getAllDistrictDistrictCode(long long1) {
		// TODO Auto-generated method stub
		return null;
	}
	}

