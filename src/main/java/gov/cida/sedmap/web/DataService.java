package gov.cida.sedmap.web;

import gov.cida.sedmap.data.DataFileMgr;
import gov.cida.sedmap.data.Fetcher;
import gov.cida.sedmap.data.FetcherConfig;
import gov.cida.sedmap.data.JdbcFetcher;
import gov.cida.sedmap.data.TimeOutFetcher;
import gov.cida.sedmap.io.EmailLinkHandler;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.TimeOutHandler;
import gov.cida.sedmap.io.ZipHandler;
import gov.cida.sedmap.io.util.ErrUtils;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

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
	/**
	 * ERROR HANDLING:
	 * 
	 * When the request is a direct download we will respond with OGC XML error
	 * response.  If its a parameter or filter issue we will (hopefully) respond
	 * with a good enough message that the user can fix it themselves.
	 * 
	 * If its an internal error (i.e. file I/O, database connectivity etc) we do
	 * not want the user to know internal messages so we will respond with a
	 * generic error. 
	 * 
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		logger.debug("doPost");

		try {
			int count = new DataFileMgr().deleteOldFiles();
			if (count>0) {
				logger.error("Deleting old data files: " + count);
			}
		} catch (Exception e) {
			logger.error("Error deleting old data files", e);
		}

		FileDownloadHandler handler = null;
		try {			
			String email = req.getParameter("email");
			
			boolean doDownload = false;
			String directDownload = req.getParameter("directDownload");
			if((directDownload != null) && (directDownload.equals("true"))) {
				doDownload = true;
			}
			
			if ( doDownload ) {
				/**
				 * JIRA NSM-82 and NSM-251
				 * To use direct download ability with error handling, we cannot
				 * use the response output stream until we have something to 
				 * hand back to the user.  We need to build our own I/O and then
				 * when we have something to give back to the user, use the response.
				 */
				
				/**
				 * JIRA NSM-251
				 * 
				 * 	In order to accomplish direct download and deal with the fact that
				 * 	we have a WAF timeout issue, we can only do UI requests when an email
				 * 	is given.  We have a validation on the UI to force an email but to
				 * 	realistically provide a Web Service we should not enforce this
				 *  requirement here.  If we do not get an email, we will then write
				 *  the resulting zip file contents directly to the request stream without
				 *  a consideration of the WAF timeout.
				 *  
				 *  If we DO get an email, then we will do our timeout logic which doesn't
				 *  return the zip contents but a link to the zip created OR a message saying
				 *  we have exceeded our processing time and an email will be sent w/ the
				 *  file link.
				 */
				if((email == null) || (email.equals(""))) {
					/**
					 * We do not have an email so we will attempt to process the request
					 * and stream back the file to the client response.
					 */
					File   tmp = null;
					String zipName = "data_" + StrUtils.uniqueName(12);
					tmp = File.createTempFile("data_" + StrUtils.uniqueName(12), ".zip");
					handler = new ZipHandler(res, new FileOutputStream(tmp), zipName);
					
					Fetcher fetcher = new JdbcFetcher(jndiDS);
						
					long startTime = System.currentTimeMillis();
					fetcher.doFetch(req, handler);
					long totalTime = System.currentTimeMillis() - startTime;
					logger.info(fetcher.toString() + ": Total request time (ms) " + totalTime);
						
					/**
					 * The fetch was successful we now stream the file to the response
					 */
					res.setContentType( handler.getContentType() );
					res.getOutputStream().write(Files.readAllBytes(tmp.toPath()));
					res.getOutputStream().flush();
					res.getOutputStream().close();					
				} else {
					/**
					 * This is a UI direct download request.  In order to make it
					 * work as efficiently as possible, we will reply to the client
					 * stream with a link to the file when it is actually finished just
					 * like we do in the email handler.  This makes the UI easy to
					 * code such that we just wait for a response from the server
					 * on the UI side and test the response content for either a file
					 * download link, a flag stating the time has exceeded or an
					 * OGC error message.
					 * 
					 * If we exceed our timeout limit while processing, we will reply
					 * to the stream with a string of TimeOutFetcher.TIME_EXCEEDED_RESPONSE
					 */
					File   tmp = null;
					tmp = File.createTempFile("data_" + StrUtils.uniqueName(12), ".zip");
					handler = new TimeOutHandler(res, tmp, email);
						
					Fetcher fetcher = new TimeOutFetcher(jndiDS);
						
					long startTime = System.currentTimeMillis();
					fetcher.doFetch(req, handler);
					long totalTime = System.currentTimeMillis() - startTime;
					logger.info(fetcher.toString() + ": Total request time (ms) " + totalTime);
				}
			} else {
				/**
				 * This is an email download request.
				 */
				File   tmp = File.createTempFile("data_" + StrUtils.uniqueName(12), ".zip");
				
				logger.debug(tmp.getAbsolutePath());
				handler = new EmailLinkHandler(res, tmp, email);
				
				Fetcher fetcher = new JdbcFetcher(jndiDS);
				
				long startTime = System.currentTimeMillis();
				fetcher.doFetch(req, handler);					// This is an "email" handler then
																// the internal workings CLOSE the client
																// response connection and releases the
																// browser but continues execution.
																// This is important as it looks like
																// this is threaded from a client POV but
																// in reality all we did was close the
																// client stream.
				long totalTime = System.currentTimeMillis() - startTime;
				logger.info(fetcher.toString() + ": Total request time (ms) " + totalTime);
			}
		} catch (Exception e) {
			String errorid = null;
			try {
				if (handler instanceof EmailLinkHandler) {
					errorid = ErrUtils.handleExceptionResponse(req,res,e);
					((EmailLinkHandler)handler).setErrorId(errorid);
					((EmailLinkHandler)handler).setExceptionThrown(e);
					handler.finishWritingFiles();
				} else {
					if (e instanceof SedmapException) {
						ErrUtils.handleExceptionResponseServices(res, (SedmapException)e);
					} else {
						/**
						 * This is not a sedmap exception.  We must
						 * create a SedmapException to respond with a legitimate
						 * xml response.
						 * 
						 * Since there really is no way to accurately determine
						 * what a value is, we'll just set it to NoApplicableCode,
						 * set the message to a Generic one and then let the
						 * ErrUtils deal with it.
						 */
						logger.error("Exception in DataService:" +  e.getMessage());
						logger.error("Due to internal exception caught, throwing generic OGC error for error handling on the client side.");
						SedmapException exception = new SedmapException(OGCExceptionCode.NoApplicableCode, e);
						exception.setExceptionMessage(SedmapException.GENERIC_ERROR);
						ErrUtils.handleExceptionResponseServices(res, exception);
					}
				}
			} catch (Exception t) {
				logger.error(t);
			}
		} finally {
			IoUtils.quiteClose(handler);
		}
	}

}