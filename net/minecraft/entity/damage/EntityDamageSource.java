package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.Vec3d;

public class EntityDamageSource extends DamageSource {
	@Nullable
	protected Entity source;
	private boolean thorns;

	public EntityDamageSource(String string, @Nullable Entity entity) {
		super(string);
		this.source = entity;
	}

	public EntityDamageSource setThorns() {
		this.thorns = true;
		return this;
	}

	public boolean isThorns() {
		return this.thorns;
	}

	@Nullable
	@Override
	public Entity getAttacker() {
		return this.source;
	}

	@Override
	public Text getDeathMessage(LivingEntity entity) {
		ItemStack itemStack = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
		String string = "death.attack." + this.name;
		String string2 = string + ".item";
		return !itemStack.isEmpty() && itemStack.hasCustomName() && CommonI18n.hasTranslation(string2)
			? new TranslatableText(string2, entity.getName(), this.source.getName(), itemStack.toHoverableText())
			: new TranslatableText(string, entity.getName(), this.source.getName());
	}

	@Override
	public boolean isScaledWithDifficulty() {
		return this.source != null && this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
	}

	@Nullable
	@Override
	public Vec3d getPosition() {
		return new Vec3d(this.source.x, this.source.y, this.source.z);
	}
}
