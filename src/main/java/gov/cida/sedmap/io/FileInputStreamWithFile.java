package gov.cida.sedmap.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileInputStreamWithFile extends FileInputStream {

	protected File file;

	public FileInputStreamWithFile(File file) throws FileNotFoundException {
		super(file);
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void delete() {
		file.delete();
	}
}
