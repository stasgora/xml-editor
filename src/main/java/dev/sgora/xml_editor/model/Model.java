package dev.sgora.xml_editor.model;

import dev.sgora.observetree.Observable;

import java.io.File;

public class Model<M> extends Observable {
	private M value;
	private File file;

	public M getValue() {
		return value;
	}

	public void setValue(M value) {
		this.value = value;
		onValueChanged();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		onValueChanged();
	}
}
