package dev.sgora.xml_editor.services.ui.element;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmptyModelFactory {
	private static final Logger logger = Logger.getLogger(EmptyModelFactory.class.getName());

	public static <M> M createEmptyModel(Class<M> modelClass, Type type) {
		try {
			return isClassInternal(modelClass) ? createLocalModel(modelClass) : createRemoteModel(modelClass, type);
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
				field.set(modelInstance, createEmptyModel(field.getType(), field.getGenericType()));
			}
		}
		return modelInstance;
	}

	private static <M> M createRemoteModel(Class<M> modelClass, Type type) throws DatatypeConfigurationException {
		M modelInstance = null;
		if(String.class.isAssignableFrom(modelClass))
			modelInstance = (M) "";
		else if(List.class.isAssignableFrom(modelClass)) {
			Type listType = ((ParameterizedType) type).getActualTypeArguments()[0];
			modelInstance = (M) Arrays.asList(createEmptyModel((Class) listType, listType));
		} else if(Number.class.isAssignableFrom(modelClass)) {
			if(BigInteger.class.isAssignableFrom(modelClass))
				modelInstance = (M) BigInteger.ZERO;
			else if(BigDecimal.class.isAssignableFrom(modelClass))
				modelInstance = (M) BigDecimal.ZERO;
		} else if(XMLGregorianCalendar.class.isAssignableFrom(modelClass))
			modelInstance = (M) createXMLCalendar(LocalDate.of(2000, 1, 1));
		return modelInstance;
	}

	public static XMLGregorianCalendar createXMLCalendar(LocalDate date) throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0);
	}

	private static boolean isClassInternal(Class clazz) {
		return clazz.getPackageName().startsWith("dev.sgora");
	}
}
