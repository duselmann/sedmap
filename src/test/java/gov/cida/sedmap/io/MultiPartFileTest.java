package gov.cida.sedmap.io;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.mock.MockResponse;

public class MultiPartFileTest {



	@Test
	public void beginWritingFiles() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		@SuppressWarnings({ "resource", "unused" })
		FileDownloadHandler handler = new MultiPartHandler(res, out, "TestBeginWritingFiles").beginWritingFiles();
		assertEquals(MultiPartHandler.MULTI_PART_CONTENT_TYPE, res.getContentType());

		res.getWriter().flush();
		IoUtils.quietClose(out);
		String actual = out.toString();

		assertEquals("Expect only content type to be set", 0, actual.length());
	}
	@Test
	public void startNewFile() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		@SuppressWarnings("resource")
		FileDownloadHandler handler = new MultiPartHandler(res, out, "TestStartNewFile");

		handler.startNewFile("text/csv", "foo.csv");
		res.getWriter().flush();
		IoUtils.quietClose(out);

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

		@SuppressWarnings("resource")
		FileDownloadHandler handler = new MultiPartHandler(res, out, "TestEndNewFile");

		handler.endNewFile();
		res.getWriter().flush();
		IoUtils.quietClose(out);
		String actual = out.toString();

		assertEquals("Expect only content type to be set", 0, actual.length());
	}
	@Test
	public void finishWritingFiles() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		@SuppressWarnings("resource")
		FileDownloadHandler handler = new MultiPartHandler(res, out, "TestFinishWritingFiles");

		handler.finishWritingFiles();
		IoUtils.quietClose(out);
		String actual = out.toString();
		assertEquals("The last boundary terminator should be present",
				1, StrUtils.occurrences(MultiPartHandler.BOUNDARY_TAG+"--", actual));
	}


}
