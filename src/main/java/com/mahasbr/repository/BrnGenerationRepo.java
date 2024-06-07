package com.mahasbr.repository;

import java.util.List;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.entity.VillageSequenceMaster;

public interface BrnGenerationRepo {

	List<Object[]> getVillageDtlByVillageName(DetailsPage details);

	Long findSeqByVillageCensusCode(String villageCensusCode);

	Long saveNewSeqNo(VillageSequenceMaster villageSequenceMaster);

}
