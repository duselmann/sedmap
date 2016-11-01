package gov.cida.sedmap.ogc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.AbstractFilter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;

import gov.cida.sedmap.io.util.StrUtils;

public class FilterWithViewParams  extends AbstractFilter implements Iterable<String> {

	protected AbstractFilter delegate;
	protected ArrayList<String> viewParamNames = new ArrayList<String>();
	protected HashMap<String,String> viewParams = new HashMap<String,String>();


	public FilterWithViewParams(AbstractFilter filter) {
		super(CommonFactoryFinder.getFilterFactory(null));
		delegate = filter;
	}


	public String getViewParam(String name) {
		return viewParams.get(name);
	}


	public void putViewParam(String name, String defaultValue, String value) {
		viewParamNames.add(name);
		viewParams.put(name, StrUtils.isEmpty(value) ?defaultValue :value);
	}


	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			Iterator<String> names = viewParamNames.iterator();
			@Override
			public boolean hasNext() {
				return names.hasNext();
			}
			@Override
			public String next() {
				return viewParams.get( names.next() );
			}
			@Override
			public void remove() {
			}
		};
	}


	public AbstractFilter getFilter() {
		return delegate;
	}


	@Override
	public boolean evaluate(SimpleFeature feature) {
		return delegate.evaluate(feature);
	}

	@Override
	public boolean accepts(SimpleFeature feature) {
		return delegate.accepts(feature);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public Object accept(FilterVisitor visitor, Object extraData) {
		return delegate.accept(visitor, extraData);
	}

	@Override
	public boolean evaluate(Object object) {
		return delegate.evaluate(object);
	}

	@Override
	@SuppressWarnings("deprecation")
	public org.geotools.filter.Filter and(Filter filter) {
		return delegate.and(filter);
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	@Override
	@SuppressWarnings("deprecation")
	public org.geotools.filter.Filter or(Filter filter) {
		return delegate.or(filter);
	}

	@Override
	@SuppressWarnings("deprecation")
	public org.geotools.filter.Filter not() {
		return delegate.not();
	}

	@Override
	@SuppressWarnings("deprecation")
	public short getFilterType() {
		return delegate.getFilterType();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}
}
