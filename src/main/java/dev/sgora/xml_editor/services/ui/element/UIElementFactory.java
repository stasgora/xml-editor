package dev.sgora.xml_editor.services.ui.element;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIElementFactory {
	private static final Logger logger = Logger.getLogger(UIElementFactory.class.getName());

	private static final String TEXT_FIELD_CLASS = "xml-field";
	private static final String MULTI_TEXT_FIELD_CLASS = "xml-multi-field";

	public static TextField createTextField(String text) {
		TextField field = new TextField(text);
		field.getStyleClass().add(TEXT_FIELD_CLASS);
		return field;
	}

	public static Label createElementTitle(String title, ElementTitleType type) {
		var label = new Label(transformFieldName(title));
		label.getStyleClass().add(type.styleClass);
		return label;
	}

	public static DatePicker createDateField(XMLGregorianCalendar date) {
		return new DatePicker(LocalDate.of(date.getYear(), date.getMonth(), date.getDay()));
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

	public static ComboBox createComboBox(Class type, Object value) throws NoSuchFieldException, IllegalAccessException {
		ComboBox<String> comboBox = new ComboBox<>();
		for (Object constant : type.getEnumConstants())
			comboBox.getItems().add(getEnumValue(constant));
		comboBox.setValue(getEnumValue(value));
		return comboBox;
	}

	public static String transformFieldName(String fieldName) {
		fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return fieldName.replaceAll("(.)([A-Z])", "$1 $2");
	}

	public static <M> M createEmptyModel(Class<M> modelClass) {
		try {
			return isClassInternal(modelClass) ? createLocalModel(modelClass) : createRemoteModel(modelClass);
		} catch (InstantiationException | InvocationTargetException | IllegalAccessException | DatatypeConfigurationException e) {
			logger.log(Level.WARNING, "Creating model instance failed", e);
			return null;
		}
	}

	private static <M> M createLocalModel(Class<M> modelClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		M modelInstance;
		if(modelClass.isEnum())
			modelInstance = modelClass.getEnumConstants()[0];
		else {
			modelInstance = (M) modelClass.getDeclaredConstructors()[0].newInstance();
			for (Field field : modelClass.getDeclaredFields()) {
				field.setAccessible(true);
				field.set(modelInstance, createEmptyModel(field.getType()));
			}
		}
		return modelInstance;
	}

	private static <M> M createRemoteModel(Class<M> modelClass) throws DatatypeConfigurationException {
		M modelInstance = null;
		if(String.class.isAssignableFrom(modelClass))
			modelInstance = (M) "";
		else if(List.class.isAssignableFrom(modelClass))
			modelInstance = (M) new ArrayList<>();
		else if(Number.class.isAssignableFrom(modelClass)) {
			if(BigInteger.class.isAssignableFrom(modelClass))
				modelInstance = (M) BigInteger.ZERO;
			else if(BigDecimal.class.isAssignableFrom(modelClass))
				modelInstance = (M) BigDecimal.ZERO;
		} else if(XMLGregorianCalendar.class.isAssignableFrom(modelClass))
			modelInstance = (M) DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2000, 1, 1, 0);
		return modelInstance;
	}

	private static boolean isClassInternal(Class clazz) {
		return clazz.getPackageName().startsWith("dev.sgora");
	}

	private static String getEnumValue(Object object) throws NoSuchFieldException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField("value");
		field.setAccessible(true);
		return (String) field.get(object);
	}
	
	public void showFieldError(Node node, String errorMessage) {
		node.getStyleClass().add("error-field");
		ContextMenu validationMessage = new ContextMenu();
		validationMessage.setAutoHide(false);
		validationMessage.getItems().add(new MenuItem(errorMessage));
		node.hoverProperty().addListener(((observable, oldVal, newVal) -> {
			if(newVal)
				validationMessage.show(node, Side.RIGHT, 10, 0);
			else
				validationMessage.hide();
		}));
	}
}
