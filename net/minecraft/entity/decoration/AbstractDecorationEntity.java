package net.minecraft.entity.decoration;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class AbstractDecorationEntity extends Entity {
	private int obstructionCheckCounter;
	protected BlockPos pos;
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
		this.prevYaw = this.yaw = (float)(this.direction.getHorizontal() * 90);
		this.updateAttachmentPosition();
	}

	private void updateAttachmentPosition() {
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

			for (int k = 0; k < i; k++) {
				for (int l = 0; l < j; l++) {
					BlockPos blockPos2 = blockPos.offset(direction, k).up(l);
					Block block = this.world.getBlockState(blockPos2).getBlock();
					if (!block.getMaterial().isSolid() && !AbstractRedstoneGateBlock.isRedstoneGate(block)) {
						return false;
					}
				}
			}

			for (Entity entity : this.world.getEntitiesIn(this, this.getBoundingBox())) {
				if (entity instanceof AbstractDecorationEntity) {
					return false;
				}
			}

			return true;
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
	public void move(double velocityX, double velocityY, double velocityZ) {
		if (!this.world.isClient && !this.removed && velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ > 0.0) {
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
		nbt.putInt("TileX", this.getTilePos().getX());
		nbt.putInt("TileY", this.getTilePos().getY());
		nbt.putInt("TileZ", this.getTilePos().getZ());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.pos = new BlockPos(nbt.getInt("TileX"), nbt.getInt("TileY"), nbt.getInt("TileZ"));
		Direction direction;
		if (nbt.contains("Direction", 99)) {
			direction = Direction.fromHorizontal(nbt.getByte("Direction"));
			this.pos = this.pos.offset(direction);
		} else if (nbt.contains("Facing", 99)) {
			direction = Direction.fromHorizontal(nbt.getByte("Facing"));
		} else {
			direction = Direction.fromHorizontal(nbt.getByte("Dir"));
		}

		this.setDirection(direction);
	}

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void onBreak(Entity entity);

	@Override
	protected boolean shouldSetPositionOnLoad() {
		return false;
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		BlockPos blockPos = this.pos;
		this.pos = new BlockPos(x, y, z);
		if (!this.pos.equals(blockPos)) {
			this.updateAttachmentPosition();
			this.velocityDirty = true;
		}
	}

	public BlockPos getTilePos() {
		return this.pos;
	}
}
