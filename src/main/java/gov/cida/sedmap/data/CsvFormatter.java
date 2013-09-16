package gov.cida.sedmap.data;

public class CsvFormatter extends CharSepFormatter {

	public CsvFormatter() {
		super("text/csv", ",", "csv");
	}

}
