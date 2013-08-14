package gov.cida.sedmap.data;

public class Column {
	public final String name;
	public final int    type;
	public final int    size;
	public final boolean nullable;

	public Column(String name, int type, int size, boolean nullable) {
		this.name = name;
		this.type = type;
		this.size = size;

		this.nullable = nullable;
	}

}
