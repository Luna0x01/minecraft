package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;

public class ArrowEntity extends AbstractArrowEntity {
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private Potion potion = Potions.EMPTY;
	private final Set<StatusEffectInstance> effects = Sets.newHashSet();

	public ArrowEntity(World world) {
		super(world);
	}

	public ArrowEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	public ArrowEntity(World world, LivingEntity livingEntity) {
		super(world, livingEntity);
	}

	public void initFromStack(ItemStack stack) {
		if (stack.getItem() == Items.TIPPED_ARROW) {
			this.potion = PotionUtil.getPotion(stack.getNbt());
			Collection<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(stack);
			if (!collection.isEmpty()) {
				for (StatusEffectInstance statusEffectInstance : collection) {
					this.effects.add(new StatusEffectInstance(statusEffectInstance));
				}
			}

			this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, collection)));
		} else if (stack.getItem() == Items.ARROW) {
			this.potion = Potions.EMPTY;
			this.effects.clear();
			this.dataTracker.set(COLOR, 0);
		}
	}

	public void addEffect(StatusEffectInstance effect) {
		this.effects.add(effect);
		this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(COLOR, 0);
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
			this.potion = Potions.EMPTY;
			this.effects.clear();
			this.dataTracker.set(COLOR, 0);
		}
	}

	private void spawnParticles(int amount) {
		int i = this.getColor();
		if (i != 0 && amount > 0) {
			double d = (double)(i >> 16 & 0xFF) / 255.0;
			double e = (double)(i >> 8 & 0xFF) / 255.0;
			double f = (double)(i >> 0 & 0xFF) / 255.0;

			for (int j = 0; j < amount; j++) {
				this.world
					.addParticle(
						ParticleType.MOB_SPELL,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
						this.y + this.random.nextDouble() * (double)this.height,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
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

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.potion != Potions.EMPTY && this.potion != null) {
			nbt.putString("Potion", Potion.REGISTRY.getIdentifier(this.potion).toString());
		}

		if (!this.effects.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (StatusEffectInstance statusEffectInstance : this.effects) {
				nbtList.add(statusEffectInstance.toNbt(new NbtCompound()));
			}

			nbt.put("CustomPotionEffects", nbtList);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Potion", 8)) {
			this.potion = PotionUtil.getPotion(nbt);
		}

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(nbt)) {
			this.addEffect(statusEffectInstance);
		}

		if (this.potion != Potions.EMPTY || !this.effects.isEmpty()) {
			this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
		}
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);

		for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
			target.addStatusEffect(
				new StatusEffectInstance(
					statusEffectInstance.getStatusEffect(),
					statusEffectInstance.getDuration() / 8,
					statusEffectInstance.getAmplifier(),
					statusEffectInstance.isAmbient(),
					statusEffectInstance.shouldShowParticles()
				)
			);
		}

		if (!this.effects.isEmpty()) {
			for (StatusEffectInstance statusEffectInstance2 : this.effects) {
				target.addStatusEffect(statusEffectInstance2);
			}
		}
	}

	@Override
	protected ItemStack asItemStack() {
		if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
			return new ItemStack(Items.ARROW);
		} else {
			ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
			PotionUtil.setPotion(itemStack, this.potion);
			PotionUtil.setCustomPotionEffects(itemStack, this.effects);
			return itemStack;
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 0) {
			int i = this.getColor();
			if (i > 0) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;

				for (int j = 0; j < 20; j++) {
					this.world
						.addParticle(
							ParticleType.MOB_SPELL,
							this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
							this.y + this.random.nextDouble() * (double)this.height,
							this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
							d,
							e,
							f
						);
				}
			}
		} else {
			super.handleStatus(status);
		}
	}
}
