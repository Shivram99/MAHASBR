package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.BrnNoConcernEntity;
import com.mahasbr.repository.BrnNoConcernRepository;

@Service
public class BrnNoConcernServiceImpl  implements BrnNoConcernService{
	
	@Autowired
	BrnNoConcernRepository brnNoConcernRepository;

	@Override
	public void save(BrnNoConcernEntity brnNoConcernEntity) {
		// TODO Auto-generated method stub
		brnNoConcernRepository.save(brnNoConcernEntity);
		
	}

}
