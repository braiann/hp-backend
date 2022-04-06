package hp.server.app.tests.controllers;

import hp.server.app.controllers.PersonController;
import hp.server.app.models.entity.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    private static String EMAIL_PERSON = "emailtest@gmail.com";
    private static String ERROR_DOCUMENT_NUMBER_EXISTS = "El DNI del usuario ya existe";
    private static String ERROR_NAME_INVALID = "El nombre y el apellido no pueden contener caracteres numericos";
    private static String ERROR_EXISTS_PERSON_BY_EMAIL = "Existe un usuario registrado con el email: " + EMAIL_PERSON;
    private static String ERROR_DELETE_BY_ID = "No existe un usuario para el id";
    private List<Person> personList = new ArrayList<>();
    private int SIZE = 5;


}
