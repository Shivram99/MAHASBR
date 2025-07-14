package com.mahasbr.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mahasbr.entity.User;
import com.mahasbr.repository.UserRepository;

public class CommonMethod {
	
	@Autowired
	UserRepository userRepository ;
	
	public Long getLoginUsernameId() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("aasasaasa22222@@@@@@@@" +username);
		Optional<User>  user= userRepository.findByUsername(username);
		
		if(user.isPresent()) {
			
			return user.get().getId();
		}
		return 0l;
	}

	}
	
