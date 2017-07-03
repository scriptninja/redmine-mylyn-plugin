package net.sf.redmine_mylyn.internal.api.client;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.TestData;
import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IssueRequestEntityTest {

	static LinkedHashMap<String, String> issueValues;
	static LinkedHashMap<String, String> issueCustomValues;
	static LinkedHashMap<String, String> timeValues;
	static LinkedHashMap<String, String> timeCutomValues;

	Method writeIssueMethod;
	Method writeIssueMethod2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		issueValues = new LinkedHashMap<String, String>();
		issueValues.put("subject", TestData.issue2.getSubject());
		issueValues.put("description", TestData.issue2.getDescription());
		issueValues.put("tracker_id", ""+TestData.issue2.getTrackerId());
		issueValues.put("project_id", ""+TestData.issue2.getProjectId());
		issueValues.put("status_id", ""+TestData.issue2.getStatusId());
		issueValues.put("priority_id", ""+TestData.issue2.getPriorityId());
		issueValues.put("start_date", "2010-05-08");
		issueValues.put("due_date", "");
		issueValues.put("done_ratio", ""+TestData.issue2.getDoneRatio());
		issueValues.put("estimated_hours", ""+TestData.issue2.getEstimatedHours());
		issueValues.put("category_id", "");
		issueValues.put("assigned_to_id", ""+TestData.issue2.getAssignedToId());
		issueValues.put("fixed_version_id", ""+TestData.issue2.getFixedVersionId());
		issueValues.put("parent_issue_id", "1");
		issueValues.put("watcher_user_ids", "[1,3]");
		
		issueCustomValues = new LinkedHashMap<String, String>();
		issueCustomValues.put("2", TestData.issue2.getCustomValues().get(5).getValue());
		issueCustomValues.put("6", TestData.issue2.getCustomValues().get(11).getValue());
		issueCustomValues.put("1", TestData.issue2.getCustomValues().get(12).getValue());
		issueCustomValues.put("9", TestData.issue2.getCustomValues().get(13).getValue());
		
		TimeEntry timeEntry = TestData.issue2.getTimeEntries().getAll().get(0); 
		timeValues = new LinkedHashMap<String, String>();
		timeValues.put("hours", ""+timeEntry.getHours());
		timeValues.put("activity_id", ""+timeEntry.getActivityId());
		timeValues.put("comments", timeEntry.getComments());
		
		timeCutomValues = new LinkedHashMap<String, String>();
		timeCutomValues.put("7", timeEntry.getCustomValues().get(5).getValue());
		
		timeEntry.addExtensionValue("extKey", "extVal");
	}

	@Before
	public void setUp() throws Exception {
		writeIssueMethod = IssueRequestEntity.class.getDeclaredMethod("writeIssue", Issue.class, String.class, TimeEntry.class);
		writeIssueMethod.setAccessible(true);

		writeIssueMethod2 = IssueRequestEntity.class.getDeclaredMethod("writeIssue", Map.class, String.class, TimeEntry.class);
		writeIssueMethod2.setAccessible(true);
	}

	@After
	public void tearDown() {
		TestData.reset();
	}
	
	@Test
	public void testWriteIssueIssue() throws Exception{
		StringBuilder builder = new StringBuilder();
		builder.append("{\"issue\":{");
		append(builder, issueValues);
		builder.append(",\"custom_field_values\":{");
		append(builder, issueCustomValues);
		builder.append("}}}");
		
		String result = (String)writeIssueMethod.invoke(null, TestData.issue2, null, null);
		assertEquals(builder.toString(), result);
	}
	
	@Test
	public void testWriteIssueMap() throws Exception{
		LinkedHashMap<String, String> issueValues = new LinkedHashMap<String, String>(IssueRequestEntityTest.issueValues);
		issueValues.remove("watcher_user_ids");
		
		StringBuilder builder = new StringBuilder();
		builder.append("{\"issue\":{");
		append(builder, issueValues);
		builder.append("}}");
		
		Map<RedmineApiIssueProperty, String> values = new LinkedHashMap<RedmineApiIssueProperty, String>();
		values.put(RedmineApiIssueProperty.SUBJECT, TestData.issue2.getSubject());
		values.put(RedmineApiIssueProperty.DESCRIPTION, TestData.issue2.getDescription());
		values.put(RedmineApiIssueProperty.TRACKER, ""+TestData.issue2.getTrackerId());//2
		values.put(RedmineApiIssueProperty.PROJECT, ""+TestData.issue2.getProjectId());//1
		values.put(RedmineApiIssueProperty.STATUS, ""+TestData.issue2.getStatusId());//2
		values.put(RedmineApiIssueProperty.PRIORITY, ""+TestData.issue2.getPriorityId());//5
		values.put(RedmineApiIssueProperty.START_DATE, "2010-05-08");
		values.put(RedmineApiIssueProperty.DUE_DATE, "");
		values.put(RedmineApiIssueProperty.DONE_RATIO, ""+TestData.issue2.getDoneRatio());//10
		values.put(RedmineApiIssueProperty.ESTIMATED_HOURS, ""+TestData.issue2.getEstimatedHours());//3.5
		values.put(RedmineApiIssueProperty.CATEGORY, "");
		values.put(RedmineApiIssueProperty.ASSIGNED_TO, ""+TestData.issue2.getAssignedToId());//3
		values.put(RedmineApiIssueProperty.FIXED_VERSION, ""+TestData.issue2.getFixedVersionId());//2
		values.put(RedmineApiIssueProperty.PARENT, ""+TestData.issue2.getParentId());//1
		
		String result = (String)writeIssueMethod2.invoke(null, values, null, null);
		assertEquals(builder.toString(), result);
	}
	
	@Test
	public void testWriteIssueIssueStringTimeEntry() throws Exception {
		TimeEntry timeEntry = TestData.issue2.getTimeEntries().getAll().get(0);
		timeEntry.addExtensionValue("extKey", "extVal");
		String comment = "comment without content";

		StringBuilder builder = new StringBuilder();
		builder.append("{\"issue\":{");
		append(builder, issueValues);
		builder.append(",\"custom_field_values\":{");
		append(builder, issueCustomValues);
		builder.append("}},");
		append(builder, "notes", comment);
		
		builder.append(",\"time_entry\":{");
		append(builder, timeValues);
		builder.append(",\"custom_field_values\":{");
		append(builder, timeCutomValues);
		builder.append("},\"extKey\":\"extVal\"}}");
		
		String result = (String)writeIssueMethod.invoke(null, TestData.issue2, comment, timeEntry);
		assertEquals(builder.toString(), result);

		TestData.reset();
	}

	@Test
	public void testWriteIssueIssue_watchersReadOnly() throws Exception{
		LinkedHashMap<String, String> issueValues = new LinkedHashMap<String, String>(IssueRequestEntityTest.issueValues);
		issueValues.remove("watcher_user_ids");
		TestData.issue2.setWatchersAddAllowed(false);
		TestData.issue2.setWatchersDeleteAllowed(false);

		StringBuilder builder = new StringBuilder();
		builder.append("{\"issue\":{");
		append(builder, issueValues);
		builder.append(",\"custom_field_values\":{");
		append(builder, issueCustomValues);
		builder.append("}}}");
		
		String result = (String)writeIssueMethod.invoke(null, TestData.issue2, null, null);
		assertEquals(builder.toString(), result);
	}
	
	static void append(StringBuilder builder, LinkedHashMap<String, String> values) {
		for (Entry<String, String> entry : values.entrySet()) {
			append(builder, entry.getKey(), entry.getValue());
			builder.append(",");
		}
		builder.setLength(builder.length()-1);
	}

	static void append(StringBuilder builder, String key, String value) {
		builder.append("\"").append(key).append("\":");
		
		if (value==null || !(value.startsWith("[") && value.endsWith("]"))) {
			builder.append("\"");
		}

		if(value!=null) {
			builder.append(value);
		}

		if (value==null || !(value.startsWith("[") && value.endsWith("]"))) {
			builder.append("\"");
		}
	}
}
