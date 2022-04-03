package hp.server.app.tests;

import hp.server.app.models.entity.Address;
import hp.server.app.models.entity.City;
import hp.server.app.models.entity.Role;

import java.util.Arrays;
import java.util.List;

public class Datos {

    public static final Role ROLE_USER = new Role(1L, "ROLE_USER");
    public static final Role ROLE_ADMIN = new Role(2L, "ROLE_ADMIN");
    public static final Role ROLE_SUPERADMIN = new Role(3L, "ROLE_SUPERADMIN");
    public static final String ROLE_USER_STR = "ROLE_USER";
    public static final String ROLE_ADMIN_STR = "ROLE_ADMIN";
    public static final String ROLE_SUPERADMIN_STR = "ROLE_SUPERADMIN";
    public static final List<Role> ROLES_LIST = Arrays.asList(new Role(1L, "ROLE_USER"),
            new Role(2L, "ROLE_ADMIN"),
            new Role(3L, "ROLE_SUPERADMIN"));
    public static final List<Address> ADDRESS_LIST = Arrays.asList(
            new Address(1L, "Addrees1", null),
            new Address(2L, "Address2", null)
    );
    public static final City CITY = new City(1L, "2000", "CITY_TEST", null);
    public static final Address NEW_ADDRESS = new Address(1L, "AddressTest", null);
}
