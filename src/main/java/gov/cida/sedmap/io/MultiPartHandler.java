package gov.cida.sedmap.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import gov.cida.sedmap.io.util.exceptions.SedmapException;

public class MultiPartHandler extends BaseHandler {

	public static final String BOUNDARY_TAG = "--AMZ90RFX875LKMFasdf09DDFF3";
	public static final String MULTI_PART_CONTENT_TYPE = "multipart/x-mixed-replace;boundary="
			+ BOUNDARY_TAG.substring(2);

	public MultiPartHandler(HttpServletResponse res, OutputStream stream, String filename) {
		super(res, stream, MULTI_PART_CONTENT_TYPE, filename);
	}

	@Override
	public FileDownloadHandler startNewFile(String contentType, String filename) throws SedmapException {
		super.startNewFile(contentType, filename);

		try {
			out.write(BOUNDARY_TAG.getBytes());
			out.write(IoUtils.LINE_SEPARATOR.getBytes());
			out.write("Content-type: ".getBytes());
			out.write(contentType.getBytes());
			out.write("\n".getBytes());
			filename = "Content-Disposition: attachment; filename=" +filename;
			out.write(filename.getBytes());
			out.write(IoUtils.LINE_SEPARATOR.getBytes());
		} catch (IOException e) {
			throw new SedmapException("Failed to write file header to user for file " + filename, e);
		}
		return this; //chain
	}

	@Override
	public FileDownloadHandler finishWritingFiles() throws SedmapException {
		try {
			out.write(BOUNDARY_TAG.getBytes());
			String endBndry = BOUNDARY_TAG + "--";
			out.write(IoUtils.LINE_SEPARATOR.getBytes());
			out.write(endBndry.getBytes());  // write the ending boundary
		} catch (IOException e) {
			throw new SedmapException("Failed to write file trailer to user for file " + name, e);
		}
		super.finishWritingFiles();
		return this; //chain
	}

}
