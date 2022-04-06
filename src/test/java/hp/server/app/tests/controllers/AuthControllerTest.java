package hp.server.app.tests.controllers;

import com.google.gson.Gson;
import hp.server.app.controllers.AuthController;
import hp.server.app.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    private Gson gson;
    private static final String MESSAGE_REGISTER_OK = "User registered successfully";
    private static final String BAD_CREDENTIALS = "Bad Credentials: Username or Password are not valid!";
    public static final String PERSON_EXISTS_BY_DOCUMENT = "El DNI del usuario ya existe";

    @BeforeEach
    public void setUp() {
        this.gson = new Gson();
    }
}
