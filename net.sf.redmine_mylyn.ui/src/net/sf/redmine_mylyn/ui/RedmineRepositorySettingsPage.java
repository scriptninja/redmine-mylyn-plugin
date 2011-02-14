package net.sf.redmine_mylyn.ui;

import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion.Release;
import net.sf.redmine_mylyn.api.exception.RedmineApiAuthenticationException;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.client.ClientFactory;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class RedmineRepositorySettingsPage extends AbstractRepositorySettingsPage {

//	private static final String EXTENSION_ID_TEXTILE = "org.eclipse.mylyn.wikitext.tasks.ui.editor.textileTaskEditorExtension"; //$NON-NLS-1$
//	private static final String EXTENSION_ID_PLAIN = "none"; //$NON-NLS-1$
//	private static final String EXTENSION_POINT_CLIENT = "org.svenk.redmine.core.clientInterface"; //$NON-NLS-1$
	
	private String checkedUrl;
	
	private RedmineServerVersion requiredVersion;

	private String detectedVersionString = null;
	
	private Text apiKeyText;

	private Label apiKeyLabel;
	
	private Button apiKeyEnableButton;
	
	public RedmineRepositorySettingsPage(TaskRepository taskRepository) {
		
		super("Redmine Repository Settings", "Example: http://www.your-domain.de/redmine", taskRepository);

		//TODO configure
		requiredVersion = new RedmineServerVersion(Release.REDMINE_1_0, Release.PLUGIN_2_7);

		setNeedsAnonymousLogin(false);
		setNeedsValidation(true);
		setNeedsHttpAuth(true);
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		checkedUrl = getRepositoryUrl();

		//Set Default Encoding
		if (getRepository()==null) {
			setEncoding("UTF-8"); //$NON-NLS-1$
		}
	}
	
	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setVersion(detectedVersionString);
		if(useApiKey()) {
			repository.setProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY, apiKeyText.getText().trim());
		} else {
			repository.removeProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY);
		}
	}
	
	@Override
	protected Validator getValidator(final TaskRepository repository) {
		return new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				detectedVersionString = null;
				
				RedmineServerVersion detectedVersion = null;
				try {
					IClient client = ClientFactory.createClient(repository);
					detectedVersion = client.checkClientConnection(monitor);
				} catch (RedmineStatusException e) {
					if(e.getCause() instanceof RedmineApiAuthenticationException) {
						throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Invalid credentials"));
					}
					throw new CoreException(e.getStatus());
				}
				checkedUrl = repository.getRepositoryUrl();
				
				validateVersion(requiredVersion, detectedVersion);
//				validateEditorExtension(repository);

				detectedVersionString = detectedVersion.toString();

				String msg = "Test of connection was successful - Redmine %s with Mylyn-Plugin %s";
				msg = String.format(msg, detectedVersion.redmine.toString(), detectedVersion.plugin.toString());
				this.setStatus(new Status(IStatus.OK, RedmineCorePlugin.PLUGIN_ID, msg));
			}
			
//			@SuppressWarnings("restriction")
//			protected void validateEditorExtension(TaskRepository repository) throws CoreException {
//				String editorExtension = repository.getProperty(TaskEditorExtensions.REPOSITORY_PROPERTY_EDITOR_EXTENSION);
//				if (!(editorExtension==null || editorExtension.equals(EXTENSION_ID_PLAIN) || editorExtension.equals(EXTENSION_ID_TEXTILE))) {
//					throw new CoreException(new Status(IStatus.WARNING, RedmineCorePlugin.PLUGIN_ID, Messages.RedmineRepositorySettingsPage_MESSAGE_WIKI_WARNING));
//				}
//			}
			
			protected void validateVersion(RedmineServerVersion required, RedmineServerVersion detected) throws CoreException {
				if (detected==null || detected.redmine==null || detected.plugin==null) {
					throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Can't detect the version of Redmine"));
				} else if (detected.redmine.compareTo(required.redmine)<0 || detected.plugin.compareTo(required.plugin)<0) {
					String msg = "Redmine %s with Mylyn-Plugin %s is required, found Version %s with %s";
					msg = String.format(msg, required.redmine.toString(), required.plugin.toString(), detected.redmine.toString(), detected.plugin.toString());
					throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, msg));
				}
			}
		};
	}

	@Override
	public String getVersion() {
		return detectedVersionString;
	}
	
	@Override
	public String getConnectorKind() {
		return RedmineCorePlugin.REPOSITORY_KIND;
	}

	@Override
	protected void createSettingControls(Composite parent) {
		super.createSettingControls(parent);
		
		//oldApiKey
		String apiKey = repository==null ? null : repository.getProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY);
		boolean useApiKey = apiKey!=null && !apiKey.isEmpty();

		//REPOSITORY_SETTING_API_KEY
		apiKeyLabel = new Label(parent, SWT.NONE);
		apiKeyLabel.setText("API-Key");

		apiKeyText = new Text(parent, SWT.BORDER);
		apiKeyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		//TODO SWT.CHANGED not available in 3.5, test this in 3.6
		apiKeyText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				isPageComplete();
				
			}
		});
//		apiKeyText.addListener(SWT.CHANGED, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				isPageComplete();
//			}
//		});
		
		if(apiKey!=null) {
			apiKeyText.setText(apiKey);
		}
		
		apiKeyEnableButton = new Button(parent, SWT.CHECK);
		apiKeyEnableButton.setText("Enable");
		apiKeyEnableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setApiKeyUsage(apiKeyEnableButton.getSelection());
				isPageComplete();
			}
		});
		
		apiKeyLabel.moveBelow(savePasswordButton);
		apiKeyText.moveBelow(apiKeyLabel);
		apiKeyEnableButton.moveBelow(apiKeyText);
		
		setApiKeyUsage(useApiKey);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
	}

	@Override
	public boolean isPageComplete() {
		String errorMessage = null;
		
		if(isMissingApiKey()) {
			errorMessage = "Enter a valid API-Key";
		}
		
		if(isMissingApiKeyUsage()) {
			errorMessage = "Additional HTTP-Auth needs an API-Key instead of Username and Password.";
		}
		
		if(errorMessage!=null) {
			setMessage(errorMessage, IMessageProvider.ERROR);
			return false;
		} else {
			return super.isPageComplete() && checkedUrl!= null && detectedVersionString != null && checkedUrl.equals(getRepositoryUrl());
		}
	}

	@Override
	protected boolean isValidUrl(String arg0) {
		return true;
	}
	
	@Override
	protected boolean isMissingCredentials() {
		return !useApiKey() && super.isMissingCredentials();
	}
	
	private boolean isMissingApiKey() {
		return useApiKey() && apiKeyText.getText().trim().isEmpty();
	}
	
	private boolean useApiKey() {
		return apiKeyEnableButton!=null && apiKeyEnableButton.getSelection();
	}
	
	protected boolean isMissingApiKeyUsage() {
		try {
			return !useApiKey() && getHttpAuth();
		} catch (NullPointerException e) {
			return false;
		}
	}

	private void setApiKeyUsage(boolean use) {
		Composite parent = apiKeyEnableButton.getParent();
		
		repositoryUserNameEditor.setEnabled(!use, parent);
		repositoryPasswordEditor.setEnabled(!use, parent);
		
		apiKeyEnableButton.setSelection(use);
		apiKeyText.setEnabled(use);
		
	}

}
