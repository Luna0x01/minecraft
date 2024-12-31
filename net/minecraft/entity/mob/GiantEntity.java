package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class GiantEntity extends HostileEntity {
	public GiantEntity(World world) {
		super(EntityType.GIANT, world);
		this.setBounds(this.width * 6.0F, this.height * 6.0F);
	}

	@Override
	public float getEyeHeight() {
		return 10.440001F;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(100.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(50.0);
	}

	@Override
	public float method_15657(BlockPos blockPos, RenderBlockView renderBlockView) {
		return renderBlockView.method_16356(blockPos) - 0.5F;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.GIANT_ENTITIE;
	}
}
