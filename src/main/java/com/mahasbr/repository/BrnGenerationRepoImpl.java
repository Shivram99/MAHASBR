package com.mahasbr.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.entity.VillageSequenceMaster;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository
public class BrnGenerationRepoImpl implements BrnGenerationRepo {

	@PersistenceContext
	EntityManager enManager;

	@Override
	public List<Object[]> getVillageDtlByVillageName(DetailsPage details) {
		String hql = "select c.CENSUS_VILLAGE_CODE,c.VILLAGE_NAME from district_master a "
				+ "inner join taluka_master  b on a.census_district_code=b.census_district_code "
				+ "inner join VILLAGE_MASTER c on c.census_taluka_code=b.census_taluka_code "
				+ "where Upper(a.district_name)=Upper('" + details.getDistrict() + "') and Upper(b.taluka_name)=Upper('" + details.getTaluka()
				+ "') and Upper(c.village_name)=Upper('" + details.getTownVillage() + "') " + "";
		Session session = enManager.unwrap(Session.class);
		Query query = session.createNativeQuery(hql);
		return query.list();
	}

	@Override
	public Long findSeqByVillageCensusCode(String villageCensusCode) {
		String hql = "SELECT CURRENT_SEQUENCE FROM VILLAGE_SEQUENCE WHERE CENSUS_VILLAGE_CODE = :villageCensusCode";
		Session session = enManager.unwrap(Session.class);
		Query query = session.createNativeQuery(hql);
		query.setParameter("villageCensusCode", villageCensusCode);
		Object result = query.uniqueResult();
		if (result != null) {
			return (Long) result;
		} else {
			return 1l;
		}
	}

	@Override
	public Long saveNewSeqNo(VillageSequenceMaster villageSequenceMaster) {
		Session session = enManager.unwrap(Session.class);
		return (Long) session.save(villageSequenceMaster);
	}

}
