package hp.server.app.services.impl;

import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.RefreshToken;
import hp.server.app.models.repository.RefreshTokenRepository;
import hp.server.app.services.PersonService;
import hp.server.app.services.RefreshTokenService;
import hp.server.app.utils.exceptions.RefreshTokenException;
import hp.server.app.utils.exceptionsmessages.ApiRestErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PersonService personService;
    @Value("${hp.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDuration;

    @Override
    public Optional<RefreshToken> getByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Long personId) throws Exception {
        logger.info("Enter to createRefreshToken()");
        RefreshToken refreshToken = new RefreshToken();
        Optional<Person> person = personService.findById(personId);
        if (!person.isPresent()) {
            throw new Exception(ApiRestErrorMessage.PERSON_ID_NOT_EXISTS + personId);
        }
        refreshToken.setPerson(person.get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        logger.info("Enter to verifyExpiration()");
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException(refreshToken.getToken(), "Refresh token was expired!");
        }
        return refreshToken;
    }

    @Override
    public int deleteByPersonId(Long personId) {
        logger.info("Enter to deleteByPersonId()");
        Optional<Person> person = personService.findById(personId);
        return refreshTokenRepository.deleteByPerson(person.get());
    }
}
