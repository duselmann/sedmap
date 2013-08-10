package gov.cida.sedmap.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class MultiPartHandler extends BaseHandler {

	public static final String BOUNDARY_TAG = "--AMZ90RFX875LKMFasdf09DDFF3";
	public static final String MULTI_PART_CONTENT_TYPE = "multipart/x-mixed-replace;boundary="
			+ BOUNDARY_TAG.substring(2);

	public MultiPartHandler(HttpServletResponse res, OutputStream stream) {
		super(res, stream, MULTI_PART_CONTENT_TYPE);
	}

	@Override
	public void startNewFile(String contentType, String filename) throws IOException {
		super.startNewFile(contentType, filename);

		out.write(BOUNDARY_TAG.getBytes());
		out.write(IoUtils.LINE_SEPARATOR.getBytes());
		out.write("Content-type: ".getBytes());
		out.write(contentType.getBytes());
		out.write("\n".getBytes());
		filename = "Content-Disposition: attachment; filename=" +filename;
		out.write(filename.getBytes());
		out.write(IoUtils.LINE_SEPARATOR.getBytes());
	}

	@Override
	public void finishWritingFiles() throws IOException {
		out.write(BOUNDARY_TAG.getBytes());
		String endBndry = BOUNDARY_TAG + "--";
		out.write(IoUtils.LINE_SEPARATOR.getBytes());
		out.write(endBndry.getBytes());  // write the ending boundary

		super.finishWritingFiles();
	}

}
