package com.mahasbr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.service.BrnGenerationService;


@RestController
@RequestMapping("/user")
public class ImportExcelDataController {
	

	@Autowired
	BrnGenerationService brnGenerationService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(DetailsPageController.class);
	
	
	private static final String CSV_FILE_LOCATION = "D:\\SBR PROJECT\\Docs\\12_AKOLA_7Act Sample.xlsx";
	
	@PostMapping(value="/uploadExcel",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
	        try {
	        	brnGenerationService.uploadExcelFile(file);
	            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error uploading file");
	        }
	    }
	/*

	@GetMapping("/importExcelSheet")
	public @ResponseBody void readCSV() {
		
		
		String fileArr[]= {"D:\\SBR PROJECT\\Docs\\12_THANE_7Act_Sample_12_13.xlsx"};
		
		
		for(String path:fileArr) {
			Workbook workbook = null;
			try {
				workbook = WorkbookFactory.create(new File(path));

				// Retrieving the number of sheets in the Workbook
				logger.info("Number of sheets: ", workbook.getNumberOfSheets());
				// Print all sheets name

				int index = 0;
				for (Sheet sheet : workbook) {
					DataFormatter dataFormatter = new DataFormatter();
					// loop through all rows and columns and create Course object
					for (Row row : sheet) {
						if (index>9) {
							DetailsPage details = new DetailsPage();
							details.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
							details.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
							details.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
							details.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
							details.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
							details.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
							details.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
							
							if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
							details.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
							
							details.setSector(dataFormatter.formatCellValue(row.getCell(9)));
							details.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
							details.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
							details.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
							
							if(details.getNameOfEstateOwner()!=null  && details.getHouseNo()!=null  && details.getStreetName()!=null && details.getLocality()!=null &&  details.getTownVillage()!=null 
									&& details.getTaluka()!=null && details.getDistrict()!=null && details.getSector()!=null && details.getNameofAuth()!=null && details.getNameofAct()!=null
									) {
								DetailsPage dbDetailsPage=importExcelSheetService.findOrgData(details);
								if(dbDetailsPage!=null) {
									if(dbDetailsPage.getActRegNo()!=dbDetailsPage.getActRegNo()) {
										DuplicateOrgDetailsEntity duplicateOrgDetailsEntity=new DuplicateOrgDetailsEntity();
										
										duplicateOrgDetailsEntity.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
										duplicateOrgDetailsEntity.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
										duplicateOrgDetailsEntity.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
										duplicateOrgDetailsEntity.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
										duplicateOrgDetailsEntity.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
										duplicateOrgDetailsEntity.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
										duplicateOrgDetailsEntity.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
										
										if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
										duplicateOrgDetailsEntity.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
										
										duplicateOrgDetailsEntity.setSector(dataFormatter.formatCellValue(row.getCell(9)));
										duplicateOrgDetailsEntity.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
										duplicateOrgDetailsEntity.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
										duplicateOrgDetailsEntity.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
										
										duplicateOrgDetailsService.save(duplicateOrgDetailsEntity);
									}
								}else {
									
									String villageCensusCode=brnGenerationService.getVillageDtlByVillageName(details);  
									if(villageCensusCode!=null) {
										Long seqNo=brnGenerationService.findSeqNoByVillageCensusCode(villageCensusCode);
										
										VillageSequenceMaster villageSequenceMaster=new VillageSequenceMaster();
										villageSequenceMaster.setCensusVillageCode(villageCensusCode);
										villageSequenceMaster.setVillageName(villageCensusCode);
										villageSequenceMaster.setCurrentSequence(seqNo);
										Long saveId=brnGenerationService.saveNewSeqNo(villageSequenceMaster);
										
										
										String brnNumber =villageCensusCode+"0000"+String.format("%0d", seqNo.toString()); //String.format("%06d0000%06d", villageCensusCode, seqNo);
										details.setBrnNo(brnNumber);
										
										
										importExcelSheetService.save(details);	
									}else {
										BrnNoConcernEntity brnNoConcernEntity=new BrnNoConcernEntity();
										brnNoConcernEntity.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
										brnNoConcernEntity.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
										brnNoConcernEntity.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
										brnNoConcernEntity.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
										brnNoConcernEntity.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
										brnNoConcernEntity.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
										brnNoConcernEntity.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
										
										if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
										brnNoConcernEntity.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
										
										brnNoConcernEntity.setSector(dataFormatter.formatCellValue(row.getCell(9)));
										brnNoConcernEntity.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
										brnNoConcernEntity.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
										brnNoConcernEntity.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
										brnNoConcernEntity.setRemark("village not found");
										
										brnNoConcernService.save(brnNoConcernEntity);
									}
									
									
								
								}
							}else {
								
								BrnNoConcernEntity brnNoConcernEntity=new BrnNoConcernEntity();
								
								brnNoConcernEntity.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
								brnNoConcernEntity.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
								brnNoConcernEntity.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
								brnNoConcernEntity.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
								brnNoConcernEntity.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
								brnNoConcernEntity.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
								brnNoConcernEntity.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
								
								if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
								brnNoConcernEntity.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
								
								brnNoConcernEntity.setSector(dataFormatter.formatCellValue(row.getCell(9)));
								brnNoConcernEntity.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
								brnNoConcernEntity.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
								brnNoConcernEntity.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
								brnNoConcernEntity.setRemark("Mandatory Parameter are missing");
								brnNoConcernService.save(brnNoConcernEntity);
							}
						}
						index++;
					}
				}

			} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
				logger.error(e.getMessage(), e);
			} finally {
				try {
					if (workbook != null)
						workbook.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}*/
}
