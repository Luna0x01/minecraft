package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ShulkerBoxBlock extends BlockWithEntity {
	public static final EnumProperty<Direction> field_18474 = FacingBlock.FACING;
	@Nullable
	private final DyeColor color;

	public ShulkerBoxBlock(@Nullable DyeColor dyeColor, Block.Builder builder) {
		super(builder);
		this.color = dyeColor;
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18474, Direction.UP));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ShulkerBoxBlockEntity(this.color);
	}

	@Override
	public boolean method_13703(BlockState state) {
		return true;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean method_13704(BlockState state) {
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else if (player.isSpectator()) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				Direction direction2 = state.getProperty(field_18474);
				boolean bl;
				if (((ShulkerBoxBlockEntity)blockEntity).method_13743() == ShulkerBoxBlockEntity.ShulkerBlockState.CLOSED) {
					Box box = VoxelShapes.matchesAnywhere()
						.getBoundingBox()
						.stretch(
							(double)(0.5F * (float)direction2.getOffsetX()), (double)(0.5F * (float)direction2.getOffsetY()), (double)(0.5F * (float)direction2.getOffsetZ())
						)
						.shrink((double)direction2.getOffsetX(), (double)direction2.getOffsetY(), (double)direction2.getOffsetZ());
					bl = world.method_16387(null, box.offset(pos.offset(direction2)));
				} else {
					bl = true;
				}

				if (bl) {
					player.method_15928(Stats.OPEN_SHULKER_BOX);
					player.openInventory((Inventory)blockEntity);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(field_18474, context.method_16151());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18474);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity) {
			ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)world.getBlockEntity(pos);
			shulkerBoxBlockEntity.method_13737(player.abilities.creativeMode);
			shulkerBoxBlockEntity.method_11662(player);
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				((ShulkerBoxBlockEntity)blockEntity).method_16835(itemStack.getName());
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
				if (!shulkerBoxBlockEntity.method_13744() && shulkerBoxBlockEntity.method_13732()) {
					ItemStack itemStack = new ItemStack(this);
					itemStack.getOrCreateNbt().put("BlockEntityTag", ((ShulkerBoxBlockEntity)blockEntity).method_13741(new NbtCompound()));
					if (shulkerBoxBlockEntity.hasCustomName()) {
						itemStack.setCustomName(shulkerBoxBlockEntity.method_15541());
						shulkerBoxBlockEntity.method_16835(null);
					}

					onBlockBreak(world, pos, itemStack);
				}

				world.updateHorizontalAdjacent(pos, state.getBlock());
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public void method_16564(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
		super.method_16564(itemStack, blockView, list, tooltipContext);
		NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
		if (nbtCompound != null) {
			if (nbtCompound.contains("LootTable", 8)) {
				list.add(new LiteralText("???????"));
			}

			if (nbtCompound.contains("Items", 9)) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
				class_2960.method_13927(nbtCompound, defaultedList);
				int i = 0;
				int j = 0;

				for (ItemStack itemStack2 : defaultedList) {
					if (!itemStack2.isEmpty()) {
						j++;
						if (i <= 4) {
							i++;
							Text text = itemStack2.getName().method_20177();
							text.append(" x").append(String.valueOf(itemStack2.getCount()));
							list.add(text);
						}
					}
				}

				if (j - i > 0) {
					list.add(new TranslatableText("container.shulkerBox.more", j - i).formatted(Formatting.ITALIC));
				}
			}
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof ShulkerBoxBlockEntity
			? VoxelShapes.method_18049(((ShulkerBoxBlockEntity)blockEntity).method_13735(state))
			: VoxelShapes.matchesAnywhere();
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput((Inventory)world.getBlockEntity(pos));
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		ItemStack itemStack = super.getPickBlock(world, pos, state);
		ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)world.getBlockEntity(pos);
		NbtCompound nbtCompound = shulkerBoxBlockEntity.method_13741(new NbtCompound());
		if (!nbtCompound.isEmpty()) {
			itemStack.addNbt("BlockEntityTag", nbtCompound);
		}

		return itemStack;
	}

	public static DyeColor colorOf(Item item) {
		return colorOf(Block.getBlockFromItem(item));
	}

	public static DyeColor colorOf(Block block) {
		return block instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)block).getColor() : null;
	}

	public static Block of(DyeColor color) {
		if (color == null) {
			return Blocks.SHULKER_BOX;
		} else {
			switch (color) {
				case WHITE:
					return Blocks.WHITE_SHULKER_BOX;
				case ORANGE:
					return Blocks.ORANGE_SHULKER_BOX;
				case MAGENTA:
					return Blocks.MAGENTA_SHULKER_BOX;
				case LIGHT_BLUE:
					return Blocks.LIGHT_BLUE_SHULKER_BOX;
				case YELLOW:
					return Blocks.YELLOW_SHULKER_BOX;
				case LIME:
					return Blocks.LIME_SHULKER_BOX;
				case PINK:
					return Blocks.PINK_SHULKER_BOX;
				case GRAY:
					return Blocks.GRAY_SHULKER_BOX;
				case LIGHT_GRAY:
					return Blocks.LIGHT_GRAY_SHULKER_BOX;
				case CYAN:
					return Blocks.CYAN_SHULKER_BOX;
				case PURPLE:
				default:
					return Blocks.PURPLE_SHULKER_BOX;
				case BLUE:
					return Blocks.BLUE_SHULKER_BOX;
				case BROWN:
					return Blocks.BROWN_SHULKER_BOX;
				case GREEN:
					return Blocks.GREEN_SHULKER_BOX;
				case RED:
					return Blocks.RED_SHULKER_BOX;
				case BLACK:
					return Blocks.BLACK_SHULKER_BOX;
			}
		}
	}

	public DyeColor getColor() {
		return this.color;
	}

	public static ItemStack stackOf(DyeColor color) {
		return new ItemStack(of(color));
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(field_18474, rotation.rotate(state.getProperty(field_18474)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(field_18474)));
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		Direction direction2 = state.getProperty(field_18474);
		ShulkerBoxBlockEntity.ShulkerBlockState shulkerBlockState = ((ShulkerBoxBlockEntity)world.getBlockEntity(pos)).method_13743();
		return shulkerBlockState != ShulkerBoxBlockEntity.ShulkerBlockState.CLOSED
				&& (shulkerBlockState != ShulkerBoxBlockEntity.ShulkerBlockState.OPENED || direction2 != direction.getOpposite() && direction2 != direction)
			? BlockRenderLayer.UNDEFINED
			: BlockRenderLayer.SOLID;
	}
}
