package com.mahasbr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.MstMenuModel;
import com.mahasbr.dto.MstMenuRoleMappingModel;
import com.mahasbr.dto.MstRoleModel;
import com.mahasbr.dto.MstSubMenuModel;
import com.mahasbr.dto.TopicModel;
import com.mahasbr.entity.Role;
import com.mahasbr.repository.CommonHomeMethodsRepo;
import com.mahasbr.util.StringHelperUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Transactional
@Service
public class CommonHomeMethodsServiceImpl implements CommonHomeMethodsService {
	// protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	CommonHomeMethodsRepo commonHomeMethodsRepo;

	@Cacheable(value = "menus", key = "#levelRoleVal + '_' + #lang")
	@Override
	public List<TopicModel> findMenuNameByRoleID(Set<Role> levelRoleVal, String lang) {
		Set<Long> roleIds = levelRoleVal.stream().map(Role::getId) // use getId() because that's your PK
				.collect(Collectors.toSet());
		List<Object[]> lstprop = commonHomeMethodsRepo.findMenuNameByRoleID(roleIds);
		List<TopicModel> lstMenuObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				TopicModel obj = new TopicModel();
				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
				if (lang.equals("en")) {
					obj.setMenuName(StringHelperUtils.isNullString(objLst[1]));
				} else {
					obj.setMenuName(StringHelperUtils.isNullString(objLst[2]));
				}
				obj.setIcon(StringHelperUtils.isNullString(objLst[3]));
				lstMenuObj.add(obj);
			}
		}
		return lstMenuObj;
	}

	@Cacheable(value = "submenus", key = "#levelRoleVal + '_' + #lang")
	@Override
	public List<TopicModel> findSubMenuByRoleID(Set<Role> levelRoleVal, String lang) {
		Set<Long> roleIds = levelRoleVal.stream().map(Role::getId) // use getId() because that's your PK
				.collect(Collectors.toSet());
		List<Object[]> lstprop = commonHomeMethodsRepo.findSubMenuByRoleID(roleIds);
		List<TopicModel> lstSubMenuObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				TopicModel obj = new TopicModel();
				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
				obj.setMenuKey(StringHelperUtils.isNullInt(objLst[1]));
				obj.setRoleKey(StringHelperUtils.isNullInt(objLst[2]));
				if (lang.equals("en")) {
					obj.setSubMenuName(StringHelperUtils.isNullString(objLst[3]));
				} else {
					obj.setSubMenuName(StringHelperUtils.isNullString(objLst[4]));
				}

				obj.setControllerName(StringHelperUtils.isNullString(objLst[5]));
				obj.setLinkName(StringHelperUtils.isNullString(objLst[6]));

				lstSubMenuObj.add(obj);
			}
		}
		return lstSubMenuObj;

	}

	@Override
	public List<MstMenuModel> findAllMenu(String language) {

		List<Object[]> lstprop = commonHomeMethodsRepo.findAllMenu();
		List<MstMenuModel> lstObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				MstMenuModel obj = new MstMenuModel();
				obj.setMenuId(StringHelperUtils.isNullInt(objLst[0]));
				obj.setMenuCode(StringHelperUtils.isNullInt(objLst[0]));
				obj.setMenuNameEnglish(StringHelperUtils.isNullString(objLst[2]));
				obj.setMenuNameMarathi(StringHelperUtils.isNullString(objLst[3]));
				obj.setIsActive(StringHelperUtils.isNullInt(Integer.parseInt(String.valueOf(objLst[4]))));
				obj.setIcon(StringHelperUtils.isNullString(objLst[5]));
				lstObj.add(obj);
			}
		}
		return lstObj;
	}

	@Override
	public List<MstRoleModel> findAllRole(String language) {

		List<Object[]> lstprop = commonHomeMethodsRepo.findAllRole();
		List<MstRoleModel> lstObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				MstRoleModel obj = new MstRoleModel();
				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
				obj.setRoleId(StringHelperUtils.isNullInt(objLst[1]));
				obj.setRoleName(StringHelperUtils.isNullString(objLst[2]));
				obj.setRoleDesc(StringHelperUtils.isNullString(objLst[3]));
				obj.setIsActive(StringHelperUtils.isNullInt(Integer.parseInt(String.valueOf(objLst[4]))));
				lstObj.add(obj);
			}
		}
		return lstObj;
	}

	@Override
	public List<MstSubMenuModel> findAllSubMenu(String language) {

		List<Object[]> lstprop = commonHomeMethodsRepo.findAllSubMenu();
		List<MstSubMenuModel> lstObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				MstSubMenuModel obj = new MstSubMenuModel();
				obj.setSubMenuCode(StringHelperUtils.isNullInt(objLst[0]));
				obj.setSubMenuId(StringHelperUtils.isNullInt(objLst[0])); // key for edit sub menu
				obj.setRoleName(StringHelperUtils.isNullString(objLst[1]));
				obj.setMenuName(StringHelperUtils.isNullString(objLst[2]));
				obj.setSubMenuEnglish(StringHelperUtils.isNullString(objLst[3]));
				obj.setSubMenuMarathi(StringHelperUtils.isNullString(objLst[4]));
				obj.setControllerName(StringHelperUtils.isNullString(objLst[5]));
				obj.setLinkName(StringHelperUtils.isNullString(objLst[6]));
				obj.setIsActive(StringHelperUtils.isNullInt(Integer.parseInt(String.valueOf(objLst[7]))));
				obj.setMenuCode(StringHelperUtils.isNullInt(objLst[8]));
				obj.setRoleId(StringHelperUtils.isNullInt(objLst[9]));
				obj.setIcon(StringHelperUtils.isNullString(objLst[10]));
				lstObj.add(obj);
			}
		}
		return lstObj;
	}

	@Override
	public List<MstMenuRoleMappingModel> findAllMenuRoleMapping(String language) {

		List<Object[]> lstprop = commonHomeMethodsRepo.findAllMenuRoleMapping();
		List<MstMenuRoleMappingModel> lstObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				MstMenuRoleMappingModel obj = new MstMenuRoleMappingModel();
				obj.setMenuMapId(StringHelperUtils.isNullInt(objLst[0]));
				if (language.equals("en")) {
					obj.setMenuName(StringHelperUtils.isNullString(objLst[1]));
				} else {
					obj.setMenuName(StringHelperUtils.isNullString(objLst[2]));
				}
				obj.setRoleName(StringHelperUtils.isNullString(objLst[3]));
				obj.setIsActive(StringHelperUtils.isNullInt(Integer.parseInt(String.valueOf(objLst[4]))));
				obj.setMenuCode(StringHelperUtils.isNullInt(objLst[5]));
				obj.setRoleId(StringHelperUtils.isNullInt(objLst[6]));
				lstObj.add(obj);
			}
		}
		return lstObj;
	}

