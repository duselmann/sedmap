package gov.cida.sedmap.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.Writer;

import org.apache.log4j.Logger;

public class WriterWithFile extends BufferedWriter {
	private static final Logger logger = Logger.getLogger(BufferedWriter.class);

	protected File file;

	public WriterWithFile(Writer out, File file) {
		super(out);
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
