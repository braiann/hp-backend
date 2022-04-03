package hp.server.app.tests.services;

import hp.server.app.HpServerApplication;
import hp.server.app.models.entity.Address;
import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.Role;
import hp.server.app.models.repository.AddressRepository;
import hp.server.app.models.repository.CityRepository;
import hp.server.app.models.repository.PersonRepository;
import hp.server.app.services.PersonService;
import hp.server.app.services.RoleService;
import hp.server.app.tests.Datos;
import hp.server.app.utils.exceptionsmessages.ApiRestErrorMessage;
import nrt.common.microservice.exceptions.CommonBusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(properties = {"spring.profiles.active=test"}, classes = HpServerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersonServiceTest {

    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private RoleService roleService;
    @MockBean
    private AddressRepository addressRepository;
    @MockBean
    private CityRepository cityRepository;
    @Autowired
    private PersonService personService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void savePersonTest() {
        System.out.println("----- savePersonTest -----");
        Person newPerson = Datos.PERSON;

        Mockito.when(personRepository.findByDocumentNumber(Mockito.anyLong())).thenReturn(null);
        Mockito.when(personRepository.findByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(cityRepository.findById(1L)).thenReturn(java.util.Optional.of(Datos.CITY));
        Mockito.when(roleService.getByDescription(Datos.ROLE_USER_STR)).thenReturn(Datos.ROLE_USER);
        Mockito.when(addressRepository.save(Mockito.any(Address.class))).then(new Answer<Address>() {

            Long sequencyId = 1L;

            @Override
            public Address answer(InvocationOnMock invocationOnMock) throws Throwable {
                Address address = invocationOnMock.getArgument(0);
                address.setId(++sequencyId);
                return address;
            }
        });
        Mockito.when(personRepository.save(Mockito.any(Person.class))).then(new Answer<Person>() {

            Long sequencyId = 1L;

            @Override
            public Person answer(InvocationOnMock invocationOnMock) throws Throwable {
                Person person = invocationOnMock.getArgument(0);
                person.setId(++sequencyId);
                person.setPassword(passwordEncoder.encode(person.getPassword()));
                return person;
            }
        });

        Role expectedRole = Datos.ROLE_USER;
        expectedRole.setId(expectedRole.getId() + 1);

        Address expectedAddress = Datos.NEW_ADDRESS;
        expectedAddress.setId(expectedAddress.getId() + 1);
        expectedAddress.setCity(Datos.CITY);

        try {
            Person person = personService.save(newPerson);

            Assertions.assertNotNull(person);
            Assertions.assertEquals(2L, person.getId());
            Assertions.assertEquals(Datos.PERSON.getDocumentType(), person.getDocumentType());
            Assertions.assertEquals(Datos.PERSON.getDocumentNumber(), person.getDocumentNumber());
            Assertions.assertEquals(Datos.PERSON.getEmail(), person.getEmail());
            Assertions.assertEquals(Datos.PERSON.getUsername(), person.getUsername());
            Assertions.assertEquals(Datos.PERSON.getBirthDate(), person.getBirthDate());
            Assertions.assertEquals(Datos.PERSON.getGender(), person.getGender());
            Assertions.assertEquals(Datos.PERSON.getNacionality(), person.getNacionality());
            Assertions.assertEquals(Datos.PERSON.getMaritalStatus(), person.getMaritalStatus());
            Assertions.assertSame(expectedRole, person.getRole());
            Assertions.assertTrue(expectedAddress.equals(person.getAddress()));

            Mockito.verify(personRepository, Mockito.times(1)).findByEmail(Mockito.anyString());
            Mockito.verify(personRepository, Mockito.times(1)).findByEmail(Mockito.anyString());
            Mockito.verify(personRepository, Mockito.times(1)).save(Mockito.any(Person.class));
            Mockito.verify(cityRepository, Mockito.times(1)).findById(1L);
            Mockito.verify(roleService, Mockito.times(1)).getByDescription(Datos.ROLE_USER_STR);
            Mockito.verify(addressRepository, Mockito.times(1)).save(Mockito.any(Address.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void savePersonReturnExceptionByEmailTest() {
        System.out.println("----- savePersonReturnExceptionByEmailTest -----");
        Person newPerson = Datos.PERSON;

        String email = Datos.PERSON.getEmail();
        Mockito.when(personRepository.findByDocumentNumber(Mockito.anyLong())).thenReturn(null);
        Mockito.when(personRepository.findByEmail(email)).thenReturn(Datos.PERSON);
        Mockito.when(personRepository.findByEmail(Mockito.anyString())).thenThrow(new CommonBusinessException(ApiRestErrorMessage.EXISTS_PERSON_BY_EMAIL + email));

        try {
            Exception exception = Assertions.assertThrows(CommonBusinessException.class, () -> {
               Person person = personService.save(newPerson);
            });

            Assertions.assertNotNull(exception);
            Assertions.assertEquals(ApiRestErrorMessage.EXISTS_PERSON_BY_EMAIL + email, exception.getMessage());

            Mockito.verify(personRepository, Mockito.times(1)).findByDocumentNumber(Mockito.anyLong());
            Mockito.verify(personRepository, Mockito.times(1)).findByEmail(email);
            Mockito.verify(personRepository, Mockito.times(0)).save(Mockito.any(Person.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getByEmailTest() {
        System.out.println("----- getByEmailTest -----");
        String email = Datos.PERSON_DB.getEmail();

        Mockito.when(personRepository.findByEmail(email)).thenReturn(Datos.PERSON_DB);

        Person person = personService.getByEmail(email);

        Assertions.assertNotNull(person);
        Assertions.assertSame(email, person.getEmail());
        Assertions.assertSame(Datos.PERSON_DB, person);

        Mockito.verify(personRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    public void getByEmailReturnNullTest() {
        System.out.println("----- getByEmailReturnNullTest -----");
        String email = Datos.PERSON_DB.getEmail();

        Mockito.when(personRepository.findByEmail(email)).thenReturn(Datos.PERSON_DB);

        Person person = personService.getByEmail("falseEmail@gmail.com");

        Assertions.assertNull(person);
        Assertions.assertNotSame(Datos.PERSON_DB, person);

        Mockito.verify(personRepository, Mockito.times(0)).findByEmail(email);
        Mockito.verify(personRepository, Mockito.times(1)).findByEmail("falseEmail@gmail.com");
    }

}
