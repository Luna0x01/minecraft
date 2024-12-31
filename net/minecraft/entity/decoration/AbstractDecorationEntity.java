package net.minecraft.entity.decoration;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class AbstractDecorationEntity extends Entity {
	private static final Predicate<Entity> IS_DECORATION = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof AbstractDecorationEntity;
		}
	};
	private int obstructionCheckCounter;
	protected BlockPos pos;
	@Nullable
	public Direction direction;

	public AbstractDecorationEntity(World world) {
		super(world);
		this.setBounds(0.5F, 0.5F);
	}

	public AbstractDecorationEntity(World world, BlockPos blockPos) {
		this(world);
		this.pos = blockPos;
	}

	@Override
	protected void initDataTracker() {
	}

	protected void setDirection(Direction direction) {
		Validate.notNull(direction);
		Validate.isTrue(direction.getAxis().isHorizontal());
		this.direction = direction;
		this.yaw = (float)(this.direction.getHorizontal() * 90);
		this.prevYaw = this.yaw;
		this.updateAttachmentPosition();
	}

	protected void updateAttachmentPosition() {
		if (this.direction != null) {
			double d = (double)this.pos.getX() + 0.5;
			double e = (double)this.pos.getY() + 0.5;
			double f = (double)this.pos.getZ() + 0.5;
			double g = 0.46875;
			double h = this.method_11146(this.getWidth());
			double i = this.method_11146(this.getHeight());
			d -= (double)this.direction.getOffsetX() * 0.46875;
			f -= (double)this.direction.getOffsetZ() * 0.46875;
			e += i;
			Direction direction = this.direction.rotateYCounterclockwise();
			d += h * (double)direction.getOffsetX();
			f += h * (double)direction.getOffsetZ();
			this.x = d;
			this.y = e;
			this.z = f;
			double j = (double)this.getWidth();
			double k = (double)this.getHeight();
			double l = (double)this.getWidth();
			if (this.direction.getAxis() == Direction.Axis.Z) {
				l = 1.0;
			} else {
				j = 1.0;
			}

			j /= 32.0;
			k /= 32.0;
			l /= 32.0;
			this.setBoundingBox(new Box(d - j, e - k, f - l, d + j, e + k, f + l));
		}
	}

	private double method_11146(int i) {
		return i % 32 == 0 ? 0.5 : 0.0;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.obstructionCheckCounter++ == 100 && !this.world.isClient) {
			this.obstructionCheckCounter = 0;
			if (!this.removed && !this.isPosValid()) {
				this.remove();
				this.onBreak(null);
			}
		}
	}

	public boolean isPosValid() {
		if (!this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()) {
			return false;
		} else {
			int i = Math.max(1, this.getWidth() / 16);
			int j = Math.max(1, this.getHeight() / 16);
			BlockPos blockPos = this.pos.offset(this.direction.getOpposite());
			Direction direction = this.direction.rotateYCounterclockwise();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = 0; k < i; k++) {
				for (int l = 0; l < j; l++) {
					int m = (i - 1) / -2;
					int n = (j - 1) / -2;
					mutable.set(blockPos).move(direction, k + m).move(Direction.UP, l + n);
					BlockState blockState = this.world.getBlockState(mutable);
					if (!blockState.getMaterial().isSolid() && !AbstractRedstoneGateBlock.isRedstoneGateBlock(blockState)) {
						return false;
					}
				}
			}

			return this.world.getEntitiesIn(this, this.getBoundingBox(), IS_DECORATION).isEmpty();
		}
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean handleAttack(Entity attacker) {
		return attacker instanceof PlayerEntity ? this.damage(DamageSource.player((PlayerEntity)attacker), 0.0F) : false;
	}

	@Override
	public Direction getHorizontalDirection() {
		return this.direction;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.removed && !this.world.isClient) {
				this.remove();
				this.scheduleVelocityUpdate();
				this.onBreak(source.getAttacker());
			}

			return true;
		}
	}

	@Override
	public void move(MovementType type, double movementX, double movementY, double movementZ) {
		if (!this.world.isClient && !this.removed && movementX * movementX + movementY * movementY + movementZ * movementZ > 0.0) {
			this.remove();
			this.onBreak(null);
		}
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		if (!this.world.isClient && !this.removed && x * x + y * y + z * z > 0.0) {
			this.remove();
			this.onBreak(null);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putByte("Facing", (byte)this.direction.getHorizontal());
		BlockPos blockPos = this.getTilePos();
		nbt.putInt("TileX", blockPos.getX());
		nbt.putInt("TileY", blockPos.getY());
		nbt.putInt("TileZ", blockPos.getZ());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.pos = new BlockPos(nbt.getInt("TileX"), nbt.getInt("TileY"), nbt.getInt("TileZ"));
		this.setDirection(Direction.fromHorizontal(nbt.getByte("Facing")));
	}

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void onBreak(@Nullable Entity entity);

	public abstract void onPlace();

	@Override
	public ItemEntity dropItem(ItemStack stack, float yOffset) {
		ItemEntity itemEntity = new ItemEntity(
			this.world,
			this.x + (double)((float)this.direction.getOffsetX() * 0.15F),
			this.y + (double)yOffset,
			this.z + (double)((float)this.direction.getOffsetZ() * 0.15F),
			stack
		);
		itemEntity.setToDefaultPickupDelay();
		this.world.spawnEntity(itemEntity);
		return itemEntity;
	}

	@Override
	protected boolean shouldSetPositionOnLoad() {
		return false;
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		this.pos = new BlockPos(x, y, z);
		this.updateAttachmentPosition();
		this.velocityDirty = true;
	}

	public BlockPos getTilePos() {
		return this.pos;
	}

	@Override
	public float applyRotation(BlockRotation rotation) {
		if (this.direction != null && this.direction.getAxis() != Direction.Axis.Y) {
			switch (rotation) {
				case CLOCKWISE_180:
					this.direction = this.direction.getOpposite();
					break;
				case COUNTERCLOCKWISE_90:
					this.direction = this.direction.rotateYCounterclockwise();
					break;
				case CLOCKWISE_90:
					this.direction = this.direction.rotateYClockwise();
			}
		}

		float f = MathHelper.wrapDegrees(this.yaw);
		switch (rotation) {
			case CLOCKWISE_180:
				return f + 180.0F;
			case COUNTERCLOCKWISE_90:
				return f + 90.0F;
			case CLOCKWISE_90:
				return f + 270.0F;
			default:
				return f;
		}
	}

	@Override
	public float applyMirror(BlockMirror mirror) {
		return this.applyRotation(mirror.getRotation(this.direction));
	}

	@Override
	public void onLightningStrike(LightningBoltEntity lightning) {
	}
}