//	@Override
//	public List<MstRoleEntity> findAllRole() {
//		// TODO Auto-generated method stub
//		return mstRoleRepo.findAll();
//	}
//
//	@Override
//	public MstRoleEntity findRole(int roleId) {
//		// TODO Auto-generated method stub
//		return mstRoleRepo.findByRoleId(roleId);
//	}
//
//	public int saveMstRole(MstRoleEntity mstRoleEntity) {
//		MstRoleEntity mstRole = new MstRoleEntity();
//		int saveId = 0;
//		if (mstRoleEntity.getRoleName() != null) {
//			mstRole.setRoleName(mstRoleEntity.getRoleName().toUpperCase());
//			mstRole.setRoleDescription(mstRoleEntity.getRoleDescription().toUpperCase());
//			List<MstRoleEntity> mstroleobj = mstRoleRepo.findAll();
//			int role = 0;
//			for (MstRoleEntity mstRoleEntity1 : mstroleobj) {
//				role = mstRoleEntity1.getRoleId();
//			}
//			mstRole.setRoleId(role + 1);
//			mstRole.setIsActive('1');
//			// mstRole.setCreatedUserId(1l);
//			// mstRole.setCreatedDate(new Date());
//			saveId = commonHomeMethodsRepo.saveMstRole(mstRole);
//		}
//
//		return saveId;
//	}
//
//	@Override
//	public MstRoleEntity deleteRoleById(int roleId) {
//		// TODO Auto-generated method stub
//		MstRoleEntity objDeptForReject = commonHomeMethodsRepo.findMstRoleId(roleId);
//		if (objDeptForReject != null) {
//			objDeptForReject.setIsActive('0'); // REJECTED
//			// objDeptForReject.setUpdatedDate(new Date());
//			commonHomeMethodsRepo.updateMstRoleStatus(objDeptForReject);
//		}
//		return objDeptForReject;
//	}
//
//	@Override
//	public String editRoleSave(@Valid MstRoleEntity mstRoleEntity) {
//		// TODO Auto-generated method stub
//
//		MstRoleEntity objrole = commonHomeMethodsRepo.findroleById(mstRoleEntity.getRoleId());
//		if (objrole != null) {
//			// objbank.setBankCode(mstBankEntity.getBankCode());
//			objrole.setRoleName(mstRoleEntity.getRoleName());
//			objrole.setRoleDescription(mstRoleEntity.getRoleDescription());
//			objrole.setIsActive(mstRoleEntity.getIsActive());
//
//			// objrole.setUpdatedDate(new Date());
//			commonHomeMethodsRepo.updaterole(objrole);
//		}
//		return "UPDATED";
//	}

	@Cacheable(value = "umenus", key = "#userName")
	@Override
	public List<TopicModel> findMenuMappedToUsername(String userName) {
		List<Object[]> lstprop = commonHomeMethodsRepo.findMenuMappedToUsername(userName);
		List<TopicModel> lstMenuObj = new ArrayList<>();
		if (!lstprop.isEmpty()) {
			for (Object[] objLst : lstprop) {
				TopicModel obj = new TopicModel();
				obj.setKey(StringHelperUtils.isNullInt(objLst[0]));
				obj.setMenuName(StringHelperUtils.isNullString(objLst[1]));
				obj.setIcon(StringHelperUtils.isNullString(objLst[3]));
				lstMenuObj.add(obj);
			}
		}
		return lstMenuObj;
	}

	@Cacheable(value = "usubmenusmenus", key = "#userName")
	@Override
	public List<TopicModel> findSubMenuByUserName(String userName) {
		List<Object[]> lstprop = commonHomeMethodsRepo.findSubMenuByUserName(userName);
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

}