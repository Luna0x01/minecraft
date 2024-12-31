package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty field_18434 = Properties.POWERED;
	private final PressurePlateBlock.ActivationRule rule;

	protected PressurePlateBlock(PressurePlateBlock.ActivationRule activationRule, Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18434, Boolean.valueOf(false)));
		this.rule = activationRule;
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return state.getProperty(field_18434) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int value) {
		return state.withProperty(field_18434, Boolean.valueOf(value > 0));
	}

	@Override
	protected void playPressSound(IWorld world, BlockPos pos) {
		if (this.material == Material.WOOD) {
			world.playSound(null, pos, Sounds.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
		} else {
			world.playSound(null, pos, Sounds.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
		}
	}

	@Override
	protected void playDepressSound(IWorld world, BlockPos pos) {
		if (this.material == Material.WOOD) {
			world.playSound(null, pos, Sounds.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
		} else {
			world.playSound(null, pos, Sounds.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
		}
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		Box box = BOX.offset(pos);
		List<? extends Entity> list;
		switch (this.rule) {
			case ALL:
				list = world.getEntities(null, box);
				break;
			case MOBS:
				list = world.getEntitiesInBox(LivingEntity.class, box);
				break;
			default:
				return 0;
		}

		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					return 15;
				}
			}
		}

		return 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18434);
	}

	public static enum ActivationRule {
		ALL,
		MOBS;
	}
}
