package gov.cida.sedmap.io;

import static org.junit.Assert.*;

import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.MultiPartHandler;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.mock.MockResponse;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class MultiPartFileTest {



	@Test
	public void beginWritingFiles() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		new MultiPartHandler(res, out).beginWritingFiles();
		assertEquals(MultiPartHandler.MULTI_PART_CONTENT_TYPE, res.getContentType());

		res.getWriter().flush();
		IoUtils.quiteClose(out);
		String actual = out.toString();

		assertEquals("Expect only content type to be set", 0, actual.length());
	}
	@Test
	public void startNewFile() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		FileDownloadHandler handler = new MultiPartHandler(res, out);

		handler.startNewFile("text/csv", "foo.csv");
		res.getWriter().flush();
		IoUtils.quiteClose(out);

		String actual = out.toString();

		assertEquals("Expect contentType", 1, StrUtils.occurrences("Content-type: text/csv", actual));
		assertTrue  ("filename was expected", actual.contains("filename=foo.csv") );
		assertEquals("Expect no boundary ", 1, StrUtils.occurrences(MultiPartHandler.BOUNDARY_TAG, actual));

		assertEquals("The last boundary terminator should not be present.",
				-1, actual.lastIndexOf(MultiPartHandler.BOUNDARY_TAG+"--"));
	}
	@Test
	public void endNewFile() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		FileDownloadHandler handler = new MultiPartHandler(res, out);

		handler.endNewFile();
		res.getWriter().flush();
		IoUtils.quiteClose(out);
		String actual = out.toString();

		assertEquals("Expect only content type to be set", 0, actual.length());
	}
	@Test
	public void finishWritingFiles() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		FileDownloadHandler handler = new MultiPartHandler(res, out);

		handler.finishWritingFiles();
		IoUtils.quiteClose(out);
		String actual = out.toString();
		assertEquals("The last boundary terminator should be present",
				1, StrUtils.occurrences(MultiPartHandler.BOUNDARY_TAG+"--", actual));
	}


}
