package com.mahasbr.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.dto.MenuCreateDTO;
import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Menu;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.RoleMenu;
import com.mahasbr.mapper.MenuMapper;
import com.mahasbr.repository.MenuRepository;
import com.mahasbr.repository.RoleMenuRepository;
import com.mahasbr.repository.RoleRepository;
@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final MenuMapper mapper;

    public MenuServiceImpl(MenuRepository menuRepository,
                           RoleRepository roleRepository,
                           RoleMenuRepository roleMenuRepository,
                           MenuMapper mapper) {
        this.menuRepository = menuRepository;
        this.roleRepository = roleRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.mapper = mapper;
    }

    /* =====================================================
       GET ALL MENUS – FULL TREE
       ===================================================== */
    @Override
    public List<MenuDTO> getAllMenus() {

        List<Menu> rootMenus = menuRepository.findRootMenusWithChildren();

        // remove any duplicated children (Hibernate safety)
        List<Menu> cleanMenus = rootMenus.stream()
                .map(this::removeDuplicates)
                .toList();

        // convert to DTO recursively
        return cleanMenus.stream()
                .map(this::convertRecursive)
                .toList();
    }


    private MenuDTO convertRecursive(Menu menu) {
        MenuDTO dto = mapper.toDTO(menu);

        dto.setChildren(
        	    menu.getChildren().stream()
        	        .sorted(Comparator.comparing(Menu::getSequence))
        	        .map(this::convertRecursive)
        	        .toList()
        	);
           return dto;
    }

    /* =====================================================
       GET MENU BY ID
       ===================================================== */
    @Override
    public MenuDTO getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Menu not found"));

        return mapper.toDTO(menu);
    }

    /* =====================================================
       CREATE MENU
       ===================================================== */
    @Override
    public MenuDTO createMenu(MenuCreateDTO dto) {

        Menu menu = new Menu();

        // Parent
        if (dto.getParentId() != null) {
            Menu parent = menuRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NoSuchElementException("Parent menu not found"));
            menu.setParent(parent);
        }

        menu.setNameEn(dto.getNameEn());
        menu.setNameMr(dto.getNameMr());
        menu.setRoute(dto.getRoute());
        menu.setIcon(dto.getIcon());
        menu.setMenuType(dto.getMenuType());
        menu.setSequence(dto.getSequence());
        menu.setActive(dto.getActive() != null ? dto.getActive() : true);

        return mapper.toDTO(menuRepository.save(menu));
    }

    /* =====================================================
       UPDATE MENU
       ===================================================== */
    @Override
    public MenuDTO updateMenu(Long id, MenuCreateDTO dto) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Menu not found"));

        // Parent handling
        if (dto.getParentId() == null) {
            menu.setParent(null);
        } else {

            if (dto.getParentId().equals(id)) {
                throw new IllegalArgumentException("Menu cannot be its own parent");
            }

            if (isDescendant(id, dto.getParentId())) {
                throw new IllegalArgumentException("Cannot assign a descendant as parent");
            }

            Menu parent = menuRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NoSuchElementException("Parent menu not found"));

            menu.setParent(parent);
        }

        // Normal fields
        menu.setNameEn(dto.getNameEn());
        menu.setNameMr(dto.getNameMr());
        menu.setRoute(dto.getRoute());
        menu.setIcon(dto.getIcon());
        menu.setSequence(dto.getSequence());
        menu.setActive(dto.getActive());

        return mapper.toDTO(menuRepository.save(menu));
    }

    /* =====================================================
       CHECK CYCLIC RELATIONSHIP
       ===================================================== */
    private boolean isDescendant(Long menuId, Long supposedParentId) {

        List<Menu> children = menuRepository.findChildren(menuId);

        for (Menu child : children) {

            if (child.getId().equals(supposedParentId)) {
                return true; // cycle found
            }

            if (isDescendant(child.getId(), supposedParentId)) {
                return true;
            }
        }

        return false;
    }

    /* =====================================================
       DELETE MENU
       ===================================================== */
    @Override
    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new NoSuchElementException("Menu not found");
        }
        menuRepository.deleteById(id);
    }

    /* =====================================================
       GET MENUS FOR MULTIPLE ROLES
       ===================================================== */
    @Override
    public List<MenuDTO> getMenusForRoles(Set<String> roleNames) {

        List<Menu> flatMenus = menuRepository.findMenusByRoleNames(roleNames);
//        List<Menu> flatMenus = menuRepository.findMenusByRoleNames();

        return buildTree(flatMenus);
    }

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


    /* =====================================================
       ROLE MENU ASSIGNMENT
       ===================================================== */
    @Override
    public void assignMenuToRole(Long menuId, Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found"));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("Menu not found"));

        RoleMenu rm = new RoleMenu();
        rm.setRole(role);
        rm.setMenu(menu);

        roleMenuRepository.save(rm);
    }

    @Override
    public void removeMenuFromRole(Long menuId, Long roleId) {
//        roleMenuRepository.deleteByRoleIdAndMenuId(roleId, menuId);
    }
    private Menu removeDuplicates(Menu menu) {

        if (menu.getChildren() == null) return menu;

        Map<Long, Menu> unique = new LinkedHashMap<>();

        for (Menu child : menu.getChildren()) {
            Menu cleaned = removeDuplicates(child);
            unique.put(child.getId(), cleaned);
        }

        menu.setChildren(new ArrayList<>(unique.values()));
        return menu;
    }
    private Menu removeDuplicateChildren(Menu menu) {

        if (menu.getChildren() == null) {
            return menu;
        }

        // Use LinkedHashMap to preserve order + remove duplicates by ID
        Map<Long, Menu> unique = new LinkedHashMap<>();

        for (Menu child : menu.getChildren()) {
            // Clean duplicates recursively
            Menu cleaned = removeDuplicateChildren(child);
            unique.put(child.getId(), cleaned);
        }

        // Replace children with unique list
        menu.setChildren(new ArrayList<>(unique.values()));
        return menu;
    }

	@Override
	public List<MenuDTO> getMenusForRole(Role role) {
		List<Menu> flatMenus = menuRepository.findMenusByRole(role);
		return buildTree(flatMenus);
	}


}
