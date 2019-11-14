package dev.sgora.xml_editor.services;

import java.util.logging.Logger;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class.getName());

	public static boolean isClassInternal(Class clazz) {
		return clazz.getPackageName().startsWith("dev.sgora");
	}
}
