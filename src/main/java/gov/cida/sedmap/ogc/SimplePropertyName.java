package gov.cida.sedmap.ogc;

import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.PropertyName;
import org.xml.sax.helpers.NamespaceSupport;

public class SimplePropertyName implements PropertyName {
	final String name;

	public SimplePropertyName(String name) {
		this.name=name;
	}

	@Override
	public String getPropertyName() {
		return name;
	}

	@Override
	public Object accept( ExpressionVisitor visitor, Object data ) {
		return visitor.visit(this, data);
	}
	@Override
	public Object evaluate( Object object ) {
		return null;
	}
	@Override
	public <T> T evaluate( Object object, Class<T> context ) {
		return null;
	}
	@Override
	public NamespaceSupport getNamespaceContext() {
		return null;
	}

}
