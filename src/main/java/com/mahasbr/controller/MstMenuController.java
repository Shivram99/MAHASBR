package com.mahasbr.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.model.MstMenuModel;
import com.mahasbr.service.MstMenuService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class MstMenuController {
	@Autowired
	MstMenuService mstMenuService;

	@PostMapping("/saveMenu")
	public String saveMenu(@RequestBody MstMenuModel mstMenuModel, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model, Locale locale, HttpSession session) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("language", locale.getLanguage());
			return "/views/mst-menu"; /* Return to HTML Page */
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User messages = new User();
		messages.setId(1l);
		messages.setUsername("ADMIN");
		long afterSaveId = mstMenuService.saveMenu(mstMenuModel, messages);
		if (afterSaveId > 0) {
			redirectAttributes.addFlashAttribute("message", "SUCCESS");
		}
		model.addAttribute("language", locale.getLanguage());
		return "Added Menu Suscessfully"; /* redirects to controller URL */
	}

}
