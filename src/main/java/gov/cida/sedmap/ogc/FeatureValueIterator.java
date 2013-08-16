package gov.cida.sedmap.ogc;

import gov.cida.sedmap.io.util.StringValueIterator;

import org.opengis.feature.simple.SimpleFeature;

public class FeatureValueIterator extends StringValueIterator {

	public FeatureValueIterator(SimpleFeature feature) {
		super( feature.getAttributes().iterator() );
	}
}
