package hp.server.app.tests;

import hp.server.app.models.dto.request.LoginRequestDTO;
import hp.server.app.models.entity.Address;
import hp.server.app.models.entity.City;
import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.Province;
import hp.server.app.models.repository.CityRepository;
import hp.server.app.models.repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class BaseTest {

    protected String REST_API_PATH = "http://localhost:8080/api";
    protected static final String FIRST_NAME_PERSON = "NombreTestJUnit";
    protected static final String LAST_NAME_PERSON = "ApellidoTestJUnit";
    protected static final String DOCUMENT_TYPE_PERSON = "DNI";
    protected static final Long DOCUMENT_NUMBER_PERSON = 99999999L;
    protected static final String PHONE_NUMBER_PERSON = "12345689";
    protected static final String USERNAME_PERSON = "UserTestJUnit";
    protected static final String EMAIL_PERSON = "emailTestJUnit@gmail.com";
    protected static final String PASSWORD_PERSON = "12345678";
    protected static final String NACIONALITY_PERSON = "Argentina";
    protected static final String GENDER_PERSON = "M";
    protected static final String MARITAL_STATUS_PERSON = "Soltero";
    protected static final LocalDateTime BIRTDAY_PERSON = LocalDateTime.now();
    protected static final String ROLE = "ROLE_USER";
    protected static final Long PROVINCE_ID  = 1L;
    protected static final Long CITY_ID = 1L;
    protected static final String ADDRESS_PERSON = "AddressTest";
    protected Long PERSON_ID;
    protected Person person;
    protected String accessToken;

    @Autowired
    protected ProvinceRepository provinceRepository;
    @Autowired
    protected CityRepository cityRepository;

    protected Person buildPerson() {
        person = new Person();
        person.setFirstName(FIRST_NAME_PERSON);
        person.setLastName(LAST_NAME_PERSON);
        person.setDocumentType(DOCUMENT_TYPE_PERSON);
        person.setDocumentNumber(DOCUMENT_NUMBER_PERSON);
        person.setPhoneNumber(PHONE_NUMBER_PERSON);
        person.setUsername(USERNAME_PERSON);
        person.setEmail(EMAIL_PERSON);
        person.setPassword(PASSWORD_PERSON);
        person.setNacionality(NACIONALITY_PERSON);
        person.setGender(GENDER_PERSON);
        person.setMaritalStatus(MARITAL_STATUS_PERSON);
        person.setBirthDate(BIRTDAY_PERSON);
        person.setRole(null);
        Province province = provinceRepository.findById(PROVINCE_ID).get();
        City city = cityRepository.findById(CITY_ID).get();
        Address address = new Address();
        address.setAddress(ADDRESS_PERSON);
        address.setCity(city);
        person.setAddress(address);
        return person;
    }

    protected LoginRequestDTO buildLoginRequestDTO(Person person) {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername(USERNAME_PERSON);
        loginRequestDTO.setPassword(PASSWORD_PERSON);
        return loginRequestDTO;
    }

}
