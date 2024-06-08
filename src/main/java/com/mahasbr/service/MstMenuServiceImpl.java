package com.mahasbr.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.MstMenu;
import com.mahasbr.entity.MstMenuRoleMapping;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.repository.MstMenuRepository;
import com.mahasbr.repository.UserRepository;

@Service
public class MstMenuServiceImpl implements MstMenuService {
	
	@Autowired
    private MstMenuRepository repository;
	
	@Autowired
	private UserRepository userRepository;

    public List<MstMenu> getAllMenus() {
        return repository.findAll();
    }

    public MstMenu getMenuById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public MstMenu createMenu(MstMenu menu) {
        return repository.save(menu);
    }

    public MstMenu updateMenu(Long id, MstMenu menu) {
        if (repository.existsById(id)) {
            menu.setMenuId(id);
            return repository.save(menu);
        } else {
            return null; // Or handle the case where the menu with the given id doesn't exist
        }
    }

    public void deleteMenu(Long id) {
        repository.deleteById(id);
    }
    
    public List<MstMenu> getMenusByUserId(Long userId) {  // Step 1: Get the User's Role(s)
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // Handle case where user is not found
            return null;
        }

        Set<Role> roles = user.getRoles();

        // Step 2: Retrieve Menus
        List<MstMenu> menus = roles.stream()
                .flatMap(role -> role.getMappings().stream())
                .map(MstMenuRoleMapping::getMenu) // Access the associated menu
                .collect(Collectors.toList());

        return menus;}
}
