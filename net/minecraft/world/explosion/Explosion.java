package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class Explosion {
	private final boolean createFire;
	private final Explosion.DestructionType blockDestructionType;
	private final Random random = new Random();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	@Nullable
	private final Entity entity;
	private final float power;
	private DamageSource damageSource;
	private final List<BlockPos> affectedBlocks = Lists.newArrayList();
	private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();

	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, List<BlockPos> list) {
		this(world, entity, d, e, f, g, false, Explosion.DestructionType.field_18687, list);
	}

	public Explosion(
		World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType, List<BlockPos> list
	) {
		this(world, entity, d, e, f, g, bl, destructionType);
		this.affectedBlocks.addAll(list);
	}

	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType) {
		this.world = world;
		this.entity = entity;
		this.power = g;
		this.x = d;
		this.y = e;
		this.z = f;
		this.createFire = bl;
		this.blockDestructionType = destructionType;
		this.damageSource = DamageSource.explosion(this);
	}

	public static float getExposure(Vec3d vec3d, Entity entity) {
		Box box = entity.getBoundingBox();
		double d = 1.0 / ((box.x2 - box.x1) * 2.0 + 1.0);
		double e = 1.0 / ((box.y2 - box.y1) * 2.0 + 1.0);
		double f = 1.0 / ((box.z2 - box.z1) * 2.0 + 1.0);
		double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
		double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
		if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
			int i = 0;
			int j = 0;

			for (float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
				for (float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
					for (float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
						double n = MathHelper.lerp((double)k, box.x1, box.x2);
						double o = MathHelper.lerp((double)l, box.y1, box.y2);
						double p = MathHelper.lerp((double)m, box.z1, box.z2);
						Vec3d vec3d2 = new Vec3d(n + g, o, p + h);
						if (entity.world
								.rayTrace(new RayTraceContext(vec3d2, vec3d, RayTraceContext.ShapeType.field_17559, RayTraceContext.FluidHandling.field_1348, entity))
								.getType()
							== HitResult.Type.field_1333) {
							i++;
						}

						j++;
					}
				}
			}

			return (float)i / (float)j;
		} else {
			return 0.0F;
		}
	}

	public void collectBlocksAndDamageEntities() {
		Set<BlockPos> set = Sets.newHashSet();
		int i = 16;

		for (int j = 0; j < 16; j++) {
			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
						double d = (double)((float)j / 15.0F * 2.0F - 1.0F);
						double e = (double)((float)k / 15.0F * 2.0F - 1.0F);
						double f = (double)((float)l / 15.0F * 2.0F - 1.0F);
						double g = Math.sqrt(d * d + e * e + f * f);
						d /= g;
						e /= g;
						f /= g;
						float h = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
						double m = this.x;
						double n = this.y;
						double o = this.z;

						for (float p = 0.3F; h > 0.0F; h -= 0.22500001F) {
							BlockPos blockPos = new BlockPos(m, n, o);
							BlockState blockState = this.world.getBlockState(blockPos);
							FluidState fluidState = this.world.getFluidState(blockPos);
							if (!blockState.isAir() || !fluidState.isEmpty()) {
								float q = Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance());
								if (this.entity != null) {
									q = this.entity.getEffectiveExplosionResistance(this, this.world, blockPos, blockState, fluidState, q);
								}

								h -= (q + 0.3F) * 0.3F;
							}

							if (h > 0.0F && (this.entity == null || this.entity.canExplosionDestroyBlock(this, this.world, blockPos, blockState, h))) {
								set.add(blockPos);
							}

							m += d * 0.3F;
							n += e * 0.3F;
							o += f * 0.3F;
						}
					}
				}
			}
		}

		this.affectedBlocks.addAll(set);
		float r = this.power * 2.0F;
		int s = MathHelper.floor(this.x - (double)r - 1.0);
		int t = MathHelper.floor(this.x + (double)r + 1.0);
		int u = MathHelper.floor(this.y - (double)r - 1.0);
		int v = MathHelper.floor(this.y + (double)r + 1.0);
		int w = MathHelper.floor(this.z - (double)r - 1.0);
		int x = MathHelper.floor(this.z + (double)r + 1.0);
		List<Entity> list = this.world.getEntities(this.entity, new Box((double)s, (double)u, (double)w, (double)t, (double)v, (double)x));
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

		for (int y = 0; y < list.size(); y++) {
			Entity entity = (Entity)list.get(y);
			if (!entity.isImmuneToExplosion()) {
				double z = (double)(MathHelper.sqrt(entity.squaredDistanceTo(vec3d)) / r);
				if (z <= 1.0) {
					double aa = entity.getX() - this.x;
					double ab = entity.getEyeY() - this.y;
					double ac = entity.getZ() - this.z;
					double ad = (double)MathHelper.sqrt(aa * aa + ab * ab + ac * ac);
					if (ad != 0.0) {
						aa /= ad;
						ab /= ad;
						ac /= ad;
						double ae = (double)getExposure(vec3d, entity);
						double af = (1.0 - z) * ae;
						entity.damage(this.getDamageSource(), (float)((int)((af * af + af) / 2.0 * 7.0 * (double)r + 1.0)));
						double ag = af;
						if (entity instanceof LivingEntity) {
							ag = ProtectionEnchantment.transformExplosionKnockback((LivingEntity)entity, af);
						}

						entity.setVelocity(entity.getVelocity().add(aa * ag, ab * ag, ac * ag));
						if (entity instanceof PlayerEntity) {
							PlayerEntity playerEntity = (PlayerEntity)entity;
							if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.abilities.flying)) {
								this.affectedPlayers.put(playerEntity, new Vec3d(aa * af, ab * af, ac * af));
							}
						}
					}
				}
			}
		}
	}

	public void affectWorld(boolean bl) {
		if (this.world.isClient) {
			this.world
				.playSound(
					this.x,
					this.y,
					this.z,
					SoundEvents.field_15152,
					SoundCategory.field_15245,
					4.0F,
					(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
					false
				);
		}

		boolean bl2 = this.blockDestructionType != Explosion.DestructionType.field_18685;
		if (bl) {
			if (!(this.power < 2.0F) && bl2) {
				this.world.addParticle(ParticleTypes.field_11221, this.x, this.y, this.z, 1.0, 0.0, 0.0);
			} else {
				this.world.addParticle(ParticleTypes.field_11236, this.x, this.y, this.z, 1.0, 0.0, 0.0);
			}
		}

		if (bl2) {
			ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList();
			Collections.shuffle(this.affectedBlocks, this.world.random);

			for (BlockPos blockPos : this.affectedBlocks) {
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (!blockState.isAir()) {
					BlockPos blockPos2 = blockPos.toImmutable();
					this.world.getProfiler().push("explosion_blocks");
					if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
						BlockEntity blockEntity = block.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
						LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
							.setRandom(this.world.random)
							.put(LootContextParameters.field_1232, blockPos)
							.put(LootContextParameters.field_1229, ItemStack.EMPTY)
							.putNullable(LootContextParameters.field_1228, blockEntity)
							.putNullable(LootContextParameters.field_1226, this.entity);
						if (this.blockDestructionType == Explosion.DestructionType.field_18687) {
							builder.put(LootContextParameters.field_1225, this.power);
						}

						blockState.getDroppedStacks(builder).forEach(itemStack -> method_24023(objectArrayList, itemStack, blockPos2));
					}

					this.world.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 3);
					block.onDestroyedByExplosion(this.world, blockPos, this);
					this.world.getProfiler().pop();
				}
			}

			ObjectListIterator var12 = objectArrayList.iterator();

			while (var12.hasNext()) {
				Pair<ItemStack, BlockPos> pair = (Pair<ItemStack, BlockPos>)var12.next();
				Block.dropStack(this.world, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
			}
		}

		if (this.createFire) {
			for (BlockPos blockPos3 : this.affectedBlocks) {
				if (this.random.nextInt(3) == 0
					&& this.world.getBlockState(blockPos3).isAir()
					&& this.world.getBlockState(blockPos3.down()).isFullOpaque(this.world, blockPos3.down())) {
					this.world.setBlockState(blockPos3, Blocks.field_10036.getDefaultState());
				}
			}
		}
	}

	private static void method_24023(ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList, ItemStack itemStack, BlockPos blockPos) {
		int i = objectArrayList.size();

		for (int j = 0; j < i; j++) {
			Pair<ItemStack, BlockPos> pair = (Pair<ItemStack, BlockPos>)objectArrayList.get(j);
			ItemStack itemStack2 = (ItemStack)pair.getFirst();
			if (ItemEntity.method_24017(itemStack2, itemStack)) {
				ItemStack itemStack3 = ItemEntity.method_24018(itemStack2, itemStack, 16);
				objectArrayList.set(j, Pair.of(itemStack3, pair.getSecond()));
				if (itemStack.isEmpty()) {
					return;
				}
			}
		}

		objectArrayList.add(Pair.of(itemStack, blockPos));
	}

	public DamageSource getDamageSource() {
		return this.damageSource;
	}

	public void setDamageSource(DamageSource damageSource) {
		this.damageSource = damageSource;
	}

	public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
		return this.affectedPlayers;
	}

	@Nullable
	public LivingEntity getCausingEntity() {
		if (this.entity == null) {
			return null;
		} else if (this.entity instanceof TntEntity) {
			return ((TntEntity)this.entity).getCausingEntity();
		} else if (this.entity instanceof LivingEntity) {
			return (LivingEntity)this.entity;
		} else {
			return this.entity instanceof ExplosiveProjectileEntity ? ((ExplosiveProjectileEntity)this.entity).owner : null;
		}
	}

	public void clearAffectedBlocks() {
		this.affectedBlocks.clear();
	}

	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}

	public static enum DestructionType {
		field_18685,
		field_18686,
		field_18687;
	}
}
