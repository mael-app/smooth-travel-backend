package com.smoothtravel.auth.filter;

import com.smoothtravel.auth.service.TokenBlacklistService;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistFilterTest {

    @Mock
    JsonWebToken jwt;

    @Mock
    TokenBlacklistService tokenBlacklistService;

    @Mock
    ContainerRequestContext requestContext;

    @InjectMocks
    TokenBlacklistFilter tokenBlacklistFilter;

    private final String testJti = "test-jti-12345";

    @Test
    void shouldAbortRequestWhenTokenIsBlacklisted() {
        when(jwt.getTokenID()).thenReturn(testJti);
        when(tokenBlacklistService.isBlacklisted(testJti)).thenReturn(true);

        tokenBlacklistFilter.filter(requestContext);

        verify(requestContext).abortWith(any(Response.class));
    }

    @Test
    void shouldAllowRequestWhenTokenIsNotBlacklisted() {
        when(jwt.getTokenID()).thenReturn(testJti);
        when(tokenBlacklistService.isBlacklisted(testJti)).thenReturn(false);

        tokenBlacklistFilter.filter(requestContext);

        verify(requestContext, never()).abortWith(null);
        verifyNoMoreInteractions(requestContext);
    }

    @Test
    void shouldAllowRequestWhenNoTokenId() {
        when(jwt.getTokenID()).thenReturn(null);

        tokenBlacklistFilter.filter(requestContext);

        verify(requestContext, never()).abortWith(null);
        verify(tokenBlacklistService, never()).isBlacklisted(null);
        verifyNoMoreInteractions(requestContext);
    }
}
