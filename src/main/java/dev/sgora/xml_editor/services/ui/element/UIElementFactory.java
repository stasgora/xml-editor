package dev.sgora.xml_editor.services.ui.element;

import dev.sgora.xml_editor.element.ElementTitleType;
import dev.sgora.xml_editor.element.EnumField;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIElementFactory {
	private static final Logger logger = Logger.getLogger(UIElementFactory.class.getName());

	private static final String TEXT_FIELD_CLASS = "xml-field";
	private static final String FIELD_ERROR_CLASS = "error-field";
	private static final String MULTI_TEXT_FIELD_CLASS = "xml-multi-field";

	public static TextField createTextField(String text, BiConsumer<Node, Object> setValue) {
		TextField field = new TextField(text);
		field.getStyleClass().add(TEXT_FIELD_CLASS);
		field.textProperty().addListener((observable, oldVal, newVal) -> setValue.accept(field, newVal));
		return field;
	}

	public static Label createElementTitle(String title, ElementTitleType type) {
		var label = new Label(transformFieldName(title));
		label.getStyleClass().add(type.styleClass);
		return label;
	}

	public static DatePicker createDateField(XMLGregorianCalendar date, BiConsumer<Node, Object> setValue) {
		DatePicker datePicker = new DatePicker(LocalDate.of(date.getYear(), date.getMonth(), date.getDay()));
		datePicker.valueProperty().addListener((observable, oldVal, newVal) -> {
			try {
				setValue.accept(datePicker, EmptyModelFactory.createXMLCalendar(newVal));
			} catch (DatatypeConfigurationException e) {
				logger.log(Level.WARNING, "Creating date failed", e);
			}
		});
		return datePicker;
	}

	public static ComboBox createComboBox(Class type, Object value, BiConsumer<Node, Object> setValue)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		ComboBox<String> comboBox = new ComboBox<>();
		Map<String, String> enumValueMap = getEnumFieldMap(type.getEnumConstants());
		comboBox.getItems().addAll(enumValueMap.keySet());
		comboBox.setValue(getEnumField(value, EnumField.VALUE));
		comboBox.setUserData(enumValueMap);
		comboBox.valueProperty().addListener((observable, oldVal, newVal) -> setValue.accept(comboBox,
				Enum.valueOf((Class<? extends Enum>) type, ((Map<String, String>) comboBox.getUserData()).get(newVal))));
		return comboBox;
	}

	public static HBox wrapFieldAsListElement(Node field, Supplier<Node> fieldSupplier, VBox container) {
		HBox layout = new HBox(5);
		layout.setAlignment(Pos.CENTER_LEFT);
		layout.getStyleClass().add(MULTI_TEXT_FIELD_CLASS);
		Button removeField = new Button("-");
		removeField.setOnAction(event -> {
			if(container.getChildren().size() > 1)
				container.getChildren().remove(layout);
		});
		removeField.setTooltip(new Tooltip("Remove item"));
		Button addField = new Button("+");
		addField.setTooltip(new Tooltip("Add item after"));
		addField.setOnAction(event -> container.getChildren().add(container.getChildren().indexOf(layout) + 1,
				wrapFieldAsListElement(fieldSupplier.get(), fieldSupplier, container)));
		layout.getChildren().addAll(field, removeField, addField);
		return layout;
	}

	public static String transformFieldName(String fieldName) {
		fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return fieldName.replaceAll("(.)([A-Z])", "$1 $2");
	}


	public static VBox createAlignedVBox(Pos alignment, int spacing, Node... nodes) {
		VBox box = new VBox(spacing, nodes);
		box.setAlignment(alignment);
		return box;
	}

	private static Map<String, String> getEnumFieldMap(Object[] values) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Map<String, String> map = new HashMap<>();
		for (Object value : values)
			map.put(getEnumField(value, EnumField.VALUE), getEnumField(value, EnumField.NAME));
		return map;
	}

	private static String getEnumField(Object object, EnumField fieldType) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return (String) object.getClass().getMethod(fieldType.name).invoke(object);
	}
	
	public static ContextMenu createFieldErrorList(Node node) {
		ContextMenu errorList = new ContextMenu();
		errorList.setAutoHide(false);
		errorList.getItems().addListener((ListChangeListener.Change<? extends MenuItem> c) -> {
			if(!c.getList().isEmpty() && !node.getStyleClass().contains(FIELD_ERROR_CLASS))
				node.getStyleClass().add(FIELD_ERROR_CLASS);
			else if(c.getList().isEmpty() && node.getStyleClass().contains(FIELD_ERROR_CLASS))
				node.getStyleClass().remove(FIELD_ERROR_CLASS);
		});
		node.hoverProperty().addListener(((observable, oldVal, newVal) -> {
			if(newVal)
				errorList.show(node, Side.RIGHT, 10, 0);
			else
				errorList.hide();
		}));
		return errorList;
	}
}
