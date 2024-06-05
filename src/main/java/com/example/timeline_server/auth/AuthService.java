package com.example.timeline_server.auth;

import com.example.timeline_server.exception.UnAuthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${sns.auth-url}")
    private String USER_SERVER_AUTH_URL;

    @Autowired
    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Integer getUserIdFromAuthServer(HttpServletRequest request) {
        HttpHeaders headers = createHeadersWithCookies(request);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserAuthInfo> authResponse = restTemplate.exchange(
                    USER_SERVER_AUTH_URL,
                    HttpMethod.GET,
                    entity,
                    UserAuthInfo.class
            );

            if (authResponse.getStatusCode().is2xxSuccessful()) {
                return authResponse.getBody().getUserId();
            } else {
                throw new UnAuthorizedException();
            }
        } catch (HttpClientErrorException e) {
            throw new UnAuthorizedException();
        } catch (RestClientException e) {
            throw new UnAuthorizedException();
        }
    }

    private HttpHeaders createHeadersWithCookies(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", request.getHeader("Cookie"));
        return headers;
    }

    public static class UserAuthInfo {
        private int userId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}