package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

public abstract class HandledScreen extends Screen {
	protected static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");
	protected int backgroundWidth = 176;
	protected int backgroundHeight = 166;
	public ScreenHandler screenHandler;
	protected int x;
	protected int y;
	private Slot focusedSlot;
	private Slot touchDragSlotStart;
	private boolean touchIsRightClickDrag;
	private ItemStack touchDragStack;
	private int touchDropX;
	private int touchDropY;
	private Slot touchDropOriginSlot;
	private long touchDropTime;
	private ItemStack touchDropReturningStack;
	private Slot touchHoveredSlot;
	private long touchDropTimer;
	protected final Set<Slot> cursorDragSlots = Sets.newHashSet();
	protected boolean isCursorDragging;
	private int heldButtonType;
	private int heldButtonCode;
	private boolean cancelNextRelease;
	private int draggedStackRemainder;
	private long lastButtonClickTime;
	private Slot lastClickedSlot;
	private int lastClickedButton;
	private boolean isDoubleClicking;
	private ItemStack quickMovingStack;

	public HandledScreen(ScreenHandler screenHandler) {
		this.screenHandler = screenHandler;
		this.cancelNextRelease = true;
	}

	@Override
	public void init() {
		super.init();
		this.client.player.openScreenHandler = this.screenHandler;
		this.x = (this.width - this.backgroundWidth) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		int i = this.x;
		int j = this.y;
		this.drawBackground(tickDelta, mouseX, mouseY);
		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
		GlStateManager.disableLighting();
		GlStateManager.disableDepthTest();
		super.render(mouseX, mouseY, tickDelta);
		DiffuseLighting.enable();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)i, (float)j, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		this.focusedSlot = null;
		int k = 240;
		int l = 240;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)k / 1.0F, (float)l / 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		for (int m = 0; m < this.screenHandler.slots.size(); m++) {
			Slot slot = (Slot)this.screenHandler.slots.get(m);
			this.drawSlot(slot);
			if (this.isPointOverSlot(slot, mouseX, mouseY) && slot.doDrawHoveringEffect()) {
				this.focusedSlot = slot;
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				int n = slot.x;
				int o = slot.y;
				GlStateManager.colorMask(true, true, true, false);
				this.fillGradient(n, o, n + 16, o + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}

		DiffuseLighting.disable();
		this.drawForeground(mouseX, mouseY);
		DiffuseLighting.enable();
		PlayerInventory playerInventory = this.client.player.inventory;
		ItemStack itemStack = this.touchDragStack == null ? playerInventory.getCursorStack() : this.touchDragStack;
		if (itemStack != null) {
			int p = 8;
			int q = this.touchDragStack == null ? 8 : 16;
			String string = null;
			if (this.touchDragStack != null && this.touchIsRightClickDrag) {
				itemStack = itemStack.copy();
				itemStack.count = MathHelper.ceil((float)itemStack.count / 2.0F);
			} else if (this.isCursorDragging && this.cursorDragSlots.size() > 1) {
				itemStack = itemStack.copy();
				itemStack.count = this.draggedStackRemainder;
				if (itemStack.count == 0) {
					string = "" + Formatting.YELLOW + "0";
				}
			}

			this.drawItem(itemStack, mouseX - i - p, mouseY - j - q, string);
		}

		if (this.touchDropReturningStack != null) {
			float f = (float)(MinecraftClient.getTime() - this.touchDropTime) / 100.0F;
			if (f >= 1.0F) {
				f = 1.0F;
				this.touchDropReturningStack = null;
			}

			int r = this.touchDropOriginSlot.x - this.touchDropX;
			int s = this.touchDropOriginSlot.y - this.touchDropY;
			int t = this.touchDropX + (int)((float)r * f);
			int u = this.touchDropY + (int)((float)s * f);
			this.drawItem(this.touchDropReturningStack, t, u, null);
		}

		GlStateManager.popMatrix();
		if (playerInventory.getCursorStack() == null && this.focusedSlot != null && this.focusedSlot.hasStack()) {
			ItemStack itemStack2 = this.focusedSlot.getStack();
			this.renderTooltip(itemStack2, mouseX, mouseY);
		}

		GlStateManager.enableLighting();
		GlStateManager.enableDepthTest();
		DiffuseLighting.enableNormally();
	}

	private void drawItem(ItemStack stack, int xPosition, int yPosition, String amountText) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zOffset = 200.0F;
		this.itemRenderer.zOffset = 200.0F;
		this.itemRenderer.renderInGuiWithOverrides(stack, xPosition, yPosition);
		this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, xPosition, yPosition - (this.touchDragStack == null ? 0 : 8), amountText);
		this.zOffset = 0.0F;
		this.itemRenderer.zOffset = 0.0F;
	}

	protected void drawForeground(int mouseX, int mouseY) {
	}

	protected abstract void drawBackground(float delta, int mouseX, int mouseY);

	private void drawSlot(Slot slot) {
		int i = slot.x;
		int j = slot.y;
		ItemStack itemStack = slot.getStack();
		boolean bl = false;
		boolean bl2 = slot == this.touchDragSlotStart && this.touchDragStack != null && !this.touchIsRightClickDrag;
		ItemStack itemStack2 = this.client.player.inventory.getCursorStack();
		String string = null;
		if (slot == this.touchDragSlotStart && this.touchDragStack != null && this.touchIsRightClickDrag && itemStack != null) {
			itemStack = itemStack.copy();
			itemStack.count /= 2;
		} else if (this.isCursorDragging && this.cursorDragSlots.contains(slot) && itemStack2 != null) {
			if (this.cursorDragSlots.size() == 1) {
				return;
			}

			if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && this.screenHandler.canInsertIntoSlot(slot)) {
				itemStack = itemStack2.copy();
				bl = true;
				ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack, slot.getStack() == null ? 0 : slot.getStack().count);
				if (itemStack.count > itemStack.getMaxCount()) {
					string = Formatting.YELLOW + "" + itemStack.getMaxCount();
					itemStack.count = itemStack.getMaxCount();
				}

				if (itemStack.count > slot.getMaxStackAmount(itemStack)) {
					string = Formatting.YELLOW + "" + slot.getMaxStackAmount(itemStack);
					itemStack.count = slot.getMaxStackAmount(itemStack);
				}
			} else {
				this.cursorDragSlots.remove(slot);
				this.calculateOffset();
			}
		}

		this.zOffset = 100.0F;
		this.itemRenderer.zOffset = 100.0F;
		if (itemStack == null) {
			String string2 = slot.getBackgroundSprite();
			if (string2 != null) {
				Sprite sprite = this.client.getSpriteAtlasTexture().getSprite(string2);
				GlStateManager.disableLighting();
				this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				this.drawSprite(i, j, sprite, 16, 16);
				GlStateManager.enableLighting();
				bl2 = true;
			}
		}

		if (!bl2) {
			if (bl) {
				fill(i, j, i + 16, j + 16, -2130706433);
			}

			GlStateManager.enableDepthTest();
			this.itemRenderer.renderInGuiWithOverrides(itemStack, i, j);
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, i, j, string);
		}

		this.itemRenderer.zOffset = 0.0F;
		this.zOffset = 0.0F;
	}

	private void calculateOffset() {
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (itemStack != null && this.isCursorDragging) {
			this.draggedStackRemainder = itemStack.count;

			for (Slot slot : this.cursorDragSlots) {
				ItemStack itemStack2 = itemStack.copy();
				int i = slot.getStack() == null ? 0 : slot.getStack().count;
				ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2, i);
				if (itemStack2.count > itemStack2.getMaxCount()) {
					itemStack2.count = itemStack2.getMaxCount();
				}

				if (itemStack2.count > slot.getMaxStackAmount(itemStack2)) {
					itemStack2.count = slot.getMaxStackAmount(itemStack2);
				}

				this.draggedStackRemainder = this.draggedStackRemainder - (itemStack2.count - i);
			}
		}
	}

	private Slot getSlotAt(int x, int y) {
		for (int i = 0; i < this.screenHandler.slots.size(); i++) {
			Slot slot = (Slot)this.screenHandler.slots.get(i);
			if (this.isPointOverSlot(slot, x, y)) {
				return slot;
			}
		}

		return null;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		boolean bl = button == this.client.options.pickItemKey.getCode() + 100;
		Slot slot = this.getSlotAt(mouseX, mouseY);
		long l = MinecraftClient.getTime();
		this.isDoubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == button;
		this.cancelNextRelease = false;
		if (button == 0 || button == 1 || bl) {
			int i = this.x;
			int j = this.y;
			boolean bl2 = mouseX < i || mouseY < j || mouseX >= i + this.backgroundWidth || mouseY >= j + this.backgroundHeight;
			int k = -1;
			if (slot != null) {
				k = slot.id;
			}

			if (bl2) {
				k = -999;
			}

			if (this.client.options.touchscreen && bl2 && this.client.player.inventory.getCursorStack() == null) {
				this.client.setScreen(null);
				return;
			}

			if (k != -1) {
				if (this.client.options.touchscreen) {
					if (slot != null && slot.hasStack()) {
						this.touchDragSlotStart = slot;
						this.touchDragStack = null;
						this.touchIsRightClickDrag = button == 1;
					} else {
						this.touchDragSlotStart = null;
					}
				} else if (!this.isCursorDragging) {
					if (this.client.player.inventory.getCursorStack() == null) {
						if (button == this.client.options.pickItemKey.getCode() + 100) {
							this.onMouseClick(slot, k, button, 3);
						} else {
							boolean bl3 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
							int m = 0;
							if (bl3) {
								this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack() : null;
								m = 1;
							} else if (k == -999) {
								m = 4;
							}

							this.onMouseClick(slot, k, button, m);
						}

						this.cancelNextRelease = true;
					} else {
						this.isCursorDragging = true;
						this.heldButtonCode = button;
						this.cursorDragSlots.clear();
						if (button == 0) {
							this.heldButtonType = 0;
						} else if (button == 1) {
							this.heldButtonType = 1;
						} else if (button == this.client.options.pickItemKey.getCode() + 100) {
							this.heldButtonType = 2;
						}
					}
				}
			}
		}

		this.lastClickedSlot = slot;
		this.lastButtonClickTime = l;
		this.lastClickedButton = button;
	}

	@Override
	protected void mouseDragged(int mouseX, int mouseY, int button, long mouseLastClicked) {
		Slot slot = this.getSlotAt(mouseX, mouseY);
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
			if (button == 0 || button == 1) {
				if (this.touchDragStack == null) {
					if (slot != this.touchDragSlotStart && this.touchDragSlotStart.getStack() != null) {
						this.touchDragStack = this.touchDragSlotStart.getStack().copy();
					}
				} else if (this.touchDragStack.count > 1 && slot != null && ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false)) {
					long l = MinecraftClient.getTime();
					if (this.touchHoveredSlot == slot) {
						if (l - this.touchDropTimer > 500L) {
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, 0);
							this.onMouseClick(slot, slot.id, 1, 0);
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, 0);
							this.touchDropTimer = l + 750L;
							this.touchDragStack.count--;
						}
					} else {
						this.touchHoveredSlot = slot;
						this.touchDropTimer = l;
					}
				}
			}
		} else if (this.isCursorDragging
			&& slot != null
			&& itemStack != null
			&& itemStack.count > this.cursorDragSlots.size()
			&& ScreenHandler.canInsertItemIntoSlot(slot, itemStack, true)
			&& slot.canInsert(itemStack)
			&& this.screenHandler.canInsertIntoSlot(slot)) {
			this.cursorDragSlots.add(slot);
			this.calculateOffset();
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		Slot slot = this.getSlotAt(mouseX, mouseY);
		int i = this.x;
		int j = this.y;
		boolean bl = mouseX < i || mouseY < j || mouseX >= i + this.backgroundWidth || mouseY >= j + this.backgroundHeight;
		int k = -1;
		if (slot != null) {
			k = slot.id;
		}

		if (bl) {
			k = -999;
		}

		if (this.isDoubleClicking && slot != null && button == 0 && this.screenHandler.canInsertIntoSlot(null, slot)) {
			if (hasShiftDown()) {
				if (slot != null && slot.inventory != null && this.quickMovingStack != null) {
					for (Slot slot2 : this.screenHandler.slots) {
						if (slot2 != null
							&& slot2.canTakeItems(this.client.player)
							&& slot2.hasStack()
							&& slot2.inventory == slot.inventory
							&& ScreenHandler.canInsertItemIntoSlot(slot2, this.quickMovingStack, true)) {
							this.onMouseClick(slot2, slot2.id, button, 1);
						}
					}
				}
			} else {
				this.onMouseClick(slot, k, button, 6);
			}

			this.isDoubleClicking = false;
			this.lastButtonClickTime = 0L;
		} else {
			if (this.isCursorDragging && this.heldButtonCode != button) {
				this.isCursorDragging = false;
				this.cursorDragSlots.clear();
				this.cancelNextRelease = true;
				return;
			}

			if (this.cancelNextRelease) {
				this.cancelNextRelease = false;
				return;
			}

			if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
				if (button == 0 || button == 1) {
					if (this.touchDragStack == null && slot != this.touchDragSlotStart) {
						this.touchDragStack = this.touchDragSlotStart.getStack();
					}

					boolean bl2 = ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false);
					if (k != -1 && this.touchDragStack != null && bl2) {
						this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, 0);
						this.onMouseClick(slot, k, 0, 0);
						if (this.client.player.inventory.getCursorStack() != null) {
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, 0);
							this.touchDropX = mouseX - i;
							this.touchDropY = mouseY - j;
							this.touchDropOriginSlot = this.touchDragSlotStart;
							this.touchDropReturningStack = this.touchDragStack;
							this.touchDropTime = MinecraftClient.getTime();
						} else {
							this.touchDropReturningStack = null;
						}
					} else if (this.touchDragStack != null) {
						this.touchDropX = mouseX - i;
						this.touchDropY = mouseY - j;
						this.touchDropOriginSlot = this.touchDragSlotStart;
						this.touchDropReturningStack = this.touchDragStack;
						this.touchDropTime = MinecraftClient.getTime();
					}

					this.touchDragStack = null;
					this.touchDragSlotStart = null;
				}
			} else if (this.isCursorDragging && !this.cursorDragSlots.isEmpty()) {
				this.onMouseClick(null, -999, ScreenHandler.packClickData(0, this.heldButtonType), 5);

				for (Slot slot3 : this.cursorDragSlots) {
					this.onMouseClick(slot3, slot3.id, ScreenHandler.packClickData(1, this.heldButtonType), 5);
				}

				this.onMouseClick(null, -999, ScreenHandler.packClickData(2, this.heldButtonType), 5);
			} else if (this.client.player.inventory.getCursorStack() != null) {
				if (button == this.client.options.pickItemKey.getCode() + 100) {
					this.onMouseClick(slot, k, button, 3);
				} else {
					boolean bl3 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
					if (bl3) {
						this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack() : null;
					}

					this.onMouseClick(slot, k, button, bl3 ? 1 : 0);
				}
			}
		}

		if (this.client.player.inventory.getCursorStack() == null) {
			this.lastButtonClickTime = 0L;
		}

		this.isCursorDragging = false;
	}

	private boolean isPointOverSlot(Slot slot, int pointX, int pointY) {
		return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
	}

	protected boolean isPointWithinBounds(int posX, int posY, int width, int height, int pointX, int pointY) {
		int i = this.x;
		int j = this.y;
		pointX -= i;
		pointY -= j;
		return pointX >= posX - 1 && pointX < posX + width + 1 && pointY >= posY - 1 && pointY < posY + height + 1;
	}

	protected void onMouseClick(Slot slot, int invSlot, int button, int slotAction) {
		if (slot != null) {
			invSlot = slot.id;
		}

		this.client.interactionManager.clickSlot(this.screenHandler.syncId, invSlot, button, slotAction, this.client.player);
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (code == 1 || code == this.client.options.inventoryKey.getCode()) {
			this.client.player.closeHandledScreen();
		}

		this.handleHotbarKeyPressed(code);
		if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
			if (code == this.client.options.pickItemKey.getCode()) {
				this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, 3);
			} else if (code == this.client.options.dropKey.getCode()) {
				this.onMouseClick(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, 4);
			}
		}
	}

	protected boolean handleHotbarKeyPressed(int keyCode) {
		if (this.client.player.inventory.getCursorStack() == null && this.focusedSlot != null) {
			for (int i = 0; i < 9; i++) {
				if (keyCode == this.client.options.hotbarKeys[i].getCode()) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, 2);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void removed() {
		if (this.client.player != null) {
			this.screenHandler.close(this.client.player);
		}
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.client.player.isAlive() || this.client.player.removed) {
			this.client.player.closeHandledScreen();
		}
	}
}
