package gov.cida.sedmap.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MockRequest implements HttpServletRequest {

	public Map<String,String> parameters;
	public Map<String,Object> attributes;

	public MockRequest(Map<String,String> params) {
		parameters = params;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);

	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		return Collections.enumeration(parameters.keySet());

	}

	@Override
	public String[] getParameterValues(String name) {
		return parameters.values().toArray(new String[]{});
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map getParameterMap() {
		return parameters;
	}


	@Override
	public String getCharacterEncoding() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getContentLength() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getContentType() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new RuntimeException("Not mocked for testing");

	}


	@Override
	public String getProtocol() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getScheme() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getServerName() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getServerPort() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getRemoteAddr() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getRemoteHost() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setAttribute(String name, Object o) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void removeAttribute(String name) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Locale getLocale() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getLocales() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isSecure() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getRealPath(String path) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getRemotePort() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getLocalName() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getLocalAddr() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getLocalPort() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getAuthType() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Cookie[] getCookies() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public long getDateHeader(String name) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getHeader(String name) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getHeaders(String name) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getHeaderNames() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getIntHeader(String name) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getMethod() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getPathInfo() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getPathTranslated() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getContextPath() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getQueryString() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getRemoteUser() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isUserInRole(String role) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Principal getUserPrincipal() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getRequestedSessionId() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getRequestURI() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public StringBuffer getRequestURL() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getServletPath() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public HttpSession getSession(boolean create) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public HttpSession getSession() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new RuntimeException("Not mocked for testing");

	}

}
