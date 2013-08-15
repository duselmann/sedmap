package gov.cida.sedmap.ogc;

import java.util.Iterator;
import org.opengis.feature.simple.SimpleFeature;

public class FeatureValueIterator implements Iterator<String>, Iterable<String> {

	protected Iterator<Object> values;


	@Override
	public Iterator<String> iterator() {
		return this;
	}


	public FeatureValueIterator(SimpleFeature feature) {
		values = feature.getAttributes().iterator();
	}

	@Override
	public boolean hasNext() {
		return values.hasNext();
	}

	@Override
	public String next() {
		String val = null;
		Object obj = null;
		try {
			obj = values.next();
			val = obj.toString();
		} catch (Exception e) {
			throw new RuntimeException("Value could not be converted to String " + val,e);
		}

		return val;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Not implemented.");
	}

}
