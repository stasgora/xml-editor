package dev.sgora.xml_editor.model;

import dev.sgora.observetree.Observable;
import dev.sgora.xml_editor.element.ComplexElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Model<M> extends Observable {
	private M value;
	private File file;
	public final List<ComplexElement> elements = new ArrayList<>();
	public final List<ComplexElement> rootElements = new ArrayList<>();

	public M getValue() {
		return value;
	}

	public void setValue(M value) {
		this.value = value;
		elements.clear();
		rootElements.clear();
		onValueChanged();
	}

	public void addElement(ComplexElement element) {
		elements.add(element);
		if(element.root)
			rootElements.add(element);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		onValueChanged();
	}
}
