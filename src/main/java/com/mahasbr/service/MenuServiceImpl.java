package com.mahasbr.service;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.dto.MenuCreateDTO;
import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Menu;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.RoleMenu;
import com.mahasbr.mapper.MenuMapper;
import com.mahasbr.model.ERole;
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
        List<Menu> rootMenus = menuRepository.findByParentIsNullAndActiveTrueOrderBySequenceAsc();
        return rootMenus.stream()
                .map(this::convertRecursive)
                .toList();
    }

    private MenuDTO convertRecursive(Menu menu) {
        MenuDTO dto = mapper.toDto(menu);

        menu.getChildren().stream()
                .filter(Menu::getActive)
                .sorted(Comparator.comparing(Menu::getSequence))
                .forEach(child -> dto.getChildren().add(convertRecursive(child)));

        return dto;
    }

    @Override
    public MenuDTO getMenuById(Long id) {
        return mapper.toDto(
                menuRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Menu not found"))
        );
    }

    /* =====================================================
       CREATE MENU
       ===================================================== */
    @Override
    public MenuDTO createMenu(MenuCreateDTO dto) {

        Menu menu = new Menu();

        // Parent handling
        if (dto.getParentId() != null) {
            Menu parent = menuRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NoSuchElementException("Parent menu not found"));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }

        menu.setNameEn(dto.getNameEn());
        menu.setNameMr(dto.getNameMr());
        menu.setRoute(dto.getRoute());
        menu.setIcon(dto.getIcon());
        menu.setMenuType(dto.getMenuType());
        menu.setSequence(dto.getSequence());
        menu.setActive(dto.getActive() != null ? dto.getActive() : true);

        return mapper.toDto(menuRepository.save(menu));
    }

    /* =====================================================
       UPDATE MENU
       ===================================================== */
    @Override
    public MenuDTO updateMenu(Long id, MenuCreateDTO dto) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Menu not found"));

        /* -----------------------------------------------
           HANDLE PARENT CHANGE (remove / update / validate)
           ----------------------------------------------- */
        if (dto.getParentId() == null) {
            // Remove parent
            menu.setParent(null);

        } else {

            if (Objects.equals(dto.getParentId(), id)) {
                throw new IllegalArgumentException("A menu cannot be its own parent");
            }

            // Prevent cycle: don't allow a descendant to be assigned as parent
            if (isDescendant(id, dto.getParentId())) {
                throw new IllegalArgumentException("Cannot assign a descendant as parent (cyclic hierarchy)");
            }

            Menu parent = menuRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NoSuchElementException("Parent menu not found"));

            menu.setParent(parent);
        }

        // Update normal fields
        menu.setNameEn(dto.getNameEn());
        menu.setNameMr(dto.getNameMr());
        menu.setRoute(dto.getRoute());
        menu.setIcon(dto.getIcon());
        menu.setSequence(dto.getSequence());
        menu.setActive(dto.getActive());

        return mapper.toDto(menuRepository.save(menu));
    }

    /* =====================================================
       HELPER – CHECK CYCLIC ASSIGNMENT
       ===================================================== */
    private boolean isDescendant(Long menuId, Long supposedParentId) {

        List<Menu> children = menuRepository.findChildren(menuId);

        for (Menu child : children) {
            if (Objects.equals(child.getId(), supposedParentId)) {
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
       ROLE → MENU MAPPING
       ===================================================== */
    @Override
    public List<MenuDTO> getMenusForRole(ERole role) {
        List<Menu> flatMenus = menuRepository.findMenusByRole(role);
        return buildTree(flatMenus);
    }

    private List<MenuDTO> buildTree(List<Menu> flat) {

        Map<Long, MenuDTO> map = new HashMap<>();
        List<MenuDTO> roots = new ArrayList<>();

        flat.forEach(m -> map.put(m.getId(), mapper.toDto(m)));

        for (Menu menu : flat) {
            if (menu.getParent() == null) {
                roots.add(map.get(menu.getId()));
            } else {
                MenuDTO parent = map.get(menu.getParent().getId());
                parent.getChildren().add(map.get(menu.getId()));
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
//        roleMenuRepository.deleteByRole_IdAndMenu_IdIn(roleId, menuId);
    }
}
