package net.minecraft.entity.projectile;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
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
		super(world);
	}

	public SpectralArrowEntity(World world, LivingEntity livingEntity) {
		super(world, livingEntity);
	}

	public SpectralArrowEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && !this.inGround) {
			this.world.addParticle(ParticleType.INSTANT_SPELL, this.x, this.y, this.z, 0.0, 0.0, 0.0);
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
		target.addStatusEffect(statusEffectInstance);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		AbstractArrowEntity.registerDataFixes(dataFixer, "SpectralArrow");
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
