package com.mahasbr.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mahasbr.entity.MstMenuRoleMapping;

@Service
public interface MstMenuRoleMappingService{
	
    public List<MstMenuRoleMapping> getAllMappings() ;

    public MstMenuRoleMapping getMappingById(Long id);

    public MstMenuRoleMapping createMapping(MstMenuRoleMapping mapping);

    public MstMenuRoleMapping updateMapping(Long id, MstMenuRoleMapping mapping);
    public void deleteMapping(Long id);
}
