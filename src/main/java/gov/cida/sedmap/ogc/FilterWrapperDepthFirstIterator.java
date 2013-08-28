package gov.cida.sedmap.ogc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.filter.BinaryLogicAbstract;
import org.opengis.filter.Filter;


class FilterWrapperDepthFirstIterator extends FilterWrapper implements Iterator<FilterWrapperDepthFirstIterator>, Iterable<FilterWrapperDepthFirstIterator> {

	Iterator<Filter> children;

	public FilterWrapperDepthFirstIterator(Filter filter) {
		super(filter);
		initChildren();
	}
	public FilterWrapperDepthFirstIterator(FilterWrapper filter) {
		super(filter);
		initChildren();
	}
	public FilterWrapperDepthFirstIterator(Filter filter, FilterWrapper parent) {
		super(filter, parent);
		initChildren();
	}

	protected void initChildren() {
		if (children != null) {
			return; // do it once
		}
		if (filter instanceof BinaryLogicAbstract) {
			BinaryLogicAbstract binaryFilter = (BinaryLogicAbstract) filter;
			children = binaryFilter.getChildren().iterator();
		} else {
			List<Filter> self = new ArrayList<Filter>();
			self.add(filter);
			children = self.iterator();
		}
	}
	@Override
	public Iterator<FilterWrapperDepthFirstIterator> iterator() {
		return this;
	}
	@Override
	public boolean hasNext() {
		return children.hasNext();
	}
	@Override
	public FilterWrapperDepthFirstIterator next() {
		return new FilterWrapperDepthFirstIterator(children.next(), this);
	}
	public FilterWrapperDepthFirstIterator parentIterator() {
		if (parent instanceof FilterWrapperDepthFirstIterator) {
			return (FilterWrapperDepthFirstIterator) parent;
		}
		return null;
	}



}
