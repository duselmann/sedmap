package gov.cida.sedmap.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.cida.sedmap.data.DataFileMgr;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.MissingFileHandler;
import gov.cida.sedmap.io.RawHandler;
import gov.cida.sedmap.io.util.ErrUtils;



public class DownloadService extends HttpServlet {

	private static final Logger logger = Logger.getLogger(DownloadService.class);

	private static final long serialVersionUID = 1L;


	static {
		// this lets me know the container has initialized this servlet
		logger.debug("class loaded: " + DownloadService.class.getName());
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
			String fileId = req.getParameter("file");
			File file = new DataFileMgr().fetchDataFile(fileId);
			logger.info("User requesting file ID " + fileId);

			if (file==null) {
				logger.info("User requested file ID missing " + file);
				handler = new MissingFileHandler(res, res.getOutputStream(), fileId);
			} else {
				logger.info("Sending User requested file " + file.getAbsolutePath());
				handler = new RawHandler(res, res.getOutputStream(), file);
			}
			handler.beginWritingFiles();
			handler.finishWritingFiles();
		} catch (Exception e) {
			ErrUtils.handleExceptionResponse(req,res,e);
		} finally {
			IoUtils.quiteClose(handler);
		}
	}

}