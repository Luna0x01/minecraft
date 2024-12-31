package net.minecraft;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public abstract class class_3162 extends HostileEntity {
	protected static final TrackedData<Byte> field_15586 = DataTracker.registerData(class_3162.class, TrackedDataHandlerRegistry.BYTE);

	protected class_3162(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15586, (byte)0);
	}

	protected boolean method_14121(int i) {
		int j = this.dataTracker.get(field_15586);
		return (j & i) != 0;
	}

	protected void method_14122(int i, boolean bl) {
		int j = this.dataTracker.get(field_15586);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.dataTracker.set(field_15586, (byte)(j & 0xFF));
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16821;
	}

	public class_3162.class_3163 method_14123() {
		return class_3162.class_3163.CROSSED;
	}

	public static enum class_3163 {
		CROSSED,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW;
	}
}
