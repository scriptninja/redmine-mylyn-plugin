package net.sf.redmine_mylyn.api.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement(name="version", namespace="http://redmin-mylyncon.sf.net/api")
@XmlAccessorType(XmlAccessType.NONE)
public class RedmineServerVersion {
	
	
	@XmlElement(namespace="http://redmin-mylyncon.sf.net/api")
	public SubVersion plugin;

	@XmlElement(namespace="http://redmin-mylyncon.sf.net/api")
	public SubVersion redmine;

	public enum Release {
		REDMINE_1_0(1, 0),
		PLUGIN_2_7(2, 7);

		public final int major;
		public final int minor;
		public final int tiny;

		Release(int major, int minor) {
			this(major, minor, 0);
		}

		Release(int major, int minor, int tiny) {
			this.major = major;
			this.minor = minor;
			this.tiny = tiny;
		}
	}
	
	RedmineServerVersion() {
	}
	
	public RedmineServerVersion(SubVersion redmine, SubVersion plugin) {
		this.redmine = redmine;
		this.plugin = plugin;
	}

	public RedmineServerVersion(Release redmine, Release plugin) {
		this(new SubVersion(redmine), new SubVersion(plugin));
	}

	@XmlAccessorType(XmlAccessType.NONE)
	public static class SubVersion implements Comparable<Release> {

		public int major;

		public int minor;

		public int tiny;
		
		private String versionString;

		SubVersion() {
		}
		
		public SubVersion(Release release) {
			major = release.major;
			minor = release.minor;
			tiny = release.tiny;
		}
		
		@XmlValue
		public String getVersionString() {
			return versionString;
		}

		public void setVersionString(String versionString) {
			this.versionString = versionString;

			String[] parts = versionString.split("\\."); //$NON-NLS-1$
			if (parts != null && parts.length >= 3) {
				try {
					major = Integer.parseInt(parts[0]);
					minor = Integer.parseInt(parts[1]);
					tiny = Integer.parseInt(parts[2]);
				} catch (NumberFormatException e) {
				}
			}
		}

		public int compareTo(Release release) {
			if (major < release.major) {
				return -1;
			}
			if (major > release.major) {
				return 1;
			}
			if (minor < release.minor) {
				return -1;
			}
			if (minor > release.minor) {
				return 1;
			}
			if (tiny < release.tiny) {
				return -1;
			}
			if (tiny > release.tiny) {
				return 1;
			}
			return 0;
		}

		public int compareTo(SubVersion version) {
			if (major < version.major) {
				return -1;
			}
			if (major > version.major) {
				return 1;
			}
			if (minor < version.minor) {
				return -1;
			}
			if (minor > version.minor) {
				return 1;
			}
			if (tiny < version.tiny) {
				return -1;
			}
			if (tiny > version.tiny) {
				return 1;
			}
			return 0;
		}

		@Override
		public String toString() {
			return String.format("%d.%d.%d", major, minor, tiny); //$NON-NLS-1$
		}

	}
	
	@Override
	public String toString() {
		if(redmine != null && plugin != null) {
			return redmine.toString() + "-" + plugin.toString(); //$NON-NLS-1$
		}
		return super.toString();
	}

}
