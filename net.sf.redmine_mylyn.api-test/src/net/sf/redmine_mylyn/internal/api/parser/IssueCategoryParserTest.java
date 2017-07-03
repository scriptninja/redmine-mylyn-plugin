package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.IssueCategories;
import net.sf.redmine_mylyn.internal.api.IssueCategoryValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueCategoryParserTest {

	InputStream input;
	AttributeParser<IssueCategories> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(IssueCategoryValidator.RESOURCE_FILE);
		testee = new  AttributeParser<IssueCategories>(IssueCategories.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		IssueCategories categories = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(categories);
		assertEquals(IssueCategoryValidator.COUNT, categories.getAll().size());
		
		IssueCategoryValidator.validate3(categories.getById(3));
	}

}
