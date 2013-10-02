package gov.cida.sedmap.web;

import gov.cida.sedmap.data.DataFileMgr;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.RawHandler;
import gov.cida.sedmap.io.util.ErrUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



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
		FileInputStream fis = null;
		try {
			String fileName = req.getParameter("file");
			File file = new DataFileMgr().getDataFile(fileName);
			fis = new FileInputStream(file);

			handler = new RawHandler(res, res.getOutputStream());
			handler.beginWritingFiles();

			int count=0;
			byte[] data=new byte[1024<<3]; // 8k buffer

			while ( (count=fis.read(data)) >0 ) {
				handler.write(data, count);
			}

			handler.finishWritingFiles();
		} catch (Exception e) {
			IoUtils.quiteClose(fis);
			IoUtils.quiteClose(handler);
			ErrUtils.handleExceptionResponse(req,res,e);
		}
	}

}