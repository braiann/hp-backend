package hp.server.app.tests.services;

import hp.server.app.HpServerApplication;
import hp.server.app.models.entity.Role;
import hp.server.app.models.repository.RoleRepository;
import hp.server.app.services.impl.RoleServiceImpl;
import hp.server.app.tests.Datos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = {"spring.profiles.active=test"}, classes = HpServerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleServiceTest {

    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private RoleServiceImpl roleService;

    @Test
    public void findAllTest() {
        System.out.println("----- findAllTest -----");
        Mockito.when(roleRepository.findAll()).thenReturn(Datos.ROLES_LIST);

        List<Role> roles = (List<Role>) roleService.findAll();
        Assertions.assertNotSame(Collections.emptyList(), roles);
        Assertions.assertEquals(Datos.ROLES_LIST.size(), roles.size());

        Mockito.verify(roleRepository).findAll();
    }

    @Test
    public void findByIdTest() {
        System.out.println("----- findByIdTest -----");
        Mockito.when(roleRepository.findById(1L)).thenReturn(java.util.Optional.of(Datos.ROLE_USER));
        Mockito.when(roleRepository.findById(2L)).thenReturn(java.util.Optional.of(Datos.ROLE_ADMIN));
        Mockito.when(roleRepository.findById(3L)).thenReturn(java.util.Optional.of(Datos.ROLE_SUPERADMIN));

        Optional<Role> roleUserOpt = roleService.findById(1L);
        Optional<Role> roleAdminOpt = roleService.findById(2L);
        Optional<Role> roleSuperAdminOpt = roleService.findById(3L);

        Role roleUser = roleUserOpt.get();
        Role roleAdmin = roleAdminOpt.get();
        Role rolesuperAdmin = roleSuperAdminOpt.get();

        Assertions.assertNotNull(roleUser);
        Assertions.assertEquals(1L, roleUser.getId());
        Assertions.assertEquals(Datos.ROLE_USER_STR, roleUser.getDescription());

        Assertions.assertNotNull(roleAdmin);
        Assertions.assertEquals(2L, roleAdmin.getId());
        Assertions.assertEquals(Datos.ROLE_ADMIN_STR, roleAdmin.getDescription());

        Assertions.assertNotNull(rolesuperAdmin);
        Assertions.assertEquals(3L, rolesuperAdmin.getId());
        Assertions.assertEquals(Datos.ROLE_SUPERADMIN_STR, rolesuperAdmin.getDescription());

        Mockito.verify(roleRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(3L);
    }

    @Test
    public void findByIdReturnNullTest() {
        System.out.println("----- findByIdReturnNullTest -----");
        Mockito.when(roleRepository.findById(1L)).thenReturn(java.util.Optional.of(Datos.ROLE_USER));
        Mockito.when(roleRepository.findById(2L)).thenReturn(java.util.Optional.of(Datos.ROLE_ADMIN));
        Mockito.when(roleRepository.findById(3L)).thenReturn(java.util.Optional.of(Datos.ROLE_SUPERADMIN));

        Optional<Role> roleOpt = roleService.findById(5L);
        Role role = roleOpt.isPresent() ? roleOpt.get() : null;

        Assertions.assertNull(role);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(5L);
    }

    @Test
    public void getByDescriptionTest() {
        System.out.println("----- getByDescriptionTest -----");
        Mockito.when(roleRepository.findByDescription(Datos.ROLE_USER_STR)).thenReturn(Datos.ROLE_USER);
        Mockito.when(roleRepository.findByDescription(Datos.ROLE_ADMIN_STR)).thenReturn(Datos.ROLE_ADMIN);
        Mockito.when(roleRepository.findByDescription(Datos.ROLE_SUPERADMIN_STR)).thenReturn(Datos.ROLE_SUPERADMIN);

        Role role = roleService.getByDescription("ROLE_USER");

        Assertions.assertNotNull(role);
        Assertions.assertEquals(Datos.ROLE_USER.getId(), role.getId());
        Assertions.assertEquals(Datos.ROLE_USER.getDescription(), role.getDescription());

        Mockito.verify(roleRepository, Mockito.times(1)).findByDescription("ROLE_USER");
    }

    @Test
    public void getByDescriptionReturnNullTest() {
        System.out.println("----- getByDescriptionReturnNullTest -----");
        Mockito.when(roleRepository.findByDescription(Datos.ROLE_USER_STR)).thenReturn(Datos.ROLE_USER);
        Mockito.when(roleRepository.findByDescription(Datos.ROLE_ADMIN_STR)).thenReturn(Datos.ROLE_ADMIN);
        Mockito.when(roleRepository.findByDescription(Datos.ROLE_SUPERADMIN_STR)).thenReturn(Datos.ROLE_SUPERADMIN);

        Role role = roleService.getByDescription("ROLE");

        Assertions.assertNull(role);

        Mockito.verify(roleRepository, Mockito.times(1)).findByDescription("ROLE");
    }

    @Test
    public void saveRoleTest() {
        System.out.println("----- saveRoleTest -----");
        // Given
        Role newRole = new Role(1L, "ROLE_TEST");

        Mockito.when(roleRepository.save(Mockito.any(Role.class))).then(new Answer<Role>() {
            Long sequencyId = 4L;

            @Override
            public Role answer(InvocationOnMock invocationOnMock) throws Throwable {
                Role role = invocationOnMock.getArgument(0);
                role.setId(++sequencyId);
                return role;
            }
        });

        try {
            // When
            Role role = roleService.save(newRole);

            // Then
            Assertions.assertNotNull(role);
            Assertions.assertEquals(5L, role.getId());
            Assertions.assertEquals("ROLE_TEST", role.getDescription());

            Mockito.verify(roleRepository).save(Mockito.any(Role.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
