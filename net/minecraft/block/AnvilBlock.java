package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilBlock extends FallingBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	public static final IntProperty DAMAGE = IntProperty.of("damage", 0, 2);
	protected static final Box field_12555 = new Box(0.0, 0.0, 0.125, 1.0, 1.0, 0.875);
	protected static final Box field_12556 = new Box(0.125, 0.0, 0.0, 0.875, 1.0, 1.0);
	protected static final Logger field_12557 = LogManager.getLogger();

	protected AnvilBlock() {
		super(Material.ANVIL);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(DAMAGE, 0));
		this.setOpacity(0);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		Direction direction = entity.getHorizontalDirection().rotateYClockwise();

		try {
			return super.getStateFromData(world, pos, dir, x, y, z, id, entity).with(FACING, direction).with(DAMAGE, id >> 2);
		} catch (IllegalArgumentException var11) {
			if (!world.isClient) {
				field_12557.warn(String.format("Invalid damage property for anvil at %s. Found %d, must be in [0, 1, 2]", pos, id >> 2));
				if (entity instanceof PlayerEntity) {
					((PlayerEntity)entity).sendMessage(new TranslatableText("Invalid damage property. Please pick in [0, 1, 2]"));
				}
			}

			return super.getStateFromData(world, pos, dir, x, y, z, 0, entity).with(FACING, direction).with(DAMAGE, 0);
		}
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (!world.isClient) {
			playerEntity.openHandledScreen(new AnvilBlock.AnvilNameableHandler(world, blockPos));
		}

		return true;
	}

	@Override
	public int getMeta(BlockState state) {
		return (Integer)state.get(DAMAGE);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		Direction direction = state.get(FACING);
		return direction.getAxis() == Direction.Axis.X ? field_12555 : field_12556;
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		stacks.add(new ItemStack(item));
		stacks.add(new ItemStack(item, 1, 1));
		stacks.add(new ItemStack(item, 1, 2));
	}

	@Override
	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
		entity.setHurtingEntities(true);
	}

	@Override
	public void onDestroyedOnLanding(World world, BlockPos pos) {
		world.syncGlobalEvent(1031, pos, 0);
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return true;
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
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.getBlock() != this ? state : state.with(FACING, rotation.rotate(state.get(FACING)));
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
