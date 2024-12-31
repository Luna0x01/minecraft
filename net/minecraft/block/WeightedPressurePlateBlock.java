package net.minecraft.block;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final IntProperty field_18585 = Properties.POWER;
	private final int weight;

	protected WeightedPressurePlateBlock(int i, Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18585, Integer.valueOf(0)));
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
	protected void playPressSound(IWorld world, BlockPos pos) {
		world.playSound(null, pos, Sounds.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
	}

	@Override
	protected void playDepressSound(IWorld world, BlockPos pos) {
		world.playSound(null, pos, Sounds.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return (Integer)state.getProperty(field_18585);
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int value) {
		return state.withProperty(field_18585, Integer.valueOf(value));
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 10;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18585);
	}
}
