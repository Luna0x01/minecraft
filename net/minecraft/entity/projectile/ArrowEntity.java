package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ArrowEntity extends ProjectileEntity {
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private Potion potion = Potions.field_8984;
	private final Set<StatusEffectInstance> effects = Sets.newHashSet();
	private boolean colorSet;

	public ArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	public ArrowEntity(World world, double d, double e, double f) {
		super(EntityType.field_6122, d, e, f, world);
	}

	public ArrowEntity(World world, LivingEntity livingEntity) {
		super(EntityType.field_6122, livingEntity, world);
	}

	public void initFromStack(ItemStack itemStack) {
		if (itemStack.getItem() == Items.field_8087) {
			this.potion = PotionUtil.getPotion(itemStack);
			Collection<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(itemStack);
			if (!collection.isEmpty()) {
				for (StatusEffectInstance statusEffectInstance : collection) {
					this.effects.add(new StatusEffectInstance(statusEffectInstance));
				}
			}

			int i = getCustomPotionColor(itemStack);
			if (i == -1) {
				this.initColor();
			} else {
				this.setColor(i);
			}
		} else if (itemStack.getItem() == Items.field_8107) {
			this.potion = Potions.field_8984;
			this.effects.clear();
			this.dataTracker.set(COLOR, -1);
		}
	}

	public static int getCustomPotionColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null && compoundTag.containsKey("CustomPotionColor", 99) ? compoundTag.getInt("CustomPotionColor") : -1;
	}

	private void initColor() {
		this.colorSet = false;
		this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
	}

	public void addEffect(StatusEffectInstance statusEffectInstance) {
		this.effects.add(statusEffectInstance);
		this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(COLOR, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			if (this.inGround) {
				if (this.inGroundTime % 5 == 0) {
					this.spawnParticles(1);
				}
			} else {
				this.spawnParticles(2);
			}
		} else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
			this.world.sendEntityStatus(this, (byte)0);
			this.potion = Potions.field_8984;
			this.effects.clear();
			this.dataTracker.set(COLOR, -1);
		}
	}

	private void spawnParticles(int i) {
		int j = this.getColor();
		if (j != -1 && i > 0) {
			double d = (double)(j >> 16 & 0xFF) / 255.0;
			double e = (double)(j >> 8 & 0xFF) / 255.0;
			double f = (double)(j >> 0 & 0xFF) / 255.0;

			for (int k = 0; k < i; k++) {
				this.world
					.addParticle(
						ParticleTypes.field_11226,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						this.y + this.random.nextDouble() * (double)this.getHeight(),
						this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						d,
						e,
						f
					);
			}
		}
	}

	public int getColor() {
		return this.dataTracker.get(COLOR);
	}

	private void setColor(int i) {
		this.colorSet = true;
		this.dataTracker.set(COLOR, i);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.potion != Potions.field_8984 && this.potion != null) {
			compoundTag.putString("Potion", Registry.POTION.getId(this.potion).toString());
		}

		if (this.colorSet) {
			compoundTag.putInt("Color", this.getColor());
		}

		if (!this.effects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.effects) {
				listTag.add(statusEffectInstance.serialize(new CompoundTag()));
			}

			compoundTag.put("CustomPotionEffects", listTag);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Potion", 8)) {
			this.potion = PotionUtil.getPotion(compoundTag);
		}

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(compoundTag)) {
			this.addEffect(statusEffectInstance);
		}

		if (compoundTag.containsKey("Color", 99)) {
			this.setColor(compoundTag.getInt("Color"));
		} else {
			this.initColor();
		}
	}

	@Override
	protected void onHit(LivingEntity livingEntity) {
		super.onHit(livingEntity);

		for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
			livingEntity.addPotionEffect(
				new StatusEffectInstance(
					statusEffectInstance.getEffectType(),
					Math.max(statusEffectInstance.getDuration() / 8, 1),
					statusEffectInstance.getAmplifier(),
					statusEffectInstance.isAmbient(),
					statusEffectInstance.shouldShowParticles()
				)
			);
		}

		if (!this.effects.isEmpty()) {
			for (StatusEffectInstance statusEffectInstance2 : this.effects) {
				livingEntity.addPotionEffect(statusEffectInstance2);
			}
		}
	}

	@Override
	protected ItemStack asItemStack() {
		if (this.effects.isEmpty() && this.potion == Potions.field_8984) {
			return new ItemStack(Items.field_8107);
		} else {
			ItemStack itemStack = new ItemStack(Items.field_8087);
			PotionUtil.setPotion(itemStack, this.potion);
			PotionUtil.setCustomPotionEffects(itemStack, this.effects);
			if (this.colorSet) {
				itemStack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
			}

			return itemStack;
		}
	}

	@Override
	public void handleStatus(byte b) {
		if (b == 0) {
			int i = this.getColor();
			if (i != -1) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;

				for (int j = 0; j < 20; j++) {
					this.world
						.addParticle(
							ParticleTypes.field_11226,
							this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
							this.y + this.random.nextDouble() * (double)this.getHeight(),
							this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
							d,
							e,
							f
						);
				}
			}
		} else {
			super.handleStatus(b);
		}
	}
}
