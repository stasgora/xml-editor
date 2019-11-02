package dev.sgora.xml_editor.element;

import javafx.scene.control.DatePicker;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.logging.Level;

public class DateElement extends Element<DatePicker, LocalDate, XMLGregorianCalendar> {
	public DateElement(Object modelObject, Field objectField, XMLGregorianCalendar value) {
		super(modelObject, objectField, value);
	}

	@Override
	protected DatePicker createUiElement(XMLGregorianCalendar date) {
		DatePicker datePicker = new DatePicker(convertModelToElementValue(date));
		datePicker.valueProperty().addListener((observable, oldVal, newVal) -> updateModelValue(newVal));
		return datePicker;
	}

	@Override
	protected XMLGregorianCalendar convertElementToModelValue(LocalDate date) throws ValueConversionError {
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0);
		} catch (DatatypeConfigurationException e) {
			logger.log(Level.WARNING, "Creating calendar factory failed", e);
			throw new ValueConversionError("Unexpected error occurred", e);
		}
	}

	@Override
	protected LocalDate convertModelToElementValue(XMLGregorianCalendar date) {
		return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
	}
}
