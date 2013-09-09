package gov.cida.sedmap.ogc;

import gov.cida.sedmap.io.util.ReflectUtil;

import java.util.ArrayList;
import java.util.List;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.AbstractFilter;
import org.geotools.filter.BinaryComparisonAbstract;
import org.geotools.filter.BinaryLogicAbstract;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;


// this class exists in order to access the protected static methods that test filter type


@SuppressWarnings("deprecation")
class FilterWrapper extends AbstractFilter {
	protected FilterWrapper parent;
	protected FilterWrapper wrapper;
	protected AbstractFilter filter;


	protected FilterWrapper() {
		super(CommonFactoryFinder.getFilterFactory(null));
		// need this to stop any recusive calls
	}
	public FilterWrapper(Filter filter) {
		this(filter, null);
	}
	public FilterWrapper(Filter filter, FilterWrapper parent) {
		this(new FilterWrapper().setFilter(filter));
		this.parent = parent;
	}
	public FilterWrapper(FilterWrapper filter, FilterWrapper parent) {
		this(filter);
		this.parent = parent;
	}
	public FilterWrapper(FilterWrapper wrapper) {
		super(null);
		this.wrapper = wrapper;
		filter  = wrapper.filter;
	}

	public FilterWrapper setFilter(Filter filter) {
		this.filter = (AbstractFilter) filter;
		return this;
	}

	@Override
	public org.geotools.filter.Filter and(Filter filter) {
		return getInnerFilter().and(filter);
	}

	@Override
	public org.geotools.filter.Filter or(Filter filter) {
		return getInnerFilter().or(filter);
	}

	@Override
	public org.geotools.filter.Filter not() {
		return getInnerFilter().not();
	}

	@Override
	public boolean evaluate(Object object) {
		return getInnerFilter().evaluate(object);
	}

	@Override
	public boolean evaluate(SimpleFeature feature) {
		return getInnerFilter().evaluate(feature);
	}

	@Override
	public boolean accepts(SimpleFeature feature) {
		return getInnerFilter().accepts(feature);
	}

	@Override
	public int hashCode() {
		return getInnerFilter().hashCode();
	}

	@Override
	public Object accept(FilterVisitor visitor, Object extraData) {
		return getInnerFilter().accept(visitor, extraData);
	}

	@Override
	public boolean equals(Object obj) {
		return getInnerFilter().equals(obj);
	}

	@Override
	public short getFilterType() {
		return getInnerFilter().getFilterType();
	}

	@Override
	public String toString() {
		return getInnerFilter().toString();
	}

	protected boolean isaLiteralFilter() {
		return ! isaLogicFilter();
	}
	protected boolean isaLogicFilter() {
		return AbstractFilter.isLogicFilter(  getInnerFilter().getFilterType() );
	}


	public boolean isInstanceOf(Class<?> clas) {
		Filter filter = getInnerFilter();
		return clas.isInstance(filter);
	}


	public void remove() {
		if (parent == null) return;

		if (parent.filter instanceof BinaryLogicAbstract) {
			BinaryLogicAbstract binaryFilter = (BinaryLogicAbstract) parent.filter;
			List<?> children = (List<?>) ReflectUtil.getDeclaredFieldValue("children",binaryFilter);
			children.remove(getInnerFilter());

			if ( ! parent.hasChildern() ) {
				parent.remove();
			}
		}
	}


	public boolean hasChildern() {
		boolean has = false;

		if (getInnerFilter() instanceof BinaryLogicAbstract) {
			BinaryLogicAbstract binaryFilter = (BinaryLogicAbstract) getInnerFilter();
			has = binaryFilter.getChildren().size() > 0;
		}
		return has;
	}

	public List<Filter> getChildren() {
		if (wrapper.isaLogicFilter()) {
			return ((BinaryLogicAbstract) getInnerFilter()).getChildren();
		}
		return new ArrayList<Filter>();
	}
	public PropertyName getExpression1() {
		if (wrapper.isaLiteralFilter()) {
			return (PropertyName) ((BinaryComparisonAbstract) getInnerFilter()).getExpression1();
		}
		return null;
	}


	public String getExpression2() {
		String value = null;
		if (wrapper.isaLiteralFilter()) {
			// TODO this presumes that the literal check ensures safe cast
			BinaryComparisonAbstract comp = (BinaryComparisonAbstract)getInnerFilter();
			Literal literal = (Literal) comp.getExpression2();
			value = literal.getValue().toString();
		}
		return value;
	}

	public AbstractFilter getInnerFilter() {
		AbstractFilter inner = filter;
		while (inner instanceof FilterWrapper) {
			inner = ((FilterWrapper)filter).getInnerFilter();
		}
		return inner;
	}


}