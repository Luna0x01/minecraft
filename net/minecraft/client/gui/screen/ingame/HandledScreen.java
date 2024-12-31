package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Set;
import net.minecraft.class_4107;
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
import net.minecraft.util.ItemAction;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public abstract class HandledScreen extends Screen {
	public static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");
	protected int backgroundWidth = 176;
	protected int backgroundHeight = 166;
	public ScreenHandler screenHandler;
	protected int x;
	protected int y;
	protected Slot focusedSlot;
	private Slot touchDragSlotStart;
	private boolean touchIsRightClickDrag;
	private ItemStack touchDragStack = ItemStack.EMPTY;
	private int touchDropX;
	private int touchDropY;
	private Slot touchDropOriginSlot;
	private long touchDropTime;
	private ItemStack touchDropReturningStack = ItemStack.EMPTY;
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
	private ItemStack quickMovingStack = ItemStack.EMPTY;

	public HandledScreen(ScreenHandler screenHandler) {
		this.screenHandler = screenHandler;
		this.cancelNextRelease = true;
	}

	@Override
	protected void init() {
		super.init();
		this.client.player.openScreenHandler = this.screenHandler;
		this.x = (this.width - this.backgroundWidth) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
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
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		for (int m = 0; m < this.screenHandler.slots.size(); m++) {
			Slot slot = (Slot)this.screenHandler.slots.get(m);
			if (slot.doDrawHoveringEffect()) {
				this.drawSlot(slot);
			}

			if (this.method_18681(slot, (double)mouseX, (double)mouseY) && slot.doDrawHoveringEffect()) {
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
		ItemStack itemStack = this.touchDragStack.isEmpty() ? playerInventory.getCursorStack() : this.touchDragStack;
		if (!itemStack.isEmpty()) {
			int p = 8;
			int q = this.touchDragStack.isEmpty() ? 8 : 16;
			String string = null;
			if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
				itemStack = itemStack.copy();
				itemStack.setCount(MathHelper.ceil((float)itemStack.getCount() / 2.0F));
			} else if (this.isCursorDragging && this.cursorDragSlots.size() > 1) {
				itemStack = itemStack.copy();
				itemStack.setCount(this.draggedStackRemainder);
				if (itemStack.isEmpty()) {
					string = "" + Formatting.YELLOW + "0";
				}
			}

			this.drawItem(itemStack, mouseX - i - 8, mouseY - j - q, string);
		}

		if (!this.touchDropReturningStack.isEmpty()) {
			float f = (float)(Util.method_20227() - this.touchDropTime) / 100.0F;
			if (f >= 1.0F) {
				f = 1.0F;
				this.touchDropReturningStack = ItemStack.EMPTY;
			}

			int r = this.touchDropOriginSlot.x - this.touchDropX;
			int s = this.touchDropOriginSlot.y - this.touchDropY;
			int t = this.touchDropX + (int)((float)r * f);
			int u = this.touchDropY + (int)((float)s * f);
			this.drawItem(this.touchDropReturningStack, t, u, null);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepthTest();
		DiffuseLighting.enableNormally();
	}

	protected void renderTooltip(int x, int y) {
		if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
			this.renderTooltip(this.focusedSlot.getStack(), x, y);
		}
	}

	private void drawItem(ItemStack stack, int xPosition, int yPosition, String amountText) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zOffset = 200.0F;
		this.field_20308.field_20932 = 200.0F;
		this.field_20308.method_19397(stack, xPosition, yPosition);
		this.field_20308.method_19384(this.textRenderer, stack, xPosition, yPosition - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
		this.zOffset = 0.0F;
		this.field_20308.field_20932 = 0.0F;
	}

	protected void drawForeground(int mouseX, int mouseY) {
	}

	protected abstract void drawBackground(float delta, int mouseX, int mouseY);

	private void drawSlot(Slot slot) {
		int i = slot.x;
		int j = slot.y;
		ItemStack itemStack = slot.getStack();
		boolean bl = false;
		boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
		ItemStack itemStack2 = this.client.player.inventory.getCursorStack();
		String string = null;
		if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
			itemStack = itemStack.copy();
			itemStack.setCount(itemStack.getCount() / 2);
		} else if (this.isCursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
			if (this.cursorDragSlots.size() == 1) {
				return;
			}

			if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && this.screenHandler.canInsertIntoSlot(slot)) {
				itemStack = itemStack2.copy();
				bl = true;
				ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
				int k = Math.min(itemStack.getMaxCount(), slot.getMaxStackAmount(itemStack));
				if (itemStack.getCount() > k) {
					string = Formatting.YELLOW.toString() + k;
					itemStack.setCount(k);
				}
			} else {
				this.cursorDragSlots.remove(slot);
				this.calculateOffset();
			}
		}

		this.zOffset = 100.0F;
		this.field_20308.field_20932 = 100.0F;
		if (itemStack.isEmpty() && slot.doDrawHoveringEffect()) {
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
			this.field_20308.method_19374(this.client.player, itemStack, i, j);
			this.field_20308.method_19384(this.textRenderer, itemStack, i, j, string);
		}

		this.field_20308.field_20932 = 0.0F;
		this.zOffset = 0.0F;
	}

	private void calculateOffset() {
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (!itemStack.isEmpty() && this.isCursorDragging) {
			if (this.heldButtonType == 2) {
				this.draggedStackRemainder = itemStack.getMaxCount();
			} else {
				this.draggedStackRemainder = itemStack.getCount();

				for (Slot slot : this.cursorDragSlots) {
					ItemStack itemStack2 = itemStack.copy();
					ItemStack itemStack3 = slot.getStack();
					int i = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
					ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2, i);
					int j = Math.min(itemStack2.getMaxCount(), slot.getMaxStackAmount(itemStack2));
					if (itemStack2.getCount() > j) {
						itemStack2.setCount(j);
					}

					this.draggedStackRemainder = this.draggedStackRemainder - (itemStack2.getCount() - i);
				}
			}
		}
	}

	private Slot method_18680(double d, double e) {
		for (int i = 0; i < this.screenHandler.slots.size(); i++) {
			Slot slot = (Slot)this.screenHandler.slots.get(i);
			if (this.method_18681(slot, d, e) && slot.doDrawHoveringEffect()) {
				return slot;
			}
		}

		return null;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			return true;
		} else {
			boolean bl = this.client.options.pickItemKey.method_18165(i);
			Slot slot = this.method_18680(d, e);
			long l = Util.method_20227();
			this.isDoubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == i;
			this.cancelNextRelease = false;
			if (i == 0 || i == 1 || bl) {
				int j = this.x;
				int k = this.y;
				boolean bl2 = this.method_14549(d, e, j, k, i);
				int m = -1;
				if (slot != null) {
					m = slot.id;
				}

				if (bl2) {
					m = -999;
				}

				if (this.client.options.touchscreen && bl2 && this.client.player.inventory.getCursorStack().isEmpty()) {
					this.client.setScreen(null);
					return true;
				}

				if (m != -1) {
					if (this.client.options.touchscreen) {
						if (slot != null && slot.hasStack()) {
							this.touchDragSlotStart = slot;
							this.touchDragStack = ItemStack.EMPTY;
							this.touchIsRightClickDrag = i == 1;
						} else {
							this.touchDragSlotStart = null;
						}
					} else if (!this.isCursorDragging) {
						if (this.client.player.inventory.getCursorStack().isEmpty()) {
							if (this.client.options.pickItemKey.method_18165(i)) {
								this.method_1131(slot, m, i, ItemAction.CLONE);
							} else {
								boolean bl3 = m != -999 && (class_4107.method_18154(340) || class_4107.method_18154(344));
								ItemAction itemAction = ItemAction.PICKUP;
								if (bl3) {
									this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
									itemAction = ItemAction.QUICK_MOVE;
								} else if (m == -999) {
									itemAction = ItemAction.THROW;
								}

								this.method_1131(slot, m, i, itemAction);
							}

							this.cancelNextRelease = true;
						} else {
							this.isCursorDragging = true;
							this.heldButtonCode = i;
							this.cursorDragSlots.clear();
							if (i == 0) {
								this.heldButtonType = 0;
							} else if (i == 1) {
								this.heldButtonType = 1;
							} else if (this.client.options.pickItemKey.method_18165(i)) {
								this.heldButtonType = 2;
							}
						}
					}
				}
			}

			this.lastClickedSlot = slot;
			this.lastButtonClickTime = l;
			this.lastClickedButton = i;
			return true;
		}
	}

	protected boolean method_14549(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		Slot slot = this.method_18680(d, e);
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
			if (i == 0 || i == 1) {
				if (this.touchDragStack.isEmpty()) {
					if (slot != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
						this.touchDragStack = this.touchDragSlotStart.getStack().copy();
					}
				} else if (this.touchDragStack.getCount() > 1 && slot != null && ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false)) {
					long l = Util.method_20227();
					if (this.touchHoveredSlot == slot) {
						if (l - this.touchDropTimer > 500L) {
							this.method_1131(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, ItemAction.PICKUP);
							this.method_1131(slot, slot.id, 1, ItemAction.PICKUP);
							this.method_1131(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, ItemAction.PICKUP);
							this.touchDropTimer = l + 750L;
							this.touchDragStack.decrement(1);
						}
					} else {
						this.touchHoveredSlot = slot;
						this.touchDropTimer = l;
					}
				}
			}
		} else if (this.isCursorDragging
			&& slot != null
			&& !itemStack.isEmpty()
			&& (itemStack.getCount() > this.cursorDragSlots.size() || this.heldButtonType == 2)
			&& ScreenHandler.canInsertItemIntoSlot(slot, itemStack, true)
			&& slot.canInsert(itemStack)
			&& this.screenHandler.canInsertIntoSlot(slot)) {
			this.cursorDragSlots.add(slot);
			this.calculateOffset();
		}

		return true;
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		Slot slot = this.method_18680(d, e);
		int j = this.x;
		int k = this.y;
		boolean bl = this.method_14549(d, e, j, k, i);
		int l = -1;
		if (slot != null) {
			l = slot.id;
		}

		if (bl) {
			l = -999;
		}

		if (this.isDoubleClicking && slot != null && i == 0 && this.screenHandler.canInsertIntoSlot(ItemStack.EMPTY, slot)) {
			if (hasShiftDown()) {
				if (!this.quickMovingStack.isEmpty()) {
					for (Slot slot2 : this.screenHandler.slots) {
						if (slot2 != null
							&& slot2.canTakeItems(this.client.player)
							&& slot2.hasStack()
							&& slot2.inventory == slot.inventory
							&& ScreenHandler.canInsertItemIntoSlot(slot2, this.quickMovingStack, true)) {
							this.method_1131(slot2, slot2.id, i, ItemAction.QUICK_MOVE);
						}
					}
				}
			} else {
				this.method_1131(slot, l, i, ItemAction.PICKUP_ALL);
			}

			this.isDoubleClicking = false;
			this.lastButtonClickTime = 0L;
		} else {
			if (this.isCursorDragging && this.heldButtonCode != i) {
				this.isCursorDragging = false;
				this.cursorDragSlots.clear();
				this.cancelNextRelease = true;
				return true;
			}

			if (this.cancelNextRelease) {
				this.cancelNextRelease = false;
				return true;
			}

			if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
				if (i == 0 || i == 1) {
					if (this.touchDragStack.isEmpty() && slot != this.touchDragSlotStart) {
						this.touchDragStack = this.touchDragSlotStart.getStack();
					}

					boolean bl2 = ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false);
					if (l != -1 && !this.touchDragStack.isEmpty() && bl2) {
						this.method_1131(this.touchDragSlotStart, this.touchDragSlotStart.id, i, ItemAction.PICKUP);
						this.method_1131(slot, l, 0, ItemAction.PICKUP);
						if (this.client.player.inventory.getCursorStack().isEmpty()) {
							this.touchDropReturningStack = ItemStack.EMPTY;
						} else {
							this.method_1131(this.touchDragSlotStart, this.touchDragSlotStart.id, i, ItemAction.PICKUP);
							this.touchDropX = MathHelper.floor(d - (double)j);
							this.touchDropY = MathHelper.floor(e - (double)k);
							this.touchDropOriginSlot = this.touchDragSlotStart;
							this.touchDropReturningStack = this.touchDragStack;
							this.touchDropTime = Util.method_20227();
						}
					} else if (!this.touchDragStack.isEmpty()) {
						this.touchDropX = MathHelper.floor(d - (double)j);
						this.touchDropY = MathHelper.floor(e - (double)k);
						this.touchDropOriginSlot = this.touchDragSlotStart;
						this.touchDropReturningStack = this.touchDragStack;
						this.touchDropTime = Util.method_20227();
					}

					this.touchDragStack = ItemStack.EMPTY;
					this.touchDragSlotStart = null;
				}
			} else if (this.isCursorDragging && !this.cursorDragSlots.isEmpty()) {
				this.method_1131(null, -999, ScreenHandler.packClickData(0, this.heldButtonType), ItemAction.QUICK_CRAFT);

				for (Slot slot3 : this.cursorDragSlots) {
					this.method_1131(slot3, slot3.id, ScreenHandler.packClickData(1, this.heldButtonType), ItemAction.QUICK_CRAFT);
				}

				this.method_1131(null, -999, ScreenHandler.packClickData(2, this.heldButtonType), ItemAction.QUICK_CRAFT);
			} else if (!this.client.player.inventory.getCursorStack().isEmpty()) {
				if (this.client.options.pickItemKey.method_18165(i)) {
					this.method_1131(slot, l, i, ItemAction.CLONE);
				} else {
					boolean bl3 = l != -999 && (class_4107.method_18154(340) || class_4107.method_18154(344));
					if (bl3) {
						this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
					}

					this.method_1131(slot, l, i, bl3 ? ItemAction.QUICK_MOVE : ItemAction.PICKUP);
				}
			}
		}

		if (this.client.player.inventory.getCursorStack().isEmpty()) {
			this.lastButtonClickTime = 0L;
		}

		this.isCursorDragging = false;
		return true;
	}

	private boolean method_18681(Slot slot, double d, double e) {
		return this.method_1134(slot.x, slot.y, 16, 16, d, e);
	}

	protected boolean method_1134(int i, int j, int k, int l, double d, double e) {
		int m = this.x;
		int n = this.y;
		d -= (double)m;
		e -= (double)n;
		return d >= (double)(i - 1) && d < (double)(i + k + 1) && e >= (double)(j - 1) && e < (double)(j + l + 1);
	}

	protected void method_1131(Slot slot, int i, int j, ItemAction itemAction) {
		if (slot != null) {
			i = slot.id;
		}

		this.client.interactionManager.method_1224(this.screenHandler.syncId, i, j, itemAction, this.client.player);
	}

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			if (i == 256 || this.client.options.inventoryKey.method_18166(i, j)) {
				this.client.player.closeHandledScreen();
			}

			this.method_4261(i, j);
			if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
				if (this.client.options.pickItemKey.method_18166(i, j)) {
					this.method_1131(this.focusedSlot, this.focusedSlot.id, 0, ItemAction.CLONE);
				} else if (this.client.options.dropKey.method_18166(i, j)) {
					this.method_1131(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, ItemAction.THROW);
				}
			}

			return true;
		}
	}

	protected boolean method_4261(int i, int j) {
		if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null) {
			for (int k = 0; k < 9; k++) {
				if (this.client.options.hotbarKeys[k].method_18166(i, j)) {
					this.method_1131(this.focusedSlot, this.focusedSlot.id, k, ItemAction.SWAP);
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
