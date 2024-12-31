package net.minecraft.entity.decoration;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity extends AbstractDecorationEntity {
	private static final Logger field_16991 = LogManager.getLogger();
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Integer> ROTATION = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private float setDropChance = 1.0F;

	public ItemFrameEntity(World world) {
		super(EntityType.ITEM_FRAME, world);
	}

	public ItemFrameEntity(World world, BlockPos blockPos, Direction direction) {
		super(EntityType.ITEM_FRAME, world, blockPos);
		this.setDirection(direction);
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
		this.getDataTracker().startTracking(ROTATION, 0);
	}

	@Override
	protected void setDirection(Direction direction) {
		Validate.notNull(direction);
		this.direction = direction;
		if (direction.getAxis().isHorizontal()) {
			this.pitch = 0.0F;
			this.yaw = (float)(this.direction.getHorizontal() * 90);
		} else {
			this.pitch = (float)(-90 * direction.getAxisDirection().offset());
			this.yaw = 0.0F;
		}

		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		this.updateAttachmentPosition();
	}

	@Override
	protected void updateAttachmentPosition() {
		if (this.direction != null) {
			double d = 0.46875;
			this.x = (double)this.pos.getX() + 0.5 - (double)this.direction.getOffsetX() * 0.46875;
			this.y = (double)this.pos.getY() + 0.5 - (double)this.direction.getOffsetY() * 0.46875;
			this.z = (double)this.pos.getZ() + 0.5 - (double)this.direction.getOffsetZ() * 0.46875;
			double e = (double)this.getWidth();
			double f = (double)this.getHeight();
			double g = (double)this.getWidth();
			Direction.Axis axis = this.direction.getAxis();
			switch (axis) {
				case X:
					e = 1.0;
					break;
				case Y:
					f = 1.0;
					break;
				case Z:
					g = 1.0;
			}

			e /= 32.0;
			f /= 32.0;
			g /= 32.0;
			this.setBoundingBox(new Box(this.x - e, this.y - f, this.z - g, this.x + e, this.y + f, this.z + g));
		}
	}

	@Override
	public boolean isPosValid() {
		if (!this.world.method_16387(this, this.getBoundingBox())) {
			return false;
		} else {
			BlockState blockState = this.world.getBlockState(this.pos.offset(this.direction.getOpposite()));
			return blockState.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGateBlock(blockState)
				? this.world.method_16288(this, this.getBoundingBox(), field_16989).isEmpty()
				: false;
		}
	}

	@Override
	public float getTargetingMargin() {
		return 0.0F;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!source.isExplosive() && !this.getHeldItemStack().isEmpty()) {
			if (!this.world.isClient) {
				this.dropHeldStack(source.getAttacker(), false);
				this.playSound(Sounds.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.damage(source, amount);
		}
	}

	@Override
	public int getWidth() {
		return 12;
	}

	@Override
	public int getHeight() {
		return 12;
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = 16.0;
		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		this.playSound(Sounds.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
		this.dropHeldStack(entity, true);
	}

	@Override
	public void onPlace() {
		this.playSound(Sounds.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
	}

	public void dropHeldStack(@Nullable Entity entity, boolean alwaysDrop) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemStack = this.getHeldItemStack();
			this.setHeldItemStack(ItemStack.EMPTY);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					this.removeFromFrame(itemStack);
					return;
				}
			}

			if (alwaysDrop) {
				this.method_15560(Items.ITEM_FRAME);
			}

			if (!itemStack.isEmpty() && this.random.nextFloat() < this.setDropChance) {
				itemStack = itemStack.copy();
				this.removeFromFrame(itemStack);
				this.method_15571(itemStack);
			}
		}
	}

	private void removeFromFrame(ItemStack map) {
		if (map.getItem() == Items.FILLED_MAP) {
			MapState mapState = FilledMapItem.method_16111(map, this.world);
			mapState.method_17935(this.pos, this.getEntityId());
		}

		map.setInItemFrame(null);
	}

	public ItemStack getHeldItemStack() {
		return this.getDataTracker().get(ITEM_STACK);
	}

	public void setHeldItemStack(ItemStack stack) {
		this.setHeldItemStack(stack, true);
	}

	private void setHeldItemStack(ItemStack stack, boolean update) {
		if (!stack.isEmpty()) {
			stack = stack.copy();
			stack.setCount(1);
			stack.setInItemFrame(this);
		}

		this.getDataTracker().set(ITEM_STACK, stack);
		if (!stack.isEmpty()) {
			this.playSound(Sounds.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
		}

		if (update && this.pos != null) {
			this.world.updateHorizontalAdjacent(this.pos, Blocks.AIR);
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (data.equals(ITEM_STACK)) {
			ItemStack itemStack = this.getHeldItemStack();
			if (!itemStack.isEmpty() && itemStack.getItemFrame() != this) {
				itemStack.setInItemFrame(this);
			}
		}
	}

	public int rotation() {
		return this.getDataTracker().get(ROTATION);
	}

	public void setRotation(int value) {
		this.setRotation(value, true);
	}

	private void setRotation(int value, boolean update) {
		this.getDataTracker().set(ROTATION, value % 8);
		if (update && this.pos != null) {
			this.world.updateHorizontalAdjacent(this.pos, Blocks.AIR);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (!this.getHeldItemStack().isEmpty()) {
			nbt.put("Item", this.getHeldItemStack().toNbt(new NbtCompound()));
			nbt.putByte("ItemRotation", (byte)this.rotation());
			nbt.putFloat("ItemDropChance", this.setDropChance);
		}

		nbt.putByte("Facing", (byte)this.direction.getId());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		NbtCompound nbtCompound = nbt.getCompound("Item");
		if (nbtCompound != null && !nbtCompound.isEmpty()) {
			ItemStack itemStack = ItemStack.from(nbtCompound);
			if (itemStack.isEmpty()) {
				field_16991.warn("Unable to load item from: {}", nbtCompound);
			}

			this.setHeldItemStack(itemStack, false);
			this.setRotation(nbt.getByte("ItemRotation"), false);
			if (nbt.contains("ItemDropChance", 99)) {
				this.setDropChance = nbt.getFloat("ItemDropChance");
			}
		}

		this.setDirection(Direction.getById(nbt.getByte("Facing")));
	}

	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!this.world.isClient) {
			if (this.getHeldItemStack().isEmpty()) {
				if (!itemStack.isEmpty()) {
					this.setHeldItemStack(itemStack);
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}
				}
			} else {
				this.playSound(Sounds.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
				this.setRotation(this.rotation() + 1);
			}
		}

		return true;
	}

	public int getComparatorPower() {
		return this.getHeldItemStack().isEmpty() ? 0 : this.rotation() % 8 + 1;
	}
}
