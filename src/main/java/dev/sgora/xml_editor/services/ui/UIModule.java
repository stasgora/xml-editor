package dev.sgora.xml_editor.services.ui;

import com.google.inject.AbstractModule;

public class UIModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(WorkspaceAction.class).to(WorkspaceActionService.class);
	}
}
