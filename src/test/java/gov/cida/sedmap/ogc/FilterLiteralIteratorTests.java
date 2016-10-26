package gov.cida.sedmap.ogc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opengis.filter.Filter;

public class FilterLiteralIteratorTests {

	String ogc_v1_0 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\"> "
			+ "        <ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        <ogc:PropertyName>Site_Id</ogc:PropertyName>"
			+ "        <ogc:Literal>5</ogc:Literal>"
			+ "        </ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        </ogc:Filter>";

	String ogc_v1_1 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"><ogc:And><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>REFERENCE_SITE</ogc:PropertyName><ogc:Literal>1</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>SAMPLE_YEARS</ogc:PropertyName><ogc:Literal>19</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>GAGE_BASIN_ID</ogc:PropertyName><ogc:Literal>23534234</ogc:Literal></ogc:PropertyIsEqualTo><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>40</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>400</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>yr1</ogc:PropertyName><ogc:Literal>1900</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>yr2</ogc:PropertyName><ogc:Literal>1950</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:Or><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>WI</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>Wisconsin</ogc:Literal></ogc:PropertyIsEqualTo></ogc:Or></ogc:And></ogc:Filter>";

	@Test
	public void test_singleValue() {
		System.out.println();
		int count = 0;
		Filter filter;
		try {
			filter = OgcUtils.ogcXmlToFilter(ogc_v1_0);
		} catch (Exception e) {
			fail(e.getMessage());
			return;
		}
		for (String value : new FilterLiteralIterator(filter)) {
			System.out.println(value);
			count++;
		}
		assertEquals(1, count);
	}
	@Test
	public void test_multipleValues() {
		System.out.println();
		int count = 0;
		Filter filter;
		try {
			filter = OgcUtils.ogcXmlToFilter(ogc_v1_1);
		} catch (Exception e) {
			fail(e.getMessage());
			return;
		}
		for (String value : new FilterLiteralIterator(filter)) {
			System.out.println(value);
			count++;
		}
		assertEquals(9, count);
	}

}
