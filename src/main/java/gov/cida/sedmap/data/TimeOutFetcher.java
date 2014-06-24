package gov.cida.sedmap.data;

import gov.cida.sedmap.data.agent.TimeOutAgent;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.InputStreamWithFile;
import gov.cida.sedmap.io.TimeOutHandler;
import gov.cida.sedmap.io.util.SessionUtil;
import gov.cida.sedmap.io.util.exceptions.SedmapException;
import gov.cida.sedmap.io.util.exceptions.SedmapException.OGCExceptionCode;
import gov.cida.sedmap.ogc.FilterWithViewParams;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class TimeOutFetcher extends Fetcher {
	private static final Logger logger = Logger.getLogger(TimeOutFetcher.class);
	
	public static final String TIME_EXCEEDED_RESPONSE = "time_exceeded";
	
	protected static final String TIMEOUT_ENV_KEY     = "sedmap/timoutfetcher/timeout";
	protected static final long TIMEOUT_DEFAULT     = 1000 * 60 * 1;	// 1 minute
	protected static final long TIMEOUT;
	
	protected static final String TIMEOUT_SLEEP_ENV_KEY     = "sedmap/timoutfetcher/sleep";
	protected static final long TIMEOUT_SLEEP_DEFAULT     = 100;	// 100ms
	protected static final long TIMEOUT_SLEEP;
	
	protected static final String KILL_THREAD_ENV_KEY = "sedmap/timoutfetcher/killthread";
	protected static final long KILL_THREAD_DEFAULT = 1000 * 60 * 60; // 1 hour
	protected static final long KILL_THREAD;
		
	static {
		TIMEOUT = SessionUtil.lookup(TIMEOUT_ENV_KEY, (int)TIMEOUT_DEFAULT);
		TIMEOUT_SLEEP = SessionUtil.lookup(TIMEOUT_SLEEP_ENV_KEY, (int)TIMEOUT_SLEEP_DEFAULT);
		KILL_THREAD  = SessionUtil.lookup(KILL_THREAD_ENV_KEY, (int)KILL_THREAD_DEFAULT);
	}
	
	private JdbcFetcher jdbcFetcher;	
	
	public TimeOutFetcher(String jndiJdbc) {
		this.jdbcFetcher = new JdbcFetcher(jndiJdbc);
	}

	@Override
	public void doFetch(HttpServletRequest req, FileDownloadHandler handler)
			throws Exception {
		/**
		 * Check to see if the handler is what we are expecting.
		 */
		TimeOutHandler timeoutHandler = null;
		if(handler instanceof TimeOutHandler) {
			timeoutHandler = (TimeOutHandler)handler;
		} else {
			logger.error("Client Handler used in TimeOut logic is not a TimeOutHandler.  Cannot proceed with data fetch.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(SedmapException.GENERIC_ERROR));
		}
		
		/**
		 * Lets create our threaded agent that actually kicks off the jdbcFetcher
		 */
		TimeOutAgent timeoutAgent = new TimeOutAgent(this.jdbcFetcher, timeoutHandler, req);
		
		/**
		 * This is where we control the JdbcFetcher and launch a doFetch() but
		 * watch the amount of time it takes.  If it takes longer than our timeout
		 * value we close the request with a message but continue the processing.
		 * 
		 * Lets start the thread and wait for it
		 */
		timeoutAgent.start();
		long  startTime = System.currentTimeMillis();
		long currentTime = startTime;		
		waitForThreadToFinish(timeoutAgent, startTime, currentTime, TIMEOUT);
				
		/**
		 * We're out of the loop.  Lets see if a timeout occurred
		 */
		if(!timeoutAgent.isRunning()) {			
			/**
			 * Data fetch was successful as the agent is no longer running. We can
			 * respond directly to the user with the filename and then return out
			 * of this method.
			 */	
			writeToResponse(timeoutHandler.getResponse(), timeoutHandler.getContentType(), timeoutHandler.getFileUrl().getBytes());
			
			/**
			 * All done.
			 */
			return;
		}
		
		/**
		 * Thread is still running so we need to time ourselves out.
		 * 
		 * Lets check a race condition of the handler where it is actually in
		 * the end of its logic and is dealing w/ an email already.
		 */
		boolean mustSendEmailManually = false;
		if(timeoutHandler.finishedWriting()) {
			/**
			 * The handler DOESNT send an email by default.  So if the handler is
			 * past its emailing logic we will need to send it manually.
			 */
			if(timeoutHandler.isPastEmailLogic()) {
				mustSendEmailManually = true;
			}
		} else {
			/**
			 * We need to notify the handler that it has timed out so it knows which logic paths to take
			 */
			timeoutHandler.setSendEmail(true);
		}
		
		/**
		 * We need to close the response with a "we timed out, we'll email you"
		 */
		writeToResponse(timeoutHandler.getResponse(), timeoutHandler.getContentType(), TIME_EXCEEDED_RESPONSE.getBytes());
		
		/**
		 * If we are here we had a timeout.  We now just block and wait for the
		 * handler to finish and then send an email to the user.
		 */
		waitForThreadToFinish(timeoutAgent, startTime, currentTime, KILL_THREAD);
		
		/**
		 * Handler is finished, lets see if it really finished
		 */
		if(timeoutAgent.isRunning()) {
			/**
			 * Darn thing is still running.  We need to kill it
			 */
			timeoutAgent.interrupt();
			String msg = "Data collection time limit exceeded.  Canceling job and exiting...";
			logger.error("TimeOutAgent time limit exceeded.  Interrupting thread and exiting.");
			throw new SedmapException(OGCExceptionCode.NoApplicableCode, new Exception(msg));
		}
		
		/**
		 * Data fetch is finally completed.  Lets exit out as long as we dont have
		 * to manually send an email (runtime condition above)
		 */
		if(mustSendEmailManually) {
			timeoutHandler.sendEmail();
		}
		return;		
	}

	@Override
	public Fetcher initJndiJdbcStore(String jndiJdbc) throws IOException {
		this.jdbcFetcher.initJndiJdbcStore(jndiJdbc);
		return this;
	}

	@Override
	protected InputStreamWithFile handleSiteData(String descriptor,
			FilterWithViewParams filter, Formatter formatter)
			throws IOException, SQLException, NamingException, Exception {
		return this.jdbcFetcher.handleSiteData(descriptor, filter, formatter);
	}

	@Override
	protected InputStreamWithFile handleDiscreteData(Iterator<String> sites,
			FilterWithViewParams filter, Formatter formatter)
			throws IOException, SQLException, NamingException, Exception {
		return this.jdbcFetcher.handleDiscreteData(sites, filter, formatter);
	}
	
	private void waitForThreadToFinish(TimeOutAgent timeoutAgent, long startTime, long currentTime, long waitTime) throws Exception {
		while((currentTime - startTime) < waitTime) {
			/**
			 * Lets check to see if the handler thread is finished
			 */
			if(!timeoutAgent.isRunning()) {
				/**
				 * Agent is done, lets exit!
				 */
				break;
			}
			
			/**
			 * Handler isnt finished yet, lets wait again.
			 */
			Thread.sleep(TIMEOUT_SLEEP);
			
			/**
			 * Update our time
			 */
			currentTime = System.currentTimeMillis();
		}
		
		/**
		 * We're out of the loop, lets see if there was an exception in the thread
		 */
		if(timeoutAgent.exceptionThrown()) {
			/**
			 * Something went wrong in the fetcher.  Lets throw the exception
			 * caught and let the data service handle it.
			 */
			throw timeoutAgent.getException();
		}
	}
	
	private void writeToResponse(HttpServletResponse response, String contentType, byte[] content) throws Exception {
		if(response != null) {
			response.setContentType( contentType );
			response.getOutputStream().write(content);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}
}
