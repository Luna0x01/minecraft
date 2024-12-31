package net.minecraft.entity.projectile;

import net.minecraft.class_4342;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class SpectralArrowEntity extends AbstractArrowEntity {
	private int duration = 200;

	public SpectralArrowEntity(World world) {
		super(EntityType.SPECTRAL_ARROW, world);
	}

	public SpectralArrowEntity(World world, LivingEntity livingEntity) {
		super(EntityType.SPECTRAL_ARROW, livingEntity, world);
	}

	public SpectralArrowEntity(World world, double d, double e, double f) {
		super(EntityType.SPECTRAL_ARROW, d, e, f, world);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && !this.inGround) {
			this.world.method_16343(class_4342.field_21352, this.x, this.y, this.z, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected ItemStack asItemStack() {
		return new ItemStack(Items.SPECTRAL_ARROW);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);
		StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.GLOWING, this.duration, 0);
		target.method_2654(statusEffectInstance);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Duration")) {
			this.duration = nbt.getInt("Duration");
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Duration", this.duration);
	}
}
