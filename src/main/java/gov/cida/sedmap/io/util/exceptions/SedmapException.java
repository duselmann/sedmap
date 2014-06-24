package gov.cida.sedmap.io.util.exceptions;

public class SedmapException extends Exception {
	private static final long serialVersionUID = 4100058197032660777L;
	
	public static String GENERIC_ERROR = "Internal Application Error.  Please contact the site administrator for resolution.";
	
	private OGCExceptionCode exceptionCode = OGCExceptionCode.NoApplicableCode;
	private Exception originalException;
	private String exceptionMessage = "";
	
	public static enum OGCExceptionCode {
		OperationNotSupported, MissingParameterValue, InvalidParameterValue, ResourceNotFound, NoApplicableCode;
		
	
		public static OGCExceptionCode getTypeFromString(String string) {
			if (string.equals("OperationNotSupported")) {
				return OperationNotSupported;
			}
			
			if (string.equals("MissingParameterValue")) {
				return MissingParameterValue;
			}
			
			if (string.equals("InvalidParameterValue")) {
				return InvalidParameterValue;
			}
			
			if (string.equals("ResourceNotFound")) {
				return ResourceNotFound;
			}
			
			if (string.equals("NoApplicableCode")) {
				return NoApplicableCode;
			}
			
			return NoApplicableCode;
		}
		
		public static String getStringFromType(OGCExceptionCode type) {
			switch (type) {
				case OperationNotSupported: {
					return "OperationNotSupported";
				}
				case MissingParameterValue: {
					return "MissingParameterValue";
				}
	
				case InvalidParameterValue: {
					return "InvalidParameterValue";
				}
	
				case ResourceNotFound: {
					return "ResourceNotFound";
				}
	
				case NoApplicableCode: {
					return "NoApplicableCode";
				}
				
				default: {
					return "NoApplicableCode";
				}
			}
		}
	}
	
	public SedmapException(OGCExceptionCode code, Exception e) {
		super();
		this.exceptionCode = code;
		this.originalException = e;
		this.exceptionMessage = e.getLocalizedMessage();
	}

	public OGCExceptionCode getExceptionCode() {
		return exceptionCode;
	}

	public Exception getOriginalException() {
		return originalException;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
