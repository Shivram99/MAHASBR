package com.mahasbr.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.dto.AssignMenuDTO;
import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Menu;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.RoleMenu;
import com.mahasbr.entity.User;
import com.mahasbr.entity.UserMenuEntity;
import com.mahasbr.mapper.MenuMapper;
import com.mahasbr.repository.MenuRepository;
import com.mahasbr.repository.RoleMenuRepository;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserMenuRepository;
import com.mahasbr.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuPermissionServiceImpl implements MenuPermissionService {

	private final RoleRepository roleRepository;
	private final RoleMenuRepository roleMenuRepository;
	private final UserRepository userRepository;
	private final UserMenuRepository userMenuRepository;
	private final MenuRepository menuRepository;
	private final MenuMapper mapper;

	@Override
	public void assignMenuToRole(AssignMenuDTO dto) {
		Role role = roleRepository.findById(dto.getRoleId())
				.orElseThrow(() -> new NoSuchElementException("Role not found"));
		List<Menu> menus = menuRepository.findAllById(dto.getMenuIds());

		if (menus.isEmpty()) {
			throw new NoSuchElementException("No menus found for the given IDs");
		}

		// Save each role-menu mapping
		for (Menu menu : menus) {
			RoleMenu rm = new RoleMenu();
			rm.setRole(role);
			rm.setMenu(menu);
			roleMenuRepository.save(rm);
		}
	}

	@Override
	public void removeMenuFromRole(AssignMenuDTO dto) {
		roleMenuRepository.deleteByRole_IdAndMenu_IdIn(dto.getRoleId(), dto.getMenuIds());
	}

	@Override
	public void assignMenuToUser(AssignMenuDTO dto) {
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new NoSuchElementException("User not found"));
//		Menu menu = menuRepository.findById(dto.getMenuId())
//				.orElseThrow(() -> new NoSuchElementException("Menu not found"));

		UserMenuEntity um = new UserMenuEntity();
		um.setUser(user);
//		um.setMenu(menu);
		userMenuRepository.save(um);
	}

	@Override
	public void removeMenuFromUser(AssignMenuDTO dto) {
		//userMenuRepository.deleteByUser_IdAndMenu_Id(dto.getUserId(), dto.getMenuId());
	}

	@Override
	public List<MenuDTO> getMenusForRole(Long roleId) {
		List<Menu> flat = menuRepository.findMenusByRoleId(roleId);
		return buildTree(flat);
	}

	@Override
	public List<MenuDTO> getMenusForUser(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));

		// ------------------------------
		// 1. Collect menus from ALL roles
		// ------------------------------
		Set<Menu> roleMenus = new HashSet<>();

		if (user.getRoles() != null) {
			for (Role role : user.getRoles()) {
				roleMenus.addAll(menuRepository.findMenusByRoleId(role.getId()));
			}
		}

		// ------------------------------
		// 2. Collect menus directly assigned to user
		// ------------------------------
		List<Menu> userMenus = menuRepository.findMenusByUserId(userId);

		// ------------------------------
		// 3. Merge into a unique set
		// ------------------------------
		Map<Long, Menu> mergedMap = new HashMap<>();

		for (Menu m : roleMenus) {
			mergedMap.put(m.getId(), m);
		}

		for (Menu m : userMenus) {
			mergedMap.put(m.getId(), m);
		}

		List<Menu> mergedMenus = new ArrayList<>(mergedMap.values());

		// ------------------------------
		// 4. Build hierarchical menu tree
		// ------------------------------
		return buildTree(mergedMenus);
	}

	// builds parent-child tree from flat list
	/* =====================================================
    BUILD HIERARCHY TREE FOR ROLE MENUS
    ===================================================== */
 private List<MenuDTO> buildTree(List<Menu> flat) {

     Map<Long, MenuDTO> dtoMap = new HashMap<>();
     List<MenuDTO> roots = new ArrayList<>();

     flat.forEach(menu -> dtoMap.put(menu.getId(), mapper.toDTO(menu)));

     for (Menu menu : flat) {

         MenuDTO dto = dtoMap.get(menu.getId());
         Long parentId = menu.getParent() != null ? menu.getParent().getId() : null;

         if (parentId == null || !dtoMap.containsKey(parentId)) {
             roots.add(dto);
         } else {
             MenuDTO parent = dtoMap.get(parentId);

             // Prevent duplicates inside children list
             if (parent.getChildren().stream().noneMatch(c -> c.getId().equals(dto.getId()))) {
                 parent.getChildren().add(dto);
             }
         }
     }

     roots.sort(Comparator.comparing(MenuDTO::getSequence));
     return roots;
 }
}
