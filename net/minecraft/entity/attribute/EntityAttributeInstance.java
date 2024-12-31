package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public interface EntityAttributeInstance {
	EntityAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double d);

	Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation);

	Set<EntityAttributeModifier> getModifiers();

	boolean hasModifier(EntityAttributeModifier entityAttributeModifier);

	@Nullable
	EntityAttributeModifier getModifier(UUID uUID);

	void addModifier(EntityAttributeModifier entityAttributeModifier);

	void removeModifier(EntityAttributeModifier entityAttributeModifier);

	void removeModifier(UUID uUID);

	void clearModifiers();

	double getValue();

	default void copyFrom(EntityAttributeInstance entityAttributeInstance) {
		this.setBaseValue(entityAttributeInstance.getBaseValue());
		Set<EntityAttributeModifier> set = entityAttributeInstance.getModifiers();
		Set<EntityAttributeModifier> set2 = this.getModifiers();
		ImmutableSet<EntityAttributeModifier> immutableSet = ImmutableSet.copyOf(Sets.difference(set, set2));
		ImmutableSet<EntityAttributeModifier> immutableSet2 = ImmutableSet.copyOf(Sets.difference(set2, set));
		immutableSet.forEach(this::addModifier);
		immutableSet2.forEach(this::removeModifier);
	}
}
