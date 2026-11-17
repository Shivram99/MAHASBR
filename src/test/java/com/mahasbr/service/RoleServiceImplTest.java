package com.mahasbr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mahasbr.entity.Role;
import com.mahasbr.repository.RoleMenuRepository;
import com.mahasbr.repository.RoleRepository;
import com.mahasbr.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleMenuRepository roleMenuRepository;

	private RoleServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new RoleServiceImpl(roleRepository, userRepository, roleMenuRepository);
	}

	private static Role role(long id, String name) {
		Role role = new Role();
		role.setId(id);
		role.setName(name);
		return role;
	}

	// ---------- name rules ----------

	@Test
	void normalizeAddsPrefixAndUppercases() {
		assertEquals("ROLE_AUDITOR", RoleServiceImpl.normalizeRoleName("auditor"));
		assertEquals("ROLE_AUDITOR", RoleServiceImpl.normalizeRoleName("  role_auditor "));
		assertEquals("ROLE_DATA_ENTRY", RoleServiceImpl.normalizeRoleName("Data Entry"));
	}

	@Test
	void normalizeRejectsBlankAndBadCharacters() {
		assertThrows(IllegalArgumentException.class, () -> RoleServiceImpl.normalizeRoleName(null));
		assertThrows(IllegalArgumentException.class, () -> RoleServiceImpl.normalizeRoleName("   "));
		assertThrows(IllegalArgumentException.class, () -> RoleServiceImpl.normalizeRoleName("admin<script>"));
		assertThrows(IllegalArgumentException.class, () -> RoleServiceImpl.normalizeRoleName("x".repeat(60)));
	}

	// ---------- create ----------

	@Test
	void createSavesNormalizedName() {
		when(roleRepository.existsByNameIgnoreCase("ROLE_AUDITOR")).thenReturn(false);
		when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

		Role request = new Role();
		request.setId(999L);          // a client-supplied id must be ignored
		request.setName("auditor");

		Role saved = service.createRole(request);

		assertEquals("ROLE_AUDITOR", saved.getName());
		assertEquals(null, saved.getId());
	}

	@Test
	void createRejectsDuplicate() {
		when(roleRepository.existsByNameIgnoreCase("ROLE_AUDITOR")).thenReturn(true);
		Role request = new Role();
		request.setName("auditor");

		assertThrows(IllegalArgumentException.class, () -> service.createRole(request));
	}

	// ---------- update ----------

	@Test
	void builtInRoleCannotBeRenamed() {
		when(roleRepository.findById(5L)).thenReturn(Optional.of(role(5L, "ROLE_DES_DISTRICT")));
		Role request = new Role();
		request.setName("ROLE_SOMETHING_ELSE");

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.updateRole(5L, request));
		assertEquals("Built-in role ROLE_DES_DISTRICT cannot be renamed.", ex.getMessage());
		verify(roleRepository, never()).save(any());
	}

	@Test
	void customRoleCanBeRenamed() {
		when(roleRepository.findById(20L)).thenReturn(Optional.of(role(20L, "ROLE_AUDITOR")));
		when(roleRepository.existsByNameIgnoreCase("ROLE_INSPECTOR")).thenReturn(false);
		when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));
		Role request = new Role();
		request.setName("inspector");

		assertEquals("ROLE_INSPECTOR", service.updateRole(20L, request).getName());
	}

	// ---------- delete ----------

	@Test
	void builtInRoleCannotBeDeleted() {
		when(roleRepository.findById(1L)).thenReturn(Optional.of(role(1L, "ROLE_ADMIN")));

		assertThrows(IllegalArgumentException.class, () -> service.deleteRole(1L));
		verify(roleRepository, never()).delete(any());
	}

	@Test
	void roleInUseCannotBeDeleted() {
		when(roleRepository.findById(20L)).thenReturn(Optional.of(role(20L, "ROLE_AUDITOR")));
		when(userRepository.existsByRoles_Id(20L)).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> service.deleteRole(20L));
		verify(roleRepository, never()).delete(any());
	}

	@Test
	void unusedCustomRoleIsDeletedWithItsMenuLinks() {
		Role auditor = role(20L, "ROLE_AUDITOR");
		when(roleRepository.findById(20L)).thenReturn(Optional.of(auditor));
		when(userRepository.existsByRoles_Id(20L)).thenReturn(false);

		service.deleteRole(20L);

		verify(roleMenuRepository).deleteByRole(auditor);
		verify(roleRepository).delete(auditor);
	}
}
