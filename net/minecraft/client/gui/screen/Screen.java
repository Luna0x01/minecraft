package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.class_4107;
import net.minecraft.class_4121;
import net.minecraft.class_4122;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.TooltipContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Screen extends class_4121 implements IdentifiableBooleanConsumer {
	private static final Logger logger = LogManager.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
	protected final List<class_4122> field_20307 = Lists.newArrayList();
	protected MinecraftClient client;
	protected HeldItemRenderer field_20308;
	public int width;
	public int height;
	protected final List<ButtonWidget> buttons = Lists.newArrayList();
	protected final List<LabelWidget> labels = Lists.newArrayList();
	public boolean passEvents;
	protected TextRenderer textRenderer;
	private URI clickedLink;

	public void render(int mouseX, int mouseY, float tickDelta) {
		for (int i = 0; i < this.buttons.size(); i++) {
			((ButtonWidget)this.buttons.get(i)).method_891(mouseX, mouseY, tickDelta);
		}

		for (int j = 0; j < this.labels.size(); j++) {
			((LabelWidget)this.labels.get(j)).method_18397(mouseX, mouseY, tickDelta);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256 && this.method_18607()) {
			this.method_18608();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	public boolean method_18607() {
		return true;
	}

	public void method_18608() {
		this.client.setScreen(null);
	}

	protected <T extends ButtonWidget> T addButton(T button) {
		this.buttons.add(button);
		this.field_20307.add(button);
		return button;
	}

	protected void renderTooltip(ItemStack stack, int x, int y) {
		this.renderTooltip(this.method_14502(stack), x, y);
	}

	public List<String> method_14502(ItemStack stack) {
		List<Text> list = stack.getTooltip(
			this.client.player, this.client.options.field_19992 ? TooltipContext.TooltipType.ADVANCED : TooltipContext.TooltipType.NORMAL
		);
		List<String> list2 = Lists.newArrayList();

		for (Text text : list) {
			list2.add(text.asFormattedString());
		}

		return list2;
	}

	public void renderTooltip(String text, int x, int y) {
		this.renderTooltip(Arrays.asList(text), x, y);
	}

	public void renderTooltip(List<String> text, int x, int y) {
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
			this.field_20308.field_20932 = 300.0F;
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
			this.field_20308.field_20932 = 0.0F;
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
				ItemStack itemStack = ItemStack.EMPTY;

				try {
					NbtElement nbtElement = StringNbtReader.parse(hoverEvent.getValue().getString());
					if (nbtElement instanceof NbtCompound) {
						itemStack = ItemStack.from((NbtCompound)nbtElement);
					}
				} catch (CommandSyntaxException var10) {
				}

				if (itemStack.isEmpty()) {
					this.renderTooltip(Formatting.RED + "Invalid Item!", x, y);
				} else {
					this.renderTooltip(itemStack, x, y);
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
				if (this.client.options.field_19992) {
					try {
						NbtCompound nbtCompound = StringNbtReader.parse(hoverEvent.getValue().getString());
						List<String> list = Lists.newArrayList();
						Text text2 = Text.Serializer.deserializeText(nbtCompound.getString("name"));
						if (text2 != null) {
							list.add(text2.asFormattedString());
						}

						if (nbtCompound.contains("type", 8)) {
							String string = nbtCompound.getString("type");
							list.add("Type: " + string);
						}

						list.add(nbtCompound.getString("id"));
						this.renderTooltip(list, x, y);
					} catch (CommandSyntaxException | JsonSyntaxException var9) {
						this.renderTooltip(Formatting.RED + "Invalid Entity!", x, y);
					}
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
				this.renderTooltip(this.client.textRenderer.wrapLines(hoverEvent.getValue().asFormattedString(), Math.max(this.width / 2, 200)), x, y);
			}

			GlStateManager.disableLighting();
		}
	}

	protected void insertText(String text, boolean override) {
	}

	public boolean handleTextClick(Text text) {
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

						if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
							throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
						}

						if (this.client.options.chatLinkPrompt) {
							this.clickedLink = uRI;
							this.client.setScreen(new ConfirmChatLinkScreen(this, clickEvent.getValue(), 31102009, false));
						} else {
							this.openLink(uRI);
						}
					} catch (URISyntaxException var5) {
						logger.error("Can't open url for {}", clickEvent, var5);
					}
				} else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
					URI uRI2 = new File(clickEvent.getValue()).toURI();
					this.openLink(uRI2);
				} else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
					this.insertText(clickEvent.getValue(), true);
				} else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					this.sendMessage(clickEvent.getValue(), false);
				} else {
					logger.error("Don't know how to handle {}", clickEvent);
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

	public void init(MinecraftClient client, int width, int height) {
		this.client = client;
		this.field_20308 = client.getHeldItemRenderer();
		this.textRenderer = client.textRenderer;
		this.width = width;
		this.height = height;
		this.buttons.clear();
		this.field_20307.clear();
		this.init();
	}

	@Override
	public List<? extends class_4122> method_18423() {
		return this.field_20307;
	}

	protected void init() {
		this.field_20307.addAll(this.labels);
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
	public void confirmResult(boolean bl, int i) {
		if (i == 31102009) {
			if (bl) {
				this.openLink(this.clickedLink);
			}

			this.clickedLink = null;
			this.client.setScreen(this);
		}
	}

	private void openLink(URI link) {
		Util.getOperatingSystem().method_20237(link);
	}

	public static boolean hasControlDown() {
		return MinecraftClient.IS_MAC ? class_4107.method_18154(343) || class_4107.method_18154(347) : class_4107.method_18154(341) || class_4107.method_18154(345);
	}

	public static boolean hasShiftDown() {
		return class_4107.method_18154(340) || class_4107.method_18154(344);
	}

	public static boolean hasAltDown() {
		return class_4107.method_18154(342) || class_4107.method_18154(346);
	}

	public static boolean isCut(int code) {
		return code == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isPaste(int code) {
		return code == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isCopy(int code) {
		return code == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isSelectAll(int code) {
		return code == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public void resize(MinecraftClient client, int width, int height) {
		this.init(client, width, height);
	}

	public static void method_18605(Runnable runnable, String string, String string2) {
		try {
			runnable.run();
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, string);
			CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
			crashReportSection.add("Screen name", (CrashCallable<String>)(() -> string2));
			throw new CrashException(crashReport);
		}
	}
}
