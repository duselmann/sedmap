package gov.cida.sedmap.ogc;

import java.util.Iterator;

import org.opengis.filter.Filter;

public class FilterLiteralIterator implements Iterable<String>, Iterator<String> {

	protected FilterWrapper    parent;
	protected Iterator<Filter> iterator;

	protected FilterWrapperDepthFirstIterator next;
	protected boolean oneLastValue;


	public FilterLiteralIterator(Filter filter) {
		if (OgcUtils.isAllFilter(filter)) {
			filter = null;
		}
		if (filter != null) {
			parent = new FilterWrapper(filter);
			next   = new FilterWrapperDepthFirstIterator(filter);
		}
		nextFilter();
		oneLastValue = next!=null;
	}

	@Override
	public String next() {
		//		FilterWrapper literal = next.next();
		String value = OgcUtils.getValue(next);
		nextFilter();
		return value;
	}

	protected void nextFilter() {
		// this is depth first
		while ( next!=null && next.hasNext() && next.isaLogicFilter() ) {
			// go into depth if available
			next = next.next();
		}
		// this is breath - back to the parent iterator and
		if ( next!=null && !next.hasNext()) {
			if ( (next = next.parentIterator()) != null ) {
				nextFilter();
			}
		}
		oneLastValue = next!=null;
		if (oneLastValue && next.hasNext()) next.next();
	}


	@Override
	public boolean hasNext() {
		return oneLastValue;
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not yet impl because not needed at initial impl");
	}
	@Override
	public Iterator<String> iterator() {
		return this;
	}
}
