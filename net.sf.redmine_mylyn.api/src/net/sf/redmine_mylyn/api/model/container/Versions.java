package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.Version;
import net.sf.redmine_mylyn.api.model.Version.Status;

@XmlRootElement(name="versions")
@XmlAccessorType(XmlAccessType.NONE)
public class Versions extends AbstractPropertyContainer<Version> {

	private static final long serialVersionUID = 1L;

	protected List<Version> versions;
	
	@Override
	@XmlElement(name="version")
	protected List<Version> getModifiableList() {
		if(versions==null) {
			versions = new ArrayList<Version>();
		}
		return versions;
	}

	public List<Version> getOpenById(int[] idlist) {
		List<Version> listed = new ArrayList<Version>(idlist.length);
		Version version;
		for (int id : idlist) {
			if ((version=getById(id))!=null && version.getStatus()!=Status.CLOSED) {
				listed.add(version);
			}
		}
		return listed;
	}

}
