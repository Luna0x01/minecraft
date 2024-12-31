package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.container.Container;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory, Tickable {
	private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private int viewerCount;
	private ShulkerBoxBlockEntity.AnimationStage animationStage = ShulkerBoxBlockEntity.AnimationStage.field_12065;
	private float animationProgress;
	private float prevAnimationProgress;
	@Nullable
	private DyeColor cachedColor;
	private boolean cachedColorUpdateNeeded;

	public ShulkerBoxBlockEntity(@Nullable DyeColor dyeColor) {
		super(BlockEntityType.field_11896);
		this.cachedColor = dyeColor;
	}

	public ShulkerBoxBlockEntity() {
		this(null);
		this.cachedColorUpdateNeeded = true;
	}

	@Override
	public void tick() {
		this.updateAnimation();
		if (this.animationStage == ShulkerBoxBlockEntity.AnimationStage.field_12066 || this.animationStage == ShulkerBoxBlockEntity.AnimationStage.field_12064) {
			this.pushEntities();
		}
	}

	protected void updateAnimation() {
		this.prevAnimationProgress = this.animationProgress;
		switch (this.animationStage) {
			case field_12065:
				this.animationProgress = 0.0F;
				break;
			case field_12066:
				this.animationProgress += 0.1F;
				if (this.animationProgress >= 1.0F) {
					this.pushEntities();
					this.animationStage = ShulkerBoxBlockEntity.AnimationStage.field_12063;
					this.animationProgress = 1.0F;
					this.updateNeighborStates();
				}
				break;
			case field_12064:
				this.animationProgress -= 0.1F;
				if (this.animationProgress <= 0.0F) {
					this.animationStage = ShulkerBoxBlockEntity.AnimationStage.field_12065;
					this.animationProgress = 0.0F;
					this.updateNeighborStates();
				}
				break;
			case field_12063:
				this.animationProgress = 1.0F;
		}
	}

	public ShulkerBoxBlockEntity.AnimationStage getAnimationStage() {
		return this.animationStage;
	}

	public Box getBoundingBox(BlockState blockState) {
		return this.getBoundingBox(blockState.get(ShulkerBoxBlock.FACING));
	}

	public Box getBoundingBox(Direction direction) {
		float f = this.getAnimationProgress(1.0F);
		return VoxelShapes.fullCube()
			.getBoundingBox()
			.stretch(
				(double)(0.5F * f * (float)direction.getOffsetX()), (double)(0.5F * f * (float)direction.getOffsetY()), (double)(0.5F * f * (float)direction.getOffsetZ())
			);
	}

	private Box getCollisionBox(Direction direction) {
		Direction direction2 = direction.getOpposite();
		return this.getBoundingBox(direction).shrink((double)direction2.getOffsetX(), (double)direction2.getOffsetY(), (double)direction2.getOffsetZ());
	}

	private void pushEntities() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() instanceof ShulkerBoxBlock) {
			Direction direction = blockState.get(ShulkerBoxBlock.FACING);
			Box box = this.getCollisionBox(direction).offset(this.pos);
			List<Entity> list = this.world.getEntities(null, box);
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					if (entity.getPistonBehavior() != PistonBehavior.field_15975) {
						double d = 0.0;
						double e = 0.0;
						double f = 0.0;
						Box box2 = entity.getBoundingBox();
						switch (direction.getAxis()) {
							case field_11048:
								if (direction.getDirection() == Direction.AxisDirection.field_11056) {
									d = box.x2 - box2.x1;
								} else {
									d = box2.x2 - box.x1;
								}

								d += 0.01;
								break;
							case field_11052:
								if (direction.getDirection() == Direction.AxisDirection.field_11056) {
									e = box.y2 - box2.y1;
								} else {
									e = box2.y2 - box.y1;
								}

								e += 0.01;
								break;
							case field_11051:
								if (direction.getDirection() == Direction.AxisDirection.field_11056) {
									f = box.z2 - box2.z1;
								} else {
									f = box2.z2 - box.z1;
								}

								f += 0.01;
						}

						entity.move(
							MovementType.field_6306, new Vec3d(d * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), f * (double)direction.getOffsetZ())
						);
					}
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.inventory.size();
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.viewerCount = j;
			if (j == 0) {
				this.animationStage = ShulkerBoxBlockEntity.AnimationStage.field_12064;
				this.updateNeighborStates();
			}

			if (j == 1) {
				this.animationStage = ShulkerBoxBlockEntity.AnimationStage.field_12066;
				this.updateNeighborStates();
			}

			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	private void updateNeighborStates() {
		this.getCachedState().updateNeighborStates(this.getWorld(), this.getPos(), 3);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			this.world.addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
			if (this.viewerCount == 1) {
				this.world.playSound(null, this.pos, SoundEvents.field_14825, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.viewerCount--;
			this.world.addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
			if (this.viewerCount <= 0) {
				this.world.playSound(null, this.pos, SoundEvents.field_14751, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.shulkerBox");
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.deserializeInventory(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		return this.serializeInventory(compoundTag);
	}

	public void deserializeInventory(CompoundTag compoundTag) {
		this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag) && compoundTag.contains("Items", 9)) {
			Inventories.fromTag(compoundTag, this.inventory);
		}
	}

	public CompoundTag serializeInventory(CompoundTag compoundTag) {
		if (!this.serializeLootTable(compoundTag)) {
			Inventories.toTag(compoundTag, this.inventory, false);
		}

		return compoundTag;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
	}

	@Override
	public int[] getInvAvailableSlots(Direction direction) {
		return AVAILABLE_SLOTS;
	}

	@Override
	public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
		return !(Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock);
	}

	@Override
	public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
		return true;
	}

	public float getAnimationProgress(float f) {
		return MathHelper.lerp(f, this.prevAnimationProgress, this.animationProgress);
	}

	@Nullable
	public DyeColor getColor() {
		if (this.cachedColorUpdateNeeded) {
			this.cachedColor = ShulkerBoxBlock.getColor(this.getCachedState().getBlock());
			this.cachedColorUpdateNeeded = false;
		}

		return this.cachedColor;
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new ShulkerBoxContainer(i, playerInventory, this);
	}

	public static enum AnimationStage {
		field_12065,
		field_12066,
		field_12063,
		field_12064;
	}
}
