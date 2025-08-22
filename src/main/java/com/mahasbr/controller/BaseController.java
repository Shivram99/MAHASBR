package com.mahasbr.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.mahasbr.dto.TopicModel;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.service.CommonHomeMethodsService;

import jakarta.servlet.http.HttpServletRequest;

public class BaseController {
	
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private CommonHomeMethodsService commonHomeMethodsService;

	public void populateAuthorizedMenus(User messages, Model model) {
		Set<Role> levelRoleVal = messages.getRoles();

		List<TopicModel> userMenuList = commonHomeMethodsService.findMenuMappedToUsername(messages.getUsername());
		if (userMenuList.size() > 0) {
			model.addAttribute("menuList", userMenuList);
			List<TopicModel> subMenuList = commonHomeMethodsService.findSubMenuByUserName(messages.getUsername());
			model.addAttribute("subMenuList", subMenuList);
		} else {
			if (levelRoleVal != null && levelRoleVal.size() != 0) {
				List<TopicModel> menuList = commonHomeMethodsService.findMenuNameByRoleID(levelRoleVal, "en");
				List<TopicModel> subMenuList = commonHomeMethodsService.findSubMenuByRoleID(levelRoleVal, "en");
				model.addAttribute("menuList", menuList);
				model.addAttribute("subMenuList", subMenuList);
			}
		}

		model.addAttribute("levelRoleVal", levelRoleVal);
		
	}


}
