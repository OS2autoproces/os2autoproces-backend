package dk.digitalidentity.ap.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectionAnalyzer {

	public static boolean verifyProjectionExtendsEntity(Class<?> entityClazz, Class<?> projectionClazz) {
		try {
			for (PropertyDescriptor entityDescriptor : Introspector.getBeanInfo(entityClazz, Object.class).getPropertyDescriptors()) {
				Method entityMethod = entityDescriptor.getReadMethod();

				if (entityMethod != null && AnnotationUtils.findAnnotation(entityMethod, JsonIgnore.class) == null) {
					PropertyDescriptor projectionDescriptor = new PropertyDescriptor(entityDescriptor.getName(), projectionClazz, entityMethod.getName(), null);

					// throws exception if method is missing
					projectionDescriptor.getReadMethod();
				}
			}
		}
		catch (Exception ex) {
			log.error("Projection does NOT extend Entity", ex);
			return false;
		}

		return true;
	}
	
	// only used for manual testing to verify if a projection has dead fields
	public static boolean verifyEntityEqualsExtension(Class<?> entityClazz, Class<?> projectionClazz) {
		try {
			for (PropertyDescriptor projectionDescriptor : Introspector.getBeanInfo(projectionClazz).getPropertyDescriptors()) {
				Method projectionMethod = projectionDescriptor.getReadMethod();

				if (projectionMethod != null && AnnotationUtils.findAnnotation(projectionMethod, JsonIgnore.class) == null) {
					PropertyDescriptor entityDescriptor = new PropertyDescriptor(entityClazz.getName(), entityClazz, projectionMethod.getName(), null);

					// throws exception if method is missing
					entityDescriptor.getReadMethod();
				}
			}
		}
		catch (Exception ex) {
			log.error("Projection does NOT extend Entity", ex);
			return false;
		}

		return true;
	}
}
