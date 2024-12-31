package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.CommonI18n;

public class ProjectileDamageSource extends EntityDamageSource {
	private final Entity attacker;

	public ProjectileDamageSource(String string, Entity entity, @Nullable Entity entity2) {
		super(string, entity);
		this.attacker = entity2;
	}

	@Nullable
	@Override
	public Entity getSource() {
		return this.source;
	}

	@Nullable
	@Override
	public Entity getAttacker() {
		return this.attacker;
	}

	@Override
	public Text getDeathMessage(LivingEntity entity) {
		Text text = this.attacker == null ? this.source.getName() : this.attacker.getName();
		ItemStack itemStack = this.attacker instanceof LivingEntity ? ((LivingEntity)this.attacker).getMainHandStack() : null;
		String string = "death.attack." + this.name;
		String string2 = string + ".item";
		return itemStack != null && itemStack.hasCustomName() && CommonI18n.hasTranslation(string2)
			? new TranslatableText(string2, entity.getName(), text, itemStack.toHoverableText())
			: new TranslatableText(string, entity.getName(), text);
	}
}
