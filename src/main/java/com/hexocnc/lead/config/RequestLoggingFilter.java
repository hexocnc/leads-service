package com.hexocnc.lead.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Logs one line per API call showing who made it: the TCP peer address, the original client from
 * {@code X-Forwarded-For} (if behind a proxy), the optional {@code X-Caller-Service} header that
 * calling services can set to identify themselves, and the {@code User-Agent}. The response body is
 * appended, truncated to {@code app.logging.response-body-max-chars} (default 2000; 0 disables it).
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    static final String CALLER_HEADER = "X-Caller-Service";

    @Value("${app.logging.response-body-max-chars:2000}")
    private int bodyMaxChars;

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {
        final long start = System.nanoTime();
        final var wrapped = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(request, wrapped);
        } finally {
            final long tookMs = (System.nanoTime() - start) / 1_000_000;
            final var query = request.getQueryString();
            log.info("{} {}{} -> {} in {} ms [caller={}, remoteAddr={}, forwardedFor={}, userAgent={}] response={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    query == null ? "" : "?" + query,
                    wrapped.getStatus(),
                    tookMs,
                    header(request, CALLER_HEADER),
                    request.getRemoteAddr(),
                    header(request, "X-Forwarded-For"),
                    header(request, "User-Agent"),
                    body(wrapped));
            // the buffered body must be written to the real response, otherwise the caller gets an empty reply
            wrapped.copyBodyToResponse();
        }
    }

    private static String header(final HttpServletRequest request, final String name) {
        final var value = request.getHeader(name);
        // strip CR/LF so a caller can't forge extra log lines
        return value == null ? "-" : value.replaceAll("[\r\n]", " ");
    }

    private String body(final ContentCachingResponseWrapper response) {
        final var bytes = response.getContentAsByteArray();
        if (bodyMaxChars <= 0 || bytes.length == 0) {
            return "-";
        }
        final var text = new String(bytes, StandardCharsets.UTF_8).replaceAll("[\r\n]+", " ");
        return text.length() <= bodyMaxChars ? text : text.substring(0, bodyMaxChars) + "...(truncated)";
    }
}
