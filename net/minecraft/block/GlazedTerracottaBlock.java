package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GlazedTerracottaBlock extends HorizontalFacingBlock {
	public GlazedTerracottaBlock(DyeColor dyeColor) {
		super(Material.STONE, MaterialColor.fromDye(dyeColor));
		this.setStrength(1.4F);
		this.setBlockSoundGroup(BlockSoundGroup.STONE);
		String string = dyeColor.getTranslationKey();
		if (string.length() > 1) {
			String string2 = string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
			this.setTranslationKey("glazedTerracotta" + string2);
		}

		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(DIRECTION)));
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(DIRECTION, entity.getHorizontalDirection().getOpposite());
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		return i | ((Direction)state.get(DIRECTION)).getHorizontal();
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(DIRECTION, Direction.fromHorizontal(data));
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.PUSH_ONLY;
	}
}
