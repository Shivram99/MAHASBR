package com.mahasbr.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.entity.Role;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.model.ERole;
import com.mahasbr.repository.RoleMenuRepository;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;

@Service
public class RoleServiceImpl implements RoleService {

	/**
	 * Built-in roles. Their names are written into the code (data-scope rules, location rules,
	 * frontend home pages and menus), so renaming or deleting one would silently break access.
	 */
	static final Set<String> SYSTEM_ROLES = Arrays.stream(ERole.values())
			.map(Enum::name)
			.collect(Collectors.toUnmodifiableSet());

	/** Spring's hasRole("X") checks for the authority "ROLE_X", so every role name needs this prefix. */
	private static final Pattern ROLE_NAME = Pattern.compile("^ROLE_[A-Z][A-Z0-9_]{1,44}$");

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final RoleMenuRepository roleMenuRepository;

	public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository,
			RoleMenuRepository roleMenuRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.roleMenuRepository = roleMenuRepository;
	}

	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Role saveOrUpdateRole(Role role) {
		// Route through the checked paths so nothing can bypass the rules below.
		return role.getId() == null ? createRole(role) : updateRole(role.getId(), role);
	}

	@Override
	public Optional<Role> findRoleById(Long id) {
		return roleRepository.findById(id);
	}

	@Override
	public Role getRoleById(Long id) {
		return roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
	}

	@Override
	public Role createRole(Role request) {
		String name = normalizeRoleName(request.getName());
		if (roleRepository.existsByNameIgnoreCase(name)) {
			throw new IllegalArgumentException("Role already exists: " + name);
		}

		Role role = new Role();   // don't save the request body as-is (it could carry an id)
		role.setName(name);
		return roleRepository.save(role);
	}

	@Override
	public Role updateRole(Long id, Role request) {
		Role existing = getRoleById(id);
		if (isSystemRole(existing)) {
			throw new IllegalArgumentException("Built-in role " + existing.getName() + " cannot be renamed.");
		}

		String name = normalizeRoleName(request.getName());
		if (!existing.getName().equalsIgnoreCase(name) && roleRepository.existsByNameIgnoreCase(name)) {
			throw new IllegalArgumentException("Role already exists: " + name);
		}

		existing.setName(name);
		return roleRepository.save(existing);
	}

	@Override
	@Transactional
	public void deleteRole(Long id) {
		Role existing = getRoleById(id);
		if (isSystemRole(existing)) {
			throw new IllegalArgumentException("Built-in role " + existing.getName() + " cannot be deleted.");
		}
		if (userRepository.existsByRoles_Id(id)) {
			throw new IllegalArgumentException(
					"Role " + existing.getName() + " is still assigned to users. Remove it from them first.");
		}

		roleMenuRepository.deleteByRole(existing);   // its menu links go with it
		roleRepository.delete(existing);
	}

	static boolean isSystemRole(Role role) {
		return role.getName() != null && SYSTEM_ROLES.contains(role.getName().toUpperCase());
	}

	/** "auditor", "Data Entry" or "role_auditor" become ROLE_AUDITOR, ROLE_DATA_ENTRY, ROLE_AUDITOR. */
	static String normalizeRoleName(String raw) {
		if (raw == null || raw.isBlank()) {
			throw new IllegalArgumentException("Role name is required.");
		}

		String name = raw.trim().toUpperCase().replaceAll("[\\s-]+", "_");
		if (!name.startsWith("ROLE_")) {
			name = "ROLE_" + name;
		}
		if (!ROLE_NAME.matcher(name).matches()) {
			throw new IllegalArgumentException(
					"Role name may contain only letters, numbers and underscores (max 50 characters).");
		}
		return name;
	}
}
