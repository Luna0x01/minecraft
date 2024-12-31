package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Potion {
	private final String baseName;
	private final ImmutableList<StatusEffectInstance> effects;

	public static Potion byId(String string) {
		return Registry.field_11143.get(Identifier.tryParse(string));
	}

	public Potion(StatusEffectInstance... statusEffectInstances) {
		this(null, statusEffectInstances);
	}

	public Potion(@Nullable String string, StatusEffectInstance... statusEffectInstances) {
		this.baseName = string;
		this.effects = ImmutableList.copyOf(statusEffectInstances);
	}

	public String finishTranslationKey(String string) {
		return string + (this.baseName == null ? Registry.field_11143.getId(this).getPath() : this.baseName);
	}

	public List<StatusEffectInstance> getEffects() {
		return this.effects;
	}

	public boolean hasInstantEffect() {
		if (!this.effects.isEmpty()) {
			UnmodifiableIterator var1 = this.effects.iterator();

			while (var1.hasNext()) {
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var1.next();
				if (statusEffectInstance.getEffectType().isInstant()) {
					return true;
				}
			}
		}

		return false;
	}
}
