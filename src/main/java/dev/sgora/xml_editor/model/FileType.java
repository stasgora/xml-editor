package dev.sgora.xml_editor.model;

public enum FileType {
	LOCAL("Local"),
	DRIVE("Drive");

	public final String name;

	FileType(String name) {
		this.name = name;
	}
}
