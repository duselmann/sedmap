package gov.cida.sedmap.io;

import java.io.BufferedWriter;
import java.io.File;
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

	public void deleteFile() {
		IoUtils.quiteClose(this);

		if ( ! file.delete() ) {
			file.deleteOnExit();
		}
	}
}
