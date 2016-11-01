package gov.cida.sedmap.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import gov.cida.sedmap.io.util.StrUtils;

public class IoUtils {

	private static final Logger logger = Logger.getLogger(IoUtils.class);

	public static final String LINE_SEPARATOR  = System.getProperty("line.separator");

	public static void quiteClose(Object ... open) {

		if (open == null) return;

		for (Object o : open) {
			if (o == null) continue;

			try {
				logger.debug("Closing resource. " + o.getClass().getName());
				if        (o instanceof Connection) {
					if ( !((Connection)o).isClosed() ) {
						((Connection)o).close();
					}
				} else if (o instanceof Statement) {
					if (  !( (Statement)o).isClosed() ) {
						( (Statement)o).close();
					}
				} else if (o instanceof ResultSet) {
					if (  !( (ResultSet)o).isClosed() ) {
						( (ResultSet)o).close();
					}
				} else if (o instanceof Closeable) {
					// cannot test for close so have to catch exception
					try { ((Closeable)o).close(); } catch (Exception e) {}
				} else {
					throw new UnsupportedOperationException("Cannot handle closing instances of " + o.getClass().getName());
				}

			} catch (UnsupportedOperationException e) {
				throw e;

			} catch (Exception e) {
				logger.warn("Failed to close resource. " + o.getClass().getName(), e);
			}
		}
	}



	public static String readTextResource(String resource) {
		String contents = "";

		try {
			InputStream in = IoUtils.class.getResourceAsStream(resource); //, "UTF-8");
			contents = readStream(in);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to open the rdb-metadata.txt file.",ex);
		}
		return contents;
	}

	// This should not be used for large data files because it does not stream
	// It is currently used for small header files and test data assertions
	// see the streamed copy method below
	public static String readStream(InputStream in) throws IOException {
		StringBuilder buf = new StringBuilder();

		InputStreamReader reader = new InputStreamReader(in);

		char[] chars = new char[1024];

		int count = 0;
		while ( (count = reader.read(chars)) > 0 ) {
			buf.append(chars,0,count);
		}

		return buf.toString();
	}
	// also for testing
	public static String readZip(File file) throws IOException {
		String read = readStream( createTmpZipStream(file) );
		deleteFile(file);
		return read;
	}


	public static String createTmpFileName(String name) {
		return name +'_'+ StrUtils.uniqueName(12);
	}
	
	public static File createTmpZipFile(String filename) throws IOException {
		File file = File.createTempFile(createTmpFileName(filename), ".zip");
		logger.debug(file.getAbsolutePath());
		return file;
	}
	
	public static ZipOutputStream createTmpZipOutStream(File file) throws IOException {
		FileOutputStream out   = new FileOutputStream(file);
		ZipOutputStream zip    = new ZipOutputStream(out);

		return zip;
	}

	public static WriterWithFile createTmpZipWriter(String filename, String extention) throws IOException {
		File file              = createTmpZipFile(filename);
		ZipOutputStream zip    = createTmpZipOutStream(file);
		
		ZipEntry entry         = new ZipEntry(filename + extention);
		OutputStreamWriter osw = new OutputStreamWriter(zip);
		WriterWithFile tmp     = new WriterWithFile(osw, file);

		zip.putNextEntry(entry);

		return tmp;
	}



	public static InputStreamWithFile createTmpZipStream(File file) throws IOException {
		try {
			FileInputStream fis = new FileInputStream(file);
			ZipInputStream  zip = new ZipInputStream(fis);
			InputStreamWithFile fisf = new InputStreamWithFile(zip, file);
	
			zip.getNextEntry(); // open the first entry
			// strictly speaking, there could be many entries; however, this is to be used for single entry
			// zip files. While the final output file will contain many entries, portions are downloaded
			// into individual single entry zip files and then merged into the one for the user.
			// The ZipHandler processes the only multiple entry zip file.
			
			return fisf;
		} catch (IOException e) {
			String filename = "";
			if (file != null) {
				filename = file.getAbsolutePath();
			}
			logger.error("Failed to open zip file " + filename, e);
			throw e;
		}
	}

    public static String readTextResource() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



	public static void copy(File sourceFile, WriterWithFile writer) throws IOException {
		try (InputStreamWithFile source = createTmpZipStream(sourceFile)) {
			Reader reader = new InputStreamReader(source);
			IOUtils.copy(reader, writer);
		}
	}
	
	public static void deleteFile(File file) {
		if (file == null) {
			return;
		}

		if (logger.isDebugEnabled()) {
			String existence = ( ! file.exists() ) ?"(not found) " :"";
			logger.debug("deleting file "+ existence + file.getAbsolutePath());
		}
		
		if ( ! file.delete() ) {
			file.deleteOnExit();
		}
	}

}
