package hp.server.app.services;

import hp.server.app.models.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    public Optional<RefreshToken> getByToken(String token);

    public RefreshToken createRefreshToken(Long personId) throws Exception;

    public RefreshToken verifyExpiration(RefreshToken refreshToken);

    public int deleteByPersonId(Long personId);
}
