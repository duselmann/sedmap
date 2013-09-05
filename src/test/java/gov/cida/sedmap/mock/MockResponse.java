package gov.cida.sedmap.mock;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockResponse implements HttpServletResponse {

	ServletOutputStream os;
	PrintWriter writer;
	String contentType;
	int    contentLength;
	String encodong;

	public MockResponse(final OutputStream out) {
		os = new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {
				out.write(b);
			}
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				out.write(b,off,len);
			}
		};
		writer = new PrintWriter(os);
	}

	@Override
	public String getCharacterEncoding() {
		return encodong;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return os;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return writer;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		encodong = charset;
	}

	@Override
	public void setContentLength(int len) {
		contentLength = len;
	}

	@Override
	public void setContentType(String type) {
		contentType = type;
	}



	// unimplemented methods //



	@Override
	public void setBufferSize(int size) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getBufferSize() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void flushBuffer() throws IOException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void resetBuffer() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isCommitted() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void reset() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setLocale(Locale loc) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Locale getLocale() {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void addCookie(Cookie cookie) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean containsHeader(String name) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String encodeURL(String url) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String encodeRedirectURL(String url) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String encodeUrl(String url) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String encodeRedirectUrl(String url) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void sendError(int sc) throws IOException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void sendRedirect(String location) throws IOException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setDateHeader(String name, long date) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void addDateHeader(String name, long date) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setHeader(String name, String value) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void addHeader(String name, String value) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setIntHeader(String name, int value) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void addIntHeader(String name, int value) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setStatus(int sc) {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setStatus(int sc, String sm) {
		throw new RuntimeException("Not mocked for testing");

	}

}
