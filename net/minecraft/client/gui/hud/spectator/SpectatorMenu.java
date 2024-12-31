package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SpectatorMenu {
	private static final SpectatorMenuCommand CLOSE_COMMAND = new SpectatorMenu.CloseSpectatorMenuCommand();
	private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(-1, true);
	private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, true);
	private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new SpectatorMenu.ChangePageSpectatorMenuCommand(1, false);
	public static final SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand() {
		@Override
		public void use(SpectatorMenu menu) {
		}

		@Override
		public Text getName() {
			return new LiteralText("");
		}

		@Override
		public void renderIcon(float brightness, int alpha) {
		}

		@Override
		public boolean isEnabled() {
			return false;
		}
	};
	private final SpectatorMenuCloseCallback closeCallback;
	private final List<SpectatorMenuState> stateStack = Lists.newArrayList();
	private SpectatorMenuCommandGroup currentGroup;
	private int selectedSlot = -1;
	private int page;

	public SpectatorMenu(SpectatorMenuCloseCallback spectatorMenuCloseCallback) {
		this.currentGroup = new RootSpectatorCommandGroup();
		this.closeCallback = spectatorMenuCloseCallback;
	}

	public SpectatorMenuCommand getCommand(int slot) {
		int i = slot + this.page * 6;
		if (this.page > 0 && slot == 0) {
			return PREVIOUS_PAGE_COMMAND;
		} else if (slot == 7) {
			return i < this.currentGroup.getCommands().size() ? NEXT_PAGE_COMMAND : DISABLED_NEXT_PAGE_COMMAND;
		} else if (slot == 8) {
			return CLOSE_COMMAND;
		} else {
			return i >= 0 && i < this.currentGroup.getCommands().size()
				? (SpectatorMenuCommand)Objects.firstNonNull(this.currentGroup.getCommands().get(i), BLANK_COMMAND)
				: BLANK_COMMAND;
		}
	}

	public List<SpectatorMenuCommand> getCommands() {
		List<SpectatorMenuCommand> list = Lists.newArrayList();

		for (int i = 0; i <= 8; i++) {
			list.add(this.getCommand(i));
		}

		return list;
	}

	public SpectatorMenuCommand getSelectedCommand() {
		return this.getCommand(this.selectedSlot);
	}

	public SpectatorMenuCommandGroup getCurrentGroup() {
		return this.currentGroup;
	}

	public void useCommand(int slot) {
		SpectatorMenuCommand spectatorMenuCommand = this.getCommand(slot);
		if (spectatorMenuCommand != BLANK_COMMAND) {
			if (this.selectedSlot == slot && spectatorMenuCommand.isEnabled()) {
				spectatorMenuCommand.use(this);
			} else {
				this.selectedSlot = slot;
			}
		}
	}

	public void close() {
		this.closeCallback.close(this);
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}

	public void selectElement(SpectatorMenuCommandGroup group) {
		this.stateStack.add(this.getCurrentState());
		this.currentGroup = group;
		this.selectedSlot = -1;
		this.page = 0;
	}

	public SpectatorMenuState getCurrentState() {
		return new SpectatorMenuState(this.currentGroup, this.getCommands(), this.selectedSlot);
	}

	static class ChangePageSpectatorMenuCommand implements SpectatorMenuCommand {
		private final int direction;
		private final boolean enabled;

		public ChangePageSpectatorMenuCommand(int i, boolean bl) {
			this.direction = i;
			this.enabled = bl;
		}

		@Override
		public void use(SpectatorMenu menu) {
			menu.page = menu.page + this.direction;
		}

		@Override
		public Text getName() {
			return this.direction < 0 ? new TranslatableText("spectatorMenu.previous_page") : new TranslatableText("spectatorMenu.next_page");
		}

		@Override
		public void renderIcon(float brightness, int alpha) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEXTURE);
			if (this.direction < 0) {
				DrawableHelper.drawTexture(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
			} else {
				DrawableHelper.drawTexture(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
			}
		}

		@Override
		public boolean isEnabled() {
			return this.enabled;
		}
	}

	static class CloseSpectatorMenuCommand implements SpectatorMenuCommand {
		private CloseSpectatorMenuCommand() {
		}

		@Override
		public void use(SpectatorMenu menu) {
			menu.close();
		}

		@Override
		public Text getName() {
			return new TranslatableText("spectatorMenu.close");
		}

		@Override
		public void renderIcon(float brightness, int alpha) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEXTURE);
			DrawableHelper.drawTexture(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
