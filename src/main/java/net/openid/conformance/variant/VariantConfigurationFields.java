package net.openid.conformance.variant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(VariantConfigurationFieldsContainer.class)
public @interface VariantConfigurationFields {

	Class<? extends Enum<?>> parameter();
	String value();
	String[] configurationFields();

}
