package gov.cida.sedmap.data;

public class TsvFormatter extends CharSepFormatter {

	public TsvFormatter() {
		super("text/tab-separated-values", "\t", "tsv");
	}

}
