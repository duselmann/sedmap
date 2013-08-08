package gov.cida.sedmap.web;

import gov.cida.sedmap.data.CsvFormatter;
import gov.cida.sedmap.data.Formatter;
import gov.cida.sedmap.data.RdbFormatter;
import gov.cida.sedmap.ogc.OgcUtils;
import gov.cida.sedmap.util.IoUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;



public class DataFetch extends HttpServlet {

	//	private static final Logger logger = LoggerFactory.getLogger(DataFetch.class);
	private static final Logger logger = Logger.getLogger(DataFetch.class);

	private static final long serialVersionUID = 1L;
	private static final String SEDMAP_DS = "java:comp/env/jdbc/sedmapDS";

	private static final Map<String, Class<? extends Formatter>> FORMATS = new HashMap<String, Class<? extends Formatter>>();
	private static final Map<String, String> FIELD_TRANSLATIONS = new HashMap<String, String>();

	static {
		FIELD_TRANSLATIONS.put("HUC_8","HUC_12");
		FORMATS.put("csv", CsvFormatter.class);
		FORMATS.put("rdb", RdbFormatter.class);
	}

	static {
		logger.debug("class loaded: " + DataFetch.class.getName());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doGet");
		doFetch(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doPost");
		doFetch(req, resp);
	}

	protected void doFetch(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doFetch");

		String ogcXml = req.getParameter("filter");
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

		if (ogcXml == null) {
			logger.error("Failed to locate OGC 'filter' parameter");
			// TODO empty result
			return;
		}
		if (ogcXml.trim() == "") {
			logger.warn("user opted for all data");
			// TODO ALL results - no where clause
			return;
		} else {

			String where = "";
			try {
				where = OgcUtils.ogcXml2Sql(ogcXml);
				where = " where " + OgcUtils.sqlTranslation(where, FIELD_TRANSLATIONS);
			} catch (Exception e) {
				logger.error("failed to parse OGC", e);
				// TODO empty results and err msg
				return;
			}

			logger.debug(where);
			Connection cn = null;
			Statement  st = null;

			String sql = "select * from sm_inst_stations " + where;
			logger.debug(sql);

			ResultSet  rs = null;
			try {
				InitialContext ctx = new InitialContext();
				DataSource ds = (DataSource) ctx.lookup(SEDMAP_DS);

				cn = ds.getConnection();
				st = cn.createStatement();
				rs = st.executeQuery(sql);

				int r = 1;
				resp.setContentType( formatter.getContentType() );
				resp.getWriter().append( formatter.fileHeader(rs) );
				while (rs.next()) {
					logger.debug("row " + r++ );
					resp.getWriter().append( formatter.fileRow(rs) );
				}

			} catch (Exception e) {
				logger.error("failed to fetch from DB", e);
				// TODO empty results and err msg
				return;
			} finally {
				IoUtils.quiteClose(rs, st, cn);
			}
		}

	}



}
