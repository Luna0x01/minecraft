package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	private final PressurePlateBlock.ActivationRule rule;

	protected PressurePlateBlock(Material material, PressurePlateBlock.ActivationRule activationRule) {
		super(material);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
		this.rule = activationRule;
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int value) {
		return state.with(POWERED, value > 0);
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		Box box = this.getPlateHitBox(pos);
		List<? extends Entity> list;
		switch (this.rule) {
			case ALL:
				list = world.getEntitiesIn(null, box);
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
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(POWERED, data == 1);
	}

	@Override
	public int getData(BlockState state) {
		return state.get(POWERED) ? 1 : 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, POWERED);
	}

	public static enum ActivationRule {
		ALL,
		MOBS;
	}
}
