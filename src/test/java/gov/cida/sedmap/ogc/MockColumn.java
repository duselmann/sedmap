package gov.cida.sedmap.ogc;

import gov.cida.sedmap.data.Column;

public class MockColumn extends Column {
	final String typeName;
	public MockColumn(String name, int type, int size, boolean nullable, String typeName) {
		super(name, type, size, nullable);
		this.typeName = typeName;
	}
}
