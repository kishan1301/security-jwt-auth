package ic.auth.service;

import ic.auth.dto.TokenInfo;

public interface TokenService {

    void invalidateToken(String bearerToken);

    TokenInfo refreshToken(String refreshToken);
}
