package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends BlockWithEntity {
	public static final IntProperty POWER = IntProperty.of("power", 0, 15);
	protected static final Box field_12639 = new Box(0.0, 0.0, 0.0, 1.0, 0.375, 1.0);
	private final boolean inverted;

	public DaylightDetectorBlock(boolean bl) {
		super(Material.WOOD);
		this.inverted = bl;
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 0));
		this.setItemGroup(ItemGroup.REDSTONE);
		this.setStrength(0.2F);
		this.setBlockSoundGroup(BlockSoundGroup.field_12759);
		this.setTranslationKey("daylightDetector");
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12639;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Integer)state.get(POWER);
	}

	public void updateState(World world, BlockPos pos) {
		if (!world.dimension.hasNoSkylight()) {
			BlockState blockState = world.getBlockState(pos);
			int i = world.getLightAtPos(LightType.SKY, pos) - world.getAmbientDarkness();
			float f = world.getSkyAngleRadians(1.0F);
			if (this.inverted) {
				i = 15 - i;
			}

			if (i > 0 && !this.inverted) {
				float g = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
				f += (g - f) * 0.2F;
				i = Math.round((float)i * MathHelper.cos(f));
			}

			i = MathHelper.clamp(i, 0, 15);
			if ((Integer)blockState.get(POWER) != i) {
				world.setBlockState(pos, blockState.with(POWER, i), 3);
			}
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
		if (playerEntity.canModifyWorld()) {
			if (world.isClient) {
				return true;
			} else {
				if (this.inverted) {
					world.setBlockState(blockPos, Blocks.DAYLIGHT_DETECTOR.getDefaultState().with(POWER, blockState.get(POWER)), 4);
					Blocks.DAYLIGHT_DETECTOR.updateState(world, blockPos);
				} else {
					world.setBlockState(blockPos, Blocks.DAYLIGHT_DETECTOR_INVERTED.getDefaultState().with(POWER, blockState.get(POWER)), 4);
					Blocks.DAYLIGHT_DETECTOR_INVERTED.updateState(world, blockPos);
				}

				return true;
			}
		} else {
			return super.method_421(world, blockPos, blockState, playerEntity, hand, itemStack, direction, f, g, h);
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.DAYLIGHT_DETECTOR);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.DAYLIGHT_DETECTOR);
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
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new DaylightDetectorBlockEntity();
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(POWER, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(POWER);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, POWER);
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		if (!this.inverted) {
			super.appendItemStacks(item, group, stacks);
		}
	}
}
