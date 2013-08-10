package gov.cida.sedmap.web;

import gov.cida.sedmap.data.CsvFormatter;
import gov.cida.sedmap.data.Formatter;
import gov.cida.sedmap.data.RdbFormatter;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.ZipHandler;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.ogc.OgcUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;



public class DataService extends HttpServlet {

	//	private static final Logger logger = LoggerFactory.getLogger(DataFetch.class);
	private static final Logger logger = Logger.getLogger(DataService.class);

	private static final long serialVersionUID = 1L;
	protected static final String SEDMAP_DS = "java:comp/env/jdbc/sedmapDS";

	protected static final Map<String, Class<? extends Formatter>> FORMATS = new HashMap<String, Class<? extends Formatter>>();
	protected static final Map<String, String> FIELD_TRANSLATIONS = new HashMap<String, String>();

	protected static final Map<String, String> SQL_QUERIES = new HashMap<String, String>();
	protected static final String[] DATA_TYPES  = {"daily","discrete"};
	protected static final String[] DATA_VALUES = {"sites","data"};


	static {
		logger.debug("class loaded: " + DataService.class.getName());

		FIELD_TRANSLATIONS.put("HUC_8","HUC_12");

		FORMATS.put("csv", CsvFormatter.class);
		FORMATS.put("rdb", RdbFormatter.class);

		SQL_QUERIES.put("daily_sites",    "select * from sm_daily_stations ");
		SQL_QUERIES.put("discrete_sites", "select * from sm_inst_stations ");
		SQL_QUERIES.put("discrete_data",  "select d.* from sm_inst_sample_fact d join sm_inst_stations s on s.USGS_STATION_ID = d.USGS_STATION_ID ");
	}



	protected static class Results {
		Connection cn;
		Statement  st;
		ResultSet  rs;
	}



	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doGet - delegating to doPost");
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doPost");

