package gov.cida.sedmap.web;

import gov.cida.sedmap.data.GeoToolsFetcher;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.ZipHandler;
import java.io.IOException;

import javax.naming.Context;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;



public class DataService extends HttpServlet {

	private static final Logger logger = Logger.getLogger(DataService.class);

	private static final long serialVersionUID = 1L;

	public static String MODE = "NORMAL"; // TODO refactor the need for this out

	// over-ride-able for testing
	public static Context ctx;



	static {
		// this lets me know the container has initialized this servlet
		logger.debug("class loaded: " + DataService.class.getName());
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

		// TODO catch and return empty file
		FileDownloadHandler handler = new ZipHandler(resp, resp.getOutputStream());
		new GeoToolsFetcher().doFetch(req, handler);
	}
}