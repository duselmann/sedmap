package gov.cida.sedmap.data.agent;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import gov.cida.sedmap.data.JdbcFetcher;
import gov.cida.sedmap.io.TimeOutHandler;

public class TimeOutAgent extends Thread {
	private static final Logger logger = Logger.getLogger(TimeOutAgent.class);
	
	private JdbcFetcher jdbcFetcher;
	private TimeOutHandler timeoutHandler;
	private HttpServletRequest request;
	
	private volatile boolean isRunning = false;
	
	private boolean isStarted = false;
	
	private Exception exception = null; 
	
	public TimeOutAgent(JdbcFetcher jdbcFetcher, TimeOutHandler timeoutHandler, HttpServletRequest request) {
		this.jdbcFetcher = jdbcFetcher;
		this.timeoutHandler = timeoutHandler;
		this.request = request;
		
		this.isRunning = false;
		isStarted = false;
		this.exception = null;
	}
	
	public void run() {
		try {
			isStarted = true;
			isRunning = true;
			jdbcFetcher.doFetch(this.request, this.timeoutHandler);
		} catch (InterruptedException e) {
			logger.info("TimeOutAgent has received a stop interrupt.  Exiting agent...");
			exception = e;
		} catch (Exception e) {
			logger.info("TimeOutAgent has received an exception.  [" + e.getMessage() + "]");
			exception = e;
		}
		isRunning = false; // this used to be set in all blocks above
	}

	public boolean isRunning() {
		return isStarted & isRunning;
	}

	public boolean isError() {
		return exception != null;
	}

	public Exception getException() {
		return exception;
	}
}
