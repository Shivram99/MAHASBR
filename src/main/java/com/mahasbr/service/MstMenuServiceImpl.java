package com.mahasbr.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.MstMenu;
import com.mahasbr.entity.User;
import com.mahasbr.model.MstMenuModel;
import com.mahasbr.repository.MstMenuRepo;

@Service
public class MstMenuServiceImpl implements MstMenuService {
	@Autowired
	MstMenuRepo mstMenuRepo;

	@Override
	public Long saveMenu(MstMenuModel mstMenuModel, User messages) {
		MstMenu objMenuEntity = new MstMenu();
		long saveId = 0;
		if (mstMenuModel.getMenuNameEnglish() != null) {
			objMenuEntity.setMenuNameEnglish(mstMenuModel.getMenuNameEnglish());
			objMenuEntity.setMenuNameMarathi(mstMenuModel.getMenuNameMarathi());
			objMenuEntity.setCreatedDateTime(new Date());
			objMenuEntity.setCreatedUserId(messages.getId());
			objMenuEntity.setIsActive("1");
			MstMenu save = mstMenuRepo.save(objMenuEntity);
			saveId = save.getMenuId();
		}

		return saveId;

	}

}
