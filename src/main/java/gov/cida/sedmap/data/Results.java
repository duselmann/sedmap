package gov.cida.sedmap.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import gov.cida.sedmap.io.IoUtils;

// nice little bundle for the JDBC results set needs
public class Results implements AutoCloseable {
	Connection cn;
	PreparedStatement ps;
	ResultSet  rs;
	
	@Override
	public void close() throws Exception {
		IoUtils.quiteClose(rs,ps,cn);
	}
}