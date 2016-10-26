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
	
	private volatile boolean running = false;
	
	private boolean hasStarted = false;
	
	private boolean exceptionThrown = false;
	private Exception exception = null; 
	
	public TimeOutAgent(JdbcFetcher jdbcFetcher, TimeOutHandler timeoutHandler, HttpServletRequest request) {
		this.jdbcFetcher = jdbcFetcher;
		this.timeoutHandler = timeoutHandler;
		this.request = request;
		
		this.running = false;
		hasStarted = false;
		this.exceptionThrown = false;
		this.exception = null;
	}
	
	public void run() {
		try {
			hasStarted = true;
			this.running = true;
			this.jdbcFetcher.doFetch(this.request, this.timeoutHandler);
			this.running = false;
		} catch (InterruptedException e) {
			logger.info("TimeOutAgent has received a stop interrupt.  Exiting agent...");
			this.running = false;
			this.exceptionThrown = true;
			this.exception = e;
		} catch (Exception e) {
			logger.info("TimeOutAgent has received an exception.  [" + e.getMessage() + "]");
			this.running = false;
			this.exceptionThrown = true;
			this.exception = e;
		}
	}

	public boolean isRunning() {
		if(!hasStarted) {
			return true;
		} else {
			return running;
		}
	}

	public boolean exceptionThrown() {
		return exceptionThrown;
	}

	public Exception getException() {
		return exception;
	}
}
