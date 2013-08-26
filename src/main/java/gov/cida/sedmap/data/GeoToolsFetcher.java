package gov.cida.sedmap.data;

import gov.cida.sedmap.io.FileInputStreamWithFile;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.WriterWithFile;
import gov.cida.sedmap.ogc.FeatureValueIterator;
import gov.cida.sedmap.ogc.OgcUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.geotools.data.DataStore;
import org.geotools.jdbc.JDBCFeatureReader;
import org.opengis.filter.Filter;

public class GeoToolsFetcher extends Fetcher {

	protected static final String NWIS_URL = "http://waterservices.usgs.gov/nwis/dv/?format=_format_&sites=_sites_&startDT=_startDate_&endDT=_endDate_&statCd=00003&parameterCd=00060";

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


	@Override
	protected InputStream handleNwisData(Iterator<String> sites, Filter filter, Formatter formatter)
			throws IOException, SQLException, NamingException {
		String url = NWIS_URL;

		// NWIS offers RDB only
		String format = "rdb";
		Formatter rdb = new RdbFormatter();
		url = url.replace("_format_",    format);

		// extract expressions from the filter we can handle
		String yr1 = OgcUtils.findFilterValue(filter, "yr1");
		String yr2 = OgcUtils.findFilterValue(filter, "yr1");

		// translate the filters to NWIS Web query params
		String startDate = yr1 + "-01-01"; // Jan  1st of the given year
		String endDate   = yr2 + "-12-31"; // Dec 31st of the given year

		url = url.replace("_startDate_", startDate);
		url = url.replace("_endDate_",   endDate);

		// open temp file
		WriterWithFile tmp = IoUtils.createTmpZipWriter("daily_data", formatter.getFileType());
		try {
			while (sites.hasNext()) {
				int batch = 0;
				String sep = "";
				StringBuilder siteList = new StringBuilder();

				// site list should be in batches of 99 site IDs
				while (batch++<99 && sites.hasNext()) {
					siteList.append(sep).append(sites.next());
					sep=",";
				}
				String sitesUrl = url.replace("_sites_",   siteList);

				// fetch the data from NWIS
				BufferedReader nwis = null;
				try {
					nwis = fetchNwisData(sitesUrl);

					String line;
					while ((line = nwis.readLine()) != null) {
						// translate from NWIS format to requested format
						line = formatter.transform(line, rdb);
						tmp.write(line);
					}
				} finally {
					IoUtils.quiteClose(nwis);
				}
			}
			// TODO
			// } catch (Exception e) {
			//     tmp.deleteFile();
		} finally {
			IoUtils.quiteClose(tmp);
		}

		return IoUtils.createTmpZipStream( tmp.getFile() );
	}



	protected BufferedReader fetchNwisData(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		URLConnection cn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(cn.getInputStream()));

		return reader;
	}


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
