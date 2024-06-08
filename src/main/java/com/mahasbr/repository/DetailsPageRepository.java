package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DetailsPage;

@Repository
public interface DetailsPageRepository extends JpaRepository<DetailsPage, Long> {

	@Query("FROM DetailsPage t WHERE t.nameOfEstateOwner=?1 and t.houseNo=?2 AND t.streetName=?3 AND t.locality=?4  AND t.taluka=?5  AND t.district=?6  AND t.sector=?7 AND t.pincode=?8 AND t.nameofAuth=?9  AND t.nameofAct=?10")
    DetailsPage getDetailsByColumn(String nameOfEstateOwner, String houseNo, String streetName, String locality,
        String townVillage, String taluka, String district, int pincode, String sector, String nameofAuth,
        String nameofAct);

}
