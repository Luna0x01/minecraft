package net.minecraft.entity.attribute;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

public interface EntityAttributeInstance {
	EntityAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double baseValue);

	Collection<AttributeModifier> getModifiers(int operation);

	Collection<AttributeModifier> getModifiers();

	boolean hasModifier(AttributeModifier modifier);

	@Nullable
	AttributeModifier getByUuid(UUID id);

	void addModifier(AttributeModifier modifier);

	void method_6193(AttributeModifier modifier);

	void method_13093(UUID uUID);

	void clearModifiers();

	double getValue();
}
