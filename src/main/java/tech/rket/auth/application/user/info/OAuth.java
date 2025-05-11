package tech.rket.auth.application.user.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuth {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("refresh_expires_in")
    private long refreshExpiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;

    private OAuth(String accessToken, String tokenType, long expiresIn, long refreshExpiresIn, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.refreshToken = refreshToken;
    }

    public static OAuth create(String accessToken, String refreshToken, long expiresIn, long refreshExpiresIn) {
        return new OAuth(accessToken, "bearer", expiresIn, refreshExpiresIn, refreshToken);
    }
}
