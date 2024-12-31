package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FallingBlockEntity extends Entity {
	private BlockState block;
	public int timeFalling;
	public boolean dropping = true;
	private boolean destroyedOnLanding;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private float fallHurtAmount = 2.0F;
	public NbtCompound tileEntityData;
	protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

	public FallingBlockEntity(World world) {
		super(world);
	}

	public FallingBlockEntity(World world, double d, double e, double f, BlockState blockState) {
		super(world);
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
		Block block = this.block.getBlock();
		if (this.block.getMaterial() == Material.AIR) {
			this.remove();
		} else {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			if (this.timeFalling++ == 0) {
				BlockPos blockPos = new BlockPos(this);
				if (this.world.getBlockState(blockPos).getBlock() == block) {
					this.world.setAir(blockPos);
				} else if (!this.world.isClient) {
					this.remove();
					return;
				}
			}

			this.velocityY -= 0.04F;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.98F;
			this.velocityY *= 0.98F;
			this.velocityZ *= 0.98F;
			if (!this.world.isClient) {
				BlockPos blockPos2 = new BlockPos(this);
				if (this.onGround) {
					BlockState blockState = this.world.getBlockState(blockPos2);
					if (FallingBlock.canFallThough(this.world.getBlockState(new BlockPos(this.x, this.y - 0.01F, this.z)))) {
						this.onGround = false;
						return;
					}

					this.velocityX *= 0.7F;
					this.velocityZ *= 0.7F;
					this.velocityY *= -0.5;
					if (blockState.getBlock() != Blocks.PISTON_EXTENSION) {
						this.remove();
						if (!this.destroyedOnLanding) {
							if (this.world.canBlockBePlaced(block, blockPos2, true, Direction.UP, null, null)
								&& !FallingBlock.canFallThough(this.world.getBlockState(blockPos2.down()))
								&& this.world.setBlockState(blockPos2, this.block, 3)) {
								if (block instanceof FallingBlock) {
									((FallingBlock)block).onDestroyedOnLanding(this.world, blockPos2);
								}

								if (this.tileEntityData != null && block instanceof BlockEntityProvider) {
									BlockEntity blockEntity = this.world.getBlockEntity(blockPos2);
									if (blockEntity != null) {
										NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());

										for (String string : this.tileEntityData.getKeys()) {
											NbtElement nbtElement = this.tileEntityData.get(string);
											if (!string.equals("x") && !string.equals("y") && !string.equals("z")) {
												nbtCompound.put(string, nbtElement.copy());
											}
										}

										blockEntity.fromNbt(nbtCompound);
										blockEntity.markDirty();
									}
								}
							} else if (this.dropping && this.world.getGameRules().getBoolean("doEntityDrops")) {
								this.dropItem(new ItemStack(block, 1, block.getMeta(this.block)), 0.0F);
							}
						}
					}
				} else if (this.timeFalling > 100 && !this.world.isClient && (blockPos2.getY() < 1 || blockPos2.getY() > 256) || this.timeFalling > 600) {
					if (this.dropping && this.world.getGameRules().getBoolean("doEntityDrops")) {
						this.dropItem(new ItemStack(block, 1, block.getMeta(this.block)), 0.0F);
					}

					this.remove();
				}
			}
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		Block block = this.block.getBlock();
		if (this.hurtEntities) {
			int i = MathHelper.ceil(fallDistance - 1.0F);
			if (i > 0) {
				List<Entity> list = Lists.newArrayList(this.world.getEntitiesIn(this, this.getBoundingBox()));
				boolean bl = block == Blocks.ANVIL;
				DamageSource damageSource = bl ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

				for (Entity entity : list) {
					entity.damage(damageSource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
				}

				if (bl && (double)this.random.nextFloat() < 0.05F + (double)i * 0.05) {
					int j = (Integer)this.block.get(AnvilBlock.DAMAGE);
					if (++j > 2) {
						this.destroyedOnLanding = true;
					} else {
						this.block = this.block.with(AnvilBlock.DAMAGE, j);
					}
				}
			}
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		Block block = this.block != null ? this.block.getBlock() : Blocks.AIR;
		Identifier identifier = Block.REGISTRY.getIdentifier(block);
		nbt.putString("Block", identifier == null ? "" : identifier.toString());
		nbt.putByte("Data", (byte)block.getData(this.block));
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
		int i = nbt.getByte("Data") & 255;
		if (nbt.contains("Block", 8)) {
			this.block = Block.get(nbt.getString("Block")).stateFromData(i);
		} else if (nbt.contains("TileID", 99)) {
			this.block = Block.getById(nbt.getInt("TileID")).stateFromData(i);
		} else {
			this.block = Block.getById(nbt.getByte("Tile") & 255).stateFromData(i);
		}

		this.timeFalling = nbt.getInt("Time");
		Block block = this.block.getBlock();
		if (nbt.contains("HurtEntities", 99)) {
			this.hurtEntities = nbt.getBoolean("HurtEntities");
			this.fallHurtAmount = nbt.getFloat("FallHurtAmount");
			this.fallHurtMax = nbt.getInt("FallHurtMax");
		} else if (block == Blocks.ANVIL) {
			this.hurtEntities = true;
		}

		if (nbt.contains("DropItem", 99)) {
			this.dropping = nbt.getBoolean("DropItem");
		}

		if (nbt.contains("TileEntityData", 10)) {
			this.tileEntityData = nbt.getCompound("TileEntityData");
		}

		if (block == null || block.getDefaultState().getMaterial() == Material.AIR) {
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
		if (this.block != null) {
			Block block = this.block.getBlock();
			section.add("Immitating block ID", Block.getIdByBlock(block));
			section.add("Immitating block data", block.getData(this.block));
		}
	}

	@Nullable
	public BlockState getBlockState() {
		return this.block;
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}
}
