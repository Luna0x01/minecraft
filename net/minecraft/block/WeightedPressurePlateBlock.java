package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.sound.Sounds;
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
		int i = Math.min(world.getEntitiesInBox(Entity.class, BOX.offset(pos)).size(), this.weight);
		if (i > 0) {
			float f = (float)Math.min(this.weight, i) / (float)this.weight;
			return MathHelper.ceil(f * 15.0F);
		} else {
			return 0;
		}
	}

	@Override
	protected void method_11549(World world, BlockPos blockPos) {
		world.method_11486(null, blockPos, Sounds.BLOCK_METAL_PRESSUREPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
	}

	@Override
	protected void method_11550(World world, BlockPos blockPos) {
		world.method_11486(null, blockPos, Sounds.BLOCK_METAL_PRESSUREPLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
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
