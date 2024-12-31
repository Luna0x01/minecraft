package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_4079;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FallingBlockEntity extends Entity {
	private BlockState block = Blocks.SAND.getDefaultState();
	public int timeFalling;
	public boolean dropping = true;
	private boolean destroyedOnLanding;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private float fallHurtAmount = 2.0F;
	public NbtCompound tileEntityData;
	protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

	public FallingBlockEntity(World world) {
		super(EntityType.FALLING_BLOCK, world);
	}

	public FallingBlockEntity(World world, double d, double e, double f, BlockState blockState) {
		this(world);
		this.block = blockState;
		this.inanimate = true;
		this.setBounds(0.98F, 0.98F);
		this.updatePosition(d, e + (double)((1.0F - this.height) / 2.0F), f);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
		this.setFallingBlockPos(new BlockPos(this));
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	public void setFallingBlockPos(BlockPos pos) {
		this.dataTracker.set(BLOCK_POS, pos);
	}

	public BlockPos getFallingBlockPos() {
		return this.dataTracker.get(BLOCK_POS);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public void tick() {
		if (this.block.isAir()) {
			this.remove();
		} else {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			Block block = this.block.getBlock();
			if (this.timeFalling++ == 0) {
				BlockPos blockPos = new BlockPos(this);
				if (this.world.getBlockState(blockPos).getBlock() == block) {
					this.world.method_8553(blockPos);
				} else if (!this.world.isClient) {
					this.remove();
					return;
				}
			}

			if (!this.hasNoGravity()) {
				this.velocityY -= 0.04F;
			}

			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			if (!this.world.isClient) {
				BlockPos blockPos2 = new BlockPos(this);
				boolean bl = this.block.getBlock() instanceof ConcretePowderBlock;
				boolean bl2 = bl && this.world.getFluidState(blockPos2).matches(FluidTags.WATER);
				double d = this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ;
				if (bl && d > 1.0) {
					BlockHitResult blockHitResult = this.world
						.method_3614(new Vec3d(this.prevX, this.prevY, this.prevZ), new Vec3d(this.x, this.y, this.z), class_4079.SOURCE_ONLY);
					if (blockHitResult != null && this.world.getFluidState(blockHitResult.getBlockPos()).matches(FluidTags.WATER)) {
						blockPos2 = blockHitResult.getBlockPos();
						bl2 = true;
					}
				}

				if (!this.onGround && !bl2) {
					if (this.timeFalling > 100 && !this.world.isClient && (blockPos2.getY() < 1 || blockPos2.getY() > 256) || this.timeFalling > 600) {
						if (this.dropping && this.world.getGameRules().getBoolean("doEntityDrops")) {
							this.method_15560(block);
						}

						this.remove();
					}
				} else {
					BlockState blockState = this.world.getBlockState(blockPos2);
					if (!bl2 && FallingBlock.canFallThough(this.world.getBlockState(new BlockPos(this.x, this.y - 0.01F, this.z)))) {
						this.onGround = false;
						return;
					}

					this.velocityX *= 0.7F;
					this.velocityZ *= 0.7F;
					this.velocityY *= -0.5;
					if (blockState.getBlock() != Blocks.MOVING_PISTON) {
						this.remove();
						if (!this.destroyedOnLanding) {
							if (blockState.getMaterial().isReplaceable()
								&& (bl2 || !FallingBlock.canFallThough(this.world.getBlockState(blockPos2.down())))
								&& this.world.setBlockState(blockPos2, this.block, 3)) {
								if (block instanceof FallingBlock) {
									((FallingBlock)block).onLanding(this.world, blockPos2, this.block, blockState);
								}

								if (this.tileEntityData != null && block instanceof BlockEntityProvider) {
									BlockEntity blockEntity = this.world.getBlockEntity(blockPos2);
									if (blockEntity != null) {
										NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());

										for (String string : this.tileEntityData.getKeys()) {
											NbtElement nbtElement = this.tileEntityData.get(string);
											if (!"x".equals(string) && !"y".equals(string) && !"z".equals(string)) {
												nbtCompound.put(string, nbtElement.copy());
											}
										}

										blockEntity.fromNbt(nbtCompound);
										blockEntity.markDirty();
									}
								}
							} else if (this.dropping && this.world.getGameRules().getBoolean("doEntityDrops")) {
								this.method_15560(block);
							}
						} else if (block instanceof FallingBlock) {
							((FallingBlock)block).method_13705(this.world, blockPos2);
						}
					}
				}
			}

			this.velocityX *= 0.98F;
			this.velocityY *= 0.98F;
			this.velocityZ *= 0.98F;
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (this.hurtEntities) {
			int i = MathHelper.ceil(fallDistance - 1.0F);
			if (i > 0) {
				List<Entity> list = Lists.newArrayList(this.world.getEntities(this, this.getBoundingBox()));
				boolean bl = this.block.isIn(BlockTags.ANVIL);
				DamageSource damageSource = bl ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

				for (Entity entity : list) {
					entity.damage(damageSource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
				}

				if (bl && (double)this.random.nextFloat() < 0.05F + (double)i * 0.05) {
					BlockState blockState = AnvilBlock.getLandingState(this.block);
					if (blockState == null) {
						this.destroyedOnLanding = true;
					} else {
						this.block = blockState;
					}
				}
			}
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.put("BlockState", NbtHelper.method_20139(this.block));
		nbt.putInt("Time", this.timeFalling);
		nbt.putBoolean("DropItem", this.dropping);
		nbt.putBoolean("HurtEntities", this.hurtEntities);
		nbt.putFloat("FallHurtAmount", this.fallHurtAmount);
		nbt.putInt("FallHurtMax", this.fallHurtMax);
		if (this.tileEntityData != null) {
			nbt.put("TileEntityData", this.tileEntityData);
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.block = NbtHelper.toBlockState(nbt.getCompound("BlockState"));
		this.timeFalling = nbt.getInt("Time");
		if (nbt.contains("HurtEntities", 99)) {
			this.hurtEntities = nbt.getBoolean("HurtEntities");
			this.fallHurtAmount = nbt.getFloat("FallHurtAmount");
			this.fallHurtMax = nbt.getInt("FallHurtMax");
		} else if (this.block.isIn(BlockTags.ANVIL)) {
			this.hurtEntities = true;
		}

		if (nbt.contains("DropItem", 99)) {
			this.dropping = nbt.getBoolean("DropItem");
		}

		if (nbt.contains("TileEntityData", 10)) {
			this.tileEntityData = nbt.getCompound("TileEntityData");
		}

		if (this.block.isAir()) {
			this.block = Blocks.SAND.getDefaultState();
		}
	}

	public World method_3056() {
		return this.world;
	}

	public void setHurtingEntities(boolean hurtEntities) {
		this.hurtEntities = hurtEntities;
	}

	@Override
	public boolean doesRenderOnFire() {
		return false;
	}

	@Override
	public void populateCrashReport(CrashReportSection section) {
		super.populateCrashReport(section);
		section.add("Immitating BlockState", this.block.toString());
	}

	public BlockState getBlockState() {
		return this.block;
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}
}
