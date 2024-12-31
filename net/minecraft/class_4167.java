package net.minecraft;

import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.world.CommandBlockExecutor;

public class class_4167 extends class_4162 {
	private final CommandBlockExecutor field_20413;

	public class_4167(CommandBlockExecutor commandBlockExecutor) {
		this.field_20413 = commandBlockExecutor;
	}

	@Override
	public CommandBlockExecutor method_18663() {
		return this.field_20413;
	}

	@Override
	int method_18664() {
		return 150;
	}

	@Override
	protected void init() {
		super.init();
		this.field_20362 = this.method_18663().isTrackingOutput();
		this.method_18665();
		this.field_20357.setText(this.method_18663().getCommand());
	}

	@Override
	protected void method_18650(CommandBlockExecutor commandBlockExecutor) {
		if (commandBlockExecutor instanceof CommandBlockMinecartEntity.class_3532) {
			CommandBlockMinecartEntity.class_3532 lv = (CommandBlockMinecartEntity.class_3532)commandBlockExecutor;
			this.client
				.getNetworkHandler()
				.sendPacket(new class_4392(lv.method_15964().getEntityId(), this.field_20357.getText(), commandBlockExecutor.isTrackingOutput()));
		}
	}
}
