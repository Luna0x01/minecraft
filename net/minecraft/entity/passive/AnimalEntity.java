package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class AnimalEntity extends PassiveEntity implements EntityCategoryProvider {
	protected Block field_11973 = Blocks.GRASS;
	private int loveTicks;
	private PlayerEntity lovingPlayer;

	public AnimalEntity(World world) {
		super(world);
	}

	@Override
	protected void mobTick() {
		if (this.age() != 0) {
			this.loveTicks = 0;
		}

		super.mobTick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.age() != 0) {
			this.loveTicks = 0;
		}

		if (this.loveTicks > 0) {
			this.loveTicks--;
			if (this.loveTicks % 10 == 0) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world
					.addParticle(
						ParticleType.HEART,
						this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
						this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						d,
						e,
						f
					);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.loveTicks = 0;
			return super.damage(source, amount);
		}
	}

	@Override
	public float getPathfindingFavor(BlockPos pos) {
		return this.world.getBlockState(pos.down()).getBlock() == this.field_11973 ? 10.0F : this.world.getBrightness(pos) - 0.5F;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("InLove", this.loveTicks);
	}

	@Override
	public double getHeightOffset() {
		return 0.14;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.loveTicks = nbt.getInt("InLove");
	}

	@Override
	public boolean canSpawn() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		return this.world.getBlockState(blockPos.down()).getBlock() == this.field_11973 && this.world.getLightLevel(blockPos) > 8 && super.canSpawn();
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	protected boolean canImmediatelyDespawn() {
		return false;
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		return 1 + this.world.random.nextInt(3);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Items.WHEAT;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!itemStack.isEmpty()) {
			if (this.isBreedingItem(itemStack) && this.age() == 0 && this.loveTicks <= 0) {
				this.eat(playerEntity, itemStack);
				this.lovePlayer(playerEntity);
				return true;
			}

			if (this.isBaby() && this.isBreedingItem(itemStack)) {
				this.eat(playerEntity, itemStack);
				this.method_10925((int)((float)(-this.age() / 20) * 0.1F), true);
				return true;
			}
		}

		return super.interactMob(playerEntity, hand);
	}

	protected void eat(PlayerEntity player, ItemStack stack) {
		if (!player.abilities.creativeMode) {
			stack.decrement(1);
		}
	}

	public void lovePlayer(PlayerEntity player) {
		this.loveTicks = 600;
		this.lovingPlayer = player;
		this.world.sendEntityStatus(this, (byte)18);
	}

	public PlayerEntity getLovingPlayer() {
		return this.lovingPlayer;
	}

	public boolean isInLove() {
		return this.loveTicks > 0;
	}

	public void resetLoveTicks() {
		this.loveTicks = 0;
	}

	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return other.getClass() != this.getClass() ? false : this.isInLove() && other.isInLove();
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 18) {
			for (int i = 0; i < 7; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world
					.addParticle(
						ParticleType.HEART,
						this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
						this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						d,
						e,
						f
					);
			}
		} else {
			super.handleStatus(status);
		}
	}
}
