package gov.cida.sedmap.data;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.util.SessionUtil;

public class DataFileMgr {
	private static final Logger logger = Logger.getLogger(DataFileMgr.class);

	protected static final String PATH_ENV_KEY   = "sedmap/data/path";
	protected static final String PATH_DEFAULT   = "/tmp";
	protected static final String DATA_PATH;

	protected static final String RETAIN_ENV_KEY = "sedmap/data/retain";
	protected static final int    RETAIN_DEFAULT = 7;
	protected static final int    RETAIN_TIME;
	public    static final int    RETAIN_DAYS;

	
	public static final String DAILY_FILENAME    = "daily_data";
	public static final String BATCH_FILENAME    = "daily_batch";
	public static final String DISCRETE_FILENAME = "discrete_data";
	public static final String DATA_FILENAME     = "data";


	static {
		DATA_PATH   = SessionUtil.lookup(PATH_ENV_KEY,  PATH_DEFAULT);

		RETAIN_DAYS = SessionUtil.lookup(RETAIN_ENV_KEY,  RETAIN_DEFAULT);
		RETAIN_TIME = RETAIN_DAYS * 24 * 3600 * 1000; //convert days to milliseconds
	}



	public File fetchDataFile(String fileId) {
		File file = new File(DATA_PATH +"/"+ DATA_FILENAME +"_"+ fileId +".zip");
		if ( file.exists() ) {
			return file;
		}
		logger.info("file was not found " + file.getAbsolutePath());
		return null;
	}



	public int deleteOldFiles() {
		int count=0;
		try {
			File[] tempFiles = fetchTempFiles();
	
			for (File tempFile : tempFiles) {
				count+=deleteIfOld(tempFile);
			}
			if (count != 0) {
				logger.info("deleted " +count+ " old files");
			}
		} catch	(Exception e) {
			logger.error("There was an issue deleting old data files",e);
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
				boolean accept = name.startsWith(DAILY_FILENAME)
						|| name.startsWith(DISCRETE_FILENAME)
						|| name.startsWith(DATA_FILENAME)
						|| name.startsWith(BATCH_FILENAME);
				return accept;
			}
		});
		return files;
	}


	public int deleteIfOld(File file) {
		if (file.lastModified()+RETAIN_TIME <  System.currentTimeMillis()) {
			IoUtils.deleteFile(file);
			return 1;
		}
		return 0;
	}
}
