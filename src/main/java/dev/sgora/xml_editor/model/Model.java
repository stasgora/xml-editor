package dev.sgora.xml_editor.model;

import dev.sgora.observetree.Observable;
import dev.sgora.xml_editor.element.ComplexElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Model<M> extends Observable {
	private M value;
	private String fileName;
	private FileType fileType;

	public final List<ComplexElement> elements = new ArrayList<>();
	public final List<ComplexElement> rootElements = new ArrayList<>();

	public M getValue() {
		return value;
	}

	public void set(M value, String fileName, FileType fileType) {
		this.value = value;
		this.fileName = fileName;
		this.fileType = fileType;

		elements.clear();
		rootElements.clear();
		onValueChanged();
	}

	public void move(String fileName, FileType fileType) {
		this.fileName = fileName;
		this.fileType = fileType;
		onValueChanged();
	}

	public void addElement(ComplexElement element) {
		elements.add(element);
		if(element.root)
			rootElements.add(element);
	}

	public String getFileName() {
		return fileName;
	}

	public FileType getFileType() {
		return fileType;
	}
}
