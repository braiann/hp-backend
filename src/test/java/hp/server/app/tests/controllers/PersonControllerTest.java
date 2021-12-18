package hp.server.app.tests.controllers;

import hp.server.app.HpServerApplication;
import hp.server.app.models.entity.*;
import hp.server.app.tests.BaseTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(properties = {"spring.profiles.active=test"}, classes = HpServerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerTest extends BaseTest {

    private static String ERROR_DOCUMENT_NUMBER_EXISTS = "El DNI del usuario ya existe";
    private static String ERROR_NAME_INVALID = "El nombre y el apellido no pueden contener caracteres numericos";
    private static String ERROR_EXISTS_PERSON_BY_EMAIL = "Existe un usuario registrado con el email: " + EMAIL_PERSON;
    private List<Person> personList = new ArrayList<>();
    private int SIZE = 5;

    @AfterEach
    public void cleanDataPerTest() {
        personRepository.deleteAll(personList);
        personList = new ArrayList<>();
    }

    @AfterAll
    public void cleanData() {
        refreshTokenRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void registerPersonWithRoleAdmin_statusOkTest() {
        System.out.println("----- TEST 1 -----");
        System.out.println("----- Register New Admin Person successfully -----");
        Person p = buildPersonForTest();

        String url = REST_API_PATH + "/person";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(p, headers);
            ResponseEntity<Person> response = restTemplate.postForEntity(url, entity, Person.class);
            Person personResponse = response.getBody();
            personList.add(personResponse);
            // Set max personId
            maxPersonId++;

            Role role = roleRepository.findByDescription(Role.ROLE_ADMIN);
            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
            Assertions.assertEquals(personResponse.getId(), maxPersonId);
            Assertions.assertEquals(personResponse.getFirstName(), p.getFirstName());
            Assertions.assertEquals(personResponse.getLastName(), p.getLastName());
            Assertions.assertEquals(personResponse.getDocumentType(), p.getDocumentType());
            Assertions.assertEquals(personResponse.getDocumentNumber(), p.getDocumentNumber());
            Assertions.assertEquals(personResponse.getUsername(), p.getUsername());
            Assertions.assertEquals(personResponse.getEmail(), p.getEmail());
            Assertions.assertEquals(personResponse.getRole(), role);
            Assertions.assertEquals(personResponse.getAddress().getAddress(), p.getAddress().getAddress());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    public void registerPersonWithoutAccessToken_statusUnauthorizedTest() {
        System.out.println("----- TEST 2 -----");
        System.out.println("----- Register New Admin Person Unauthorized -----");
        Person p = buildPersonForTest();

        String url = REST_API_PATH + "/person";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(p, headers);
            ResponseEntity<Person> response = restTemplate.postForEntity(url, entity, Person.class);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    public void registerPersonWithRepeatDNI_statusInternatServerErrorTest() {
        System.out.println("----- TEST 3 -----");
        System.out.println("----- Register New Admin Person with repeat DNI -----");
        Person p = buildPersonForTest();
        // Set the repeat dni into object
        p.setDocumentNumber(DOCUMENT_NUMBER_PERSON);
        String url = REST_API_PATH + "/person";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(p, headers);
            ResponseEntity<Person> response = restTemplate.postForEntity(url, entity, Person.class);
        } catch (Exception e) {
            Assertions.assertEquals(getMessageFromException(e.getMessage()), ERROR_DOCUMENT_NUMBER_EXISTS);
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    public void registerPersonWithNameOrSurnameInvalid_statusInternatServerErrorTest() {
        System.out.println("----- TEST 4 -----");
        System.out.println("----- Register New Admin Person with name or surname invalid -----");
        Person p = buildPersonForTest();
        // Set an invalid firstName
        p.setFirstName("436j");
        String url = REST_API_PATH + "/person";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(p, headers);
            ResponseEntity<Person> response = restTemplate.postForEntity(url, entity, Person.class);
        } catch (Exception e) {
            Assertions.assertEquals(getMessageFromException(e.getMessage()), ERROR_NAME_INVALID);
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    public void registerPersonWithExistsEmail_statusInternatServerErrorTest() {
        System.out.println("----- TEST 5 -----");
        System.out.println("----- Register New Admin Person with exists email -----");
        Person p = buildPersonForTest();
        // Set an exists email
        p.setEmail(EMAIL_PERSON);
        String url = REST_API_PATH + "/person";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(p, headers);
            ResponseEntity<Person> response = restTemplate.postForEntity(url, entity, Person.class);
        } catch (Exception e) {
            Assertions.assertEquals(getMessageFromException(e.getMessage()) + "ail: " + EMAIL_PERSON, ERROR_EXISTS_PERSON_BY_EMAIL);
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    public void getAll_statusOkTest() {
        System.out.println("----- TEST 6 -----");
        System.out.println("----- Get All -----");

        for (int i = 0; i < SIZE; i++) {
            Person p = buildPersonForTest();
            p.setAddress(null);
            personList.add(p);
        }

        personRepository.saveAll(personList);

        String url = REST_API_PATH + "/person";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<Person[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Person[].class);
            List<Person> personListResponse = Arrays.asList(response.getBody());

            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(personListResponse.size(), SIZE + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(7)
    public void getAll_statusUnauthorizedTest() {
        System.out.println("----- TEST 7 -----");
        System.out.println("----- Get All with status 401 -----");

        String url = REST_API_PATH + "/person";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<Person[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Person[].class);
            List<Person> personListResponse = Arrays.asList(response.getBody());
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
            e.printStackTrace();
        }
    }

    @Test
    @Order(8)
    public void getById_statusOkTest() {
        System.out.println("----- TEST 8 -----");
        System.out.println("----- Get by ID Status OK -----");

        Long personId = maxPersonId - 1;
        String url = REST_API_PATH + "/person/" + personId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<Person> response = restTemplate.exchange(url, HttpMethod.GET, entity, Person.class);
            Person personResponse = response.getBody();

            Role role = roleRepository.findByDescription(Role.ROLE_USER);
            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(personResponse.getId(), personId);
            Assertions.assertEquals(personResponse.getFirstName(), person.getFirstName());
            Assertions.assertEquals(personResponse.getLastName(), person.getLastName());
            Assertions.assertEquals(personResponse.getDocumentType(), person.getDocumentType());
            Assertions.assertEquals(personResponse.getDocumentNumber(), person.getDocumentNumber());
            Assertions.assertEquals(personResponse.getUsername(), person.getUsername());
            Assertions.assertEquals(personResponse.getEmail(), person.getEmail());
            Assertions.assertEquals(personResponse.getRole(), role);
            Assertions.assertEquals(personResponse.getAddress().getAddress(), person.getAddress().getAddress());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(9)
    public void getById_statusUnauthorizedTest() {
        System.out.println("----- TEST 9 -----");
        System.out.println("----- Get by ID with status 401 -----");

        Long personId = maxPersonId - 1;
        String url = REST_API_PATH + "/person/" + personId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<Person> response = restTemplate.exchange(url, HttpMethod.GET, entity, Person.class);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
            e.printStackTrace();
        }
    }

    @Test
    @Order(10)
    public void delete_statusUnauthorizedTest() {
        System.out.println("----- TEST 10 -----");
        System.out.println("----- Delete with status 401 -----");

        Long personId = maxPersonId - 1;
        String url = REST_API_PATH + "/person/" + personId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
            e.printStackTrace();
        }
    }

    @Test
    @Order(11)
    public void delete_statusNoContentTest() {
        System.out.println("----- TEST 11 -----");
        System.out.println("----- Delete with status 204 -----");
        Long personId = maxPersonId - 1;
        String url = REST_API_PATH + "/person/" + personId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

            Assertions.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
            e.printStackTrace();
        }
    }

    private Person buildPersonForTest() {
        Person p = new Person();
        p.setFirstName("TestName");
        p.setLastName("TestSurname");
        p.setDocumentType("DNI");
        p.setDocumentNumber(33495887L);
        p.setEmail("testEmail@test.com");
        p.setUsername("testUsername");
        p.setPassword("12345678");
        p.setGender("M");
        p.setBirthDate(LocalDateTime.now());
        p.setMaritalStatus("Soltero");
        p.setNacionality("Argentino");
        p.setPhoneNumber("444555666");
        p.setRole(roleRepository.findByDescription(Role.ROLE_ADMIN));
        Province province = provinceRepository.findById(PROVINCE_ID).get();
        City city = cityRepository.findById(CITY_ID).get();
        Address address = new Address();
        address.setAddress("addressTest");
        address.setCity(city);
        p.setAddress(address);
        return p;
    }

    private String getMessageFromException(String message) {
        String[] messageSplit = message.split(",");
        String[] messageError = messageSplit[1].split(":");
        return messageError[1].substring(1, messageError[1].length() - 3);
    }
}
