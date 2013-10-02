package gov.cida.sedmap.io;


import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

public class RawHandler extends BaseHandler {

	protected static final String CONTENT_TYPE= "application/zip"; // TODO make this configurable

	public RawHandler(HttpServletResponse res, OutputStream stream) {
		super(res, stream, CONTENT_TYPE);
	}

	@Override
	public FileDownloadHandler beginWritingFiles() throws IOException {
		super.beginWritingFiles();
		resp.addHeader("Content-Disposition", "attachment; filename=data.zip");
		return this; //chain
	}
}
