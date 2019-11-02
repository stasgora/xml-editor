package dev.sgora.xml_editor.services;

import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErrorUtil {
	private static final Logger logger = Logger.getLogger(ErrorUtil.class.getName());

	public static <P> Consumer<P> wrap(UnsafeConsumer<P> consumer, String errorMessage) {
		return param -> {
			try {
				consumer.consume(param);
			} catch (Exception e) {
				logger.log(Level.SEVERE, errorMessage, e);
			}
		};
	}

	public static <P, R> Function<P, R> wrap(UnsafeFunction<P, R> supplier, String errorMessage) {
		return param -> {
			try {
				return supplier.apply(param);
			} catch (Exception e) {
				logger.log(Level.SEVERE, errorMessage, e);
				return null;
			}
		};
	}

	public static <D, R> BiConsumer<D, R> wrap(UnsafeBiConsumer<D, R> consumer, String errorMessage) {
		return (param1, param2) -> {
			try {
				consumer.consume(param1, param2);
			} catch (Exception e) {
				logger.log(Level.SEVERE, errorMessage, e);
			}
		};
	}

	@FunctionalInterface
	public interface UnsafeConsumer<P> {
		void consume(P param) throws Exception;
	}

	@FunctionalInterface
	public interface UnsafeFunction<P, R> {
		R apply(P param) throws Exception;
	}

	@FunctionalInterface
	public interface UnsafeBiConsumer<D, R> {
		void consume(D param1, R param2) throws Exception;
	}
}
