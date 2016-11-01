package gov.cida.sedmap.data;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.jdbc.JDBCFeatureReader;

import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.WriterWithFile;
import gov.cida.sedmap.ogc.FeatureValueIterator;
import gov.cida.sedmap.ogc.FilterWithViewParams;
import gov.cida.sedmap.ogc.OgcUtils;

// TODO this was left in an unfinished state once it was determined that GeoTools was insufficient
public class GeoToolsFetcher extends Fetcher {


	protected DataStore store;


	/* test parameters
	 * format => rdb
	 * sites  => 09402500,09403850
	 * start  => 2013
	 * end    => 2013
	 * 
	 * return data as of 8/16/2013
	 * header lines =>  34
	 * data   lines => 282
	 * total  lines => 316
	 */

	@Override
	public Fetcher initJndiJdbcStore(String jndiJdbc) throws Exception {
		store = OgcUtils.jndiOracleDataStore(jndiJdbc);
		return this;
	}


	// this will work if the query is against a table or layer feature but not a join query

	@Override
	protected File handleSiteData(String descriptor, FilterWithViewParams filter, Formatter formatter)
			throws Exception {

		String tableName = getDataTable(descriptor);

		try (JDBCFeatureReader reader = OgcUtils.executeQuery(store, tableName, filter.getFilter());
			 WriterWithFile tmp = IoUtils.createTmpZipWriter(descriptor, formatter.getFileType()) ) {
			
			List<Column> columns = getTableMetadata(tableName);
                            String[] columnNames = Column.getColumnNames(columns.iterator());
                            Iterator<String> columnNamesIter = Arrays.asList(columnNames).iterator();
			String header = formatter.fileHeader(columnNamesIter, HeaderType.SITE);
                            tmp.write(header);

			while (reader.hasNext()) {
				FeatureValueIterator values = new FeatureValueIterator(reader.next());
				String line = formatter.fileRow(values);
				tmp.write(line);
			}
			
			return tmp.getFile();
		}

	}


	@Override
	protected File handleDiscreteData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
			throws Exception {
		throw new UnsupportedOperationException("Not yet impl");
	}
}
