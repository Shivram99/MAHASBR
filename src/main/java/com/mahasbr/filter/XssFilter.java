package com.mahasbr.filter;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@WebFilter("/*")
public class XssFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("XssFilter initialized");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logger.info("XssFilter executing");

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// **Skip XSS filtering for multipart requests**
        if (httpRequest.getContentType() != null && httpRequest.getContentType().startsWith("multipart")) {
            chain.doFilter(request, response);
            return;
        }
		XssRequestWrapper wrappedRequest = new XssRequestWrapper(httpRequest, httpResponse);
		chain.doFilter(wrappedRequest, httpResponse);
	}

	@Override
	public void destroy() {
		logger.info("XssFilter destroyed");
	}

	private static class XssRequestWrapper extends HttpServletRequestWrapper {
		private final HttpServletResponse response;
		private final String requestBody;

		public XssRequestWrapper(HttpServletRequest request, HttpServletResponse response) throws IOException {
			super(request);
			this.response = response;
			this.requestBody = getBodyFromRequest(request);
		}

		@Override
		public String getParameter(String name) {
			String value = super.getParameter(name);
			return cleanAndNotify(name, value);
		}

		@Override
		public String[] getParameterValues(String name) {
			String[] values = super.getParameterValues(name);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					values[i] = cleanAndNotify(name, values[i]);
				}
			}
			return values;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			Map<String, String[]> parameterMap = super.getParameterMap();
			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String[] values = entry.getValue();
				for (int i = 0; i < values.length; i++) {
					values[i] = cleanAndNotify(entry.getKey(), values[i]);
				}
			}
			return parameterMap;
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			if (requestBody == null) {
				return super.getInputStream();
			}
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes());
			return new CustomServletInputStream(byteArrayInputStream);
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(getInputStream()));
		}

		private String getBodyFromRequest(HttpServletRequest request) throws IOException {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
				String body = reader.lines().collect(Collectors.joining("\n"));
				return cleanAndNotify("body", body);
			}
		}

		private String cleanAndNotify(String name, String value) {
			if (value != null) {
				// Use Jsoup to clean the input
				String cleanedValue = Jsoup.clean(value, Safelist.none());
				if (!value.equals(cleanedValue)) {
					logger.debug("Original value for {}: {}", name, value);
					logger.debug("Cleaned value for {}: {}", name, cleanedValue);
					response.addHeader("X-XSS-Protection", "1; mode=block");
					response.addHeader("X-XSS-Cleaned", "true");
					logger.error("XSS protection triggered for parameter: {}", name);
				}
				return cleanedValue;
			}
			return null;
		}
	}

	private static class CustomServletInputStream extends ServletInputStream {
		private final ByteArrayInputStream byteArrayInputStream;

		public CustomServletInputStream(ByteArrayInputStream byteArrayInputStream) {
			this.byteArrayInputStream = byteArrayInputStream;
		}

		@Override
		public int read() throws IOException {
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
			// No-op
		}
	}
}
