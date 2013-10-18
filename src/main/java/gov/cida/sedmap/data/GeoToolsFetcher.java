package gov.cida.sedmap.data;

import gov.cida.sedmap.io.InputStreamWithFile;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.WriterWithFile;
import gov.cida.sedmap.ogc.FeatureValueIterator;
import gov.cida.sedmap.ogc.FilterWithViewParams;
import gov.cida.sedmap.ogc.OgcUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.geotools.data.DataStore;
import org.geotools.jdbc.JDBCFeatureReader;

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
	public Fetcher initJndiJdbcStore(String jndiJdbc) throws IOException {
		store = OgcUtils.jndiOracleDataStore(jndiJdbc);
		return this;
	}


	// this will work if the query is against a table or layer feature but not a join query

	@Override
	protected InputStreamWithFile handleSiteData(String descriptor, FilterWithViewParams filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		InputStreamWithFile fileData = null;

		JDBCFeatureReader reader = null;
		try {
			String tableName = getDataTable(descriptor);
			reader = OgcUtils.executeQuery(store, tableName, filter.getFilter());

			WriterWithFile tmp = IoUtils.createTmpZipWriter(descriptor, formatter.getFileType());

			try {
				List<Column> columns = getTableMetadata(tableName);
				String header = formatter.fileHeader(columns);
				tmp.write(header);

				while (reader.hasNext()) {
					FeatureValueIterator values = new FeatureValueIterator(reader.next());
					String line = formatter.fileRow(values);
					tmp.write(line);
				}
			} finally {
				IoUtils.quiteClose(tmp);
			}

			fileData = IoUtils.createTmpZipStream( tmp.getFile() );
		} finally {
			IoUtils.quiteClose(reader);
		}

		return fileData;
	}


	@Override
	protected InputStreamWithFile handleDiscreteData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		// TODO IMPL
		throw new RuntimeException("Not yet impl");
	}
}
