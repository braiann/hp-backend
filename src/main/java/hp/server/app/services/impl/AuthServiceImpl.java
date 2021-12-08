package hp.server.app.services.impl;

import hp.server.app.models.dto.request.PasswordRequestDTO;
import hp.server.app.models.dto.response.JwtResponseDTO;
import hp.server.app.models.dto.response.MessageResponse;
import hp.server.app.models.entity.PasswordReset;
import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.RefreshToken;
import hp.server.app.models.repository.PasswordResetRepository;
import hp.server.app.security.jwt.JwtUtils;
import hp.server.app.security.services.UserDetailsImpl;
import hp.server.app.services.AuthService;
import hp.server.app.services.PersonService;
import hp.server.app.services.RefreshTokenService;
import hp.server.app.utils.StringUtil;
import hp.server.app.utils.email.EmailService;
import hp.server.app.utils.exceptionsmessages.ApiRestErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private PersonService personService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Override
    public JwtResponseDTO authenticateUser(Authentication authentication) {
        logger.info("Enter to authenticateUser()");

        try {
            String accessToken = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority()).collect(Collectors.toList());

            // Generate a refresh token por response
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            return new JwtResponseDTO(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    accessToken,
                    refreshToken.getToken(),
                    "Bearer"
            );
        } catch (Exception e) {
            logger.error("An error occurred");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MessageResponse saveNewUser(Person person) throws Exception {
        logger.info("Enter to saveNewUser()");
        try {
            Person newPerson = personService.save(person);
            if (newPerson != null) {
                // TODO: send email after registration if a registration is ok
                emailService.sendEmail(newPerson.getEmail(), "Bienvenido",
                        "La registracion ha sido exitosa!");
            }

            return new MessageResponse("User registered successfully");
        } catch (Exception e) {
            return new MessageResponse(e.getMessage());
        }
    }

    @Override
    public void requestPasswordChange(String email) throws Exception {
        logger.info("Enter to requestPasswordChange()");

        // First valid if the email has a correct format
        if (!StringUtil.validEmail(email)) {
            throw new Exception(ApiRestErrorMessage.EMAIL_INVALID);
        }

        Person person = personService.getByEmail(email);
        if (person == null) {
            throw new Exception(ApiRestErrorMessage.PERSON_NOT_EXIST_BY_EMAIL);
        }

        String codeReset = "";
        boolean flag = false;
        while (!flag) {
            codeReset = getCode();
            PasswordReset ps = passwordResetRepository.findByCode(codeReset);
            flag = ps == null ? true : false;
        }

        // Build our entity and persist into database
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setPerson(person);
        passwordReset.setCode(codeReset);
        passwordReset.setExpiryDate(calculateExpiryDate(PasswordReset.EXPIRATION));
        passwordResetRepository.save(passwordReset);

        // TODO: send email to user
        emailService.sendEmail(person.getEmail(), "Restablecer Contraseña",
                "El codigo para restablecer su contraseña es: " + codeReset);
    }

    @Override
    public void resetPassword(PasswordRequestDTO passwordRequestDTO) throws Exception {
        logger.info("Enter to resetPassword");

        // Receive the passwordReset object by code;
        PasswordReset passwordReset = passwordResetRepository.findByCode(passwordRequestDTO.getCode());
        if (passwordReset == null || passwordReset.getCode().isEmpty()) {
            throw new Exception(ApiRestErrorMessage.CODE_RESET_PASSWORD_INVALID);
        }

        // TODO: Valid if the code not expired
        if (isCodeExpired(passwordReset)) {
            throw new Exception(ApiRestErrorMessage.CODE_EXPIRED);
        }

        // TODO: Valid if the both password are the same
        if (!confirmPassword(passwordRequestDTO.getNewPassword(), passwordRequestDTO.getConfirmPassword())) {
            throw new Exception(ApiRestErrorMessage.ERROR_PASSWORD_NOT_EQUALS);
        }

        Person person = passwordReset.getPerson();
        personService.updatePersonPassword(person);
    }

    private boolean isCodeExpired(PasswordReset passwordReset) {
        Calendar calendar = Calendar.getInstance();
        return passwordReset.getExpiryDate().before(calendar.getTime());
    }

    private boolean confirmPassword(String newPassword, String confirmPassword) {
        return newPassword.equalsIgnoreCase(confirmPassword);
    }

    private String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            code = code + String.valueOf(random.nextInt(10));
        }
        return code;
    }

    private Date calculateExpiryDate(int expiryTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expiryTime);
        return new Date(calendar.getTime().getTime());
    }

}
