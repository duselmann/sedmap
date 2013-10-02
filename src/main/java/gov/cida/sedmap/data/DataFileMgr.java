package gov.cida.sedmap.data;

import java.io.File;
import java.io.FilenameFilter;
import gov.cida.sedmap.io.util.SessionUtil;

public class DataFileMgr {

	protected static final String PATH_ENV_KEY = "sedmap/data/path";
	protected static final String PATH_DEFAULT = "/tmp";
	protected static final String DATA_PATH;

	protected static final String RETAIN_ENV_KEY = "sedmap/data/retain";
	protected static final int    RETAIN_DEFAULT = 7;
	protected static final int    RETAIN_DAYS;



	static {
		DATA_PATH      = SessionUtil.lookup(PATH_ENV_KEY,  PATH_DEFAULT);

		int retainDays = SessionUtil.lookup(RETAIN_ENV_KEY,  RETAIN_DEFAULT);
		RETAIN_DAYS    = retainDays * 24 * 3600 * 1000; //convert days to milliseconds
	}



	public File getDataFile(String fileName) { // TODO fetch not get
		File file = new File(DATA_PATH +"/"+ fileName);
		if ( file.exists() ) {
			return file;
		}
		return null;
	}



	public int deleteOldFiles() {
		File[] tempFiles = fetchTempFiles();

		int count=0;
		for (File tempFile : tempFiles) {
			count+=deleteIfOld(tempFile);
		}
		return count;
	}



	public File[] fetchTempFiles() {
		// maybe recursively check sub-directories

		File dir = new File(DATA_PATH);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				boolean accept = name.startsWith("daily_")
						|| name.startsWith("discrete_")
						|| name.startsWith("data_");
				return accept;
			}
		});
		return files;
	}


	public int deleteIfOld(File file) {
		if (file.lastModified()+RETAIN_DAYS <  System.currentTimeMillis()) {
			file.delete();
			return 1;
		}
		return 0;
	}
}
