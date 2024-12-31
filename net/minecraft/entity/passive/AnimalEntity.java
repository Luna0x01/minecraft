package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class AnimalEntity extends PassiveEntity implements EntityCategoryProvider {
	protected Block field_11973 = Blocks.GRASS_BLOCK;
	private int loveTicks;
	private UUID field_16550;

	protected AnimalEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
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
					.method_16343(
						class_4342.field_21351,
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
	public float method_15657(BlockPos blockPos, RenderBlockView renderBlockView) {
		return renderBlockView.getBlockState(blockPos.down()).getBlock() == this.field_11973 ? 10.0F : renderBlockView.method_16356(blockPos) - 0.5F;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("InLove", this.loveTicks);
		if (this.field_16550 != null) {
			nbt.putUuid("LoveCause", this.field_16550);
		}
	}

	@Override
	public double getHeightOffset() {
		return 0.14;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.loveTicks = nbt.getInt("InLove");
		this.field_16550 = nbt.containsUuid("LoveCause") ? nbt.getUuid("LoveCause") : null;
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		return iWorld.getBlockState(blockPos.down()).getBlock() == this.field_11973 && iWorld.method_16379(blockPos, 0) > 8 && super.method_15652(iWorld, bl);
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canImmediatelyDespawn() {
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
		if (this.isBreedingItem(itemStack)) {
			if (this.age() == 0 && this.method_15741()) {
				this.eat(playerEntity, itemStack);
				this.lovePlayer(playerEntity);
				return true;
			}

			if (this.isBaby()) {
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

	public boolean method_15741() {
		return this.loveTicks <= 0;
	}

	public void lovePlayer(@Nullable PlayerEntity player) {
		this.loveTicks = 600;
		if (player != null) {
			this.field_16550 = player.getUuid();
		}

		this.world.sendEntityStatus(this, (byte)18);
	}

	public void method_15740(int i) {
		this.loveTicks = i;
	}

	@Nullable
	public ServerPlayerEntity method_15103() {
		if (this.field_16550 == null) {
			return null;
		} else {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.field_16550);
			return playerEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerEntity : null;
		}
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
					.method_16343(
						class_4342.field_21351,
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
