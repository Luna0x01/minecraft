package net.minecraft.block.entity;

import net.minecraft.class_2960;
import net.minecraft.class_3743;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ChestScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class ChestBlockEntity extends class_2737 implements class_3743, Tickable {
	private DefaultedList<ItemStack> field_15152 = DefaultedList.ofSize(27, ItemStack.EMPTY);
	protected float animationAngle;
	protected float animationAnglePrev;
	protected int viewerCount;
	private int ticksOpen;

	protected ChestBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public ChestBlockEntity() {
		this(BlockEntityType.CHEST);
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15152) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Text method_15540() {
		Text text = this.method_15541();
		return (Text)(text != null ? text : new TranslatableText("container.chest"));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_15152 = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11661(nbt)) {
			class_2960.method_13927(nbt, this.field_15152);
		}

		if (nbt.contains("CustomName", 8)) {
			this.field_18643 = Text.Serializer.deserializeText(nbt.getString("CustomName"));
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (!this.method_11663(nbt)) {
			class_2960.method_13923(nbt, this.field_15152);
		}

		Text text = this.method_15541();
		if (text != null) {
			nbt.putString("CustomName", Text.Serializer.serialize(text));
		}

		return nbt;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void tick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		this.ticksOpen++;
		if (!this.world.isClient && this.viewerCount != 0 && (this.ticksOpen + i + j + k) % 200 == 0) {
			this.viewerCount = 0;
			float f = 5.0F;

			for (PlayerEntity playerEntity : this.world
				.getEntitiesInBox(
					PlayerEntity.class,
					new Box(
						(double)((float)i - 5.0F),
						(double)((float)j - 5.0F),
						(double)((float)k - 5.0F),
						(double)((float)(i + 1) + 5.0F),
						(double)((float)(j + 1) + 5.0F),
						(double)((float)(k + 1) + 5.0F)
					)
				)) {
				if (playerEntity.openScreenHandler instanceof ChestScreenHandler) {
					Inventory inventory = ((ChestScreenHandler)playerEntity.openScreenHandler).getInventory();
					if (inventory == this || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(this)) {
						this.viewerCount++;
					}
				}
			}
		}

		this.animationAnglePrev = this.animationAngle;
		float g = 0.1F;
		if (this.viewerCount > 0 && this.animationAngle == 0.0F) {
			this.method_16794(Sounds.BLOCK_CHEST_OPEN);
		}

		if (this.viewerCount == 0 && this.animationAngle > 0.0F || this.viewerCount > 0 && this.animationAngle < 1.0F) {
			float h = this.animationAngle;
			if (this.viewerCount > 0) {
				this.animationAngle += 0.1F;
			} else {
				this.animationAngle -= 0.1F;
			}

			if (this.animationAngle > 1.0F) {
				this.animationAngle = 1.0F;
			}

			float l = 0.5F;
			if (this.animationAngle < 0.5F && h >= 0.5F) {
				this.method_16794(Sounds.BLOCK_CHEST_CLOSE);
			}

			if (this.animationAngle < 0.0F) {
				this.animationAngle = 0.0F;
			}
		}
	}

	private void method_16794(Sound sound) {
		ChestType chestType = this.method_16783().getProperty(ChestBlock.CHEST_TYPE);
		if (chestType != ChestType.LEFT) {
			double d = (double)this.pos.getX() + 0.5;
			double e = (double)this.pos.getY() + 0.5;
			double f = (double)this.pos.getZ() + 0.5;
			if (chestType == ChestType.RIGHT) {
				Direction direction = ChestBlock.getFacing(this.method_16783());
				d += (double)direction.getOffsetX() * 0.5;
				f += (double)direction.getOffsetZ() * 0.5;
			}

			this.world.playSound(null, d, e, f, sound, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		if (code == 1) {
			this.viewerCount = data;
			return true;
		} else {
			return super.onBlockAction(code, data);
		}
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			this.method_16795();
		}
	}

	@Override
	public void onInvClose(PlayerEntity player) {
		if (!player.isSpectator()) {
			this.viewerCount--;
			this.method_16795();
		}
	}

	protected void method_16795() {
		Block block = this.method_16783().getBlock();
		if (block instanceof ChestBlock) {
			this.world.addBlockAction(this.pos, block, 1, this.viewerCount);
			this.world.updateNeighborsAlways(this.pos, block);
		}
	}

	@Override
	public String getId() {
		return "minecraft:chest";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		this.method_11662(player);
		return new ChestScreenHandler(inventory, this, player);
	}

	@Override
	protected DefaultedList<ItemStack> method_13730() {
		return this.field_15152;
	}

	@Override
	protected void method_16834(DefaultedList<ItemStack> defaultedList) {
		this.field_15152 = defaultedList;
	}

	@Override
	public float method_16830(float f) {
		return this.animationAnglePrev + (this.animationAngle - this.animationAnglePrev) * f;
	}

	public static int method_16792(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		if (blockState.getBlock().hasBlockEntity()) {
			BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				return ((ChestBlockEntity)blockEntity).viewerCount;
			}
		}

		return 0;
	}

	public static void method_16793(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
		DefaultedList<ItemStack> defaultedList = chestBlockEntity.method_13730();
		chestBlockEntity.method_16834(chestBlockEntity2.method_13730());
		chestBlockEntity2.method_16834(defaultedList);
	}
}
