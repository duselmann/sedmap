package gov.cida.sedmap.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.util.SessionUtil;
import gov.cida.sedmap.io.util.exceptions.SedmapException;

// nice little bundle for the JDBC results set needs
public class Results implements AutoCloseable {
	private static final Logger logger = Logger.getLogger(Results.class);
	
	
	Connection cn;
	PreparedStatement ps;
	ResultSet  rs;
	
	@Override
	public void close() throws Exception {
		IoUtils.quiteClose(rs,ps,cn);
	}
	
	
	protected Results openQuery(String jndiDS, String sql) throws SedmapException {
		try {
			DataSource ds = SessionUtil.lookupDataSource(jndiDS);
			cn = ds.getConnection();			
		} catch (NamingException e) {
			String msg = "Error fetching JDBC data source";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		} catch (SQLException e) {
			String msg = "Error fetching JDBC connection";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}
		
		try {
			ps = cn.prepareStatement(sql);
		} catch (SQLException e) {
			String msg = "Error creating SQL statement on JDBC connection";
			logger.error(msg,e);
			throw new SedmapException(msg, e);
		}

		return this;
	}
}