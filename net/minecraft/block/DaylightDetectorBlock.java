package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends BlockWithEntity {
	public static final IntProperty POWER = IntProperty.of("power", 0, 15);
	private final boolean inverted;

	public DaylightDetectorBlock(boolean bl) {
		super(Material.WOOD);
		this.inverted = bl;
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 0));
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
		this.setItemGroup(ItemGroup.REDSTONE);
		this.setStrength(0.2F);
		this.setSound(WOOD);
		this.setTranslationKey("daylightDetector");
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return (Integer)state.get(POWER);
	}

	public void updateState(World world, BlockPos pos) {
		if (!world.dimension.hasNoSkylight()) {
			BlockState blockState = world.getBlockState(pos);
			int i = world.getLightAtPos(LightType.SKY, pos) - world.getAmbientDarkness();
			float f = world.getSkyAngleRadians(1.0F);
			float g = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
			f += (g - f) * 0.2F;
			i = Math.round((float)i * MathHelper.cos(f));
			i = MathHelper.clamp(i, 0, 15);
			if (this.inverted) {
				i = 15 - i;
			}

			if ((Integer)blockState.get(POWER) != i) {
				world.setBlockState(pos, blockState.with(POWER, i), 3);
			}
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (player.canModifyWorld()) {
			if (world.isClient) {
				return true;
			} else {
				if (this.inverted) {
					world.setBlockState(pos, Blocks.DAYLIGHT_DETECTOR.getDefaultState().with(POWER, state.get(POWER)), 4);
					Blocks.DAYLIGHT_DETECTOR.updateState(world, pos);
				} else {
					world.setBlockState(pos, Blocks.DAYLIGHT_DETECTOR_INVERTED.getDefaultState().with(POWER, state.get(POWER)), 4);
					Blocks.DAYLIGHT_DETECTOR_INVERTED.updateState(world, pos);
				}

				return true;
			}
		} else {
			return super.onUse(world, pos, state, player, direction, posX, posY, posZ);
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.DAYLIGHT_DETECTOR);
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Item.fromBlock(Blocks.DAYLIGHT_DETECTOR);
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
	public int getBlockType() {
		return 3;
	}

	@Override
	public boolean emitsRedstonePower() {
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
