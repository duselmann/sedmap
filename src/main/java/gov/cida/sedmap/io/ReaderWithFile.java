package gov.cida.sedmap.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class ReaderWithFile extends BufferedReader {
	protected File file;

	public ReaderWithFile(InputStream in, File file) {
		this(new InputStreamReader(in) ,file);
	}
	public ReaderWithFile(Reader in, File file) {
		super(in);
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void deleteFile() {
		IoUtils.quiteClose(this);
		IoUtils.deleteFile(file);
	}
}
