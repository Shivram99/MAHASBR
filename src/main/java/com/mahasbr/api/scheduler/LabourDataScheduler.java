package com.mahasbr.api.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mahasbr.api.entity.FactoryDataEntity;
import com.mahasbr.api.entity.ShopDataEntity;
import com.mahasbr.api.repository.FactoryDataRepository;
import com.mahasbr.api.repository.ShopDataRepository;
import com.mahasbr.api.service.LabourExternalApiService;

import reactor.core.scheduler.Schedulers;

@Component
public class LabourDataScheduler {

	private final LabourExternalApiService labourExternalApiService;
	private final ShopDataRepository shopRepo;
	private final FactoryDataRepository factoryRepo;

	public LabourDataScheduler(LabourExternalApiService labourExternalApiService, ShopDataRepository shopRepo,
			FactoryDataRepository factoryRepo) {
		this.labourExternalApiService = labourExternalApiService;
		this.shopRepo = shopRepo;
		this.factoryRepo = factoryRepo;
	}

	/**
	 * Scheduled Job to fetch Shops and Factories data from external system and save
	 * only new records into local database.
	 *
	 * Runs every 100 seconds (configured with fixedRate).
	 */
	@Scheduled(fixedRate = 100000)
	public void fetchDataJob() {
		labourExternalApiService.authenticate()
				.flatMapMany(token -> labourExternalApiService.getShopData(token).collectList()
						.zipWith(labourExternalApiService.getFactoryData(token).collectList()))
				.subscribeOn(Schedulers.boundedElastic()).subscribe(tuple -> {
					List<com.mahasbr.api.dto.ShopData> shops = tuple.getT1();
					List<com.mahasbr.api.dto.FactoryData> factories = tuple.getT2();

					System.out.println("✅ LabourDataScheduler Run at: " + LocalDateTime.now());
					System.out.println("Shops received: " + shops.size());
					System.out.println("Factories received: " + factories.size());

					// ---- Save only new Shops ----
					List<Integer> existingShopSrNos = shopRepo.findAllSrNos(); // returns List<Integer>
					List<ShopDataEntity> newShops = shops.stream()
							.filter(shop -> !existingShopSrNos.contains(shop.getSrNo())).map(shop -> {
								ShopDataEntity entity = new ShopDataEntity();
								entity.setSrNo(shop.getSrNo());
								entity.setNameOfEstablishmentOrOwner(shop.getNameOfEstablishmentOrOwner());
								entity.setStreetName(shop.getStreetName());
								entity.setLocality(shop.getLocality());
								entity.setPinCode(shop.getPinCode());
								entity.setTelephoneMobNumber(shop.getTelephoneMobNumber());
								entity.setEmailAddress(shop.getEmailAddress());
								entity.setHeadOfficeStreetName(shop.getHeadOfficeStreetName());
								entity.setHeadOfficeLocality(shop.getHeadOfficeLocality());
								entity.setHeadOfficePinCode(shop.getHeadOfficePinCode());
								entity.setHeadOfficeTelephoneMobNumber(shop.getHeadOfficeTelephoneMobNumber());
								entity.setHeadOfficeEmailAddress(shop.getHeadOfficeEmailAddress());
								entity.setTotalNumberOfPersonsWorking(shop.getTotalNumberOfPersonsWorking());
								entity.setNameOfAuthority(shop.getNameOfAuthority());
								entity.setDateOfRegistration(shop.getDateOfRegistration());
								entity.setDateOfDeregistrationExpiry(shop.getDateOfDeregistrationExpiry());
								return entity;
							}).collect(Collectors.toList());

					shopRepo.saveAll(newShops);
					System.out.println("New Shops saved: " + newShops.size());

					// ---- Save only new Factories ----
					List<Integer> existingFactorySrNos = factoryRepo.findAllSrNos(); // returns List<Integer>
					List<FactoryDataEntity> newFactories = factories.stream()
							.filter(factory -> !existingFactorySrNos.contains(factory.getSrNo())).map(factory -> {
								FactoryDataEntity entity = new FactoryDataEntity();
								entity.setSrNo(factory.getSrNo());
								entity.setNameOfEstablishmentOrOwner(factory.getNameOfEstablishmentOrOwner());
								entity.setStreetName(factory.getStreetName());
								entity.setLocality(factory.getLocality());
								entity.setPinCode(factory.getPinCode());
								entity.setTelephoneMobNumber(factory.getTelephoneMobNumber());
								entity.setEmailAddress(factory.getEmailAddress());
								entity.setHeadOfficeStreetName(factory.getHeadOfficeStreetName());
								entity.setHeadOfficeLocality(factory.getHeadOfficeLocality());
								entity.setHeadOfficePinCode(factory.getHeadOfficePinCode());
								entity.setHeadOfficeTelephoneMobNumber(factory.getHeadOfficeTelephoneMobNumber());
								entity.setHeadOfficeEmailAddress(factory.getHeadOfficeEmailAddress());
								entity.setYearOfStartOfOperation(factory.getYearOfStartOfOperation());
								entity.setTotalNumberOfPersonsWorking(factory.getTotalNumberOfPersonsWorking());
								entity.setTownVillage(factory.getTownVillage());
								entity.setTaluka(factory.getTaluka());
								entity.setDistrict(factory.getDistrict());
								entity.setAddrLandmark(factory.getAddrLandmark());
								entity.setNameOfAuthority(factory.getNameOfAuthority());
								entity.setApprovalDate(factory.getApprovalDate());
								entity.setDateOfDeregistrationExpiry(factory.getDateOfDeregistrationExpiry());
								return entity;
							}).collect(Collectors.toList());

					factoryRepo.saveAll(newFactories);
					System.out.println("New Factories saved: " + newFactories.size());
				});
	}
}
