package gov.cida.sedmap.util;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.cida.sedmap.io.util.StrUtils;

public class StrUtilsTest {


	@Test
	public void uniqueName_length() throws Exception {
		String name = StrUtils.uniqueName(10);
		assertEquals("expect 10 chars", 10, name.length());
	}


	@Test
	public void uniqueName_uniqueness() throws Exception {
		String name1 = StrUtils.uniqueName(10);
		String name2 = StrUtils.uniqueName(10);
		assertFalse("expect unique strings", name1.equals(name2) );
	}



	@Test
	public void occurences_three() throws Exception {
		int actual = StrUtils.occurrences("A","ABABAB");
		int expect = 3;
		assertEquals("Expect 3 occurrences", expect, actual);
	}

	@Test
	public void occurences_zero() throws Exception {
		int actual = StrUtils.occurrences("Z","ABABAB");
		int expect = 0;
		assertEquals("Expect 0 occurrences", expect, actual);
	}

}
