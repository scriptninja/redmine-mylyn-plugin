package net.sf.redmine_mylyn.ui;

import java.text.MessageFormat;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.common.logging.LogServiceImpl;
import net.sf.redmine_mylyn.core.IRedmineSpentTimeManager;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TaskRepositoryLocationUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

public class RedmineUiPlugin extends AbstractUIPlugin implements LogListener {

	public static final String PLUGIN_ID = "net.sf.redmine_mylyn.ui"; //$NON-NLS-1$

	private static RedmineUiPlugin plugin;

	private final ISelectionListener taskListSelectionListener;
	
	private IStructuredSelection taskListSelection;
	
	private IRedmineSpentTimeManager spentTimeManager;
	
	private ServiceReference logReaderServiceReference;
	
	private LogReaderService logReaderService;
	
	private boolean isEclipseVersionLesserThan37 = false;

	public RedmineUiPlugin() {
		super();
		
		isEclipseVersionLesserThan37 = Platform.getBundle("org.eclipse.core.runtime").getVersion().compareTo(new Version(3, 7, 0)) < 0;
		
		taskListSelectionListener = new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
				if (arg1 instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)arg1;
					taskListSelection = selection.isEmpty() ? null : selection;
				}
			}
		};
	}
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		
		RedmineUiPlugin.plugin = this;
		
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(RedmineCorePlugin.REPOSITORY_KIND);
		if(connector instanceof RedmineRepositoryConnector) {
			RedmineRepositoryConnector redmineConnector = (RedmineRepositoryConnector)connector;
			
			redmineConnector.setTaskRepositoryLocationFactory(new TaskRepositoryLocationUiFactory());
			TasksUi.getRepositoryManager().addListener(redmineConnector.getClientManager());
			TasksUi.getRepositoryManager().addListener(RedmineCorePlugin.getDefault().getExtensionManager());
			
			RedmineCorePlugin.getDefault().setConnector(redmineConnector);
		}
		
		
		logReaderServiceReference = bundleContext.getServiceReference(LogReaderService.class.getName());
		if(logReaderServiceReference!=null) {
			logReaderService = (LogReaderService)bundleContext.getService(logReaderServiceReference);
			logReaderService.addLogListener(this);
		}
		
		try {
			ISelectionService selServive = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			selServive.addSelectionListener(taskListSelectionListener);
		} catch (NullPointerException e) {}
		
		spentTimeManager = RedmineCorePlugin.getDefault().getSpentTimeManager(TasksUi.getTaskActivityManager());
		
		
		//Workaround for a bundle with experimental addons
		Bundle extrasBundle = Platform.getBundle("org.tigase.redmine_mylyn.ui.extras"); //$NON-NLS-1$
		if(extrasBundle!=null) {
			try {
				extrasBundle.start(Bundle.START_TRANSIENT);
			} catch (BundleException e ) {
				getLogService(this.getClass()).error(e, "Can't start bundle org.tigase.redmine_mylyn.ui.extras"); //$NON-NLS-1$
			} catch (IllegalStateException e ) {
				getLogService(this.getClass()).error(e, "Can't start bundle org.tigase.redmine_mylyn.ui.extras"); //$NON-NLS-1$
			}
		}
	}

	
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		try {
			ISelectionService selServive = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			selServive.removePostSelectionListener(taskListSelectionListener);
		} catch (NullPointerException e) {}

		if(logReaderService!=null) {
			logReaderService.removeLogListener(this);
			bundleContext.ungetService(logReaderServiceReference);
		}
		
		RedmineUiPlugin.plugin = null;
		
		super.stop(bundleContext);
	}

	public static RedmineUiPlugin getDefault() {
		return plugin;
	}
	
	@Override
	public void logged(LogEntry entry) {
		if (entry.getBundle().getSymbolicName().startsWith("net.sf.redmine_mylyn.")) { //$NON-NLS-1$
			IStatus status = buildStatus(entry);

			if (isEclipseVersionLesserThan37) {
				getLog().log(buildStatus(entry));
			}
			

			if(status.getSeverity()==IStatus.ERROR) {
				StatusManager.getManager().handle(status, StatusManager.SHOW);
			}
		}
	}
	
	private IStatus buildStatus(LogEntry entry) {
		int severity = entry.getLevel()==LogService.LOG_ERROR ? IStatus.ERROR : IStatus.INFO;
		String pluginId = entry.getBundle().getSymbolicName();
		
		return new Status(severity, pluginId, entry.getMessage(), entry.getException());
	}

	public IStructuredSelection getTaskListSelection() {
		return taskListSelection;
	}

	public IRedmineSpentTimeManager getSpentTimeManager() {
		return spentTimeManager;
	}
	
	public static ILogService getLogService(Class<?> clazz) {
		return LogServiceImpl.getInstance((plugin==null ? null : plugin.getBundle()), clazz);
	}

	public static IStatus toStatus(Throwable e, String message) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, e);
	}

	public static IStatus toStatus(Throwable e, String message, Object... params) {
		message = MessageFormat.format(message, params);
		return toStatus(e, message);
	}

}
