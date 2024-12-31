package net.minecraft.dragon;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2994 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final EnderDragonEntity field_14714;
	private final class_2987[] field_14715 = new class_2987[class_2993.method_13201()];
	private class_2987 field_14716;

	public class_2994(EnderDragonEntity enderDragonEntity) {
		this.field_14714 = enderDragonEntity;
		this.method_13203(class_2993.HOVER);
	}

	public void method_13203(class_2993<?> arg) {
		if (this.field_14716 == null || arg != this.field_14716.method_13189()) {
			if (this.field_14716 != null) {
				this.field_14716.method_13185();
			}

			this.field_14716 = this.method_13204((class_2993<class_2987>)arg);
			if (!this.field_14714.world.isClient) {
				this.field_14714.getDataTracker().set(EnderDragonEntity.field_14661, arg.method_13200());
			}

			LOGGER.debug("Dragon is now in phase {} on the {}", arg, this.field_14714.world.isClient ? "client" : "server");
			this.field_14716.method_13184();
		}
	}

	public class_2987 method_13202() {
		return this.field_14716;
	}

	public <T extends class_2987> T method_13204(class_2993<T> arg) {
		int i = arg.method_13200();
		if (this.field_14715[i] == null) {
			this.field_14715[i] = arg.method_13199(this.field_14714);
		}

		return (T)this.field_14715[i];
	}
}
