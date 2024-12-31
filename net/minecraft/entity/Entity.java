package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements CommandSource {
	private static final Logger LOGGER_ = LogManager.getLogger();
	private static final Box HITBOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static double renderDistanceMultiplier = 1.0;
	private static int entityCount;
	private int entityId;
	public boolean inanimate;
	private final List<Entity> passengerList;
	protected int ridingCooldown;
	private Entity mount;
	public boolean teleporting;
	public World world;
	public double prevX;
	public double prevY;
	public double prevZ;
	public double x;
	public double y;
	public double z;
	public double velocityX;
	public double velocityY;
	public double velocityZ;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	private Box boundingBox;
	public boolean onGround;
	public boolean horizontalCollision;
	public boolean verticalCollision;
	public boolean colliding;
	public boolean velocityModified;
	protected boolean inLava;
	private boolean outsideWorldBorder;
	public boolean removed;
	public float width;
	public float height;
	public float prevHorizontalSpeed;
	public float horizontalSpeed;
	public float distanceTraveled;
	public float fallDistance;
	private int field_3233;
	public double prevTickX;
	public double prevTickY;
	public double prevTickZ;
	public float stepHeight;
	public boolean noClip;
	public float pushSpeedReduction;
	protected Random random;
	public int ticksAlive;
	public int fireResistance;
	private int fireTicks;
	protected boolean touchingWater;
	public int timeUntilRegen;
	protected boolean firstUpdate;
	protected boolean isFireImmune;
	protected DataTracker dataTracker;
	protected static final TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<String> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public boolean updateNeeded;
	public int chunkX;
	public int chunkY;
	public int chunkZ;
	public long tracedX;
	public long tracedY;
	public long tracedZ;
	public boolean ignoreCameraFrustum;
	public boolean velocityDirty;
	public int netherPortalCooldown;
	protected boolean changingDimension;
	protected int netherPortalTime;
	public int dimension;
	protected BlockPos lastPortalBlockPos;
	protected Vec3d lastPortalVec3d;
	protected Direction teleportDirection;
	private boolean invulnerable;
	protected UUID playerUuid;
	protected String uuidString;
	private final CommandStats commandStats;
	private final List<ItemStack> field_14496;
	protected boolean isGlowing;
	private final Set<String> scoreboardTags;
	private boolean teleportRequested;

	public Entity(World world) {
		this.entityId = entityCount++;
		this.passengerList = Lists.newArrayList();
		this.boundingBox = HITBOX;
		this.width = 0.6F;
		this.height = 1.8F;
		this.field_3233 = 1;
		this.random = new Random();
		this.fireResistance = 1;
		this.firstUpdate = true;
		this.playerUuid = MathHelper.randomUuid(this.random);
		this.uuidString = this.playerUuid.toString();
		this.commandStats = new CommandStats();
		this.field_14496 = Lists.newArrayList();
		this.scoreboardTags = Sets.newHashSet();
		this.world = world;
		this.updatePosition(0.0, 0.0, 0.0);
		if (world != null) {
			this.dimension = world.dimension.getDimensionType().getId();
		}

		this.dataTracker = new DataTracker(this);
		this.dataTracker.startTracking(FLAGS, (byte)0);
		this.dataTracker.startTracking(AIR, 300);
		this.dataTracker.startTracking(NAME_VISIBLE, false);
		this.dataTracker.startTracking(CUSTOM_NAME, "");
		this.dataTracker.startTracking(SILENT, false);
		this.dataTracker.startTracking(NO_GRAVITY, false);
		this.initDataTracker();
	}

	public int getEntityId() {
		return this.entityId;
	}

	public void setEntityId(int id) {
		this.entityId = id;
	}

	public Set<String> getScoreboardTags() {
		return this.scoreboardTags;
	}

	public boolean addScoreboardTag(String tag) {
		if (this.scoreboardTags.size() >= 1024) {
			return false;
		} else {
			this.scoreboardTags.add(tag);
			return true;
		}
	}

	public boolean removeScoreboardTag(String tag) {
		return this.scoreboardTags.remove(tag);
	}

	public void kill() {
		this.remove();
	}

	protected abstract void initDataTracker();

	public DataTracker getDataTracker() {
		return this.dataTracker;
	}

	public boolean equals(Object object) {
		return object instanceof Entity ? ((Entity)object).entityId == this.entityId : false;
	}

	public int hashCode() {
		return this.entityId;
	}

	protected void afterSpawn() {
		if (this.world != null) {
			while (this.y > 0.0 && this.y < 256.0) {
				this.updatePosition(this.x, this.y, this.z);
				if (this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()) {
					break;
				}

				this.y++;
			}

			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
			this.pitch = 0.0F;
		}
	}

	public void remove() {
		this.removed = true;
	}

	public void method_12991(boolean bl) {
	}

	protected void setBounds(float width, float height) {
		if (width != this.width || height != this.height) {
			float f = this.width;
			this.width = width;
			this.height = height;
			Box box = this.getBoundingBox();
			this.setBoundingBox(new Box(box.minX, box.minY, box.minZ, box.minX + (double)this.width, box.minY + (double)this.height, box.minZ + (double)this.width));
			if (this.width > f && !this.firstUpdate && !this.world.isClient) {
				this.move((double)(f - this.width), 0.0, (double)(f - this.width));
			}
		}
	}

	protected void setRotation(float yaw, float pitch) {
		this.yaw = yaw % 360.0F;
		this.pitch = pitch % 360.0F;
	}

	public void updatePosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float f = this.width / 2.0F;
		float g = this.height;
		this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
	}

	public void increaseTransforms(float yaw, float pitch) {
		float f = this.pitch;
		float g = this.yaw;
		this.yaw = (float)((double)this.yaw + (double)yaw * 0.15);
		this.pitch = (float)((double)this.pitch - (double)pitch * 0.15);
		this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
		this.prevPitch = this.prevPitch + (this.pitch - f);
		this.prevYaw = this.prevYaw + (this.yaw - g);
		if (this.mount != null) {
			this.mount.onPassengerLookAround(this);
		}
	}

	public void tick() {
		if (!this.world.isClient) {
			this.setFlag(6, this.isGlowing());
		}

		this.baseTick();
	}

	public void baseTick() {
		this.world.profiler.push("entityBaseTick");
		if (this.hasMount() && this.getVehicle().removed) {
			this.stopRiding();
		}

		if (this.ridingCooldown > 0) {
			this.ridingCooldown--;
		}

		this.prevHorizontalSpeed = this.horizontalSpeed;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		if (!this.world.isClient && this.world instanceof ServerWorld) {
			this.world.profiler.push("portal");
			if (this.changingDimension) {
				MinecraftServer minecraftServer = this.world.getServer();
				if (minecraftServer.isNetherAllowed()) {
					if (!this.hasMount()) {
						int i = this.getMaxNetherPortalTime();
						if (this.netherPortalTime++ >= i) {
							this.netherPortalTime = i;
							this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
							int j;
							if (this.world.dimension.getDimensionType().getId() == -1) {
								j = 0;
							} else {
								j = -1;
							}

							this.changeDimension(j);
						}
					}

					this.changingDimension = false;
				}
			} else {
				if (this.netherPortalTime > 0) {
					this.netherPortalTime -= 4;
				}

				if (this.netherPortalTime < 0) {
					this.netherPortalTime = 0;
				}
			}

			this.tickNetherPortalCooldown();
			this.world.profiler.pop();
		}

		this.attemptSprintingParticles();
		this.updateWaterState();
		if (this.world.isClient) {
			this.fireTicks = 0;
		} else if (this.fireTicks > 0) {
			if (this.isFireImmune) {
				this.fireTicks -= 4;
				if (this.fireTicks < 0) {
					this.fireTicks = 0;
				}
			} else {
				if (this.fireTicks % 20 == 0) {
					this.damage(DamageSource.ON_FIRE, 1.0F);
				}

				this.fireTicks--;
			}
		}

		if (this.isTouchingLava()) {
			this.setOnFireFromLava();
			this.fallDistance *= 0.5F;
		}

		if (this.y < -64.0) {
			this.destroy();
		}

		if (!this.world.isClient) {
			this.setFlag(0, this.fireTicks > 0);
		}

		this.firstUpdate = false;
		this.world.profiler.pop();
	}

	protected void tickNetherPortalCooldown() {
		if (this.netherPortalCooldown > 0) {
			this.netherPortalCooldown--;
		}
	}

	public int getMaxNetherPortalTime() {
		return 1;
	}

	protected void setOnFireFromLava() {
		if (!this.isFireImmune) {
			this.damage(DamageSource.LAVA, 4.0F);
			this.setOnFireFor(15);
		}
	}

	public void setOnFireFor(int seconds) {
		int i = seconds * 20;
		if (this instanceof LivingEntity) {
			i = ProtectionEnchantment.method_11466((LivingEntity)this, i);
		}

		if (this.fireTicks < i) {
			this.fireTicks = i;
		}
	}

	public void extinguish() {
		this.fireTicks = 0;
	}

	protected void destroy() {
		this.remove();
	}

	public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
		Box box = this.getBoundingBox().offset(offsetX, offsetY, offsetZ);
		return this.doesNotCollide(box);
	}

	private boolean doesNotCollide(Box box) {
		return this.world.doesBoxCollide(this, box).isEmpty() && !this.world.containsFluid(box);
	}

	public void move(double velocityX, double velocityY, double velocityZ) {
		if (this.noClip) {
			this.setBoundingBox(this.getBoundingBox().offset(velocityX, velocityY, velocityZ));
			this.updateSubmergedInWaterState();
		} else {
			this.world.profiler.push("move");
			double d = this.x;
			double e = this.y;
			double f = this.z;
			if (this.inLava) {
				this.inLava = false;
				velocityX *= 0.25;
				velocityY *= 0.05F;
				velocityZ *= 0.25;
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			double g = velocityX;
			double h = velocityY;
			double i = velocityZ;
			boolean bl = this.onGround && this.isSneaking() && this instanceof PlayerEntity;
			if (bl) {
				for (double j = 0.05; velocityX != 0.0 && this.world.doesBoxCollide(this, this.getBoundingBox().offset(velocityX, -1.0, 0.0)).isEmpty(); g = velocityX) {
					if (velocityX < 0.05 && velocityX >= -0.05) {
						velocityX = 0.0;
					} else if (velocityX > 0.0) {
						velocityX -= 0.05;
					} else {
						velocityX += 0.05;
					}
				}

				for (; velocityZ != 0.0 && this.world.doesBoxCollide(this, this.getBoundingBox().offset(0.0, -1.0, velocityZ)).isEmpty(); i = velocityZ) {
					if (velocityZ < 0.05 && velocityZ >= -0.05) {
						velocityZ = 0.0;
					} else if (velocityZ > 0.0) {
						velocityZ -= 0.05;
					} else {
						velocityZ += 0.05;
					}
				}

				for (;
					velocityX != 0.0 && velocityZ != 0.0 && this.world.doesBoxCollide(this, this.getBoundingBox().offset(velocityX, -1.0, velocityZ)).isEmpty();
					i = velocityZ
				) {
					if (velocityX < 0.05 && velocityX >= -0.05) {
						velocityX = 0.0;
					} else if (velocityX > 0.0) {
						velocityX -= 0.05;
					} else {
						velocityX += 0.05;
					}

					g = velocityX;
					if (velocityZ < 0.05 && velocityZ >= -0.05) {
						velocityZ = 0.0;
					} else if (velocityZ > 0.0) {
						velocityZ -= 0.05;
					} else {
						velocityZ += 0.05;
					}
				}
			}

			List<Box> list = this.world.doesBoxCollide(this, this.getBoundingBox().stretch(velocityX, velocityY, velocityZ));
			Box box = this.getBoundingBox();
			int k = 0;

			for (int l = list.size(); k < l; k++) {
				velocityY = ((Box)list.get(k)).method_589(this.getBoundingBox(), velocityY);
			}

			this.setBoundingBox(this.getBoundingBox().offset(0.0, velocityY, 0.0));
			boolean bl2 = this.onGround || h != velocityY && h < 0.0;
			int m = 0;

			for (int n = list.size(); m < n; m++) {
				velocityX = ((Box)list.get(m)).method_583(this.getBoundingBox(), velocityX);
			}

			this.setBoundingBox(this.getBoundingBox().offset(velocityX, 0.0, 0.0));
			m = 0;

			for (int p = list.size(); m < p; m++) {
				velocityZ = ((Box)list.get(m)).method_594(this.getBoundingBox(), velocityZ);
			}

			this.setBoundingBox(this.getBoundingBox().offset(0.0, 0.0, velocityZ));
			if (this.stepHeight > 0.0F && bl2 && (g != velocityX || i != velocityZ)) {
				double q = velocityX;
				double r = velocityY;
				double s = velocityZ;
				Box box2 = this.getBoundingBox();
				this.setBoundingBox(box);
				velocityY = (double)this.stepHeight;
				List<Box> list2 = this.world.doesBoxCollide(this, this.getBoundingBox().stretch(g, velocityY, i));
				Box box3 = this.getBoundingBox();
				Box box4 = box3.stretch(g, 0.0, i);
				double t = velocityY;
				int u = 0;

				for (int v = list2.size(); u < v; u++) {
					t = ((Box)list2.get(u)).method_589(box4, t);
				}

				box3 = box3.offset(0.0, t, 0.0);
				double w = g;
				int x = 0;

				for (int y = list2.size(); x < y; x++) {
					w = ((Box)list2.get(x)).method_583(box3, w);
				}

				box3 = box3.offset(w, 0.0, 0.0);
				double z = i;
				int aa = 0;

				for (int ab = list2.size(); aa < ab; aa++) {
					z = ((Box)list2.get(aa)).method_594(box3, z);
				}

				box3 = box3.offset(0.0, 0.0, z);
				Box box5 = this.getBoundingBox();
				double ac = velocityY;
				int ad = 0;

				for (int ae = list2.size(); ad < ae; ad++) {
					ac = ((Box)list2.get(ad)).method_589(box5, ac);
				}

				Box var80 = box5.offset(0.0, ac, 0.0);
				double af = g;
				int ag = 0;

				for (int ah = list2.size(); ag < ah; ag++) {
					af = ((Box)list2.get(ag)).method_583(var80, af);
				}

				Box var81 = var80.offset(af, 0.0, 0.0);
				double ai = i;
				int aj = 0;

				for (int ak = list2.size(); aj < ak; aj++) {
					ai = ((Box)list2.get(aj)).method_594(var81, ai);
				}

				Box var82 = var81.offset(0.0, 0.0, ai);
				double al = w * w + z * z;
				double am = af * af + ai * ai;
				if (al > am) {
					velocityX = w;
					velocityZ = z;
					velocityY = -t;
					this.setBoundingBox(box3);
				} else {
					velocityX = af;
					velocityZ = ai;
					velocityY = -ac;
					this.setBoundingBox(var82);
				}

				int an = 0;

				for (int ao = list2.size(); an < ao; an++) {
					velocityY = ((Box)list2.get(an)).method_589(this.getBoundingBox(), velocityY);
				}

				this.setBoundingBox(this.getBoundingBox().offset(0.0, velocityY, 0.0));
				if (q * q + s * s >= velocityX * velocityX + velocityZ * velocityZ) {
					velocityX = q;
					velocityY = r;
					velocityZ = s;
					this.setBoundingBox(box2);
				}
			}

			this.world.profiler.pop();
			this.world.profiler.push("rest");
			this.updateSubmergedInWaterState();
			this.horizontalCollision = g != velocityX || i != velocityZ;
			this.verticalCollision = h != velocityY;
			this.onGround = this.verticalCollision && h < 0.0;
			this.colliding = this.horizontalCollision || this.verticalCollision;
			m = MathHelper.floor(this.x);
			int aq = MathHelper.floor(this.y - 0.2F);
			int ar = MathHelper.floor(this.z);
			BlockPos blockPos = new BlockPos(m, aq, ar);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getMaterial() == Material.AIR) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				Block block = blockState2.getBlock();
				if (block instanceof FenceBlock || block instanceof WallBlock || block instanceof FenceGateBlock) {
					blockState = blockState2;
					blockPos = blockPos2;
				}
			}

			this.fall(velocityY, this.onGround, blockState, blockPos);
			if (g != velocityX) {
				this.velocityX = 0.0;
			}

			if (i != velocityZ) {
				this.velocityZ = 0.0;
			}

			Block block2 = blockState.getBlock();
			if (h != velocityY) {
				block2.setEntityVelocity(this.world, this);
			}

			if (this.canClimb() && !bl && !this.hasMount()) {
				double as = this.x - d;
				double at = this.y - e;
				double au = this.z - f;
				if (block2 != Blocks.LADDER) {
					at = 0.0;
				}

				if (block2 != null && this.onGround) {
					block2.onSteppedOn(this.world, blockPos, this);
				}

				this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(as * as + au * au) * 0.6);
				this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(as * as + at * at + au * au) * 0.6);
				if (this.distanceTraveled > (float)this.field_3233 && blockState.getMaterial() != Material.AIR) {
					this.field_3233 = (int)this.distanceTraveled + 1;
					if (this.isTouchingWater()) {
						float av = MathHelper.sqrt(this.velocityX * this.velocityX * 0.2F + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ * 0.2F) * 0.35F;
						if (av > 1.0F) {
							av = 1.0F;
						}

						this.playSound(this.method_12984(), av, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
					}

					this.playStepSound(blockPos, block2);
				}
			}

			try {
				this.checkBlockCollision();
			} catch (Throwable var52) {
				CrashReport crashReport = CrashReport.create(var52, "Checking entity block collision");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
				this.populateCrashReport(crashReportSection);
				throw new CrashException(crashReport);
			}

			boolean bl3 = this.tickFire();
			if (this.world.containsFireSource(this.getBoundingBox().contract(0.001))) {
				this.burn(1);
				if (!bl3) {
					this.fireTicks++;
					if (this.fireTicks == 0) {
						this.setOnFireFor(8);
					}
				}
			} else if (this.fireTicks <= 0) {
				this.fireTicks = -this.fireResistance;
			}

			if (bl3 && this.fireTicks > 0) {
				this.playSound(Sounds.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				this.fireTicks = -this.fireResistance;
			}

			this.world.profiler.pop();
		}
	}

	public void updateSubmergedInWaterState() {
		Box box = this.getBoundingBox();
		this.x = (box.minX + box.maxX) / 2.0;
		this.y = box.minY;
		this.z = (box.minZ + box.maxZ) / 2.0;
	}

	protected Sound method_12984() {
		return Sounds.ENTITY_GENERIC_SWIM;
	}

	protected Sound method_12985() {
		return Sounds.ENTITY_GENERIC_SPLASH;
	}

	protected void checkBlockCollision() {
		Box box = this.getBoundingBox();
		BlockPos.Pooled pooled = BlockPos.Pooled.method_12567(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
		BlockPos.Pooled pooled2 = BlockPos.Pooled.method_12567(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
		BlockPos.Pooled pooled3 = BlockPos.Pooled.get();
		if (this.world.isRegionLoaded(pooled, pooled2)) {
			for (int i = pooled.getX(); i <= pooled2.getX(); i++) {
				for (int j = pooled.getY(); j <= pooled2.getY(); j++) {
					for (int k = pooled.getZ(); k <= pooled2.getZ(); k++) {
						pooled3.setPosition(i, j, k);
						BlockState blockState = this.world.getBlockState(pooled3);

						try {
							blockState.getBlock().onEntityCollision(this.world, pooled3, blockState, this);
						} catch (Throwable var12) {
							CrashReport crashReport = CrashReport.create(var12, "Colliding entity with block");
							CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
							CrashReportSection.addBlockInfo(crashReportSection, pooled3, blockState);
							throw new CrashException(crashReport);
						}
					}
				}
			}
		}

		pooled.method_12576();
		pooled2.method_12576();
		pooled3.method_12576();
	}

	protected void playStepSound(BlockPos pos, Block block) {
		BlockSoundGroup blockSoundGroup = block.getSoundGroup();
		if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER) {
			blockSoundGroup = Blocks.SNOW_LAYER.getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		} else if (!block.getDefaultState().getMaterial().isFluid()) {
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		}
	}

	public void playSound(Sound event, float volume, float pitch) {
		if (!this.isSilent()) {
			this.world.playSound(null, this.x, this.y, this.z, event, this.getSoundCategory(), volume, pitch);
		}
	}

	public boolean isSilent() {
		return this.dataTracker.get(SILENT);
	}

	public void setSilent(boolean silent) {
		this.dataTracker.set(SILENT, silent);
	}

	public boolean hasNoGravity() {
		return this.dataTracker.get(NO_GRAVITY);
	}

	public void setNoGravity(boolean noGravity) {
		this.dataTracker.set(NO_GRAVITY, noGravity);
	}

	protected boolean canClimb() {
		return true;
	}

	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
		if (onGround) {
			if (this.fallDistance > 0.0F) {
				landedState.getBlock().onLandedUpon(this.world, landedPos, this, this.fallDistance);
			}

			this.fallDistance = 0.0F;
		} else if (heightDifference < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - heightDifference);
		}
	}

	@Nullable
	public Box getBox() {
		return null;
	}

	protected void burn(int time) {
		if (!this.isFireImmune) {
			this.damage(DamageSource.FIRE, (float)time);
		}
	}

	public final boolean isFireImmune() {
		return this.isFireImmune;
	}

	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (this.hasPassengers()) {
			for (Entity entity : this.getPassengerList()) {
				entity.handleFallDamage(fallDistance, damageMultiplier);
			}
		}
	}

	public boolean tickFire() {
		if (this.touchingWater) {
			return true;
		} else {
			BlockPos.Pooled pooled = BlockPos.Pooled.method_12567(this.x, this.y, this.z);
			if (!this.world.hasRain(pooled) && !this.world.hasRain(pooled.set(this.x, this.y + (double)this.height, this.z))) {
				pooled.method_12576();
				return false;
			} else {
				pooled.method_12576();
				return true;
			}
		}
	}

	public boolean isTouchingWater() {
		return this.touchingWater;
	}

	public boolean updateWaterState() {
		if (this.getVehicle() instanceof BoatEntity) {
			this.touchingWater = false;
		} else if (this.world.method_3610(this.getBoundingBox().expand(0.0, -0.4F, 0.0).contract(0.001), Material.WATER, this)) {
			if (!this.touchingWater && !this.firstUpdate) {
				this.onSwimmingStart();
			}

			this.fallDistance = 0.0F;
			this.touchingWater = true;
			this.fireTicks = 0;
		} else {
			this.touchingWater = false;
		}

		return this.touchingWater;
	}

	protected void onSwimmingStart() {
		float f = MathHelper.sqrt(this.velocityX * this.velocityX * 0.2F + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ * 0.2F) * 0.2F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		this.playSound(this.method_12985(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		float g = (float)MathHelper.floor(this.getBoundingBox().minY);

		for (int i = 0; (float)i < 1.0F + this.width * 20.0F; i++) {
			float h = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world
				.addParticle(
					ParticleType.BUBBLE,
					this.x + (double)h,
					(double)(g + 1.0F),
					this.z + (double)j,
					this.velocityX,
					this.velocityY - (double)(this.random.nextFloat() * 0.2F),
					this.velocityZ
				);
		}

		for (int k = 0; (float)k < 1.0F + this.width * 20.0F; k++) {
			float l = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float m = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world.addParticle(ParticleType.WATER, this.x + (double)l, (double)(g + 1.0F), this.z + (double)m, this.velocityX, this.velocityY, this.velocityZ);
		}
	}

	public void attemptSprintingParticles() {
		if (this.isSprinting() && !this.isTouchingWater()) {
			this.spawnSprintingParticles();
		}
	}

	protected void spawnSprintingParticles() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			this.world
				.addParticle(
					ParticleType.BLOCK_CRACK,
					this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					this.getBoundingBox().minY + 0.1,
					this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					-this.velocityX * 4.0,
					1.5,
					-this.velocityZ * 4.0,
					Block.getByBlockState(blockState)
				);
		}
	}

	public boolean isSubmergedIn(Material material) {
		if (this.getVehicle() instanceof BoatEntity) {
			return false;
		} else {
			double d = this.y + (double)this.getEyeHeight();
			BlockPos blockPos = new BlockPos(this.x, d, this.z);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getMaterial() == material) {
				float f = AbstractFluidBlock.getHeightPercent(blockState.getBlock().getData(blockState)) - 0.11111111F;
				float g = (float)(blockPos.getY() + 1) - f;
				boolean bl = d < (double)g;
				return !bl && this instanceof PlayerEntity ? false : bl;
			} else {
				return false;
			}
		}
	}

	public boolean isTouchingLava() {
		return this.world.containsMaterial(this.getBoundingBox().expand(-0.1F, -0.4F, -0.1F), Material.LAVA);
	}

	public void updateVelocity(float f, float g, float h) {
		float i = f * f + g * g;
		if (!(i < 1.0E-4F)) {
			i = MathHelper.sqrt(i);
			if (i < 1.0F) {
				i = 1.0F;
			}

			i = h / i;
			f *= i;
			g *= i;
			float j = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
			float k = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
			this.velocityX += (double)(f * k - g * j);
			this.velocityZ += (double)(g * k + f * j);
		}
	}

	public int getLightmapCoordinates(float f) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
		if (this.world.blockExists(mutable)) {
			mutable.setY(MathHelper.floor(this.y + (double)this.getEyeHeight()));
			return this.world.getLight(mutable, 0);
		} else {
			return 0;
		}
	}

	public float getBrightnessAtEyes(float f) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
		if (this.world.blockExists(mutable)) {
			mutable.setY(MathHelper.floor(this.y + (double)this.getEyeHeight()));
			return this.world.getBrightness(mutable);
		} else {
			return 0.0F;
		}
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.x = MathHelper.clamp(x, -3.0E7, 3.0E7);
		this.y = y;
		this.z = MathHelper.clamp(z, -3.0E7, 3.0E7);
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
		this.yaw = yaw;
		this.pitch = pitch;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		double d = (double)(this.prevYaw - yaw);
		if (d < -180.0) {
			this.prevYaw += 360.0F;
		}

		if (d >= 180.0) {
			this.prevYaw -= 360.0F;
		}

		this.updatePosition(this.x, this.y, this.z);
		this.setRotation(yaw, pitch);
	}

	public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
		this.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, yaw, pitch);
	}

	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevTickX = this.x;
		this.prevTickY = this.y;
		this.prevTickZ = this.z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.updatePosition(this.x, this.y, this.z);
	}

	public float distanceTo(Entity entity) {
		float f = (float)(this.x - entity.x);
		float g = (float)(this.y - entity.y);
		float h = (float)(this.z - entity.z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public double squaredDistanceTo(double x, double y, double z) {
		double d = this.x - x;
		double e = this.y - y;
		double f = this.z - z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceTo(BlockPos pos) {
		return pos.squaredDistanceTo(this.x, this.y, this.z);
	}

	public double squaredDistanceToCenter(BlockPos pos) {
		return pos.squaredDistanceToCenter(this.x, this.y, this.z);
	}

	public double distanceTo(double x, double y, double z) {
		double d = this.x - x;
		double e = this.y - y;
		double f = this.z - z;
		return (double)MathHelper.sqrt(d * d + e * e + f * f);
	}

	public double squaredDistanceTo(Entity entity) {
		double d = this.x - entity.x;
		double e = this.y - entity.y;
		double f = this.z - entity.z;
		return d * d + e * e + f * f;
	}

	public void onPlayerCollision(PlayerEntity player) {
	}

	public void pushAwayFrom(Entity entity) {
		if (!this.isConnectedThroughVehicle(entity)) {
			if (!entity.noClip && !this.noClip) {
				double d = entity.x - this.x;
				double e = entity.z - this.z;
				double f = MathHelper.absMax(d, e);
				if (f >= 0.01F) {
					f = (double)MathHelper.sqrt(f);
					d /= f;
					e /= f;
					double g = 1.0 / f;
					if (g > 1.0) {
						g = 1.0;
					}

					d *= g;
					e *= g;
					d *= 0.05F;
					e *= 0.05F;
					d *= (double)(1.0F - this.pushSpeedReduction);
					e *= (double)(1.0F - this.pushSpeedReduction);
					if (!this.hasPassengers()) {
						this.addVelocity(-d, 0.0, -e);
					}

					if (!entity.hasPassengers()) {
						entity.addVelocity(d, 0.0, e);
					}
				}
			}
		}
	}

	public void addVelocity(double x, double y, double z) {
		this.velocityX += x;
		this.velocityY += y;
		this.velocityZ += z;
		this.velocityDirty = true;
	}

	protected void scheduleVelocityUpdate() {
		this.velocityModified = true;
	}

	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			return false;
		}
	}

	public Vec3d getRotationVector(float vector) {
		if (vector == 1.0F) {
			return this.getRotationVector(this.pitch, this.yaw);
		} else {
			float f = this.prevPitch + (this.pitch - this.prevPitch) * vector;
			float g = this.prevYaw + (this.yaw - this.prevYaw) * vector;
			return this.getRotationVector(f, g);
		}
	}

	protected final Vec3d getRotationVector(float pitch, float yaw) {
		float f = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
		float g = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
		float h = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
		float i = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
		return new Vec3d((double)(g * h), (double)i, (double)(f * h));
	}

	public Vec3d getCameraPosVec(float tickDelta) {
		if (tickDelta == 1.0F) {
			return new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z);
		} else {
			double d = this.prevX + (this.x - this.prevX) * (double)tickDelta;
			double e = this.prevY + (this.y - this.prevY) * (double)tickDelta + (double)this.getEyeHeight();
			double f = this.prevZ + (this.z - this.prevZ) * (double)tickDelta;
			return new Vec3d(d, e, f);
		}
	}

	@Nullable
	public BlockHitResult rayTrace(double maxDistance, float tickDelta) {
		Vec3d vec3d = this.getCameraPosVec(tickDelta);
		Vec3d vec3d2 = this.getRotationVector(tickDelta);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
		return this.world.rayTrace(vec3d, vec3d3, false, false, true);
	}

	public boolean collides() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public void updateKilledAdvancementCriterion(Entity entity, int score) {
	}

	public boolean shouldRender(double x, double y, double z) {
		double d = this.x - x;
		double e = this.y - y;
		double f = this.z - z;
		double g = d * d + e * e + f * f;
		return this.shouldRender(g);
	}

	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverage();
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * renderDistanceMultiplier;
		return distance < d * d;
	}

	public boolean saveSelfToNbt(NbtCompound nbt) {
		String string = this.getSavedEntityId();
		if (!this.removed && string != null) {
			nbt.putString("id", string);
			this.toNbt(nbt);
			return true;
		} else {
			return false;
		}
	}

	public boolean saveToNbt(NbtCompound nbt) {
		String string = this.getSavedEntityId();
		if (!this.removed && string != null && !this.hasMount()) {
			nbt.putString("id", string);
			this.toNbt(nbt);
			return true;
		} else {
			return false;
		}
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		try {
			nbt.put("Pos", this.toListNbt(this.x, this.y, this.z));
			nbt.put("Motion", this.toListNbt(this.velocityX, this.velocityY, this.velocityZ));
			nbt.put("Rotation", this.toListNbt(this.yaw, this.pitch));
			nbt.putFloat("FallDistance", this.fallDistance);
			nbt.putShort("Fire", (short)this.fireTicks);
			nbt.putShort("Air", (short)this.getAir());
			nbt.putBoolean("OnGround", this.onGround);
			nbt.putInt("Dimension", this.dimension);
			nbt.putBoolean("Invulnerable", this.invulnerable);
			nbt.putInt("PortalCooldown", this.netherPortalCooldown);
			nbt.putUuid("UUID", this.getUuid());
			if (this.getCustomName() != null && !this.getCustomName().isEmpty()) {
				nbt.putString("CustomName", this.getCustomName());
			}

			if (this.isCustomNameVisible()) {
				nbt.putBoolean("CustomNameVisible", this.isCustomNameVisible());
			}

			this.commandStats.toNbt(nbt);
			if (this.isSilent()) {
				nbt.putBoolean("Silent", this.isSilent());
			}

			if (this.hasNoGravity()) {
				nbt.putBoolean("NoGravity", this.hasNoGravity());
			}

			if (this.isGlowing) {
				nbt.putBoolean("Glowing", this.isGlowing);
			}

			if (this.scoreboardTags.size() > 0) {
				NbtList nbtList = new NbtList();

				for (String string : this.scoreboardTags) {
					nbtList.add(new NbtString(string));
				}

				nbt.put("Tags", nbtList);
			}

			this.writeCustomDataToNbt(nbt);
			if (this.hasPassengers()) {
				NbtList nbtList2 = new NbtList();

				for (Entity entity : this.getPassengerList()) {
					NbtCompound nbtCompound = new NbtCompound();
					if (entity.saveSelfToNbt(nbtCompound)) {
						nbtList2.add(nbtCompound);
					}
				}

				if (!nbtList2.isEmpty()) {
					nbt.put("Passengers", nbtList2);
				}
			}

			return nbt;
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void fromNbt(NbtCompound nbt) {
		try {
			NbtList nbtList = nbt.getList("Pos", 6);
			NbtList nbtList2 = nbt.getList("Motion", 6);
			NbtList nbtList3 = nbt.getList("Rotation", 5);
			this.velocityX = nbtList2.getDouble(0);
			this.velocityY = nbtList2.getDouble(1);
			this.velocityZ = nbtList2.getDouble(2);
			if (Math.abs(this.velocityX) > 10.0) {
				this.velocityX = 0.0;
			}

			if (Math.abs(this.velocityY) > 10.0) {
				this.velocityY = 0.0;
			}

			if (Math.abs(this.velocityZ) > 10.0) {
				this.velocityZ = 0.0;
			}

			this.x = nbtList.getDouble(0);
			this.y = nbtList.getDouble(1);
			this.z = nbtList.getDouble(2);
			this.prevTickX = this.x;
			this.prevTickY = this.y;
			this.prevTickZ = this.z;
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			this.yaw = nbtList3.getFloat(0);
			this.pitch = nbtList3.getFloat(1);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
			this.setHeadYaw(this.yaw);
			this.setYaw(this.yaw);
			this.fallDistance = nbt.getFloat("FallDistance");
			this.fireTicks = nbt.getShort("Fire");
			this.setAir(nbt.getShort("Air"));
			this.onGround = nbt.getBoolean("OnGround");
			if (nbt.contains("Dimension")) {
				this.dimension = nbt.getInt("Dimension");
			}

			this.invulnerable = nbt.getBoolean("Invulnerable");
			this.netherPortalCooldown = nbt.getInt("PortalCooldown");
			if (nbt.containsUuid("UUID")) {
				this.playerUuid = nbt.getUuid("UUID");
				this.uuidString = this.playerUuid.toString();
			}

			this.updatePosition(this.x, this.y, this.z);
			this.setRotation(this.yaw, this.pitch);
			if (nbt.contains("CustomName", 8)) {
				this.setCustomName(nbt.getString("CustomName"));
			}

			this.setCustomNameVisible(nbt.getBoolean("CustomNameVisible"));
			this.commandStats.fromNbt(nbt);
			this.setSilent(nbt.getBoolean("Silent"));
			this.setNoGravity(nbt.getBoolean("NoGravity"));
			this.setGlowing(nbt.getBoolean("Glowing"));
			if (nbt.contains("Tags", 9)) {
				this.scoreboardTags.clear();
				NbtList nbtList4 = nbt.getList("Tags", 8);
				int i = Math.min(nbtList4.size(), 1024);

				for (int j = 0; j < i; j++) {
					this.scoreboardTags.add(nbtList4.getString(j));
				}
			}

			this.readCustomDataFromNbt(nbt);
			if (this.shouldSetPositionOnLoad()) {
				this.updatePosition(this.x, this.y, this.z);
			}
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Loading entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	protected boolean shouldSetPositionOnLoad() {
		return true;
	}

	protected final String getSavedEntityId() {
		return EntityType.getEntityName(this);
	}

	protected abstract void readCustomDataFromNbt(NbtCompound nbt);

	protected abstract void writeCustomDataToNbt(NbtCompound nbt);

	protected NbtList toListNbt(double... values) {
		NbtList nbtList = new NbtList();

		for (double d : values) {
			nbtList.add(new NbtDouble(d));
		}

		return nbtList;
	}

	protected NbtList toListNbt(float... values) {
		NbtList nbtList = new NbtList();

		for (float f : values) {
			nbtList.add(new NbtFloat(f));
		}

		return nbtList;
	}

	public ItemEntity dropItem(Item item, int yOffset) {
		return this.dropItem(item, yOffset, 0.0F);
	}

	public ItemEntity dropItem(Item item, int count, float yOffset) {
		return this.dropItem(new ItemStack(item, count, 0), yOffset);
	}

	public ItemEntity dropItem(ItemStack stack, float yOffset) {
		if (stack.count != 0 && stack.getItem() != null) {
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y + (double)yOffset, this.z, stack);
			itemEntity.setToDefaultPickupDelay();
			this.world.spawnEntity(itemEntity);
			return itemEntity;
		} else {
			return null;
		}
	}

	public boolean isAlive() {
		return !this.removed;
	}

	public boolean isInsideWall() {
		if (this.noClip) {
			return false;
		} else {
			BlockPos.Pooled pooled = BlockPos.Pooled.get();

			for (int i = 0; i < 8; i++) {
				int j = MathHelper.floor(this.y + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
				int k = MathHelper.floor(this.x + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
				int l = MathHelper.floor(this.z + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
				if (pooled.getX() != k || pooled.getY() != j || pooled.getZ() != l) {
					pooled.setPosition(k, j, l);
					if (this.world.getBlockState(pooled).getBlock().isLeafBlock()) {
						pooled.method_12576();
						return true;
					}
				}
			}

			pooled.method_12576();
			return false;
		}
	}

	public boolean method_6100(PlayerEntity playerEntity, @Nullable ItemStack itemStack, Hand hand) {
		return false;
	}

	@Nullable
	public Box getHardCollisionBox(Entity collidingEntity) {
		return null;
	}

	public void tickRiding() {
		Entity entity = this.getVehicle();
		if (this.hasMount() && entity.removed) {
			this.stopRiding();
		} else {
			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
			this.tick();
			if (this.hasMount()) {
				entity.updatePassengerPosition(this);
			}
		}
	}

	public void updatePassengerPosition(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			passenger.updatePosition(this.x, this.y + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.z);
		}
	}

	public void onPassengerLookAround(Entity passenger) {
	}

	public double getHeightOffset() {
		return 0.0;
	}

	public double getMountedHeightOffset() {
		return (double)this.height * 0.75;
	}

	public boolean ride(Entity entity) {
		return this.startRiding(entity, false);
	}

	public boolean startRiding(Entity entity, boolean force) {
		if (force || this.canStartRiding(entity) && entity.canAddPassenger(this)) {
			if (this.hasMount()) {
				this.stopRiding();
			}

			this.mount = entity;
			this.mount.addPassenger(this);
			return true;
		} else {
			return false;
		}
	}

	protected boolean canStartRiding(Entity entity) {
		return this.ridingCooldown <= 0;
	}

	public void removeAllPassengers() {
		for (int i = this.passengerList.size() - 1; i >= 0; i--) {
			((Entity)this.passengerList.get(i)).stopRiding();
		}
	}

	public void stopRiding() {
		if (this.mount != null) {
			Entity entity = this.mount;
			this.mount = null;
			entity.removePassenger(this);
		}
	}

	protected void addPassenger(Entity passenger) {
		if (passenger.getVehicle() != this) {
			throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
		} else {
			if (!this.world.isClient && passenger instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
				this.passengerList.add(0, passenger);
			} else {
				this.passengerList.add(passenger);
			}
		}
	}

	protected void removePassenger(Entity passenger) {
		if (passenger.getVehicle() == this) {
			throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
		} else {
			this.passengerList.remove(passenger);
			passenger.ridingCooldown = 60;
		}
	}

	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < 1;
	}

	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.updatePosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	public float getTargetingMargin() {
		return 0.0F;
	}

	public Vec3d getRotation() {
		return null;
	}

	public Vec2f getRotationClient() {
		return new Vec2f(this.pitch, this.yaw);
	}

	public Vec3d getRotationVecClient() {
		return Vec3d.fromPolar(this.getRotationClient());
	}

	public void setInNetherPortal(BlockPos pos) {
		if (this.netherPortalCooldown > 0) {
			this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
		} else {
			if (!this.world.isClient && !pos.equals(this.lastPortalBlockPos)) {
				this.lastPortalBlockPos = new BlockPos(pos);
				BlockPattern.Result result = Blocks.NETHER_PORTAL.findPortal(this.world, this.lastPortalBlockPos);
				double d = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
				double e = result.getForwards().getAxis() == Direction.Axis.X ? this.z : this.x;
				e = Math.abs(
					MathHelper.minusDiv(
						e - (double)(result.getForwards().rotateYClockwise().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d, d - (double)result.getWidth()
					)
				);
				double f = MathHelper.minusDiv(this.y - 1.0, (double)result.getFrontTopLeft().getY(), (double)(result.getFrontTopLeft().getY() - result.getHeight()));
				this.lastPortalVec3d = new Vec3d(e, f, 0.0);
				this.teleportDirection = result.getForwards();
			}

			this.changingDimension = true;
		}
	}

	public int getDefaultNetherPortalCooldown() {
		return 300;
	}

	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
	}

	public void handleStatus(byte status) {
	}

	public void animateDamage() {
	}

	public Iterable<ItemStack> getItemsHand() {
		return this.field_14496;
	}

	public Iterable<ItemStack> getArmorItems() {
		return this.field_14496;
	}

	public Iterable<ItemStack> getItemsEquipped() {
		return Iterables.concat(this.getItemsHand(), this.getArmorItems());
	}

	public void equipStack(EquipmentSlot slot, @Nullable ItemStack stack) {
	}

	public boolean isOnFire() {
		boolean bl = this.world != null && this.world.isClient;
		return !this.isFireImmune && (this.fireTicks > 0 || bl && this.getFlag(0));
	}

	public boolean hasMount() {
		return this.getVehicle() != null;
	}

	public boolean hasPassengers() {
		return !this.getPassengerList().isEmpty();
	}

	public boolean isSneaking() {
		return this.getFlag(1);
	}

	public void setSneaking(boolean sneaking) {
		this.setFlag(1, sneaking);
	}

	public boolean isSprinting() {
		return this.getFlag(3);
	}

	public void setSprinting(boolean sprinting) {
		this.setFlag(3, sprinting);
	}

	public boolean isGlowing() {
		return this.isGlowing || this.world.isClient && this.getFlag(6);
	}

	public void setGlowing(boolean glowing) {
		this.isGlowing = glowing;
		if (!this.world.isClient) {
			this.setFlag(6, this.isGlowing);
		}
	}

	public boolean isInvisible() {
		return this.getFlag(5);
	}

	public boolean isInvisibleTo(PlayerEntity player) {
		if (player.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam != null && player != null && player.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles()
				? false
				: this.isInvisible();
		}
	}

	@Nullable
	public AbstractTeam getScoreboardTeam() {
		return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
	}

	public boolean isTeammate(Entity other) {
		return this.isTeamPlayer(other.getScoreboardTeam());
	}

	public boolean isTeamPlayer(AbstractTeam team) {
		return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(team) : false;
	}

	public void setInvisible(boolean invisible) {
		this.setFlag(5, invisible);
	}

	protected boolean getFlag(int index) {
		return (this.dataTracker.get(FLAGS) & 1 << index) != 0;
	}

	protected void setFlag(int index, boolean value) {
		byte b = this.dataTracker.get(FLAGS);
		if (value) {
			this.dataTracker.set(FLAGS, (byte)(b | 1 << index));
		} else {
			this.dataTracker.set(FLAGS, (byte)(b & ~(1 << index)));
		}
	}

	public int getAir() {
		return this.dataTracker.get(AIR);
	}

	public void setAir(int air) {
		this.dataTracker.set(AIR, air);
	}

	public void onLightningStrike(LightningBoltEntity lightning) {
		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
		this.fireTicks++;
		if (this.fireTicks == 0) {
			this.setOnFireFor(8);
		}
	}

	public void onKilledOther(LivingEntity other) {
	}

	protected boolean pushOutOfBlocks(double x, double y, double z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		double d = x - (double)blockPos.getX();
		double e = y - (double)blockPos.getY();
		double f = z - (double)blockPos.getZ();
		List<Box> list = this.world.method_3608(this.getBoundingBox());
		if (list.isEmpty()) {
			return false;
		} else {
			Direction direction = Direction.UP;
			double g = Double.MAX_VALUE;
			if (!this.world.method_11492(blockPos.west()) && d < g) {
				g = d;
				direction = Direction.WEST;
			}

			if (!this.world.method_11492(blockPos.east()) && 1.0 - d < g) {
				g = 1.0 - d;
				direction = Direction.EAST;
			}

			if (!this.world.method_11492(blockPos.north()) && f < g) {
				g = f;
				direction = Direction.NORTH;
			}

			if (!this.world.method_11492(blockPos.south()) && 1.0 - f < g) {
				g = 1.0 - f;
				direction = Direction.SOUTH;
			}

			if (!this.world.method_11492(blockPos.up()) && 1.0 - e < g) {
				g = 1.0 - e;
				direction = Direction.UP;
			}

			float h = this.random.nextFloat() * 0.2F + 0.1F;
			float i = (float)direction.getAxisDirection().offset();
			if (direction.getAxis() == Direction.Axis.X) {
				this.velocityX += (double)(i * h);
			} else if (direction.getAxis() == Direction.Axis.Y) {
				this.velocityY += (double)(i * h);
			} else if (direction.getAxis() == Direction.Axis.Z) {
				this.velocityZ += (double)(i * h);
			}

			return true;
		}
	}

	public void setInLava() {
		this.inLava = true;
		this.fallDistance = 0.0F;
	}

	@Override
	public String getTranslationKey() {
		if (this.hasCustomName()) {
			return this.getCustomName();
		} else {
			String string = EntityType.getEntityName(this);
			if (string == null) {
				string = "generic";
			}

			return CommonI18n.translate("entity." + string + ".name");
		}
	}

	public Entity[] getParts() {
		return null;
	}

	public boolean isPartOf(Entity entity) {
		return this == entity;
	}

	public float getHeadRotation() {
		return 0.0F;
	}

	public void setHeadYaw(float headYaw) {
	}

	public void setYaw(float yaw) {
	}

	public boolean isAttackable() {
		return true;
	}

	public boolean handleAttack(Entity attacker) {
		return false;
	}

	public String toString() {
		return String.format(
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.getTranslationKey(),
			this.entityId,
			this.world == null ? "~NULL~" : this.world.getLevelProperties().getLevelName(),
			this.x,
			this.y,
			this.z
		);
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public void copyPosition(Entity original) {
		this.refreshPositionAndAngles(original.x, original.y, original.z, original.yaw, original.pitch);
	}

	private void copyPortalInfo(Entity original) {
		NbtCompound nbtCompound = original.toNbt(new NbtCompound());
		nbtCompound.remove("Dimension");
		this.fromNbt(nbtCompound);
		this.netherPortalCooldown = original.netherPortalCooldown;
		this.lastPortalBlockPos = original.lastPortalBlockPos;
		this.lastPortalVec3d = original.lastPortalVec3d;
		this.teleportDirection = original.teleportDirection;
	}

	@Nullable
	public Entity changeDimension(int newDimension) {
		if (!this.world.isClient && !this.removed) {
			this.world.profiler.push("changeDimension");
			MinecraftServer minecraftServer = this.getMinecraftServer();
			int i = this.dimension;
			ServerWorld serverWorld = minecraftServer.getWorld(i);
			ServerWorld serverWorld2 = minecraftServer.getWorld(newDimension);
			this.dimension = newDimension;
			if (i == 1 && newDimension == 1) {
				serverWorld2 = minecraftServer.getWorld(0);
				this.dimension = 0;
			}

			this.world.removeEntity(this);
			this.removed = false;
			this.world.profiler.push("reposition");
			BlockPos blockPos;
			if (newDimension == 1) {
				blockPos = serverWorld2.getForcedSpawnPoint();
			} else {
				double d = this.x;
				double e = this.z;
				double f = 8.0;
				if (newDimension == -1) {
					d = MathHelper.clamp(d / 8.0, serverWorld2.getWorldBorder().getBoundWest() + 16.0, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
					e = MathHelper.clamp(e / 8.0, serverWorld2.getWorldBorder().getBoundNorth() + 16.0, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
				} else if (newDimension == 0) {
					d = MathHelper.clamp(d * 8.0, serverWorld2.getWorldBorder().getBoundWest() + 16.0, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
					e = MathHelper.clamp(e * 8.0, serverWorld2.getWorldBorder().getBoundNorth() + 16.0, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
				}

				d = (double)MathHelper.clamp((int)d, -29999872, 29999872);
				e = (double)MathHelper.clamp((int)e, -29999872, 29999872);
				float g = this.yaw;
				this.refreshPositionAndAngles(d, this.y, e, 90.0F, 0.0F);
				PortalTeleporter portalTeleporter = serverWorld2.getPortalTeleporter();
				portalTeleporter.method_8584(this, g);
				blockPos = new BlockPos(this);
			}

			serverWorld.checkChunk(this, false);
			this.world.profiler.swap("reloading");
			Entity entity = EntityType.createInstanceFromName(EntityType.getEntityName(this), serverWorld2);
			if (entity != null) {
				entity.copyPortalInfo(this);
				if (i == 1 && newDimension == 1) {
					BlockPos blockPos3 = serverWorld2.getTopPosition(serverWorld2.getSpawnPos());
					entity.refreshPositionAndAngles(blockPos3, entity.yaw, entity.pitch);
				} else {
					entity.refreshPositionAndAngles(blockPos, entity.yaw, entity.pitch);
				}

				boolean bl = entity.teleporting;
				entity.teleporting = true;
				serverWorld2.spawnEntity(entity);
				entity.teleporting = bl;
				serverWorld2.checkChunk(entity, false);
			}

			this.removed = true;
			this.world.profiler.pop();
			serverWorld.resetIdleTimeout();
			serverWorld2.resetIdleTimeout();
			this.world.profiler.pop();
			return entity;
		} else {
			return null;
		}
	}

	public boolean canUsePortals() {
		return true;
	}

	public float getBlastResistance(Explosion explosion, World world, BlockPos pos, BlockState state) {
		return state.getBlock().getBlastResistance(this);
	}

	public boolean canExplosionDestroyBlock(Explosion explosion, World world, BlockPos pos, BlockState state, float explosionPower) {
		return true;
	}

	public int getSafeFallDistance() {
		return 3;
	}

	public Vec3d getLastNetherPortalDirectionVector() {
		return this.lastPortalVec3d;
	}

	public Direction getLastNetherPortalDirection() {
		return this.teleportDirection;
	}

	public boolean canAvoidTraps() {
		return false;
	}

	public void populateCrashReport(CrashReportSection section) {
		section.add("Entity Type", new CrashCallable<String>() {
			public String call() throws Exception {
				return EntityType.getEntityName(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
			}
		});
		section.add("Entity ID", this.entityId);
		section.add("Entity Name", new CrashCallable<String>() {
			public String call() throws Exception {
				return Entity.this.getTranslationKey();
			}
		});
		section.add("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.x, this.y, this.z));
		section.add("Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)));
		section.add("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.velocityX, this.velocityY, this.velocityZ));
		section.add("Entity's Passengers", new CrashCallable<String>() {
			public String call() throws Exception {
				return Entity.this.getPassengerList().toString();
			}
		});
		section.add("Entity's Vehicle", new CrashCallable<String>() {
			public String call() throws Exception {
				return Entity.this.getVehicle().toString();
			}
		});
	}

	public boolean doesRenderOnFire() {
		return this.isOnFire();
	}

	public void setUuid(UUID uuid) {
		this.playerUuid = uuid;
		this.uuidString = this.playerUuid.toString();
	}

	public UUID getUuid() {
		return this.playerUuid;
	}

	public String getEntityName() {
		return this.uuidString;
	}

	public boolean canFly() {
		return true;
	}

	public static double getRenderDistanceMultiplier() {
		return renderDistanceMultiplier;
	}

	public static void setRenderDistanceMultiplier(double value) {
		renderDistanceMultiplier = value;
	}

	@Override
	public Text getName() {
		LiteralText literalText = new LiteralText(Team.decorateName(this.getScoreboardTeam(), this.getTranslationKey()));
		literalText.getStyle().setHoverEvent(this.getHoverEvent());
		literalText.getStyle().setInsertion(this.getEntityName());
		return literalText;
	}

	public void setCustomName(String name) {
		this.dataTracker.set(CUSTOM_NAME, name);
	}

	public String getCustomName() {
		return this.dataTracker.get(CUSTOM_NAME);
	}

	public boolean hasCustomName() {
		return !this.dataTracker.get(CUSTOM_NAME).isEmpty();
	}

	public void setCustomNameVisible(boolean visible) {
		this.dataTracker.set(NAME_VISIBLE, visible);
	}

	public boolean isCustomNameVisible() {
		return this.dataTracker.get(NAME_VISIBLE);
	}

	public void refreshPositionAfterTeleport(double x, double y, double z) {
		this.teleportRequested = true;
		this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
		this.world.checkChunk(this, false);
	}

	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void onTrackedDataSet(TrackedData<?> data) {
	}

	public Direction getHorizontalDirection() {
		return Direction.fromHorizontal(MathHelper.floor((double)(this.yaw * 4.0F / 360.0F) + 0.5) & 3);
	}

	public Direction getMovementDirection() {
		return this.getHorizontalDirection();
	}

	protected HoverEvent getHoverEvent() {
		NbtCompound nbtCompound = new NbtCompound();
		String string = EntityType.getEntityName(this);
		nbtCompound.putString("id", this.getEntityName());
		if (string != null) {
			nbtCompound.putString("type", string);
		}

		nbtCompound.putString("name", this.getTranslationKey());
		return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new LiteralText(nbtCompound.toString()));
	}

	public boolean isSpectatedBy(ServerPlayerEntity player) {
		return true;
	}

	public Box getBoundingBox() {
		return this.boundingBox;
	}

	public Box getVisibilityBoundingBox() {
		return this.getBoundingBox();
	}

	public void setBoundingBox(Box boundingBox) {
		this.boundingBox = boundingBox;
	}

	public float getEyeHeight() {
		return this.height * 0.85F;
	}

	public boolean isOutsideWorldBorder() {
		return this.outsideWorldBorder;
	}

	public void setOutsideWorldBorder(boolean value) {
		this.outsideWorldBorder = value;
	}

	public boolean equip(int slot, ItemStack item) {
		return false;
	}

	@Override
	public void sendMessage(Text text) {
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return true;
	}

	@Override
	public BlockPos getBlockPos() {
		return new BlockPos(this.x, this.y + 0.5, this.z);
	}

	@Override
	public Vec3d getPos() {
		return new Vec3d(this.x, this.y, this.z);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public Entity getEntity() {
		return this;
	}

	@Override
	public boolean sendCommandFeedback() {
		return false;
	}

	@Override
	public void setStat(CommandStats.Type statsType, int value) {
		if (this.world != null && !this.world.isClient) {
			this.commandStats.method_10792(this.world.getServer(), this, statsType, value);
		}
	}

	@Nullable
	@Override
	public MinecraftServer getMinecraftServer() {
		return this.world.getServer();
	}

	public CommandStats getCommandStats() {
		return this.commandStats;
	}

	public void method_10965(Entity entity) {
		this.commandStats.setAllStats(entity.getCommandStats());
	}

	public ActionResult method_12976(PlayerEntity playerEntity, Vec3d vec3d, @Nullable ItemStack itemStack, Hand hand) {
		return ActionResult.PASS;
	}

	public boolean isImmuneToExplosion() {
		return false;
	}

	protected void dealDamage(LivingEntity attacker, Entity target) {
		if (target instanceof LivingEntity) {
			EnchantmentHelper.onUserDamaged((LivingEntity)target, attacker);
		}

		EnchantmentHelper.onTargetDamaged(attacker, target);
	}

	public void onStartedTrackingBy(ServerPlayerEntity player) {
	}

	public void onStoppedTrackingBy(ServerPlayerEntity player) {
	}

	public float applyRotation(BlockRotation rotation) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (rotation) {
			case CLOCKWISE_180:
				return f + 180.0F;
			case COUNTERCLOCKWISE_90:
				return f + 270.0F;
			case CLOCKWISE_90:
				return f + 90.0F;
			default:
				return f;
		}
	}

	public float applyMirror(BlockMirror mirror) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (mirror) {
			case LEFT_RIGHT:
				return -f;
			case FRONT_BACK:
				return 180.0F - f;
			default:
				return f;
		}
	}

	public boolean entityDataRequiresOperator() {
		return false;
	}

	public boolean teleportRequested() {
		boolean bl = this.teleportRequested;
		this.teleportRequested = false;
		return bl;
	}

	@Nullable
	public Entity getPrimaryPassenger() {
		return null;
	}

	public List<Entity> getPassengerList() {
		return (List<Entity>)(this.passengerList.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengerList));
	}

	public boolean hasPassenger(Entity passenger) {
		for (Entity entity : this.getPassengerList()) {
			if (entity.equals(passenger)) {
				return true;
			}
		}

		return false;
	}

	public Collection<Entity> getPassengersDeep() {
		Set<Entity> set = Sets.newHashSet();
		this.getPassengersDeep(Entity.class, set);
		return set;
	}

	public <T extends Entity> Collection<T> getPassengersDeep(Class<T> type) {
		Set<T> set = Sets.newHashSet();
		this.getPassengersDeep(type, set);
		return set;
	}

	private <T extends Entity> void getPassengersDeep(Class<T> type, Set<T> set) {
		for (Entity entity : this.getPassengerList()) {
			if (type.isAssignableFrom(entity.getClass())) {
				set.add(entity);
			}

			entity.getPassengersDeep(type, set);
		}
	}

	public Entity getRootVehicle() {
		Entity entity = this;

		while (entity.hasMount()) {
			entity = entity.getVehicle();
		}

		return entity;
	}

	public boolean isConnectedThroughVehicle(Entity entity) {
		return this.getRootVehicle() == entity.getRootVehicle();
	}

	public boolean hasPassengerDeep(Entity passenger) {
		for (Entity entity : this.getPassengerList()) {
			if (entity.equals(passenger)) {
				return true;
			}

			if (entity.hasPassengerDeep(passenger)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_13003() {
		Entity entity = this.getPrimaryPassenger();
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).isMainPlayer() : !this.world.isClient;
	}

	@Nullable
	public Entity getVehicle() {
		return this.mount;
	}

	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.NORMAL;
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}
}
