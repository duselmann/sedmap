package gov.cida.sedmap.data;

import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.ogc.FeatureValueIterator;
import gov.cida.sedmap.ogc.OgcUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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



	public void initJndiJdbcStore(String jndiJdbc) throws IOException {
		store = OgcUtils.jndiOracleDataStore(jndiJdbc);
	}


	@Override
	protected InputStream handleNwisData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		throw new RuntimeException("Not implemented.");

		// TODO descriptor should always be "daily_data"

		// TODO first fetch the sites associated with the filter
		// TODO actually the sites where fetched in a file prior

		// TODO then extract expressions from the filter we can handle
		// TODO translate the filters to NWIS Web query params
		// TODO fetch the data from NWIS
		// TODO process with the formatter
	}



	@Override
	protected InputStream handleLocalData(String descriptor, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		InputStream fileData = null;

		JDBCFeatureReader reader = null;
		try {
			reader = OgcUtils.executeQuery(store, "tableName", filter);

			File   tmp = File.createTempFile(descriptor + StrUtils.uniqueName(12), formatter.getFileType());
			FileWriter tmpw = new FileWriter(tmp);

			String     tableName = Fetcher.DATA_TABLES.get(descriptor);
			List<Column> columns = Fetcher.getTableMetadata(tableName);
			String header = formatter.fileHeader(columns);
			tmpw.write(header);

			while (reader.hasNext()) {
				FeatureValueIterator values = new FeatureValueIterator(reader.next());
				String row = formatter.fileRow(values);
				tmpw.write(row);
			}
			IoUtils.quiteClose(tmpw);

			fileData = new FileInputStream(tmp);
			tmp.delete(); // TODO not for delayed download

		} finally {
			IoUtils.quiteClose(reader);
		}

		return fileData;
	}
}
