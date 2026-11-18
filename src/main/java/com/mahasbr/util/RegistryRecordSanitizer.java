package com.mahasbr.util;

import java.lang.reflect.Field;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Makes a record from an external API safe to store without rejecting it: text is trimmed,
 * empty text becomes blank (null), text longer than its column is cut to fit, and an invalid
 * e-mail address is left blank. The limits are read from the entity's own @Size / @Column
 * annotations, so they never drift from the database.
 */
public final class RegistryRecordSanitizer {

	private static final java.util.regex.Pattern EMAIL = java.util.regex.Pattern
			.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private RegistryRecordSanitizer() {
	}

	public static void sanitize(MstRegistryDetailsPageEntity entity) {
		for (Field field : MstRegistryDetailsPageEntity.class.getDeclaredFields()) {
			if (field.getType() != String.class) {
				continue;
			}
			try {
				field.setAccessible(true);
				String value = (String) field.get(entity);
				if (value == null) {
					continue;
				}
				value = value.trim();
				if (value.isEmpty()) {
					field.set(entity, null);
					continue;
				}
				if (field.isAnnotationPresent(Email.class) && !EMAIL.matcher(value).matches()) {
					field.set(entity, null);
					continue;
				}
				int max = maxLength(field);
				field.set(entity, value.length() > max ? value.substring(0, max) : value);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Cannot sanitize field " + field.getName(), e);
			}
		}
	}

	private static int maxLength(Field field) {
		Size size = field.getAnnotation(Size.class);
		if (size != null) {
			return size.max();
		}
		Column column = field.getAnnotation(Column.class);
		return column != null ? column.length() : 255;
	}
}
