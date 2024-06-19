package com.mahasbr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
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

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommonServiceImpl implements CommonService {
	private static final Logger logger = LoggerFactory.getLogger(CircularServiceImpl.class);
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
	public List<DistrictMaster> getAllDistrict(){
		return districtMasterRepository.findAll();
	}

	// for Get All District List by Districtcode
	@Override
	public DistrictMaster getAllDistrictDistrictCode(long censusDistrictCode) throws Exception {
		 Optional<DistrictMaster> districtCode = districtMasterRepository.findById(censusDistrictCode);
	        if (districtCode.isPresent())
	            return districtCode.get();
	        else {

	            throw new Exception("*** Id is not present ***");
	       }
	}

	@Override
	public List<TalukaMaster> getAllTalukaByDistrictCode(long censusDistrictCode) {
		 List<TalukaMaster> taluka= talukaMasterRepository.findBycensusDistrictCode(censusDistrictCode);		 
		return taluka;
	}

	@Override
	public List<VillageMaster> getAllVillageTalukaCode(Long censusTalukaCode) {
		 List<VillageMaster> village= villageMasterRepository.findBycensusTalukaCode(censusTalukaCode);	
		return village;
	}


	@Override
	public List<TopicModel> findMenuNameByRoleID(Long levelRoleVal) {

		List<Object[]> lstprop = commonHomeMethodsRepo.findMenuNameByRoleID(levelRoleVal);
		List<TopicModel> lstMenuObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				TopicModel obj = new TopicModel();
				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
				obj.setMenuName(StringHelperUtils.isNullString(objLst[1]));
				//	obj.setMenuName(StringHelperUtils.isNullString(objLst[2]));
				lstMenuObj.add(obj);
			}
		}
		return lstMenuObj;
	}

	@Override
	public List<TopicModel> findSubMenuByRoleID(Long levelRoleVal) {
		List<Object[]> lstprop = commonHomeMethodsRepo.findSubMenuByRoleID(levelRoleVal);
		List<TopicModel> lstSubMenuObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				TopicModel obj = new TopicModel();
				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
				obj.setMenuKey(StringHelperUtils.isNullInt(objLst[1]));
				obj.setRoleKey(StringHelperUtils.isNullInt(objLst[2]));
					obj.setSubMenuName(StringHelperUtils.isNullString(objLst[3]));
					
				obj.setControllerName(StringHelperUtils.isNullString(objLst[5]));
				obj.setLinkName(StringHelperUtils.isNullString(objLst[6]));

				lstSubMenuObj.add(obj);
			}
		}
		return lstSubMenuObj;

	}

	@Override
	public List<VillageMaster> getAllVillageTalukaCode(long censusTalukaCode) {
		// TODO Auto-generated method stub
		return null;
	}

	
		  // Helper method to convert MultipartFile to File
	    private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {
	        java.io.File convFile = new java.io.File(file.getOriginalFilename());
	        file.transferTo(convFile);
	        return convFile;
	    }

	
	    

	    @Override
		public boolean isSafe(File f) {
			boolean safeState = false;
			try {
				if ((f != null) && f.exists()) {
					// Load stream in PDF parser
					// If the stream is not a PDF then exception will be throwed
					// here and safe state will be set to FALSE
					PdfReader reader = new PdfReader(f.getAbsolutePath());
					// Check 1:
					// Detect if the document contains any JavaScript code
					String jsCode = reader.getJavaScript();
					if (jsCode == null) {
						// OK no JS code then when pass to check 2:
						// Detect if the document has any embedded files
						PdfDictionary root = reader.getCatalog();
						PdfDictionary names = root.getAsDict(PdfName.NAMES);
						PdfArray namesArray = null;
						if (names != null) {
							PdfDictionary embeddedFiles = names.getAsDict(PdfName.EMBEDDEDFILES);
							namesArray = embeddedFiles.getAsArray(PdfName.NAMES);
						}
						// Get safe state from number of embedded files
						safeState = ((namesArray == null) || namesArray.isEmpty());
					}
				}
			} catch (Exception e) {
				safeState = false;
				logger.warn("Error during Pdf file analysis !", e);
			}
			return safeState;
		}


		@Override
		public void safelyRemoveFile(Path p) {
	        try {
	            if (p != null) {
	                // Remove temporary file
	                if (!Files.deleteIfExists(p)) {
	                    // If remove fail then overwrite content to sanitize it
	                    Files.write(p, "-".getBytes("utf8"), StandardOpenOption.CREATE);
	                }
	            }
	        } catch (Exception e) {
	        	logger.warn("Cannot safely remove file !", e);
	        }
	    }


		



}
