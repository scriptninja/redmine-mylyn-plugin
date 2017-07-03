package net.sf.redmine_mylyn.internal.ui.editor;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.User;
import net.sf.redmine_mylyn.api.model.container.Users;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RedmineWatchersEditor extends AbstractAttributeEditor {

	private final static int COLUMN_IDX_ID     = 0;
	private final static int COLUMN_IDX_NAME   = 1;
	private final static int COLUMN_IDX_MARKER = 2;
	
	private final Users users;
	
	Table table;
	
	TableViewer viewer;
	
	Color removeMarkerColor;
	Color addMarkerColor;
	Color defaultColor;
	
	public RedmineWatchersEditor(Configuration configuration, TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.SINGLE));

		this.users = configuration.getUsers();
		
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
//		defaultColor = toolkit.getColors().getColor(IFormColors.TB_FG);
//		removeMarkerColor = toolkit.getColors().getColor(IFormColors.TB_TOGGLE);
//		addMarkerColor = toolkit.getColors().getColor(IFormColors.TB_PREFIX);
		
		table = new Table(parent, SWT.MULTI);
		table.setMenu(buildContextMenu(table));
		
		TableColumn idColumn = new TableColumn(table, SWT.NONE);
		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		TableColumn markerColumn = new TableColumn(table, SWT.NONE);
		
		for (String userId : getTaskAttribute().getValues()) {
			User user = users.getById(RedmineUtil.parseIntegerId(userId));
			
			if (user!=null) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(COLUMN_IDX_ID, userId);
				item.setText(COLUMN_IDX_NAME, RedmineUtil.formatUserPresentation(user.getLogin(), user.getName()));
			}
		}
		
		TaskAttribute addAttribute = getTaskAttribute().getAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey());
		if (addAttribute!=null) {
			for (String userId : addAttribute.getValues()) {
				User user = users.getById(RedmineUtil.parseIntegerId(userId));
				
				if (user!=null) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(COLUMN_IDX_ID, userId);
					item.setText(COLUMN_IDX_NAME, RedmineUtil.formatUserPresentation(user.getLogin(), user.getName()));
				}
			}
		}
		
		nameColumn.pack();
		markerColumn.pack();
		idColumn.pack();
		idColumn.setWidth(0);
		idColumn.setResizable(false);
		
		updateMarker();
		
//		toolkit.paintBordersFor(composite);
		toolkit.adapt(table, false, false);
		setControl(table);

		
	}
	
	public void addWatcher(IRepositoryPerson person) {
		User user = users.getByLogin(person.getPersonId());
		TaskAttribute attribute = getTaskAttribute().getAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey());
		if (user!=null && attribute!=null) {
			
			String userId = ""+user.getId(); //$NON-NLS-1$
			if (!attribute.getValues().contains(userId) && !getTaskAttribute().getValues().contains(userId)) {
				attribute.addValue(userId);
				
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(COLUMN_IDX_ID, userId);
				item.setText(COLUMN_IDX_NAME, RedmineUtil.formatUserPresentation(user.getLogin(), user.getName()));
				
				updateMarker();
				attributeChanged();
			}
			
		}
	}
	
	private boolean markForRemove(String userId) {
		TaskAttribute attribute = getTaskAttribute().getAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey()); 
		if(attribute!=null && attribute.getValues().contains(userId)) {
			attribute.removeValue(userId);
			return true;
		} else {
			attribute = getTaskAttribute().getAttribute(RedmineAttribute.WATCHERS_REMOVE.getTaskKey()); 
			if (attribute!=null && !attribute.getValues().contains(userId)) {
				attribute.addValue(userId);
			}
		}
		return false;
	}
	
	private void unmarkFromRemove(String userId) {
		TaskAttribute attribute = getTaskAttribute().getAttribute(RedmineAttribute.WATCHERS_REMOVE.getTaskKey()); 
		if (attribute!=null && attribute.getValues().contains(userId)) {
			attribute.removeValue(userId);
		}
		
	}
	
	private void updateMarker() {
		TaskAttribute addAttribute = getTaskAttribute().getAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey());
		TaskAttribute removeAttribute = getTaskAttribute().getAttribute(RedmineAttribute.WATCHERS_REMOVE.getTaskKey());
		
		for(TableItem item : table.getItems()) {
			String userId = item.getText(COLUMN_IDX_ID);
			if(userId==null || userId.isEmpty()) {
				continue;
			}
			
			if (addAttribute!=null && addAttribute.getValues().contains(userId)) {
				item.setText(COLUMN_IDX_MARKER, "(+)");
			} else if (removeAttribute!=null && removeAttribute.getValues().contains(userId)) {
				item.setText(COLUMN_IDX_MARKER, "(-)");
			} else {
				item.setText(COLUMN_IDX_MARKER, "");
			}
		}
		
		table.getColumn(COLUMN_IDX_NAME).pack();
		table.getColumn(COLUMN_IDX_MARKER).pack();
	}
	
	private Menu buildContextMenu(Control parent) {
		Menu contextMenu = new Menu( table);
		
		MenuItem markItem = new MenuItem( contextMenu, SWT.PUSH );
		markItem.setText("Mark to remove");
		markItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int idx :  table.getSelectionIndices()) {
					TableItem item = table.getItem(idx);
					String userId = item.getText(COLUMN_IDX_ID);
					
					if(!(userId==null || userId.isEmpty())) {
						if (markForRemove(userId)) {
							item.dispose();
						}
					}
				}
				updateMarker();
				attributeChanged();
			}
		});

		MenuItem unmarkItem = new MenuItem( contextMenu, SWT.PUSH );
		unmarkItem.setText("Unmark (don't remove)");
		unmarkItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int idx : table.getSelectionIndices()) {
					TableItem item = table.getItem(idx);
					String userId = item.getText(COLUMN_IDX_ID);
					
					if(!(userId==null || userId.isEmpty())) {
						unmarkFromRemove(userId);
					}
				}				
				updateMarker();
				attributeChanged();
			}
		});
		
		return contextMenu;
	}
	
	
}
