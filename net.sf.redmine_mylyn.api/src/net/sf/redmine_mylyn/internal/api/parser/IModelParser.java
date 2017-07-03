package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;

public interface IModelParser<T> {

	T parseResponse(InputStream input, int sc) throws RedmineApiErrorException;
	
}
