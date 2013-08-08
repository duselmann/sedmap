package gov.cida.sedmap.ogc;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.opengis.filter.Filter;

import static org.junit.Assert.*;

public class OgcUtilsTests {

	String ogc_v1_0 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\"> "
			+ "        <ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        <ogc:PropertyName>attName</ogc:PropertyName>"
			+ "        <ogc:Literal>5</ogc:Literal>"
			+ "        </ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        </ogc:Filter>";

	String ogc_v1_1 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"><ogc:And><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>REFERENCE_SITE</ogc:PropertyName><ogc:Literal>1</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>SAMPLE_YEARS</ogc:PropertyName><ogc:Literal>19</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>GAGE_BASIN_ID</ogc:PropertyName><ogc:Literal>23534234</ogc:Literal></ogc:PropertyIsEqualTo><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>40</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>400</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>SOIL_K</ogc:PropertyName><ogc:Literal>.5</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>SOIL_K</ogc:PropertyName><ogc:Literal>.7</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:Or><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>WI</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>Wisconsin</ogc:Literal></ogc:PropertyIsEqualTo></ogc:Or></ogc:And></ogc:Filter>";


	@Test
	public void test_ogcXml2Filter_simple_v1_0() {
		Filter filter = OgcUtils.ogcXml2Filter(ogc_v1_0);

		String expected = "[ attName >= 5 ]";
		String actual   = filter.toString();

		assertEquals("Testing simple filter parse", expected, actual);
	}

	@Test
	public void test_ogcXml2Sql_simple_v1_0() {
		String expected = "( attName >= 5 )";
		String actual   = OgcUtils.ogcXml2Sql(ogc_v1_0);

		assertEquals("Testing conversion from toString to SQL", expected, actual);
	}

	@Test
	public void test_ogcXml2Sql_complex_v1_1() {
		String sql = "Parse Failed";
		try {
			sql = OgcUtils.ogcXml2Sql(ogc_v1_1);
		} catch (Exception e) {
			fail("Requires OGC Filter v1.1 parsing to pass. " + e.getMessage());
		}

		System.out.println( sql );
	}


	@Test
	public void test_invalidChars_none() {
		String qry = "[ foo=bar ]";
		try {
			OgcUtils.checkForInvalidChars(qry);
		} catch (Exception e) {
			fail("No invalid chars should not throw exception. " + e.getMessage());
		}
	}


	@Test(expected=RuntimeException.class)
	public void test_invalidChars_openParentheses() {
		String qry = "[ foo=( ]";

		OgcUtils.checkForInvalidChars(qry);
	}
	@Test(expected=RuntimeException.class)
	public void test_invalidChars_closedParentheses() {
		String qry = "[ foo=) ]";

		OgcUtils.checkForInvalidChars(qry);
	}
	@Test(expected=RuntimeException.class)
	public void test_invalidChars_semicolon() {
		String qry = "[ foo=dfsdf; ]";

		OgcUtils.checkForInvalidChars(qry);
	}


	@Test
	public void test_sqlTranslation_Brakets2parentheses() {
		String query  = "[ ]";
		String expect = "( )";
		String actual = OgcUtils.sqlTranslation(query);

		assertEquals("square brackets replaced with parentheses", expect, actual);
	}

	@Test
	public void test_sqlTranslation_isLike2like() {
		String query  = " is like ";
		String expect = " like ";
		String actual = OgcUtils.sqlTranslation(query);

		assertEquals("remove 'is' from 'is like'", expect, actual);
	}


	@Test
	public void test_sqlTranslation_wildcard2string() {
		String query  = " bar* ";
		String expect = " 'bar%' ";
		String actual = OgcUtils.sqlTranslation(query);

		assertEquals("convert wildcard from bar* to 'bar%' ", expect, actual);
	}

	@Test
	public void test_sqlTranslation_fieldTranslations() {
		Map<String, String> FIELD_TRANSLATIONS = new HashMap<String, String>();
		FIELD_TRANSLATIONS.put("HUC_8","HUC_12");

		String query  = "((( DA >= 10 ) AND ( DA <= 40 )) AND ( HUC_8 like '03%' ))";
		String expect = "((( DA >= 10 ) AND ( DA <= 40 )) AND ( HUC_12 like '03%' ))";
		String actual = OgcUtils.sqlTranslation(query, FIELD_TRANSLATIONS);

		assertEquals("convert wildcard from bar* to 'bar%' ", expect, actual);
	}




}
