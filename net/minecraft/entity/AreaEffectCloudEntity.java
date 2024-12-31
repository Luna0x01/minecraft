package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class AreaEffectCloudEntity extends Entity {
	private static final TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> PARTICLE_ID = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_15026 = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_15027 = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private Potion potion = Potions.EMPTY;
	private final List<StatusEffectInstance> effects = Lists.newArrayList();
	private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();
	private int duration = 600;
	private int waitTime = 20;
	private int reapplicationDelay = 20;
	private boolean customColor;
	private int durationOnUse;
	private float radiusOnUse;
	private float radiusGrowth;
	private LivingEntity owner;
	private UUID ownerUuid;

	public AreaEffectCloudEntity(World world) {
		super(world);
		this.noClip = true;
		this.isFireImmune = true;
		this.setRadius(3.0F);
	}

	public AreaEffectCloudEntity(World world, double d, double e, double f) {
		this(world);
		this.updatePosition(d, e, f);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(COLOR, 0);
		this.getDataTracker().startTracking(RADIUS, 0.5F);
		this.getDataTracker().startTracking(WAITING, false);
		this.getDataTracker().startTracking(PARTICLE_ID, ParticleType.MOB_SPELL.getId());
		this.getDataTracker().startTracking(field_15026, 0);
		this.getDataTracker().startTracking(field_15027, 0);
	}

	public void setRadius(float radius) {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		this.setBounds(radius * 2.0F, 0.5F);
		this.updatePosition(d, e, f);
		if (!this.world.isClient) {
			this.getDataTracker().set(RADIUS, radius);
		}
	}

	public float getRadius() {
		return this.getDataTracker().get(RADIUS);
	}

	public void setPotion(Potion potion) {
		this.potion = potion;
		if (!this.customColor) {
			if (potion == Potions.EMPTY && this.effects.isEmpty()) {
				this.getDataTracker().set(COLOR, 0);
			} else {
				this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(potion, this.effects)));
			}
		}
	}

	public void addEffect(StatusEffectInstance effect) {
		this.effects.add(effect);
		if (!this.customColor) {
			this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
		}
	}

	public int getColor() {
		return this.getDataTracker().get(COLOR);
	}

	public void setColor(int rgb) {
		this.customColor = true;
		this.getDataTracker().set(COLOR, rgb);
	}

	public ParticleType getParticleType() {
		return ParticleType.getById(this.getDataTracker().get(PARTICLE_ID));
	}

	public void setParticleType(ParticleType particle) {
		this.getDataTracker().set(PARTICLE_ID, particle.getId());
	}

	public int method_13487() {
		return this.getDataTracker().get(field_15026);
	}

	public void method_13485(int i) {
		this.getDataTracker().set(field_15026, i);
	}

	public int method_13488() {
		return this.getDataTracker().get(field_15027);
	}

	public void method_13486(int i) {
		this.getDataTracker().set(field_15027, i);
	}

	protected void setWaiting(boolean waiting) {
		this.getDataTracker().set(WAITING, waiting);
	}

	public boolean isWaiting() {
		return this.getDataTracker().get(WAITING);
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public void tick() {
		super.tick();
		boolean bl = this.isWaiting();
		float f = this.getRadius();
		if (this.world.isClient) {
			ParticleType particleType = this.getParticleType();
			int[] is = new int[particleType.getArgs()];
			if (is.length > 0) {
				is[0] = this.method_13487();
			}

			if (is.length > 1) {
				is[1] = this.method_13488();
			}

			if (bl) {
				if (this.random.nextBoolean()) {
					for (int i = 0; i < 2; i++) {
						float g = this.random.nextFloat() * (float) (Math.PI * 2);
						float h = MathHelper.sqrt(this.random.nextFloat()) * 0.2F;
						float j = MathHelper.cos(g) * h;
						float k = MathHelper.sin(g) * h;
						if (particleType == ParticleType.MOB_SPELL) {
							int l = this.random.nextBoolean() ? 16777215 : this.getColor();
							int m = l >> 16 & 0xFF;
							int n = l >> 8 & 0xFF;
							int o = l & 0xFF;
							this.world
								.addParticle(
									ParticleType.MOB_SPELL,
									this.x + (double)j,
									this.y,
									this.z + (double)k,
									(double)((float)m / 255.0F),
									(double)((float)n / 255.0F),
									(double)((float)o / 255.0F)
								);
						} else {
							this.world.addParticle(particleType, this.x + (double)j, this.y, this.z + (double)k, 0.0, 0.0, 0.0, is);
						}
					}
				}
			} else {
				float p = (float) Math.PI * f * f;

				for (int q = 0; (float)q < p; q++) {
					float r = this.random.nextFloat() * (float) (Math.PI * 2);
					float s = MathHelper.sqrt(this.random.nextFloat()) * f;
					float t = MathHelper.cos(r) * s;
					float u = MathHelper.sin(r) * s;
					if (particleType == ParticleType.MOB_SPELL) {
						int v = this.getColor();
						int w = v >> 16 & 0xFF;
						int x = v >> 8 & 0xFF;
						int y = v & 0xFF;
						this.world
							.addParticle(
								ParticleType.MOB_SPELL,
								this.x + (double)t,
								this.y,
								this.z + (double)u,
								(double)((float)w / 255.0F),
								(double)((float)x / 255.0F),
								(double)((float)y / 255.0F)
							);
					} else {
						this.world
							.addParticle(
								particleType,
								this.x + (double)t,
								this.y,
								this.z + (double)u,
								(0.5 - this.random.nextDouble()) * 0.15,
								0.01F,
								(0.5 - this.random.nextDouble()) * 0.15,
								is
							);
					}
				}
			}
		} else {
			if (this.ticksAlive >= this.waitTime + this.duration) {
				this.remove();
				return;
			}

			boolean bl2 = this.ticksAlive < this.waitTime;
			if (bl != bl2) {
				this.setWaiting(bl2);
			}

			if (bl2) {
				return;
			}

			if (this.radiusGrowth != 0.0F) {
				f += this.radiusGrowth;
				if (f < 0.5F) {
					this.remove();
					return;
				}

				this.setRadius(f);
			}

			if (this.ticksAlive % 5 == 0) {
				Iterator<Entry<Entity, Integer>> iterator = this.affectedEntities.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<Entity, Integer> entry = (Entry<Entity, Integer>)iterator.next();
					if (this.ticksAlive >= (Integer)entry.getValue()) {
						iterator.remove();
					}
				}

				List<StatusEffectInstance> list = Lists.newArrayList();

				for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
					list.add(
						new StatusEffectInstance(
							statusEffectInstance.getStatusEffect(),
							statusEffectInstance.getDuration() / 4,
							statusEffectInstance.getAmplifier(),
							statusEffectInstance.isAmbient(),
							statusEffectInstance.shouldShowParticles()
						)
					);
				}

				list.addAll(this.effects);
				if (list.isEmpty()) {
					this.affectedEntities.clear();
				} else {
					List<LivingEntity> list2 = this.world.getEntitiesInBox(LivingEntity.class, this.getBoundingBox());
					if (!list2.isEmpty()) {
						for (LivingEntity livingEntity : list2) {
							if (!this.affectedEntities.containsKey(livingEntity) && livingEntity.method_13057()) {
								double d = livingEntity.x - this.x;
								double e = livingEntity.z - this.z;
								double z = d * d + e * e;
								if (z <= (double)(f * f)) {
									this.affectedEntities.put(livingEntity, this.ticksAlive + this.reapplicationDelay);

									for (StatusEffectInstance statusEffectInstance2 : list) {
										if (statusEffectInstance2.getStatusEffect().isInstant()) {
											statusEffectInstance2.getStatusEffect().method_6088(this, this.method_12965(), livingEntity, statusEffectInstance2.getAmplifier(), 0.5);
										} else {
											livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance2));
										}
									}

									if (this.radiusOnUse != 0.0F) {
										f += this.radiusOnUse;
										if (f < 0.5F) {
											this.remove();
											return;
										}

										this.setRadius(f);
									}

									if (this.durationOnUse != 0) {
										this.duration = this.duration + this.durationOnUse;
										if (this.duration <= 0) {
											this.remove();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void method_12956(float f) {
		this.radiusOnUse = f;
	}

	public void method_12958(float f) {
		this.radiusGrowth = f;
	}

	public void method_12959(int i) {
		this.waitTime = i;
	}

	public void method_12954(@Nullable LivingEntity livingEntity) {
		this.owner = livingEntity;
		this.ownerUuid = livingEntity == null ? null : livingEntity.getUuid();
	}

	@Nullable
	public LivingEntity method_12965() {
		if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.world).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			}
		}

		return this.owner;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.ticksAlive = nbt.getInt("Age");
		this.duration = nbt.getInt("Duration");
		this.waitTime = nbt.getInt("WaitTime");
		this.reapplicationDelay = nbt.getInt("ReapplicationDelay");
		this.durationOnUse = nbt.getInt("DurationOnUse");
		this.radiusOnUse = nbt.getFloat("RadiusOnUse");
		this.radiusGrowth = nbt.getFloat("RadiusPerTick");
		this.setRadius(nbt.getFloat("Radius"));
		this.ownerUuid = nbt.getUuid("OwnerUUID");
		if (nbt.contains("Particle", 8)) {
			ParticleType particleType = ParticleType.method_12582(nbt.getString("Particle"));
			if (particleType != null) {
				this.setParticleType(particleType);
				this.method_13485(nbt.getInt("ParticleParam1"));
				this.method_13486(nbt.getInt("ParticleParam2"));
			}
		}

		if (nbt.contains("Color", 99)) {
			this.setColor(nbt.getInt("Color"));
		}

		if (nbt.contains("Potion", 8)) {
			this.setPotion(PotionUtil.getPotion(nbt));
		}

		if (nbt.contains("Effects", 9)) {
			NbtList nbtList = nbt.getList("Effects", 10);
			this.effects.clear();

			for (int i = 0; i < nbtList.size(); i++) {
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtList.getCompound(i));
				if (statusEffectInstance != null) {
					this.addEffect(statusEffectInstance);
				}
			}
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("Age", this.ticksAlive);
		nbt.putInt("Duration", this.duration);
		nbt.putInt("WaitTime", this.waitTime);
		nbt.putInt("ReapplicationDelay", this.reapplicationDelay);
		nbt.putInt("DurationOnUse", this.durationOnUse);
		nbt.putFloat("RadiusOnUse", this.radiusOnUse);
		nbt.putFloat("RadiusPerTick", this.radiusGrowth);
		nbt.putFloat("Radius", this.getRadius());
		nbt.putString("Particle", this.getParticleType().getName());
		nbt.putInt("ParticleParam1", this.method_13487());
		nbt.putInt("ParticleParam2", this.method_13488());
		if (this.ownerUuid != null) {
			nbt.putUuid("OwnerUUID", this.ownerUuid);
		}

		if (this.customColor) {
			nbt.putInt("Color", this.getColor());
		}

		if (this.potion != Potions.EMPTY && this.potion != null) {
			nbt.putString("Potion", Potion.REGISTRY.getIdentifier(this.potion).toString());
		}

		if (!this.effects.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (StatusEffectInstance statusEffectInstance : this.effects) {
				nbtList.add(statusEffectInstance.toNbt(new NbtCompound()));
			}

			nbt.put("Effects", nbtList);
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (RADIUS.equals(data)) {
			this.setRadius(this.getRadius());
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}
}
