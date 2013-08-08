package gov.cida.sedmap.mock;

import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

// use initial context so we do not have to impl all methods but is might be a good idea
public class MockContext extends InitialContext {

	Map<String,Object> mockEnv;

	public MockContext(Map<String,Object> env) throws NamingException {
		super();
		mockEnv = env;
	}

	@Override
	public Object lookup(String name) throws NamingException {
		return mockEnv.get(name);
	}

}