		// TODO catch
		FileDownloadHandler handler = new ZipHandler(resp, resp.getOutputStream());
		doFetch(req, handler);
	}



	protected void doFetch(HttpServletRequest req, FileDownloadHandler handler)
			throws ServletException, IOException {
		logger.debug("doFetch");

		String    dataTypes = getDataTypes(req);
		Formatter formatter = getFormatter(req);
		String    ogcXml    = getFilter(req);
		String    where     = ogc2sql(ogcXml); // TODO test for SQL injection


		handler.beginWritingFiles(); // start writing files

		for (String value : DATA_VALUES) { // check for daily and discrete
			if ( ! dataTypes.contains(value) ) continue;
			for (String site  : DATA_TYPES) { // check for sites and data
				if ( ! dataTypes.contains(site) ) continue;

				StringBuilder  name = new StringBuilder();
				String   descriptor = name.append(site).append('_').append(value).toString();
				String     filename = descriptor + formatter.getFileType();

				InputStream fileData = null;
				try {
					if ( "daily_data".equals(descriptor) ) {
						fileData = handleNwisData(descriptor, where, formatter);
					} else {
						fileData = handleLocalData(descriptor, where, formatter);
					}
					handler.writeFile(formatter.getContentType(), filename, fileData);

				} catch (Exception e) {
					logger.error("failed to fetch from DB", e);
					// TODO empty results and err msg to user
					return;
				} finally {
					IoUtils.quiteClose(fileData);
				}
			}
		}
		handler.finishWritingFiles(); // done writing files

	}



	protected InputStream handleLocalData(String descriptor, String where, Formatter formatter)
			throws IOException, SQLException, NamingException {
		InputStream fileData = null;
		Results rs = new Results();

		try {
			String sql = SQL_QUERIES.get(descriptor) + where;
			logger.debug(sql);
			rs = getData(sql);

			File   tmp = File.createTempFile(descriptor + StrUtils.uniqueName(12), formatter.getFileType());
			FileWriter tmpw = new FileWriter(tmp);

			String header = formatter.fileHeader(rs.rs);
			//logger.debug(header);
			tmpw.write(header);
			while (rs.rs.next()) {
				String row = formatter.fileRow(rs.rs);
				//logger.debug(row);
				tmpw.write(row);
			}
			IoUtils.quiteClose(tmpw);

			fileData = new FileInputStream(tmp);
			tmp.delete(); // TODO not for delayed download

		} finally {
			IoUtils.quiteClose(rs.rs, rs.st, rs.cn);
		}

		return fileData;
	}


	protected InputStream handleNwisData(String descriptor, String where, Formatter formatter)
			throws IOException, SQLException, NamingException {
		InputStream fileData = null;

		// TODO handle nwis request

		return fileData;
	}


	protected String ogc2sql(String ogcXml) {
		String where = "";

		if ("".equals(ogcXml)) return where;
		try {
			where = OgcUtils.ogcXml2Sql(ogcXml);
			if (where != null && where.trim().length()>0) {
				where = " where " + OgcUtils.sqlTranslation(where, FIELD_TRANSLATIONS);
			} else {
				where = "";
			}
		} catch (Exception e) {
			// TODO empty results and err msg
			throw new RuntimeException("Failed to parse OGC", e);
		}

		return where;
	}



	protected String getFilter(HttpServletRequest req) {
		String ogcXml = req.getParameter("filter");

		if (ogcXml == null) {
			logger.warn("Failed to locate OGC 'filter' parameter - using default");
			// TODO empty result
			return "";
		}

		return ogcXml;
	}



	protected String getDataTypes(HttpServletRequest req) {
		String types = req.getParameter("dataTypes");

		// expecting a string with terms "daily_discrete_sites_data" in it
		// sites means site data - no samples
		// data  means site data - no site info
		// daily and discrete refer to samples
		// - "daily_discrete_sites_data" means they want all data
		// - "daily_sites" means they want all daily site info only

		if (types == null) {
			logger.warn("Failed to locate 'dataTypes' parameter - using default");
			types = "daily_discrete_sites";
		}

		return types;
	}



	protected Formatter getFormatter(HttpServletRequest req) {
		String format = req.getParameter("format");
		format = FORMATS.containsKey(format) ?format :"rdb";

		Formatter formatter = null;
		try {
			formatter = FORMATS.get(format).newInstance();
		} catch (Exception e) {
			logger.warn("Could not instantiate formatter for '" +format+"' with class "
					+FORMATS.get(format)+". Using RDB as fall-back.");
			formatter = new RdbFormatter();
		}

		return formatter;
	}



	protected Results getData(String sql) throws NamingException, SQLException {
		Results r = new Results();

		try {
			Context ctx = getContext();
			DataSource ds = (DataSource) ctx.lookup(SEDMAP_DS);
			r.cn = ds.getConnection();
			r.st = r.cn.createStatement();
			r.rs = r.st.executeQuery(sql);
		} catch (SQLException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}

		return r;
	}

	// over-ride-able for testing
	protected Context getContext() throws NamingException {
		InitialContext ctx = new InitialContext();
		return ctx;
	}


	/*

 public void doGett(HttpServletRequest req, HttpServletResponse  res)
                            throws ServletException, IOException{

      final byte[] CRLF = new byte[]{(byte)'\r', (byte)'\n'};
      String boundaryTxt = "--AMZ90RFX875LKMFasdf09DDFF3";
      String fileName = "Servlet_multipart";

      res.setContentType("multipart/x-mixed-replace;boundary=" + boundaryTxt.substring(2));

     // Read from file into servlet output stream
     ServletOutputStream stream = res.getOutputStream();

     byte[] boundary = boundaryTxt.getBytes();
     stream.write(boundary, 0, boundary.length);  // write the first boundary
     stream.write(CRLF, 1, 1);         // pls LF
     byte[] contentType = "Content-type: text/plain\n".getBytes();

     // Write out a couple of files
     for(int i = 0; i < 2; i++) {
        stream.write(contentType, 0, contentType.length);
        byte[] contDisp = ("Content-Disposition: attachment; filename="
                                    + fileName + i + "\r\n\r\n").getBytes(); // blank line follows
        stream.write(contDisp, 0, contDisp.length);

        // Lets read this file???
        BufferedInputStream fif = new BufferedInputStream(
                       new FileInputStream("D:\\JavaDevelopment\\runtime\\CheckClassRefs.ini"));

        if (verbose) System.out.println("Sending file " + i + " to client as '" + fileName + i +"'");
        int data = 0;
        int  byteCnt = 0;
        while((data = fif.read()) != -1) {
            stream.write(data);
            byteCnt++;
        }
        fif.close();
        stream.write(boundary, 0, boundary.length);  // write bndry after data
        stream.write(CRLF, 1, 1);  // plus LF
        if (verbose) System.out.println("Finished sending file " + i +". " + byteCnt + " bytes");
     } // end for(i)  outputing files

     byte[] endBndry = (boundary + "--").getBytes();
     stream.write(endBndry, 0, endBndry.length);  // write the ending boundary
     stream.close();
    }

	 */



}
