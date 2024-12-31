package net.minecraft.entity.attribute;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.UUID;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
	private final double amount;
	private final int operation;
	private final String name;
	private final UUID id;
	private boolean serialized = true;

	public AttributeModifier(String string, double d, int i) {
		this(MathHelper.randomUuid(ThreadLocalRandom.current()), string, d, i);
	}

	public AttributeModifier(UUID uUID, String string, double d, int i) {
		this.id = uUID;
		this.name = string;
		this.amount = d;
		this.operation = i;
		Validate.notEmpty(string, "Modifier name cannot be empty", new Object[0]);
		Validate.inclusiveBetween(0L, 2L, (long)i, "Invalid operation");
	}

	public UUID getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getOperation() {
		return this.operation;
	}

	public double getAmount() {
		return this.amount;
	}

	public boolean isSerialized() {
		return this.serialized;
	}

	public AttributeModifier setSerialized(boolean serialized) {
		this.serialized = serialized;
		return this;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			AttributeModifier attributeModifier = (AttributeModifier)object;
			return this.id != null ? this.id.equals(attributeModifier.id) : attributeModifier.id == null;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.id != null ? this.id.hashCode() : 0;
	}

	public String toString() {
		return "AttributeModifier{amount="
			+ this.amount
			+ ", operation="
			+ this.operation
			+ ", name='"
			+ this.name
			+ '\''
			+ ", id="
			+ this.id
			+ ", serialize="
			+ this.serialized
			+ '}';
	}
}
