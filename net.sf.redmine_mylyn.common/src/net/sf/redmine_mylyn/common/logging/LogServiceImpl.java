package net.sf.redmine_mylyn.common.logging;

import java.text.MessageFormat;

// import net.sf.redmine_mylyn.internal.common.logging.ExtendedLogServiceImpl;

// import org.eclipse.equinox.log.ExtendedLogService;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;


public class LogServiceImpl implements ILogService {

	private LogService logService;
	
	private static LogServiceImpl instance;

	public LogServiceImpl() {
		instance = this;
	}
	
	
	public static ILogService getInstance(Bundle bundle, Class<?> loggerName) {
		if(instance==null) {
			instance = new LogServiceImpl();
		}
		
//		if(instance.logService!=null && instance.logService instanceof ExtendedLogService) {
//			return new ExtendedLogServiceImpl((ExtendedLogService)instance.logService, bundle, loggerName);
//		}
		
		return instance;
	}
	
	public synchronized void setLogService(LogService logService) {
		this.logService = logService;
	}
	
	public synchronized void unsetLogService(LogService logService) {
		if(this.logService==logService) {
			this.logService=null;
		}
	}

	@Override
	public void error(Throwable t, String message, Object... values) {
		if(logService!=null) {
			logService.log(LogService.LOG_ERROR, MessageFormat.format(message, values), t);
		}
	}

	@Override
	public void error(String message, Object... values) {
		if(logService!=null) {
			logService.log(LogService.LOG_ERROR, MessageFormat.format(message, values));
		}
	}

	@Override
	public void info(String message, Object... values) {
		if(logService!=null) {
			logService.log(LogService.LOG_INFO, MessageFormat.format(message, values));
		}
	}

	@Override
	public void debug(String message, Object... values) {
		if(logService!=null) {
			logService.log(LogService.LOG_DEBUG, MessageFormat.format(message, values));
		}
	}
	
}
