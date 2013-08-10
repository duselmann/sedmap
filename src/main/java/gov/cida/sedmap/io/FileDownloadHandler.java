package gov.cida.sedmap.io;

import java.io.IOException;
import java.io.InputStream;

public interface FileDownloadHandler {

	String getContentType();
	void beginWritingFiles() throws IOException;
	void startNewFile(String contentType, String filename) throws IOException;
	void writeFile(String contentType, String filename, InputStream fileData) throws IOException;
	void endNewFile() throws IOException;
	void finishWritingFiles() throws IOException;
	void write(byte[] data) throws IOException;
	void write(String data) throws IOException;

}