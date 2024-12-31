package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AnvilBlock extends FallingBlock {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public static final IntProperty DAMAGE = IntProperty.of("damage", 0, 2);

	protected AnvilBlock() {
		super(Material.ANVIL);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(DAMAGE, 0));
		this.setOpacity(0);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		Direction direction = entity.getHorizontalDirection().rotateYClockwise();
		return super.getStateFromData(world, pos, dir, x, y, z, id, entity).with(FACING, direction).with(DAMAGE, id >> 2);
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (!world.isClient) {
			player.openHandledScreen(new AnvilBlock.AnvilNameableHandler(world, pos));
		}

		return true;
	}

	@Override
	public int getMeta(BlockState state) {
		return (Integer)state.get(DAMAGE);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		Direction direction = view.getBlockState(pos).get(FACING);
		if (direction.getAxis() == Direction.Axis.X) {
			this.setBoundingBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
		} else {
			this.setBoundingBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		stacks.add(new ItemStack(item, 1, 0));
		stacks.add(new ItemStack(item, 1, 1));
		stacks.add(new ItemStack(item, 1, 2));
	}

	@Override
	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
		entity.setHurtingEntities(true);
	}

	@Override
	public void onDestroyedOnLanding(World world, BlockPos pos) {
		world.syncGlobalEvent(1022, pos, 0);
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return true;
	}

	@Override
	public BlockState getRenderState(BlockState state) {
		return this.getDefaultState().with(FACING, Direction.SOUTH);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.fromHorizontal(data & 3)).with(DAMAGE, (data & 15) >> 2);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
		return i | (Integer)state.get(DAMAGE) << 2;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, DAMAGE);
	}

	public static class AnvilNameableHandler implements NamedScreenHandlerFactory {
		private final World world;
		private final BlockPos pos;

		public AnvilNameableHandler(World world, BlockPos blockPos) {
			this.world = world;
			this.pos = blockPos;
		}

		@Override
		public String getTranslationKey() {
			return "anvil";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public Text getName() {
			return new TranslatableText(Blocks.ANVIL.getTranslationKey() + ".name");
		}

		@Override
		public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
			return new AnvilScreenHandler(inventory, this.world, this.pos, player);
		}

		@Override
		public String getId() {
			return "minecraft:anvil";
		}
	}
}
