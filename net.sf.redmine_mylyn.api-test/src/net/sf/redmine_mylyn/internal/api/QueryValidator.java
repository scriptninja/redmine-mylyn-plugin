package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.redmine_mylyn.api.model.Query;

public class QueryValidator {

	public final static String RESOURCE_FILE = "/xmldata/queries.xml";
	
	public final static int COUNT = 5;
	
	public static void validate9(Query obj) {
		assertNotNull(obj);
		assertEquals(9, obj.getId());
		assertEquals("Open issues grouped by list custom field", obj.getName());
		assertEquals(0, obj.getProjectId());
	}

	public static void validate1(Query obj) {
		assertNotNull(obj);
		assertEquals(1, obj.getId());
		assertEquals("Multiple custom fields query", obj.getName());
		assertEquals(1, obj.getProjectId());
	}
	
}
