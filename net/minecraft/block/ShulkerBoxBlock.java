package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ShulkerBoxBlock extends BlockWithEntity {
	public static final EnumProperty<Direction> FACING = DirectionProperty.of("facing");
	private final DyeColor color;

	public ShulkerBoxBlock(DyeColor dyeColor) {
		super(Material.STONE, MaterialColor.AIR);
		this.color = dyeColor;
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new ShulkerBoxBlockEntity(this.color);
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
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
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		if (world.isClient) {
			return true;
		} else if (player.isSpectator()) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				Direction direction2 = state.get(FACING);
				boolean bl;
				if (((ShulkerBoxBlockEntity)blockEntity).method_13743() == ShulkerBoxBlockEntity.ShulkerBlockState.CLOSED) {
					Box box = collisionBox.stretch(
							(double)(0.5F * (float)direction2.getOffsetX()), (double)(0.5F * (float)direction2.getOffsetY()), (double)(0.5F * (float)direction2.getOffsetZ())
						)
						.shrink((double)direction2.getOffsetX(), (double)direction2.getOffsetY(), (double)direction2.getOffsetZ());
					bl = !world.method_11488(box.offset(pos.offset(direction2)));
				} else {
					bl = true;
				}

				if (bl) {
					player.incrementStat(Stats.OPEN_SHULKER_BOX);
					player.openInventory((Inventory)blockEntity);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, dir);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.getById(data);
		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity) {
			ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)world.getBlockEntity(pos);
			shulkerBoxBlockEntity.method_13737(player.abilities.creativeMode);
			shulkerBoxBlockEntity.method_11662(player);
		}
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShulkerBoxBlockEntity) {
				((ShulkerBoxBlockEntity)blockEntity).setName(itemStack.getCustomName());
			}
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof ShulkerBoxBlockEntity) {
			ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
			if (!shulkerBoxBlockEntity.method_13744() && shulkerBoxBlockEntity.method_13732()) {
				ItemStack itemStack = new ItemStack(Item.fromBlock(this));
				NbtCompound nbtCompound = new NbtCompound();
				NbtCompound nbtCompound2 = new NbtCompound();
				nbtCompound.put("BlockEntityTag", ((ShulkerBoxBlockEntity)blockEntity).method_13741(nbtCompound2));
				itemStack.setNbt(nbtCompound);
				if (shulkerBoxBlockEntity.hasCustomName()) {
					itemStack.setCustomName(shulkerBoxBlockEntity.getTranslationKey());
					shulkerBoxBlockEntity.setName("");
				}

				onBlockBreak(world, pos, itemStack);
			}

			world.updateHorizontalAdjacent(pos, state.getBlock());
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public void method_14306(ItemStack itemStack, @Nullable World world, List<String> list, TooltipContext tooltipContext) {
		super.method_14306(itemStack, world, list, tooltipContext);
		NbtCompound nbtCompound = itemStack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("BlockEntityTag", 10)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockEntityTag");
			if (nbtCompound2.contains("LootTable", 8)) {
				list.add("???????");
			}

			if (nbtCompound2.contains("Items", 9)) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
				class_2960.method_13927(nbtCompound2, defaultedList);
				int i = 0;
				int j = 0;

				for (ItemStack itemStack2 : defaultedList) {
					if (!itemStack2.isEmpty()) {
						j++;
						if (i <= 4) {
							i++;
							list.add(String.format("%s x%d", itemStack2.getCustomName(), itemStack2.getCount()));
						}
					}
				}

				if (j - i > 0) {
					list.add(String.format(Formatting.ITALIC + CommonI18n.translate("container.shulkerBox.more"), j - i));
				}
			}
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		BlockEntity blockEntity = view.getBlockEntity(pos);
		return blockEntity instanceof ShulkerBoxBlockEntity ? ((ShulkerBoxBlockEntity)blockEntity).method_13735(state) : collisionBox;
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
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		ItemStack itemStack = super.getItemStack(world, blockPos, blockState);
		ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)world.getBlockEntity(blockPos);
		NbtCompound nbtCompound = shulkerBoxBlockEntity.method_13741(new NbtCompound());
		if (!nbtCompound.isEmpty()) {
			itemStack.putSubNbt("BlockEntityTag", nbtCompound);
		}

		return itemStack;
	}

	public static DyeColor colorOf(Item item) {
		return colorOf(Block.getBlockFromItem(item));
	}

	public static DyeColor colorOf(Block block) {
		return block instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)block).getColor() : DyeColor.PURPLE;
	}

	public static Block of(DyeColor color) {
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
			case SILVER:
				return Blocks.SILVER_SHULKER_BOX;
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

	public DyeColor getColor() {
		return this.color;
	}

	public static ItemStack stackOf(DyeColor color) {
		return new ItemStack(of(color));
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		state = this.getBlockState(state, world, pos);
		Direction direction2 = state.get(FACING);
		ShulkerBoxBlockEntity.ShulkerBlockState shulkerBlockState = ((ShulkerBoxBlockEntity)world.getBlockEntity(pos)).method_13743();
		return shulkerBlockState != ShulkerBoxBlockEntity.ShulkerBlockState.CLOSED
				&& (shulkerBlockState != ShulkerBoxBlockEntity.ShulkerBlockState.OPENED || direction2 != direction.getOpposite() && direction2 != direction)
			? BlockRenderLayer.UNDEFINED
			: BlockRenderLayer.SOLID;
	}
}
