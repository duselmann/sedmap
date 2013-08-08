package gov.cida.sedmap.data;

public class CsvFormatter extends CharSepFormatter {
	public static final String CONTENT_TYPE = "text/csv";

	public CsvFormatter() {
		super("text/csv", ",");
	}

}
