package gov.cida.sedmap.io.util.exceptions;

/**
 * This class is a wrapper for a generic exception an is used for logic when
 * a timeout has occurred.
 * 
 * @author prusso
 *
 */
public class TimeOutException extends Exception {
	private static final long serialVersionUID = -8577218559591139843L;
	
	public TimeOutException(String msg) {
		super(msg);
	}
}
