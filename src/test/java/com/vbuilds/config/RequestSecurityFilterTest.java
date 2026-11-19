package com.vbuilds.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestSecurityFilterTest {
    private final RequestSecurityFilter filter = new RequestSecurityFilter("http://localhost:8080");

    private MockHttpServletRequest request(String method) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, "/api/builds/1");
        return request;
    }

    @Test
    void acceptsSameOriginMutation() throws Exception {
        var request = request("POST");
        request.addHeader("Origin", "http://localhost:8080");
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertEquals("no-store", response.getHeader("Cache-Control"));
        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
    }

    @Test
    void rejectsCrossOriginMutation() throws Exception {
        var request = request("POST");
        request.addHeader("Origin", "https://evil.example");
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertEquals(403, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test
    void rejectsMissingOriginAndReferer() throws Exception {
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request("DELETE"), response, chain);
        assertEquals(403, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test
    void acceptsSameOriginRefererFallback() throws Exception {
        var request = request("PUT");
        request.addHeader("Referer", "http://localhost:8080/HTML/editbuild.html?id=1");
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    void rejectsMaliciousRefererPrefix() throws Exception {
        var request = request("POST");
        request.addHeader("Referer", "http://localhost:8080.evil.example/page");
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertEquals(403, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test
    void rejectsCrossOriginEvenWithGoodReferer() throws Exception {
        var request = request("POST");
        request.addHeader("Origin", "https://evil.example");
        request.addHeader("Referer", "http://localhost:8080/");
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertEquals(403, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test
    void allowsReadOnlyRequestsWithoutOrigin() throws Exception {
        var request = request("GET");
        var response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }
}
