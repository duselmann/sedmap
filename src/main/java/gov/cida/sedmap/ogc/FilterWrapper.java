package gov.cida.sedmap.ogc;

import org.geotools.filter.AbstractFilter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;


// this class exists in order to access the protected static methods that test filter type


@SuppressWarnings("deprecation")
public class FilterWrapper extends AbstractFilter {
	private AbstractFilter filter;

	public FilterWrapper(AbstractFilter filter) {
		super(null);
		this.filter = filter;
	}

	@Override
	public org.geotools.filter.Filter and(Filter filter) {
		return this.filter.and(filter);
	}

	@Override
	public org.geotools.filter.Filter or(Filter filter) {
		return this.filter.or(filter);
	}

	@Override
	public org.geotools.filter.Filter not() {
		return filter.not();
	}

	@Override
	public boolean evaluate(Object object) {
		return filter.evaluate(object);
	}

	@Override
	public boolean evaluate(SimpleFeature feature) {
		return filter.evaluate(feature);
	}

	@Override
	public boolean accepts(SimpleFeature feature) {
		return filter.accepts(feature);
	}

	@Override
	public int hashCode() {
		return filter.hashCode();
	}

	@Override
	public Object accept(FilterVisitor visitor, Object extraData) {
		return filter.accept(visitor, extraData);
	}

	@Override
	public boolean equals(Object obj) {
		return filter.equals(obj);
	}

	@Override
	public short getFilterType() {
		return filter.getFilterType();
	}

	@Override
	public String toString() {
		return filter.toString();
	}

	protected boolean isaMathFilter() {
		return AbstractFilter.isMathFilter( filter.getFilterType() );
	}
	protected boolean isaLogicFilter() {
		return AbstractFilter.isLogicFilter(  filter.getFilterType() );
	}

}