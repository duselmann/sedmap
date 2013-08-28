package gov.cida.sedmap.data;

import gov.cida.sedmap.io.FileInputStreamWithFile;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.WriterWithFile;
import gov.cida.sedmap.ogc.FeatureValueIterator;
import gov.cida.sedmap.ogc.OgcUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.geotools.data.DataStore;
import org.geotools.jdbc.JDBCFeatureReader;
import org.opengis.filter.Filter;

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
	protected InputStream handleLocalData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		FileInputStreamWithFile fileData = null;

		JDBCFeatureReader reader = null;
		try {
			String tableName = getDataTable(descriptor);
			reader = OgcUtils.executeQuery(store, tableName, filter);

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
				// TODO
				// } catch (Exception e) {
				//     tmp.deleteFile();
			} finally {
				IoUtils.quiteClose(tmp);
			}

			fileData = IoUtils.createTmpZipStream( tmp.getFile() );
		} finally {
			IoUtils.quiteClose(reader);
		}

		return fileData;
	}
}
