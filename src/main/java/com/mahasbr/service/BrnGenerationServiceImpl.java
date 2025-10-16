package com.mahasbr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.BrnNoConcernEntity;
import com.mahasbr.entity.DetailsPage;
import com.mahasbr.entity.VillageSequenceMaster;
import com.mahasbr.repository.BrnGenerationRepo;
import com.mahasbr.repository.BrnNoConcernRepository;
import com.mahasbr.repository.DetailsPageRepository;

@Service
public class BrnGenerationServiceImpl implements BrnGenerationService {
//
//	
//	private static final Logger logger = LoggerFactory.getLogger(BrnGenerationServiceImpl.class);
//	
//	@Autowired
//	BrnGenerationRepo brnGenerationRepo;
//	
//	
//	@Autowired
//	BrnNoConcernRepository brnNoConcernRepository;
//
//
//	@Autowired
//	DetailsPageRepository detailsPageRepository;
//	
//	
//	@Override
//	public String getVillageDtlByVillageName(DetailsPage details) {
//	//	String villageCensusName = null;
//		String villageCensusCode = null;
//		List<Object[]> lstObjectArr = brnGenerationRepo.getVillageDtlByVillageName(details);
//		if (lstObjectArr.size() > 0) {
//			for (Object[] objL : lstObjectArr) {
//				//villageCensusName = objL[0].toString();
//				villageCensusCode = objL[0].toString();
//				return villageCensusCode;
//			}
//		}
//		return null;
//	}
//
//	
//
//	@Override
//	public void uploadExcelFile(MultipartFile file) {
//			Workbook workbook = null;
//			try {
//				InputStream inputStream = file.getInputStream();
//				workbook = WorkbookFactory.create(inputStream);
//
//				//logger.info("Number of sheets: ", workbook.getNumberOfSheets());
//
//				int index = 0;
//				for (Sheet sheet : workbook) {
//					DataFormatter dataFormatter = new DataFormatter();
//					// loop through all rows and columns and create Course object
//					for (Row row : sheet) {
//						if (index>9) {
//							DetailsPage details = new DetailsPage();
//							details.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
//							details.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
//							details.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
//							details.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
//							details.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
//							details.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
//							details.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
//							
//							if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
//							details.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
//							
//							details.setSector(dataFormatter.formatCellValue(row.getCell(9)));
//							details.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
//							details.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
//							details.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
//							
//							if((details.getNameOfEstateOwner()!=""  && details.getHouseNo()!=""  && details.getStreetName()!="" && details.getLocality()!="" &&  details.getTownVillage()!="" 
//									&& details.getTaluka()!="" && details.getDistrict()!="" && details.getSector()!="" && details.getNameofAuth()!="" && details.getNameofAct()!=""
//									)) {
//								
//								DetailsPage dbDetailsPage=detailsPageRepository.getDetailsByColumn(details.getNameOfEstateOwner(),details.getHouseNo(), details.getStreetName(), details.getLocality(), details.getTownVillage(),
//										details.getTaluka(),details.getDistrict(), details.getSector(), details.getNameofAuth(),
//										
//										
//										
//										details.getNameofAct());
//								if(dbDetailsPage!=null) {
//									if(details.getActRegNo()!=dbDetailsPage.getActRegNo()) {
//										DuplicateOrgDetailsEntity duplicateOrgDetailsEntity=new DuplicateOrgDetailsEntity();
//										
//										duplicateOrgDetailsEntity.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
//										duplicateOrgDetailsEntity.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
//										duplicateOrgDetailsEntity.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
//										duplicateOrgDetailsEntity.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
//										duplicateOrgDetailsEntity.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
//										duplicateOrgDetailsEntity.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
//										duplicateOrgDetailsEntity.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
//										
//										if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
//										duplicateOrgDetailsEntity.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
//										
//										duplicateOrgDetailsEntity.setSector(dataFormatter.formatCellValue(row.getCell(9)));
//										duplicateOrgDetailsEntity.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
//										duplicateOrgDetailsEntity.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
//										duplicateOrgDetailsEntity.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
//										
//										duplicateOrgDetailsRepository.save(duplicateOrgDetailsEntity);
//										
//									}
//									
//								}else {
//									
//									String villageCensusCode=getVillageDtlByVillageName(details);  
//									if(villageCensusCode!=null) {
//										Long seqNo= brnGenerationRepo.findSeqByVillageCensusCode(villageCensusCode);
//										seqNo=seqNo+1;
//										
//										VillageSequenceMaster villageSequenceMaster=new VillageSequenceMaster();
//										villageSequenceMaster.setCensusVillageCode(villageCensusCode);
//										villageSequenceMaster.setVillageName(villageCensusCode);
//										villageSequenceMaster.setCurrentSequence(seqNo);
//										Long saveId=brnGenerationRepo.saveNewSeqNo(villageSequenceMaster);
//										
//										String sixDigitSeqNo =String.format("%06d",seqNo.intValue());
//										
//										
//										String brnNo=villageCensusCode+"0000"+sixDigitSeqNo;
//										
//										if(brnNo.length()>16) {
//											System.out.println("brnNo"+brnNo);
//										}
//										
//										details.setBrnNo(brnNo);
//										
//										detailsPageRepository.save(details);	
//									}else {
//										BrnNoConcernEntity brnNoConcernEntity=new BrnNoConcernEntity();
//										brnNoConcernEntity.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
//										brnNoConcernEntity.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
//										brnNoConcernEntity.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
//										brnNoConcernEntity.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
//										brnNoConcernEntity.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
//										brnNoConcernEntity.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
//										brnNoConcernEntity.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
//										
//										if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
//										brnNoConcernEntity.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
//										
//										brnNoConcernEntity.setSector(dataFormatter.formatCellValue(row.getCell(9)));
//										brnNoConcernEntity.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
//										brnNoConcernEntity.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
//										brnNoConcernEntity.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
//										brnNoConcernEntity.setRemark("village not found");
//										
//										brnNoConcernRepository.save(brnNoConcernEntity);
//									}
//									
//									
//								
//								}
//							}else {
//								
//								BrnNoConcernEntity brnNoConcernEntity=new BrnNoConcernEntity();
//								
//								brnNoConcernEntity.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
//								brnNoConcernEntity.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
//								brnNoConcernEntity.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
//								brnNoConcernEntity.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
//								brnNoConcernEntity.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
//								brnNoConcernEntity.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
//								brnNoConcernEntity.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
//								
//								if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
//								brnNoConcernEntity.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
//								
//								brnNoConcernEntity.setSector(dataFormatter.formatCellValue(row.getCell(9)));
//								brnNoConcernEntity.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
//								brnNoConcernEntity.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
//								brnNoConcernEntity.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
//								brnNoConcernEntity.setRemark("Mandatory Parameter are missing");
//								brnNoConcernRepository.save(brnNoConcernEntity);
//							}
//						}
//						index++;
//					}
//				}
//
//			} catch (EncryptedDocumentException  | IOException e) {
//				logger.error(e.getMessage(), e);
//			} finally {
//				try {
//					if (workbook != null)
//						workbook.close();
//				} catch (IOException e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//	}
//
//
//
//
//
}
