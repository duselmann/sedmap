package gov.cida.sedmap.web;

import gov.cida.sedmap.data.Fetcher;
import gov.cida.sedmap.data.FetcherConfig;
import gov.cida.sedmap.data.JdbcFetcher;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.ZipHandler;
import gov.cida.sedmap.io.util.ErrUtils;

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

		FileDownloadHandler handler = null;
		try {
			Fetcher fetcher = new JdbcFetcher(jndiDS);

			handler = new ZipHandler(res, res.getOutputStream());
			fetcher.doFetch(req, handler);
		} catch (Exception e) {
			IoUtils.quiteClose(handler);
			ErrUtils.handleExceptionResponse(req,res,e);
		}
	}

}