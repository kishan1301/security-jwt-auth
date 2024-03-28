package ic.auth.service;

public interface TokenService {

    void invalidateToken(String bearerToken);
}
