package net.minecraft.client.gui.screen.ingame;

import net.minecraft.class_4162;
import net.minecraft.class_4391;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;

public class CommandBlockScreen extends class_4162 {
	private final CommandBlockBlockEntity field_13331;
	private ButtonWidget field_13332;
	private ButtonWidget field_13333;
	private ButtonWidget field_13334;
	private CommandBlockBlockEntity.class_2736 field_13335 = CommandBlockBlockEntity.class_2736.REDSTONE;
	private boolean field_13337;
	private boolean field_13338;

	public CommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
		this.field_13331 = commandBlockBlockEntity;
	}

	@Override
	CommandBlockExecutor method_18663() {
		return this.field_13331.getCommandExecutor();
	}

	@Override
	int method_18664() {
		return 135;
	}

	@Override
	protected void init() {
		super.init();
		this.field_13332 = this.addButton(new ButtonWidget(5, this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.translate("advMode.mode.sequence")) {
			@Override
			public void method_18374(double d, double e) {
				CommandBlockScreen.this.method_12193();
				CommandBlockScreen.this.method_12192();
			}
		});
		this.field_13333 = this.addButton(new ButtonWidget(6, this.width / 2 - 50, 165, 100, 20, I18n.translate("advMode.mode.unconditional")) {
			@Override
			public void method_18374(double d, double e) {
				CommandBlockScreen.this.field_13337 = !CommandBlockScreen.this.field_13337;
				CommandBlockScreen.this.method_12194();
			}
		});
		this.field_13334 = this.addButton(new ButtonWidget(7, this.width / 2 + 50 + 4, 165, 100, 20, I18n.translate("advMode.mode.redstoneTriggered")) {
			@Override
			public void method_18374(double d, double e) {
				CommandBlockScreen.this.field_13338 = !CommandBlockScreen.this.field_13338;
				CommandBlockScreen.this.method_12195();
			}
		});
		this.field_20359.active = false;
		this.field_20361.active = false;
		this.field_13332.active = false;
		this.field_13333.active = false;
		this.field_13334.active = false;
	}

	public void method_12191() {
		CommandBlockExecutor commandBlockExecutor = this.field_13331.getCommandExecutor();
		this.field_20357.setText(commandBlockExecutor.getCommand());
		this.field_20362 = commandBlockExecutor.isTrackingOutput();
		this.field_13335 = this.field_13331.method_11657();
		this.field_13337 = this.field_13331.method_11658();
		this.field_13338 = this.field_13331.method_11654();
		this.method_18665();
		this.method_12192();
		this.method_12194();
		this.method_12195();
		this.field_20359.active = true;
		this.field_20361.active = true;
		this.field_13332.active = true;
		this.field_13333.active = true;
		this.field_13334.active = true;
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		this.method_18665();
		this.method_12192();
		this.method_12194();
		this.method_12195();
		this.field_20359.active = true;
		this.field_20361.active = true;
		this.field_13332.active = true;
		this.field_13333.active = true;
		this.field_13334.active = true;
	}

	@Override
	protected void method_18650(CommandBlockExecutor commandBlockExecutor) {
		this.client
			.getNetworkHandler()
			.sendPacket(
				new class_4391(
					new BlockPos(commandBlockExecutor.method_16274()),
					this.field_20357.getText(),
					this.field_13335,
					commandBlockExecutor.isTrackingOutput(),
					this.field_13337,
					this.field_13338
				)
			);
	}

	private void method_12192() {
		switch (this.field_13335) {
			case SEQUENCE:
				this.field_13332.message = I18n.translate("advMode.mode.sequence");
				break;
			case AUTO:
				this.field_13332.message = I18n.translate("advMode.mode.auto");
				break;
			case REDSTONE:
				this.field_13332.message = I18n.translate("advMode.mode.redstone");
		}
	}

	private void method_12193() {
		switch (this.field_13335) {
			case SEQUENCE:
				this.field_13335 = CommandBlockBlockEntity.class_2736.AUTO;
				break;
			case AUTO:
				this.field_13335 = CommandBlockBlockEntity.class_2736.REDSTONE;
				break;
			case REDSTONE:
				this.field_13335 = CommandBlockBlockEntity.class_2736.SEQUENCE;
		}
	}

	private void method_12194() {
		if (this.field_13337) {
			this.field_13333.message = I18n.translate("advMode.mode.conditional");
		} else {
			this.field_13333.message = I18n.translate("advMode.mode.unconditional");
		}
	}

	private void method_12195() {
		if (this.field_13338) {
			this.field_13334.message = I18n.translate("advMode.mode.autoexec.bat");
		} else {
			this.field_13334.message = I18n.translate("advMode.mode.redstoneTriggered");
		}
	}
}
