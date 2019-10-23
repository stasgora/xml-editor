package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ModelUIMapper<M> {
	private final M model;

	@Inject
	private ModelUIMapper(M model) {
		this.model = model;
	}

}
