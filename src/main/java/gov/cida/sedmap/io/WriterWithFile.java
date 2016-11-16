package gov.cida.sedmap.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;


public class WriterWithFile extends BufferedWriter {
	protected File file;

	public WriterWithFile(Writer out, File file) {
		super(out);
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	
	public void writeLine(String line) throws IOException {
		write(line);
		write(IoUtils.LINE_SEPARATOR);
	}

	public void deleteFile() {
		IoUtils.quietClose(this);
		IoUtils.deleteFile(file);
	}
}
