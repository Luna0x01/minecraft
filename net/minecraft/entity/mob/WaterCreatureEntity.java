package net.minecraft.entity.mob;

import net.minecraft.class_3462;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class WaterCreatureEntity extends PathAwareEntity implements EntityCategoryProvider {
	protected WaterCreatureEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean method_2607() {
		return true;
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16822;
	}

	@Override
	public boolean method_15653(RenderBlockView renderBlockView) {
		return renderBlockView.method_16382(this, this.getBoundingBox()) && renderBlockView.method_16387(this, this.getBoundingBox());
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canImmediatelyDespawn() {
		return true;
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		return 1 + this.world.random.nextInt(3);
	}

	protected void method_15826(int i) {
		if (this.isAlive() && !this.method_15575()) {
			this.setAir(i - 1);
			if (this.getAir() == -20) {
				this.setAir(0);
				this.damage(DamageSource.DROWN, 2.0F);
			}
		} else {
			this.setAir(300);
		}
	}

	@Override
	public void baseTick() {
		int i = this.getAir();
		super.baseTick();
		this.method_15826(i);
	}

	@Override
	public boolean canFly() {
		return false;
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return false;
	}
}
