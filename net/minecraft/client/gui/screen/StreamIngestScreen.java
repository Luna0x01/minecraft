package net.minecraft.client.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.twitch.TwitchStream;
import net.minecraft.util.Formatting;
import tv.twitch.broadcast.IngestServer;

public class StreamIngestScreen extends Screen {
	private final Screen parent;
	private String title;
	private StreamIngestScreen.StreamIngestListWidget widget;

	public StreamIngestScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public void init() {
		this.title = I18n.translate("options.stream.ingest.title");
		this.widget = new StreamIngestScreen.StreamIngestListWidget(this.client);
		if (!this.client.getTwitchStreamProvider().isTesting()) {
			this.client.getTwitchStreamProvider().setStreamListener();
		}

		this.buttons.add(new ButtonWidget(1, this.width / 2 - 155, this.height - 24 - 6, 150, 20, I18n.translate("gui.done")));
		this.buttons.add(new ButtonWidget(2, this.width / 2 + 5, this.height - 24 - 6, 150, 20, I18n.translate("options.stream.ingest.reset")));
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.widget.handleMouse();
	}

	@Override
	public void removed() {
		if (this.client.getTwitchStreamProvider().isTesting()) {
			this.client.getTwitchStreamProvider().getTwitchStream().shutdown();
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				this.client.setScreen(this.parent);
			} else {
				this.client.options.currentTexturePackName = "";
				this.client.options.save();
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.widget.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	class StreamIngestListWidget extends ListWidget {
		public StreamIngestListWidget(MinecraftClient minecraftClient) {
			super(
				minecraftClient,
				StreamIngestScreen.this.width,
				StreamIngestScreen.this.height,
				32,
				StreamIngestScreen.this.height - 35,
				(int)((double)minecraftClient.textRenderer.fontHeight * 3.5)
			);
			this.setRenderSelection(false);
		}

		@Override
		protected int getEntryCount() {
			return this.client.getTwitchStreamProvider().getIngestServers().length;
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
			this.client.options.currentTexturePackName = this.client.getTwitchStreamProvider().getIngestServers()[index].serverUrl;
			this.client.options.save();
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return this.client.getTwitchStreamProvider().getIngestServers()[index].serverUrl.equals(this.client.options.currentTexturePackName);
		}

		@Override
		protected void renderBackground() {
		}

		@Override
		protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
			IngestServer ingestServer = this.client.getTwitchStreamProvider().getIngestServers()[index];
			String string = ingestServer.serverUrl.replaceAll("\\{stream_key\\}", "");
			String string2 = (int)ingestServer.bitrateKbps + " kbps";
			String string3 = null;
			TwitchStream twitchStream = this.client.getTwitchStreamProvider().getTwitchStream();
			if (twitchStream != null) {
				if (ingestServer == twitchStream.getIngestServer()) {
					string = Formatting.GREEN + string;
					string2 = (int)(twitchStream.getTestCompletePercentage() * 100.0F) + "%";
				} else if (index < twitchStream.getAvailableServers()) {
					if (ingestServer.bitrateKbps == 0.0F) {
						string2 = Formatting.RED + "Down!";
					}
				} else {
					string2 = Formatting.OBFUSCATED + "1234" + Formatting.RESET + " kbps";
				}
			} else if (ingestServer.bitrateKbps == 0.0F) {
				string2 = Formatting.RED + "Down!";
			}

			x -= 15;
			if (this.isEntrySelected(index)) {
				string3 = Formatting.BLUE + "(Preferred)";
			} else if (ingestServer.defaultServer) {
				string3 = Formatting.GREEN + "(Default)";
			}

			StreamIngestScreen.this.drawWithShadow(StreamIngestScreen.this.textRenderer, ingestServer.serverName, x + 2, y + 5, 16777215);
			StreamIngestScreen.this.drawWithShadow(
				StreamIngestScreen.this.textRenderer, string, x + 2, y + StreamIngestScreen.this.textRenderer.fontHeight + 5 + 3, 3158064
			);
			StreamIngestScreen.this.drawWithShadow(
				StreamIngestScreen.this.textRenderer,
				string2,
				this.getScrollbarPosition() - 5 - StreamIngestScreen.this.textRenderer.getStringWidth(string2),
				y + 5,
				8421504
			);
			if (string3 != null) {
				StreamIngestScreen.this.drawWithShadow(
					StreamIngestScreen.this.textRenderer,
					string3,
					this.getScrollbarPosition() - 5 - StreamIngestScreen.this.textRenderer.getStringWidth(string3),
					y + 5 + 3 + StreamIngestScreen.this.textRenderer.fontHeight,
					8421504
				);
			}
		}

		@Override
		protected int getScrollbarPosition() {
			return super.getScrollbarPosition() + 15;
		}
	}
}
