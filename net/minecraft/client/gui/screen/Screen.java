package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.advancement.Achievement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class Screen extends DrawableHelper implements IdentifiableBooleanConsumer {
	private static final Logger logger = LogManager.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
	private static final Splitter LINE_SPLITTER = Splitter.on('\n');
	protected MinecraftClient client;
	protected ItemRenderer itemRenderer;
	public int width;
	public int height;
	protected List<ButtonWidget> buttons = Lists.newArrayList();
	protected List<LabelWidget> labels = Lists.newArrayList();
	public boolean passEvents;
	protected TextRenderer textRenderer;
	private ButtonWidget prevClickedButton;
	private int pressedMouseButton;
	private long lastClicked;
	private int touchHeld;
	private URI clickedLink;

	public void render(int mouseX, int mouseY, float tickDelta) {
		for (int i = 0; i < this.buttons.size(); i++) {
			((ButtonWidget)this.buttons.get(i)).render(this.client, mouseX, mouseY);
		}

		for (int j = 0; j < this.labels.size(); j++) {
			((LabelWidget)this.labels.get(j)).render(this.client, mouseX, mouseY);
		}
	}

	protected void keyPressed(char id, int code) {
		if (code == 1) {
			this.client.setScreen(null);
			if (this.client.currentScreen == null) {
				this.client.closeScreen();
			}
		}
	}

	protected <T extends ButtonWidget> T addButton(T button) {
		this.buttons.add(button);
		return button;
	}

	public static String getClipboard() {
		try {
			Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return (String)transferable.getTransferData(DataFlavor.stringFlavor);
			}
		} catch (Exception var1) {
		}

		return "";
	}

	public static void setClipboard(String string) {
		if (!StringUtils.isEmpty(string)) {
			try {
				StringSelection stringSelection = new StringSelection(string);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
			} catch (Exception var2) {
			}
		}
	}

	protected void renderTooltip(ItemStack stack, int x, int y) {
		List<String> list = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips);

		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				list.set(i, stack.getRarity().formatting + (String)list.get(i));
			} else {
				list.set(i, Formatting.GRAY + (String)list.get(i));
			}
		}

		this.renderTooltip(list, x, y);
	}

	protected void renderTooltip(String text, int x, int y) {
		this.renderTooltip(Arrays.asList(text), x, y);
	}

	protected void renderTooltip(List<String> text, int x, int y) {
		if (!text.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			DiffuseLighting.disable();
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			int i = 0;

			for (String string : text) {
				int j = this.textRenderer.getStringWidth(string);
				if (j > i) {
					i = j;
				}
			}

			int k = x + 12;
			int l = y - 12;
			int n = 8;
			if (text.size() > 1) {
				n += 2 + (text.size() - 1) * 10;
			}

			if (k + i > this.width) {
				k -= 28 + i;
			}

			if (l + n + 6 > this.height) {
				l = this.height - n - 6;
			}

			this.zOffset = 300.0F;
			this.itemRenderer.zOffset = 300.0F;
			int o = -267386864;
			this.fillGradient(k - 3, l - 4, k + i + 3, l - 3, -267386864, -267386864);
			this.fillGradient(k - 3, l + n + 3, k + i + 3, l + n + 4, -267386864, -267386864);
			this.fillGradient(k - 3, l - 3, k + i + 3, l + n + 3, -267386864, -267386864);
			this.fillGradient(k - 4, l - 3, k - 3, l + n + 3, -267386864, -267386864);
			this.fillGradient(k + i + 3, l - 3, k + i + 4, l + n + 3, -267386864, -267386864);
			int p = 1347420415;
			int q = 1344798847;
			this.fillGradient(k - 3, l - 3 + 1, k - 3 + 1, l + n + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(k + i + 2, l - 3 + 1, k + i + 3, l + n + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(k - 3, l - 3, k + i + 3, l - 3 + 1, 1347420415, 1347420415);
			this.fillGradient(k - 3, l + n + 2, k + i + 3, l + n + 3, 1344798847, 1344798847);

			for (int r = 0; r < text.size(); r++) {
				String string2 = (String)text.get(r);
				this.textRenderer.drawWithShadow(string2, (float)k, (float)l, -1);
				if (r == 0) {
					l += 2;
				}

				l += 10;
			}

			this.zOffset = 0.0F;
			this.itemRenderer.zOffset = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			DiffuseLighting.enableNormally();
			GlStateManager.enableRescaleNormal();
		}
	}

	protected void renderTextHoverEffect(Text text, int x, int y) {
		if (text != null && text.getStyle().getHoverEvent() != null) {
			HoverEvent hoverEvent = text.getStyle().getHoverEvent();
			if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
				ItemStack itemStack = null;

				try {
					NbtElement nbtElement = StringNbtReader.parse(hoverEvent.getValue().asUnformattedString());
					if (nbtElement instanceof NbtCompound) {
						itemStack = ItemStack.fromNbt((NbtCompound)nbtElement);
					}
				} catch (NbtException var11) {
				}

				if (itemStack != null) {
					this.renderTooltip(itemStack, x, y);
				} else {
					this.renderTooltip(Formatting.RED + "Invalid Item!", x, y);
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
				if (this.client.options.advancedItemTooltips) {
					try {
						NbtElement nbtElement2 = StringNbtReader.parse(hoverEvent.getValue().asUnformattedString());
						if (nbtElement2 instanceof NbtCompound) {
							List<String> list = Lists.newArrayList();
							NbtCompound nbtCompound = (NbtCompound)nbtElement2;
							list.add(nbtCompound.getString("name"));
							if (nbtCompound.contains("type", 8)) {
								String string = nbtCompound.getString("type");
								list.add("Type: " + string + " (" + EntityType.getIdByName(string) + ")");
							}

							list.add(nbtCompound.getString("id"));
							this.renderTooltip(list, x, y);
						} else {
							this.renderTooltip(Formatting.RED + "Invalid Entity!", x, y);
						}
					} catch (NbtException var10) {
						this.renderTooltip(Formatting.RED + "Invalid Entity!", x, y);
					}
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
				this.renderTooltip(LINE_SPLITTER.splitToList(hoverEvent.getValue().asFormattedString()), x, y);
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
				Stat stat = Stats.getAStat(hoverEvent.getValue().asUnformattedString());
				if (stat != null) {
					Text text2 = stat.getText();
					Text text3 = new TranslatableText("stats.tooltip.type." + (stat.isAchievement() ? "achievement" : "statistic"));
					text3.getStyle().setItalic(true);
					String string2 = stat instanceof Achievement ? ((Achievement)stat).getDescription() : null;
					List<String> list2 = Lists.newArrayList(new String[]{text2.asFormattedString(), text3.asFormattedString()});
					if (string2 != null) {
						list2.addAll(this.textRenderer.wrapLines(string2, 150));
					}

					this.renderTooltip(list2, x, y);
				} else {
					this.renderTooltip(Formatting.RED + "Invalid statistic/achievement!", x, y);
				}
			}

			GlStateManager.disableLighting();
		}
	}

	protected void insertText(String text, boolean override) {
	}

	protected boolean handleTextClick(Text text) {
		if (text == null) {
			return false;
		} else {
			ClickEvent clickEvent = text.getStyle().getClickEvent();
			if (hasShiftDown()) {
				if (text.getStyle().getInsertion() != null) {
					this.insertText(text.getStyle().getInsertion(), false);
				}
			} else if (clickEvent != null) {
				if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
					if (!this.client.options.chatLink) {
						return false;
					}

					try {
						URI uRI = new URI(clickEvent.getValue());
						String string = uRI.getScheme();
						if (string == null) {
							throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
						}

						if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase())) {
							throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase());
						}

						if (this.client.options.chatLinkPrompt) {
							this.clickedLink = uRI;
							this.client.setScreen(new ConfirmChatLinkScreen(this, clickEvent.getValue(), 31102009, false));
						} else {
							this.openLink(uRI);
						}
					} catch (URISyntaxException var5) {
						logger.error("Can't open url for {}", new Object[]{clickEvent, var5});
					}
				} else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
					URI uRI2 = new File(clickEvent.getValue()).toURI();
					this.openLink(uRI2);
				} else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
					this.insertText(clickEvent.getValue(), true);
				} else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					this.sendMessage(clickEvent.getValue(), false);
				} else {
					logger.error("Don't know how to handle {}", new Object[]{clickEvent});
				}

				return true;
			}

			return false;
		}
	}

	public void sendMessage(String text) {
		this.sendMessage(text, true);
	}

	public void sendMessage(String text, boolean toHud) {
		if (toHud) {
			this.client.inGameHud.getChatHud().addToMessageHistory(text);
		}

		this.client.player.sendChatMessage(text);
	}

	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0) {
			for (int i = 0; i < this.buttons.size(); i++) {
				ButtonWidget buttonWidget = (ButtonWidget)this.buttons.get(i);
				if (buttonWidget.isMouseOver(this.client, mouseX, mouseY)) {
					this.prevClickedButton = buttonWidget;
					buttonWidget.playDownSound(this.client.getSoundManager());
					this.buttonClicked(buttonWidget);
				}
			}
		}
	}

	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (this.prevClickedButton != null && button == 0) {
			this.prevClickedButton.mouseReleased(mouseX, mouseY);
			this.prevClickedButton = null;
		}
	}

	protected void mouseDragged(int mouseX, int mouseY, int button, long mouseLastClicked) {
	}

	protected void buttonClicked(ButtonWidget button) {
	}

	public void init(MinecraftClient client, int width, int height) {
		this.client = client;
		this.itemRenderer = client.getItemRenderer();
		this.textRenderer = client.textRenderer;
		this.width = width;
		this.height = height;
		this.buttons.clear();
		this.init();
	}

	public void setScreenBounds(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void init() {
	}

	public void handleInput() {
		if (Mouse.isCreated()) {
			while (Mouse.next()) {
				this.handleMouse();
			}
		}

		if (Keyboard.isCreated()) {
			while (Keyboard.next()) {
				this.handleKeyboard();
			}
		}
	}

	public void handleMouse() {
		int i = Mouse.getEventX() * this.width / this.client.width;
		int j = this.height - Mouse.getEventY() * this.height / this.client.height - 1;
		int k = Mouse.getEventButton();
		if (Mouse.getEventButtonState()) {
			if (this.client.options.touchscreen && this.touchHeld++ > 0) {
				return;
			}

			this.pressedMouseButton = k;
			this.lastClicked = MinecraftClient.getTime();
			this.mouseClicked(i, j, this.pressedMouseButton);
		} else if (k != -1) {
			if (this.client.options.touchscreen && --this.touchHeld > 0) {
				return;
			}

			this.pressedMouseButton = -1;
			this.mouseReleased(i, j, k);
		} else if (this.pressedMouseButton != -1 && this.lastClicked > 0L) {
			long l = MinecraftClient.getTime() - this.lastClicked;
			this.mouseDragged(i, j, this.pressedMouseButton, l);
		}
	}

	public void handleKeyboard() {
		char c = Keyboard.getEventCharacter();
		if (Keyboard.getEventKey() == 0 && c >= ' ' || Keyboard.getEventKeyState()) {
			this.keyPressed(c, Keyboard.getEventKey());
		}

		this.client.handleKeyInput();
	}

	public void tick() {
	}

	public void removed() {
	}

	public void renderBackground() {
		this.renderBackground(0);
	}

	public void renderBackground(int alpha) {
		if (this.client.world != null) {
			this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.renderDirtBackground(alpha);
		}
	}

	public void renderDirtBackground(int alpha) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.client.getTextureManager().bindTexture(OPTIONS_BACKGROUND_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, 0.0).texture(0.0, (double)((float)this.height / 32.0F + (float)alpha)).color(64, 64, 64, 255).next();
		bufferBuilder.vertex((double)this.width, (double)this.height, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)alpha))
			.color(64, 64, 64, 255)
			.next();
		bufferBuilder.vertex((double)this.width, 0.0, 0.0).texture((double)((float)this.width / 32.0F), (double)alpha).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, (double)alpha).color(64, 64, 64, 255).next();
		tessellator.draw();
	}

	public boolean shouldPauseGame() {
		return true;
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		if (id == 31102009) {
			if (confirmed) {
				this.openLink(this.clickedLink);
			}

			this.clickedLink = null;
			this.client.setScreen(this);
		}
	}

	private void openLink(URI link) {
		try {
			Class<?> class_ = Class.forName("java.awt.Desktop");
			Object object = class_.getMethod("getDesktop").invoke(null);
			class_.getMethod("browse", URI.class).invoke(object, link);
		} catch (Throwable var4) {
			Throwable throwable2 = var4.getCause();
			logger.error("Couldn't open link: {}", new Object[]{throwable2 == null ? "<UNKNOWN>" : throwable2.getMessage()});
		}
	}

	public static boolean hasControlDown() {
		return MinecraftClient.IS_MAC ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
	}

	public static boolean hasShiftDown() {
		return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
	}

	public static boolean hasAltDown() {
		return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
	}

	public static boolean isCut(int code) {
		return code == 45 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isPaste(int code) {
		return code == 47 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isCopy(int code) {
		return code == 46 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isSelectAll(int code) {
		return code == 30 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public void resize(MinecraftClient client, int width, int height) {
		this.init(client, width, height);
	}
}
