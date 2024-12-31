package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final IntProperty POWER = IntProperty.of("power", 0, 15);
	private final int weight;

	protected WeightedPressurePlateBlock(Material material, int i) {
		this(material, i, material.getColor());
	}

	protected WeightedPressurePlateBlock(Material material, int i, MaterialColor materialColor) {
		super(material, materialColor);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 0));
		this.weight = i;
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		int i = Math.min(world.getEntitiesInBox(Entity.class, this.getPlateHitBox(pos)).size(), this.weight);
		if (i > 0) {
			float f = (float)Math.min(this.weight, i) / (float)this.weight;
			return MathHelper.ceil(f * 15.0F);
		} else {
			return 0;
		}
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return (Integer)state.get(POWER);
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int value) {
		return state.with(POWER, value);
	}

	@Override
	public int getTickRate(World world) {
		return 10;
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
}
