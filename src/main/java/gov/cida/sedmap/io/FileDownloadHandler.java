package gov.cida.sedmap.io;

import java.io.Closeable;
import java.io.InputStream;

public interface FileDownloadHandler extends Closeable {

	String getContentType();
	FileDownloadHandler beginWritingFiles() throws Exception;
	FileDownloadHandler startNewFile(String contentType, String filename) throws Exception;
	FileDownloadHandler writeFile(String contentType, String filename, InputStream fileData) throws Exception;
	FileDownloadHandler endNewFile() throws Exception;
	FileDownloadHandler finishWritingFiles() throws Exception;
	FileDownloadHandler write(byte[] data) throws Exception;
	FileDownloadHandler write(byte[] data, int length) throws Exception;
	FileDownloadHandler write(String data) throws Exception;
	boolean isAlive();

}