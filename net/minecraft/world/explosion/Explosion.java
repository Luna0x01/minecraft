package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Explosion {
	private final boolean createFire;
	private final boolean destructive;
	private final Random random = new Random();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	private final Entity causingEntity;
	private final float power;
	private DamageSource field_17489;
	private final List<BlockPos> affectedBlocks = Lists.newArrayList();
	private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();

	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, List<BlockPos> list) {
		this(world, entity, d, e, f, g, false, true, list);
	}

	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, boolean bl2, List<BlockPos> list) {
		this(world, entity, d, e, f, g, bl, bl2);
		this.affectedBlocks.addAll(list);
	}

	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, boolean bl2) {
		this.world = world;
		this.causingEntity = entity;
		this.power = g;
		this.x = d;
		this.y = e;
		this.z = f;
		this.createFire = bl;
		this.destructive = bl2;
		this.field_17489 = DamageSource.explosion(this);
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
								if (this.causingEntity != null) {
									q = this.causingEntity.method_10932(this, this.world, blockPos, blockState, fluidState, q);
								}

								h -= (q + 0.3F) * 0.3F;
							}

							if (h > 0.0F && (this.causingEntity == null || this.causingEntity.method_10933(this, this.world, blockPos, blockState, h))) {
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
		List<Entity> list = this.world.getEntities(this.causingEntity, new Box((double)s, (double)u, (double)w, (double)t, (double)v, (double)x));
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

		for (int y = 0; y < list.size(); y++) {
			Entity entity = (Entity)list.get(y);
			if (!entity.isImmuneToExplosion()) {
				double z = entity.distanceTo(this.x, this.y, this.z) / (double)r;
				if (z <= 1.0) {
					double aa = entity.x - this.x;
					double ab = entity.y + (double)entity.getEyeHeight() - this.y;
					double ac = entity.z - this.z;
					double ad = (double)MathHelper.sqrt(aa * aa + ab * ab + ac * ac);
					if (ad != 0.0) {
						aa /= ad;
						ab /= ad;
						ac /= ad;
						double ae = (double)this.world.method_3612(vec3d, entity.getBoundingBox());
						double af = (1.0 - z) * ae;
						entity.damage(this.method_16295(), (float)((int)((af * af + af) / 2.0 * 7.0 * (double)r + 1.0)));
						double ag = af;
						if (entity instanceof LivingEntity) {
							ag = ProtectionEnchantment.method_11465((LivingEntity)entity, af);
						}

						entity.velocityX += aa * ag;
						entity.velocityY += ab * ag;
						entity.velocityZ += ac * ag;
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

	public void affectWorld(boolean showSmallParticles) {
		this.world
			.playSound(
				null,
				this.x,
				this.y,
				this.z,
				Sounds.ENTITY_GENERIC_EXPLODE,
				SoundCategory.BLOCKS,
				4.0F,
				(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F
			);
		if (!(this.power < 2.0F) && this.destructive) {
			this.world.method_16343(class_4342.field_21394, this.x, this.y, this.z, 1.0, 0.0, 0.0);
		} else {
			this.world.method_16343(class_4342.field_21395, this.x, this.y, this.z, 1.0, 0.0, 0.0);
		}

		if (this.destructive) {
			for (BlockPos blockPos : this.affectedBlocks) {
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (showSmallParticles) {
					double d = (double)((float)blockPos.getX() + this.world.random.nextFloat());
					double e = (double)((float)blockPos.getY() + this.world.random.nextFloat());
					double f = (double)((float)blockPos.getZ() + this.world.random.nextFloat());
					double g = d - this.x;
					double h = e - this.y;
					double i = f - this.z;
					double j = (double)MathHelper.sqrt(g * g + h * h + i * i);
					g /= j;
					h /= j;
					i /= j;
					double k = 0.5 / (j / (double)this.power + 0.1);
					k *= (double)(this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
					g *= k;
					h *= k;
					i *= k;
					this.world.method_16343(class_4342.field_21360, (d + this.x) / 2.0, (e + this.y) / 2.0, (f + this.z) / 2.0, g, h, i);
					this.world.method_16343(class_4342.field_21363, d, e, f, g, h, i);
				}

				if (!blockState.isAir()) {
					if (block.shouldDropItemsOnExplosion(this)) {
						blockState.method_16866(this.world, blockPos, 1.0F / this.power, 0);
					}

					this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
					block.onDestroyedByExplosion(this.world, blockPos, this);
				}
			}
		}

		if (this.createFire) {
			for (BlockPos blockPos2 : this.affectedBlocks) {
				if (this.world.getBlockState(blockPos2).isAir()
					&& this.world.getBlockState(blockPos2.down()).isFullOpaque(this.world, blockPos2.down())
					&& this.random.nextInt(3) == 0) {
					this.world.setBlockState(blockPos2, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	public DamageSource method_16295() {
		return this.field_17489;
	}

	public void method_16294(DamageSource damageSource) {
		this.field_17489 = damageSource;
	}

	public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
		return this.affectedPlayers;
	}

	@Nullable
	public LivingEntity getCausingEntity() {
		if (this.causingEntity == null) {
			return null;
		} else if (this.causingEntity instanceof TntEntity) {
			return ((TntEntity)this.causingEntity).getIgniter();
		} else {
			return this.causingEntity instanceof LivingEntity ? (LivingEntity)this.causingEntity : null;
		}
	}

	public void clearAffectedBlocks() {
		this.affectedBlocks.clear();
	}

	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}
}
