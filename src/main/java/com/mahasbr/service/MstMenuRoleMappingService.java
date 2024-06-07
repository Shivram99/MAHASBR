package com.mahasbr.service;

import com.mahasbr.entity.User;
import com.mahasbr.model.MstMenuRoleMappingModel;

public interface MstMenuRoleMappingService {

	long saveMenuRoleMapping(MstMenuRoleMappingModel mstMenuRoleMappingModel, User messages);

}
