package hp.server.app.tests.controllers;

import hp.server.app.HpServerApplication;
import hp.server.app.models.dto.request.LoginRequestDTO;
import hp.server.app.models.dto.request.RefreshTokenRequestDTO;
import hp.server.app.models.dto.response.JwtResponseDTO;
import hp.server.app.models.dto.response.MessageResponse;
import hp.server.app.models.dto.response.RefreshTokenResponseDTO;
import hp.server.app.models.entity.Person;
import hp.server.app.models.repository.PersonRepository;
import hp.server.app.models.repository.RefreshTokenRepository;
import hp.server.app.tests.BaseTest;
import hp.server.app.utils.exceptions.RefreshTokenException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import java.util.Optional;

@SpringBootTest(properties = {"spring.profiles.active=test"}, classes = HpServerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest extends BaseTest {

    private static final String MESSAGE_REGISTER_OK = "User registered successfully";
    private static final String BAD_CREDENTIALS = "Bad Credentials: Username or Password are not valid!";
    public static final String PERSON_EXISTS_BY_DOCUMENT = "El DNI del usuario ya existe";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeAll
    public void setUp() {
        Optional<Person> personDb = personRepository.findByUsername(USERNAME_PERSON);
        if (personDb.isPresent()) {
            refreshTokenRepository.deleteAll();
            personRepository.delete(personDb.get());
        }
    }

    @AfterAll
    public void cleanData() {
        if (personRepository.findByUsername(USERNAME_PERSON).isPresent()) {
            refreshTokenRepository.deleteAll();
            personRepository.delete(person);
        }
    }

    @Test
    @Order(1)
    public void registerNewPerson_statusOkTest() {
        System.out.println("----- TEST 1 -----");
        System.out.println("----- Register New Person Successfully -----");
        person = buildPerson();
        try {
            String url = REST_API_PATH + "/auth/signup";

            // Build the request header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(person, headers);
            ResponseEntity<MessageResponse> response = restTemplate.postForEntity(url, entity, MessageResponse.class);
            MessageResponse messageResponse = response.getBody();

            // Receive the new person registered
            Optional<Person> p = personRepository.findByUsername(USERNAME_PERSON);
            if (p.isPresent()) {
                person = p.get();
                PERSON_ID = person.getId();
            }

            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(messageResponse.getMessage(), MESSAGE_REGISTER_OK);
            Assertions.assertEquals(person.getFirstName(), FIRST_NAME_PERSON);
            Assertions.assertEquals(person.getLastName(), LAST_NAME_PERSON);
            Assertions.assertEquals(person.getEmail(), EMAIL_PERSON);
            Assertions.assertEquals(person.getUsername(), USERNAME_PERSON);
            Assertions.assertEquals(person.getDocumentNumber(), DOCUMENT_NUMBER_PERSON);
            Assertions.assertEquals(person.getDocumentType(), DOCUMENT_TYPE_PERSON);
            Assertions.assertEquals(person.getGender(), GENDER_PERSON);
            Assertions.assertEquals(person.getBirthDate(), BIRTDAY_PERSON);
            Assertions.assertEquals(person.getRole().getDescription(), ROLE);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    public void registerNewPerson_existByDocumentNumberstatusOkTest() {
        System.out.println("----- TEST 2 -----");
        System.out.println("----- Register New Person Error: Exists person email -----");
        person = buildPerson();
        try {
            String url = REST_API_PATH + "/auth/signup";

            // Build the request header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(person, headers);
            ResponseEntity<MessageResponse> response = restTemplate.postForEntity(url, entity, MessageResponse.class);
            MessageResponse messageResponse = response.getBody();

            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(messageResponse.getMessage(), PERSON_EXISTS_BY_DOCUMENT);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    public void login_statusOkTest() {
        System.out.println("----- TEST 2 -----");
        System.out.println("----- Login Person Successfully -----");
        LoginRequestDTO loginRequestDTO = buildLoginRequestDTO(person);
        try {
            String url = REST_API_PATH + "/auth/signin";

            // Build the request header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(loginRequestDTO, headers);
            ResponseEntity<JwtResponseDTO> response = restTemplate.postForEntity(url, entity, JwtResponseDTO.class);
            JwtResponseDTO jwtResponseDTO = response.getBody();

            accessToken = jwtResponseDTO.getAccessToken();
            refreshToken = jwtResponseDTO.getRefreshToken();

            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(jwtResponseDTO.getId(), PERSON_ID);
            Assertions.assertEquals(jwtResponseDTO.getEmail(), EMAIL_PERSON);
            Assertions.assertEquals(jwtResponseDTO.getUsername(), USERNAME_PERSON);
            Assertions.assertEquals(jwtResponseDTO.getRoles().size(), 1);
            Assertions.assertEquals(jwtResponseDTO.getType(), "Bearer");
            Assertions.assertNotEquals(jwtResponseDTO.getAccessToken(), null);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    public void login_badCredentials_statusBadRequestTest() {
        System.out.println("----- TEST 3 -----");
        System.out.println("----- Login Person Bad Credentials -----");
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("FalseUser");
        loginRequestDTO.setPassword("FalsePassword");
        try {
            String url = REST_API_PATH + "/auth/signin";

            // Build the request header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(loginRequestDTO, headers);
            ResponseEntity<JwtResponseDTO> response = restTemplate.postForEntity(url, entity, JwtResponseDTO.class);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.BAD_REQUEST);
            Assertions.assertEquals(getMessageResponseFromMessageException(e.getMessage()), BAD_CREDENTIALS);
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    public void refreshToken_statusOkTest() {
        System.out.println("----- TEST 5 -----");
        System.out.println("----- Get Refresh Token Successfully -----");
        RefreshTokenRequestDTO refreshTokenRequestDTO = new RefreshTokenRequestDTO(refreshToken);
        try {
            String url = REST_API_PATH + "/auth/refreshtoken";

            // Build the request header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(refreshTokenRequestDTO, headers);
            ResponseEntity<RefreshTokenResponseDTO> response = restTemplate.postForEntity(url, entity, RefreshTokenResponseDTO.class);
            RefreshTokenResponseDTO refreshTokenResponseDTO = response.getBody();

            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(refreshTokenResponseDTO.getRefreshToken(), refreshToken);
            Assertions.assertEquals(refreshTokenResponseDTO.getTokenType(), "Bearer");
            Assertions.assertNotEquals(refreshTokenResponseDTO.getAccessToken(), null);
            Assertions.assertNotEquals(refreshTokenResponseDTO.getAccessToken(), accessToken);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    public void refreshToken_refreshTokenInvalidTest() {
        System.out.println("----- TEST 6 -----");
        System.out.println("----- Get Refresh Token Failure -----");
        RefreshTokenRequestDTO refreshTokenRequestDTO = new RefreshTokenRequestDTO("refreshTokenFalse");
        try {
            String url = REST_API_PATH + "/auth/refreshtoken";

            // Build the request header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(refreshTokenRequestDTO, headers);
            ResponseEntity<RefreshTokenResponseDTO> response = restTemplate.postForEntity(url, entity, RefreshTokenResponseDTO.class);
            RefreshTokenResponseDTO refreshTokenResponseDTO = response.getBody();
        } catch (RefreshTokenException e) {
            Assertions.assertEquals(e.getMessage(), "Refresh token is not exists in database!");
            e.printStackTrace();
        }
    }



    private String getMessageResponseFromMessageException(String message) {
        String[] messageSplit = message.split("400");
        String messageJson = messageSplit[1].substring(4, messageSplit[1].length() - 1);
        Gson gson = new Gson();
        MessageResponse messageResponse = new MessageResponse();
        messageResponse = gson.fromJson(messageJson, MessageResponse.class);
        return messageResponse.getMessage();
    }

}
