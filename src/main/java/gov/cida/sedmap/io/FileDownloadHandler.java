package gov.cida.sedmap.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface FileDownloadHandler extends Closeable {

	String getContentType();
	FileDownloadHandler beginWritingFiles() throws IOException;
	FileDownloadHandler startNewFile(String contentType, String filename) throws IOException;
	FileDownloadHandler writeFile(String contentType, String filename, InputStream fileData) throws IOException;
	FileDownloadHandler endNewFile() throws IOException;
	FileDownloadHandler finishWritingFiles() throws IOException;
	FileDownloadHandler write(byte[] data) throws IOException;
	FileDownloadHandler write(byte[] data, int length) throws IOException;
	FileDownloadHandler write(String data) throws IOException;
	boolean isAlive();

}