package net.minecraft.entity.attribute;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EntityAttributeInstanceImpl implements EntityAttributeInstance {
	private final AbstractEntityAttributeContainer field_6822;
	private final EntityAttribute field_6823;
	private final Map<Integer, Set<AttributeModifier>> field_6824 = Maps.newHashMap();
	private final Map<String, Set<AttributeModifier>> modifiersByName = Maps.newHashMap();
	private final Map<UUID, AttributeModifier> modifiersByUuid = Maps.newHashMap();
	private double baseValue;
	private boolean needsRefresh = true;
	private double cachedValue;

	public EntityAttributeInstanceImpl(AbstractEntityAttributeContainer abstractEntityAttributeContainer, EntityAttribute entityAttribute) {
		this.field_6822 = abstractEntityAttributeContainer;
		this.field_6823 = entityAttribute;
		this.baseValue = entityAttribute.getDefaultValue();

		for (int i = 0; i < 3; i++) {
			this.field_6824.put(i, Sets.newHashSet());
		}
	}

	@Override
	public EntityAttribute getAttribute() {
		return this.field_6823;
	}

	@Override
	public double getBaseValue() {
		return this.baseValue;
	}

	@Override
	public void setBaseValue(double baseValue) {
		if (baseValue != this.getBaseValue()) {
			this.baseValue = baseValue;
			this.invalidateCache();
		}
	}

	@Override
	public Collection<AttributeModifier> getModifiers(int operation) {
		return (Collection<AttributeModifier>)this.field_6824.get(operation);
	}

	@Override
	public Collection<AttributeModifier> getModifiers() {
		Set<AttributeModifier> set = Sets.newHashSet();

		for (int i = 0; i < 3; i++) {
			set.addAll(this.getModifiers(i));
		}

		return set;
	}

	@Override
	public AttributeModifier getByUuid(UUID id) {
		return (AttributeModifier)this.modifiersByUuid.get(id);
	}

	@Override
	public boolean hasModifier(AttributeModifier modifier) {
		return this.modifiersByUuid.get(modifier.getId()) != null;
	}

	@Override
	public void addModifier(AttributeModifier modifier) {
		if (this.getByUuid(modifier.getId()) != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			Set<AttributeModifier> set = (Set<AttributeModifier>)this.modifiersByName.get(modifier.getName());
			if (set == null) {
				set = Sets.newHashSet();
				this.modifiersByName.put(modifier.getName(), set);
			}

			((Set)this.field_6824.get(modifier.getOperation())).add(modifier);
			set.add(modifier);
			this.modifiersByUuid.put(modifier.getId(), modifier);
			this.invalidateCache();
		}
	}

	protected void invalidateCache() {
		this.needsRefresh = true;
		this.field_6822.add(this);
	}

	@Override
	public void method_6193(AttributeModifier modifier) {
		for (int i = 0; i < 3; i++) {
			Set<AttributeModifier> set = (Set<AttributeModifier>)this.field_6824.get(i);
			set.remove(modifier);
		}

		Set<AttributeModifier> set2 = (Set<AttributeModifier>)this.modifiersByName.get(modifier.getName());
		if (set2 != null) {
			set2.remove(modifier);
			if (set2.isEmpty()) {
				this.modifiersByName.remove(modifier.getName());
			}
		}

		this.modifiersByUuid.remove(modifier.getId());
		this.invalidateCache();
	}

	@Override
	public void clearModifiers() {
		Collection<AttributeModifier> collection = this.getModifiers();
		if (collection != null) {
			for (AttributeModifier attributeModifier : Lists.newArrayList(collection)) {
				this.method_6193(attributeModifier);
			}
		}
	}

	@Override
	public double getValue() {
		if (this.needsRefresh) {
			this.cachedValue = this.computeValue();
			this.needsRefresh = false;
		}

		return this.cachedValue;
	}

	private double computeValue() {
		double d = this.getBaseValue();

		for (AttributeModifier attributeModifier : this.method_10998(0)) {
			d += attributeModifier.getAmount();
		}

		double e = d;

		for (AttributeModifier attributeModifier2 : this.method_10998(1)) {
			e += d * attributeModifier2.getAmount();
		}

		for (AttributeModifier attributeModifier3 : this.method_10998(2)) {
			e *= 1.0 + attributeModifier3.getAmount();
		}

		return this.field_6823.clamp(e);
	}

	private Collection<AttributeModifier> method_10998(int i) {
		Set<AttributeModifier> set = Sets.newHashSet(this.getModifiers(i));

		for (EntityAttribute entityAttribute = this.field_6823.getParent(); entityAttribute != null; entityAttribute = entityAttribute.getParent()) {
			EntityAttributeInstance entityAttributeInstance = this.field_6822.get(entityAttribute);
			if (entityAttributeInstance != null) {
				set.addAll(entityAttributeInstance.getModifiers(i));
			}
		}

		return set;
	}
}
