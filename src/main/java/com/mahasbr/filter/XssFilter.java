package com.mahasbr.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private static class XssRequestWrapper extends HttpServletRequestWrapper {
        private final String body;
        private static final Pattern[] patterns = new Pattern[]{
                Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
                Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
                Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
        };

        public XssRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            // Cache the body to allow multiple reads
            body = sanitize(request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual));
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return sanitize(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) return null;
            for (int i = 0; i < values.length; i++) {
                values[i] = sanitize(values[i]);
            }
            return values;
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))));
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream =
                    new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            return new ServletInputStream() {
                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // Not used
                }
            };
        }

        public String getRequestBody() {
            return body;
        }

        private String sanitize(String value) {
            if (value == null) {
                return null;
            }
            // Remove all malicious patterns
            String clean = value;
            for (Pattern scriptPattern : patterns) {
                clean = scriptPattern.matcher(clean).replaceAll("");
            }
            // Encode basic HTML entities
            clean = clean.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            return clean;
        }
    }
}
