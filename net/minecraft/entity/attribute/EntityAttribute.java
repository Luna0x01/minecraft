package net.minecraft.entity.attribute;

public interface EntityAttribute {
	String getId();

	double clamp(double value);

	double getDefaultValue();

	boolean isTracked();

	EntityAttribute getParent();
}
