package gov.cida.sedmap.ogc;

import org.opengis.feature.simple.SimpleFeature;

import gov.cida.sedmap.io.util.StringValueIterator;

public class FeatureValueIterator extends StringValueIterator {

	public FeatureValueIterator(SimpleFeature feature) {
		super( feature.getAttributes().iterator() );
	}
}
