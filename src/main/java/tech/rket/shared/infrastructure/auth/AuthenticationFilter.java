package tech.rket.shared.infrastructure.auth;

import co.elastic.apm.api.Traced;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.util.ULocale;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.rket.shared.infrastructure.log.JDLogger;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;


@Component
@Order(011)
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = JDLogger.getLogger(AuthenticationFilter.class, Map.of("category", "AUTH"));
    private static final String HEADER = "Authorization";
    private final ObjectMapper objectMapper = ObjectMapperGenerator.jsonMapper();
    @Value("${oauth.secret-key}")
    private String secretKey;
    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        secretKey = secretKey.replace("'", "");
        this.algorithm = Algorithm.HMAC512(secretKey);
    }

    private void preflightHeaders(HttpServletResponse response) {
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "HEAD, GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.setStatus(200);
    }


    private void handleRefreshToken(HttpServletRequest request) {
        TokenInfo tokenInfo = (TokenInfo) request.getAttribute("TOKEN_INFO");
        RKetSecurityContextHelper.injectRefreshToken(tokenInfo.getJwtId());
    }

    private void handleAuthenticated(HttpServletRequest request) {
        TokenInfo tokenInfo = (TokenInfo) request.getAttribute("TOKEN_INFO");
        UserLoginInfo userLoginInfo = new UserLoginInfo(
                tokenInfo.getJwtId(),
                tokenInfo.getSubject(),
                tokenInfo.getEmail(),
                tokenInfo.getTenantId(),
                tokenInfo.getRole(),
                tokenInfo.getPermissions(),
                tokenInfo.getLocale() != null ? new ULocale(tokenInfo.getLocale()).toLocale() : null
        );
        RKetSecurityContextHelper.inject(userLoginInfo);
    }

    private TokenType validateToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);
        String PREFIX = "Bearer ";
        if (authenticationHeader == null || authenticationHeader.isBlank()) {
            return TokenType.EMPTY;
        }
        if (authenticationHeader.startsWith(PREFIX)) {
            String token = authenticationHeader.substring(PREFIX.length()).trim();
            return getType(token, request);
        } else {
            return TokenType.INVALID;
        }
    }

    private TokenType getType(String token, HttpServletRequest request) {
        try {
            DecodedJWT jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);
            String string = jwt.getHeaderClaim("token_type").asString();
            if (string == null || string.isBlank()) {
                return TokenType.INVALID;
            }
            TokenInfo tokenInfo = null;
            try {
                tokenInfo = objectMapper.readValue(Base64.getDecoder().decode(jwt.getPayload().getBytes(StandardCharsets.UTF_8)), TokenInfo.class);
            } catch (IOException e) {
                return TokenType.INVALID;
            }
            request.setAttribute("TOKEN_INFO", tokenInfo);
            if (string.toUpperCase().equals(TokenType.ACCESS_TOKEN.name())) {
                return TokenType.ACCESS_TOKEN;
            } else if (string.toUpperCase().equals(TokenType.REFRESH_TOKEN.name())) {
                return TokenType.REFRESH_TOKEN;
            } else {
                return TokenType.INVALID;
            }
        } catch (TokenExpiredException e) {
            return TokenType.EXPIRED;
        } catch (JWTVerificationException e) {
            log.error("{}", e.getLocalizedMessage());
            return TokenType.INVALID;
        }
    }

    @Traced(value = "AUTH.Filter", asExit = true, action = "filter", subtype = "auth", type = "request")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("authentication filter started.");
        if (Objects.equals(request.getMethod(), "OPTIONS")) {
            log.debug("Option request detected.");
            preflightHeaders(response);
            return;
        }
        TokenType tokenType = validateToken(request);

        if (tokenType == TokenType.INVALID) {
            log.error("Token is not valid.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is not valid.");
            return;
        } else if (tokenType == TokenType.EXPIRED) {
            log.error("Token is expired.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired.");
            return;
        } else if (tokenType == TokenType.EMPTY) {
            log.debug("Empty Token.");
            RKetSecurityContextHelper.injectAnonymous();
        } else if (tokenType == TokenType.ACCESS_TOKEN) {
            log.debug("Access Token.");
            handleAuthenticated(request);
        } else if (tokenType == TokenType.REFRESH_TOKEN) {
            log.debug("Refresh Token.");
            handleRefreshToken(request);
        } else {
            log.error("You shall not pass.");
            throw new AccessDeniedException("You shall not pass.");
        }
        log.trace("Authentication finished");
        filterChain.doFilter(request, response);
    }

    private enum TokenType {
        LEGACY, ACCESS_TOKEN, REFRESH_TOKEN, EMPTY, INVALID, EXPIRED,
    }
}
