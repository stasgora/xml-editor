package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.enums.EnumField;
import javafx.scene.control.ComboBox;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class EnumElement extends ValueElement<ComboBox, String, Object> {
	private final Class enumType;
	private final Map<String, String> enumValueMap;

	public EnumElement(Object modelObject, Field objectField, Object value) {
		super(modelObject, objectField, value);
		this.enumType = value.getClass();
		enumValueMap = getEnumFieldMap(enumType.getEnumConstants());
	}

	@Override
	protected ComboBox createUIElement(Object value) {
		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.getItems().addAll(enumValueMap.keySet());
		comboBox.setValue(convertModelToElementValue(value));
		comboBox.valueProperty().addListener((observable, oldVal, newVal) -> updateModelValue(newVal));
		return comboBox;
	}

	@Override
	protected Object convertElementToModelValue(String value) {
		return Enum.valueOf((Class<? extends Enum>) enumType, enumValueMap.get(value));
	}

	@Override
	protected String convertModelToElementValue(Object value) {
		return getEnumField(value, EnumField.VALUE);
	}

	private Map<String, String> getEnumFieldMap(Object[] values) {
		Map<String, String> map = new HashMap<>();
		for (Object value : values)
			map.put(getEnumField(value, EnumField.VALUE), getEnumField(value, EnumField.NAME));
		return map;
	}

	private String getEnumField(Object object, EnumField fieldType) {
		try {
			return (String) object.getClass().getMethod(fieldType.name).invoke(object);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			logger.log(Level.WARNING, "Getting enum field value failed", e);
		}
		return null;
	}
}
