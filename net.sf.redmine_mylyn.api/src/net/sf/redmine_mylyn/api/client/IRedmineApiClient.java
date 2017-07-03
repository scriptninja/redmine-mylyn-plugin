package net.sf.redmine_mylyn.api.client;


import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.exception.RedmineApiInvalidDataException;
import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.query.Query;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IRedmineApiClient {

	public Configuration getConfiguration();

	public RedmineServerVersion detectServerVersion(IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public void updateConfiguration(IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public int[] getUpdatedIssueIds(int[] issues, Date updatedSince, IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public Issue[] getIssues(IProgressMonitor monitor, int... issueIds) throws RedmineApiErrorException;
	
	public Issue[] query(Query query, IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public Issue createIssue(Issue issue, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException;

	public void updateIssue(Issue issue, String comment, TimeEntry timeEntry, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException;

	public void updateIssue(int issueId, Map<RedmineApiIssueProperty, String> issueValues, String comment, TimeEntry timeEntry, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException;

	public InputStream getAttachmentContent(int attachmentId, String fileName, IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public void uploadAttachment(int issueId, Attachment attachment, InputStream content, String comment, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException;

}
