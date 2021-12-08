package hp.server.app.services.impl;

import hp.server.app.models.dto.response.JwtResponseDTO;
import hp.server.app.models.dto.response.MessageResponse;
import hp.server.app.models.entity.Person;
import hp.server.app.security.jwt.JwtUtils;
import hp.server.app.security.services.UserDetailsImpl;
import hp.server.app.services.AuthService;
import hp.server.app.services.PersonService;
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
    private PersonService personService;

    @Override
    public JwtResponseDTO authenticateUser(Authentication authentication) {
        logger.info("Enter to authenticateUser()");

        String accessToken = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority()).collect(Collectors.toList());

        return new JwtResponseDTO(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                accessToken,
                "Bearer"
        );
    }

    @Override
    public MessageResponse saveNewUser(Person person) throws Exception {
        logger.info("Enter to saveNewUser()");
        try {
            Person newPerson = personService.save(person);
            return new MessageResponse("User registered successfully");
        } catch (Exception e) {
            return new MessageResponse(e.getMessage());
        }
    }
}
