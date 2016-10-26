package gov.cida.sedmap.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamWithFile extends InputStream {

	protected InputStream in;
	protected File file;


	public InputStreamWithFile(File file) throws FileNotFoundException {
		in   = new FileInputStream(file);
		this.file = file;
	}
	public InputStreamWithFile(InputStream in, File file) throws FileNotFoundException {
		this.in = in;
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void deleteFile() {
		IoUtils.quiteClose(this);
		IoUtils.deleteFile(file);
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}
	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}
}
