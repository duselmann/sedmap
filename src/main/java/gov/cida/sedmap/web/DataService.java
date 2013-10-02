package gov.cida.sedmap.web;

import gov.cida.sedmap.data.DataFileMgr;
import gov.cida.sedmap.data.Fetcher;
import gov.cida.sedmap.data.FetcherConfig;
import gov.cida.sedmap.data.JdbcFetcher;
import gov.cida.sedmap.io.EmailLinkHandler;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.ZipHandler;
import gov.cida.sedmap.io.util.ErrUtils;
import gov.cida.sedmap.io.util.StrUtils;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



public class DataService extends HttpServlet {

	private static final Logger logger = Logger.getLogger(DataService.class);

	private static final long serialVersionUID = 1L;

	protected String jndiDS = "java:comp/env/jdbc/sedmapDS";



	static {
		// this lets me know the container has initialized this servlet
		logger.debug("class loaded: " + DataService.class.getName());
	}



	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.info("initializing fetcher configuration");
		Fetcher.conf = new FetcherConfig(jndiDS).init();
	}



	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		logger.debug("doGet - delegating to doPost");
		doPost(req, res);
	}



	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		logger.debug("doPost");

		try {
			new DataFileMgr().deleteOldFiles();
		} catch (Exception e) {
			logger.error("Error deleting old data files", e);
		}

		FileDownloadHandler handler = null;
		try {
			Fetcher fetcher = new JdbcFetcher(jndiDS);

			String email = req.getParameter("email");
			if ( StrUtils.isEmpty(email) ) {
				handler = new ZipHandler(res, res.getOutputStream());
			} else {
				File   tmp = File.createTempFile("data_" + StrUtils.uniqueName(12), ".zip");
				logger.debug(tmp.getAbsolutePath());
				handler = new EmailLinkHandler(res, tmp, email);
			}

			fetcher.doFetch(req, handler);
		} catch (Exception e) {
			IoUtils.quiteClose(handler);
			ErrUtils.handleExceptionResponse(req,res,e);
		}
	}

}