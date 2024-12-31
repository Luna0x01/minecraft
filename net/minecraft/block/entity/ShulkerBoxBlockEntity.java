package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.sound.Sounds;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShapes;

public class ShulkerBoxBlockEntity extends class_2737 implements SidedInventory, Tickable {
	private static final int[] field_15158 = IntStream.range(0, 27).toArray();
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private boolean field_15160;
	private int field_15161;
	private ShulkerBoxBlockEntity.ShulkerBlockState state = ShulkerBoxBlockEntity.ShulkerBlockState.CLOSED;
	private float field_15163;
	private float field_15164;
	private DyeColor color;
	private boolean field_18644;
	private boolean field_15166;

	public ShulkerBoxBlockEntity(@Nullable DyeColor dyeColor) {
		super(BlockEntityType.SHULKER_BOX);
		this.color = dyeColor;
	}

	public ShulkerBoxBlockEntity() {
		this(null);
		this.field_18644 = true;
	}

	@Override
	public void tick() {
		this.method_13742();
		if (this.state == ShulkerBoxBlockEntity.ShulkerBlockState.OPENING || this.state == ShulkerBoxBlockEntity.ShulkerBlockState.CLOSING) {
			this.method_13733();
		}
	}

	protected void method_13742() {
		this.field_15164 = this.field_15163;
		switch (this.state) {
			case CLOSED:
				this.field_15163 = 0.0F;
				break;
			case OPENING:
				this.field_15163 += 0.1F;
				if (this.field_15163 >= 1.0F) {
					this.method_13733();
					this.state = ShulkerBoxBlockEntity.ShulkerBlockState.OPENED;
					this.field_15163 = 1.0F;
				}
				break;
			case CLOSING:
				this.field_15163 -= 0.1F;
				if (this.field_15163 <= 0.0F) {
					this.state = ShulkerBoxBlockEntity.ShulkerBlockState.CLOSED;
					this.field_15163 = 0.0F;
				}
				break;
			case OPENED:
				this.field_15163 = 1.0F;
		}
	}

	public ShulkerBoxBlockEntity.ShulkerBlockState method_13743() {
		return this.state;
	}

	public Box method_13735(BlockState state) {
		return this.method_13738(state.getProperty(ShulkerBoxBlock.field_18474));
	}

	public Box method_13738(Direction direction) {
		return VoxelShapes.matchesAnywhere()
			.getBoundingBox()
			.stretch(
				(double)(0.5F * this.method_13734(1.0F) * (float)direction.getOffsetX()),
				(double)(0.5F * this.method_13734(1.0F) * (float)direction.getOffsetY()),
				(double)(0.5F * this.method_13734(1.0F) * (float)direction.getOffsetZ())
			);
	}

	private Box method_13739(Direction direction) {
		Direction direction2 = direction.getOpposite();
		return this.method_13738(direction).shrink((double)direction2.getOffsetX(), (double)direction2.getOffsetY(), (double)direction2.getOffsetZ());
	}

	private void method_13733() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() instanceof ShulkerBoxBlock) {
			Direction direction = blockState.getProperty(ShulkerBoxBlock.field_18474);
			Box box = this.method_13739(direction).offset(this.pos);
			List<Entity> list = this.world.getEntities(null, box);
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
						double d = 0.0;
						double e = 0.0;
						double f = 0.0;
						Box box2 = entity.getBoundingBox();
						switch (direction.getAxis()) {
							case X:
								if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
									d = box.maxX - box2.minX;
								} else {
									d = box2.maxX - box.minX;
								}

								d += 0.01;
								break;
							case Y:
								if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
									e = box.maxY - box2.minY;
								} else {
									e = box2.maxY - box.minY;
								}

								e += 0.01;
								break;
							case Z:
								if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
									f = box.maxZ - box2.minZ;
								} else {
									f = box2.maxZ - box.minZ;
								}

								f += 0.01;
						}

						entity.move(MovementType.SHULKER_BOX, d * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), f * (double)direction.getOffsetZ());
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
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		if (code == 1) {
			this.field_15161 = data;
			if (data == 0) {
				this.state = ShulkerBoxBlockEntity.ShulkerBlockState.CLOSING;
			}

			if (data == 1) {
				this.state = ShulkerBoxBlockEntity.ShulkerBlockState.OPENING;
			}

			return true;
		} else {
			return super.onBlockAction(code, data);
		}
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.field_15161 < 0) {
				this.field_15161 = 0;
			}

			this.field_15161++;
			this.world.addBlockAction(this.pos, this.method_16783().getBlock(), 1, this.field_15161);
			if (this.field_15161 == 1) {
				this.world.playSound(null, this.pos, Sounds.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void onInvClose(PlayerEntity player) {
		if (!player.isSpectator()) {
			this.field_15161--;
			this.world.addBlockAction(this.pos, this.method_16783().getBlock(), 1, this.field_15161);
			if (this.field_15161 <= 0) {
				this.world.playSound(null, this.pos, Sounds.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new ShulkerBoxScreenHandler(inventory, this, player);
	}

	@Override
	public String getId() {
		return "minecraft:shulker_box";
	}

	@Override
	public Text method_15540() {
		Text text = this.method_15541();
		return (Text)(text != null ? text : new TranslatableText("container.shulkerBox"));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.method_13740(nbt);
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		return this.method_13741(nbt);
	}

	public void method_13740(NbtCompound nbtCompound) {
		this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11661(nbtCompound) && nbtCompound.contains("Items", 9)) {
			class_2960.method_13927(nbtCompound, this.inventory);
		}

		if (nbtCompound.contains("CustomName", 8)) {
			this.field_18643 = Text.Serializer.deserializeText(nbtCompound.getString("CustomName"));
		}
	}

	public NbtCompound method_13741(NbtCompound nbtCompound) {
		if (!this.method_11663(nbtCompound)) {
			class_2960.method_13924(nbtCompound, this.inventory, false);
		}

		Text text = this.method_15541();
		if (text != null) {
			nbtCompound.putString("CustomName", Text.Serializer.serialize(text));
		}

		if (!nbtCompound.contains("Lock") && this.hasLock()) {
			this.getLock().toNbt(nbtCompound);
		}

		return nbtCompound;
	}

	@Override
	protected DefaultedList<ItemStack> method_13730() {
		return this.inventory;
	}

	@Override
	protected void method_16834(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return field_15158;
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir) {
		return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public void clear() {
		this.field_15160 = true;
		super.clear();
	}

	public boolean method_13744() {
		return this.field_15160;
	}

	public float method_13734(float f) {
		return this.field_15164 + (this.field_15163 - this.field_15164) * f;
	}

	public DyeColor getColor() {
		if (this.field_18644) {
			this.color = ShulkerBoxBlock.colorOf(this.method_16783().getBlock());
			this.field_18644 = false;
		}

		return this.color;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 10, this.getUpdatePacketContent());
	}

	public boolean method_13731() {
		return this.field_15166;
	}

	public void method_13737(boolean bl) {
		this.field_15166 = bl;
	}

	public boolean method_13732() {
		return !this.method_13731() || !this.isEmpty() || this.hasCustomName() || this.field_12852 != null;
	}

	public static enum ShulkerBlockState {
		CLOSED,
		OPENING,
		OPENED,
		CLOSING;
	}
}
