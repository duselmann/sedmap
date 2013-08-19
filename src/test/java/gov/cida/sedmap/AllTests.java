package gov.cida.sedmap;


import gov.cida.sedmap.data.CharFormatterTest;
import gov.cida.sedmap.data.CsvFormatterTest;
import gov.cida.sedmap.data.FetcherTest;
import gov.cida.sedmap.data.GeoToolsFetcherTest;
import gov.cida.sedmap.data.JdbcFetcherTest;
import gov.cida.sedmap.data.RdbFormatterTest;
import gov.cida.sedmap.io.IoUtilsTest;
import gov.cida.sedmap.io.MultiPartFileTest;
import gov.cida.sedmap.ogc.OgcUtilsTests;
import gov.cida.sedmap.util.StrUtilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CharFormatterTest.class, CsvFormatterTest.class,
	FetcherTest.class, GeoToolsFetcherTest.class, JdbcFetcherTest.class,
	RdbFormatterTest.class, IoUtilsTest.class, MultiPartFileTest.class,
	OgcUtilsTests.class, StrUtilsTest.class})
public class AllTests {

}
