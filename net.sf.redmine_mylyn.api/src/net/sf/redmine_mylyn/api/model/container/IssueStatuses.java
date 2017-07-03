package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.IssueStatus;

@XmlRootElement(name="issueStatuses")
@XmlAccessorType(XmlAccessType.NONE)
public class IssueStatuses extends AbstractSortedPropertyContainer<IssueStatus> {

	private static final long serialVersionUID = 1L;

	protected List<IssueStatus> issueStatus;
	
	protected IssueStatus defaultStatus;
	
	@Override
	@XmlElement(name="issueStatus")
	protected List<IssueStatus> getModifiableList() {
		if(issueStatus==null) {
			issueStatus = new ArrayList<IssueStatus>() {

				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean add(IssueStatus e) {
					if(defaultStatus==null || e.isDefault()) {
						defaultStatus = e;
					}
					return super.add(e);
				}
				
			};
		}
		return issueStatus;
	}

	public IssueStatus getDefault() {
		return defaultStatus;
	}
}
