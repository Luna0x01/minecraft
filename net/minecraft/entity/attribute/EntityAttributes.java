package net.minecraft.entity.attribute;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAttributes {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final EntityAttribute GENERIC_MAX_HEALTH = new ClampedEntityAttribute(null, "generic.maxHealth", 20.0, 0.0, 1024.0)
		.setName("Max Health")
		.setTracked(true);
	public static final EntityAttribute GENERIC_FOLLOW_RANGE = new ClampedEntityAttribute(null, "generic.followRange", 32.0, 0.0, 2048.0).setName("Follow Range");
	public static final EntityAttribute GENERIC_KNOCKBACK_RESISTANCE = new ClampedEntityAttribute(null, "generic.knockbackResistance", 0.0, 0.0, 1.0)
		.setName("Knockback Resistance");
	public static final EntityAttribute GENERIC_MOVEMENT_SPEED = new ClampedEntityAttribute(null, "generic.movementSpeed", 0.7F, 0.0, 1024.0)
		.setName("Movement Speed")
		.setTracked(true);
	public static final EntityAttribute GENERIC_ATTACK_DAMAGE = new ClampedEntityAttribute(null, "generic.attackDamage", 2.0, 0.0, 2048.0);
	public static final EntityAttribute GENERIC_ATTACK_SPEED = new ClampedEntityAttribute(null, "generic.attackSpeed", 4.0, 0.0, 1024.0).setTracked(true);
	public static final EntityAttribute GENERIC_ARMOR = new ClampedEntityAttribute(null, "generic.armor", 0.0, 0.0, 30.0).setTracked(true);
	public static final EntityAttribute GENERIC_ARMOR_TOUGHNESS = new ClampedEntityAttribute(null, "generic.armorToughness", 0.0, 0.0, 20.0).setTracked(true);
	public static final EntityAttribute GENERIC_LUCK = new ClampedEntityAttribute(null, "generic.luck", 0.0, -1024.0, 1024.0).setTracked(true);

	public static NbtList toNbt(AbstractEntityAttributeContainer container) {
		NbtList nbtList = new NbtList();

		for (EntityAttributeInstance entityAttributeInstance : container.values()) {
			nbtList.add(toNbt(entityAttributeInstance));
		}

		return nbtList;
	}

	private static NbtCompound toNbt(EntityAttributeInstance instance) {
		NbtCompound nbtCompound = new NbtCompound();
		EntityAttribute entityAttribute = instance.getAttribute();
		nbtCompound.putString("Name", entityAttribute.getId());
		nbtCompound.putDouble("Base", instance.getBaseValue());
		Collection<AttributeModifier> collection = instance.getModifiers();
		if (collection != null && !collection.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (AttributeModifier attributeModifier : collection) {
				if (attributeModifier.isSerialized()) {
					nbtList.add(toNbt(attributeModifier));
				}
			}

			nbtCompound.put("Modifiers", nbtList);
		}

		return nbtCompound;
	}

	public static NbtCompound toNbt(AttributeModifier modifier) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", modifier.getName());
		nbtCompound.putDouble("Amount", modifier.getAmount());
		nbtCompound.putInt("Operation", modifier.getOperation());
		nbtCompound.putUuid("UUID", modifier.getId());
		return nbtCompound;
	}

	public static void fromNbt(AbstractEntityAttributeContainer container, NbtList nbt) {
		for (int i = 0; i < nbt.size(); i++) {
			NbtCompound nbtCompound = nbt.getCompound(i);
			EntityAttributeInstance entityAttributeInstance = container.get(nbtCompound.getString("Name"));
			if (entityAttributeInstance != null) {
				fromNbt(entityAttributeInstance, nbtCompound);
			} else {
				LOGGER.warn("Ignoring unknown attribute '{}'", new Object[]{nbtCompound.getString("Name")});
			}
		}
	}

	private static void fromNbt(EntityAttributeInstance instance, NbtCompound nbt) {
		instance.setBaseValue(nbt.getDouble("Base"));
		if (nbt.contains("Modifiers", 9)) {
			NbtList nbtList = nbt.getList("Modifiers", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				AttributeModifier attributeModifier = fromNbt(nbtList.getCompound(i));
				if (attributeModifier != null) {
					AttributeModifier attributeModifier2 = instance.getByUuid(attributeModifier.getId());
					if (attributeModifier2 != null) {
						instance.method_6193(attributeModifier2);
					}

					instance.addModifier(attributeModifier);
				}
			}
		}
	}

	@Nullable
	public static AttributeModifier fromNbt(NbtCompound nbt) {
		UUID uUID = nbt.getUuid("UUID");

		try {
			return new AttributeModifier(uUID, nbt.getString("Name"), nbt.getDouble("Amount"), nbt.getInt("Operation"));
		} catch (Exception var3) {
			LOGGER.warn("Unable to create attribute: {}", new Object[]{var3.getMessage()});
			return null;
		}
	}
}
