package hp.server.app.services;

import hp.server.app.models.dto.response.JwtResponseDTO;
import hp.server.app.models.dto.response.MessageResponse;
import hp.server.app.models.entity.Person;
import org.springframework.security.core.Authentication;

public interface AuthService {

    public JwtResponseDTO authenticateUser(Authentication authentication);
    public MessageResponse saveNewUser(Person person) throws Exception;
}
