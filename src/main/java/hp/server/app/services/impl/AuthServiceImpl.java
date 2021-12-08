package hp.server.app.services.impl;

import hp.server.app.models.dto.response.JwtResponseDTO;
import hp.server.app.models.dto.response.MessageResponse;
import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.RefreshToken;
import hp.server.app.security.jwt.JwtUtils;
import hp.server.app.security.services.UserDetailsImpl;
import hp.server.app.services.AuthService;
import hp.server.app.services.PersonService;
import hp.server.app.services.RefreshTokenService;
import hp.server.app.utils.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
