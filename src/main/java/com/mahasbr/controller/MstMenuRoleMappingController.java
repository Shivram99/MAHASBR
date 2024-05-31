package com.mahasbr.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mahasbr.entity.User;
import com.mahasbr.model.MstMenuRoleMappingModel;
import com.mahasbr.service.MstMenuRoleMappingService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/developer")
public class MstMenuRoleMappingController {
	@Autowired
	MstMenuRoleMappingService mstMenuRoleMappingService;
	
	@PostMapping("/saveMenuRoleMapping")
	public String saveMenuRoleMapping(@ModelAttribute("mstMenuRoleMappingModel") @RequestBody MstMenuRoleMappingModel mstMenuRoleMappingModel,
									BindingResult bindingResult,RedirectAttributes redirectAttributes, Model model, Locale locale, HttpSession session) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("language", locale.getLanguage());
			return "/views/mst-menu-role-mapping"; /*Return to HTML Page*/
		} 
		User messages  = (User) session.getAttribute("MY_SESSION_MESSAGES");
		
		long afterSaveId = mstMenuRoleMappingService.saveMenuRoleMapping(mstMenuRoleMappingModel,messages);
		if(afterSaveId > 0) {
			redirectAttributes.addFlashAttribute("message","SUCCESS");
		}
		model.addAttribute("language", locale.getLanguage());
		return "redirect:/developer/mstMenuRoleMapping"; /*redirects to controller URL*/
	}
	
	
}
