package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.class_3175;
import net.minecraft.class_3257;
import net.minecraft.class_3278;
import net.minecraft.class_3283;
import net.minecraft.class_3284;
import net.minecraft.class_3286;
import net.minecraft.class_3287;
import net.minecraft.class_3306;
import net.minecraft.class_3320;
import net.minecraft.class_3536;
import net.minecraft.class_4113;
import net.minecraft.class_4122;
import net.minecraft.class_4397;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CraftingBlockData;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class RecipeBookScreen extends DrawableHelper implements class_4122, class_3287, class_4397<Ingredient> {
	protected static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private int field_16043;
	private int field_16044;
	private int field_16045;
	protected final class_3278 field_16046 = new class_3278();
	private final List<class_3284> field_16047 = Lists.newArrayList();
	private class_3284 field_16048;
	protected class_3257 field_16049;
	protected class_3536 field_20450;
	protected MinecraftClient client;
	private TextFieldWidget field_16052;
	private String field_16053 = "";
	protected class_3320 field_20451;
	protected final class_3283 field_16055 = new class_3283();
	protected final class_3175 field_16056 = new class_3175();
	private int field_16057;
	private boolean field_20452;

	public void method_18793(int i, int j, MinecraftClient minecraftClient, boolean bl, class_3536 arg) {
		this.client = minecraftClient;
		this.field_16044 = i;
		this.field_16045 = j;
		this.field_20450 = arg;
		minecraftClient.player.openScreenHandler = arg;
		this.field_20451 = minecraftClient.player.method_14675();
		this.field_16057 = minecraftClient.player.inventory.method_14153();
		if (this.method_14590()) {
			this.method_18795(bl);
		}

		minecraftClient.field_19946.method_18191(true);
	}

	public void method_18795(boolean bl) {
		this.field_16043 = bl ? 0 : 86;
		int i = (this.field_16044 - 147) / 2 - this.field_16043;
		int j = (this.field_16045 - 166) / 2;
		this.field_16056.method_14166();
		this.client.player.inventory.method_15921(this.field_16056);
		this.field_20450.method_15978(this.field_16056);
		String string = this.field_16052 != null ? this.field_16052.getText() : "";
		this.field_16052 = new TextFieldWidget(0, this.client.textRenderer, i + 25, j + 14, 80, this.client.textRenderer.fontHeight + 5);
		this.field_16052.setMaxLength(50);
		this.field_16052.setHasBorder(false);
		this.field_16052.setVisible(true);
		this.field_16052.setEditableColor(16777215);
		this.field_16052.setText(string);
		this.field_16055.method_14606(this.client, i, j);
		this.field_16055.method_14607(this);
		this.field_16049 = new class_3257(0, i + 110, j + 12, 26, 16, this.field_20451.method_21393(this.field_20450));
		this.method_18791();
		this.field_16047.clear();

		for (class_4113 lv : class_3320.method_18137(this.field_20450)) {
			this.field_16047.add(new class_3284(0, lv));
		}

		if (this.field_16048 != null) {
			this.field_16048 = (class_3284)this.field_16047.stream().filter(arg -> arg.method_14616().equals(this.field_16048.method_14616())).findFirst().orElse(null);
		}

		if (this.field_16048 == null) {
			this.field_16048 = (class_3284)this.field_16047.get(0);
		}

		this.field_16048.method_14478(true);
		this.method_14589(false);
		this.method_14598();
	}

	protected void method_18791() {
		this.field_16049.method_14477(152, 41, 28, 18, TEXTURE);
	}

	public void method_14573() {
		this.field_16052 = null;
		this.field_16048 = null;
		this.client.field_19946.method_18191(false);
	}

	public int method_14585(boolean bl, int i, int j) {
		int k;
		if (this.method_14590() && !bl) {
			k = 177 + (i - j - 200) / 2;
		} else {
			k = (i - j) / 2;
		}

		return k;
	}

	public void method_14587() {
		this.method_14584(!this.method_14590());
	}

	public boolean method_14590() {
		return this.field_20451.method_21392();
	}

	protected void method_14584(boolean bl) {
		this.field_20451.method_21397(bl);
		if (!bl) {
			this.field_16055.method_14611();
		}

		this.method_14601();
	}

	public void method_14579(@Nullable Slot slot) {
		if (slot != null && slot.id < this.field_20450.method_15984()) {
			this.field_16046.method_14551();
			if (this.method_14590()) {
				this.method_14599();
			}
		}
	}

	private void method_14589(boolean bl) {
		List<class_3286> list = this.field_20451.method_18138(this.field_16048.method_14616());
		list.forEach(arg -> arg.method_14626(this.field_16056, this.field_20450.method_15982(), this.field_20450.method_15983(), this.field_20451));
		List<class_3286> list2 = Lists.newArrayList(list);
		list2.removeIf(arg -> !arg.method_14625());
		list2.removeIf(arg -> !arg.method_14633());
		String string = this.field_16052.getText();
		if (!string.isEmpty()) {
			ObjectSet<class_3286> objectSet = new ObjectLinkedOpenHashSet(this.client.method_14460(class_3306.field_16178).method_14707(string.toLowerCase(Locale.ROOT)));
			list2.removeIf(arg -> !objectSet.contains(arg));
		}

		if (this.field_20451.method_21393(this.field_20450)) {
			list2.removeIf(arg -> !arg.method_14630());
		}

		this.field_16055.method_14609(list2, bl);
	}

	private void method_14598() {
		int i = (this.field_16044 - 147) / 2 - this.field_16043 - 30;
		int j = (this.field_16045 - 166) / 2 + 3;
		int k = 27;
		int l = 0;

		for (class_3284 lv : this.field_16047) {
			class_4113 lv2 = lv.method_14616();
			if (lv2 == class_4113.SEARCH || lv2 == class_4113.FURNACE_SEARCH) {
				lv.visible = true;
				lv.method_14480(i, j + 27 * l++);
			} else if (lv.method_18801(this.field_20451)) {
				lv.method_14480(i, j + 27 * l++);
				lv.method_14614(this.client);
			}
		}
	}

	public void method_14594() {
		if (this.method_14590()) {
			if (this.field_16057 != this.client.player.inventory.method_14153()) {
				this.method_14599();
				this.field_16057 = this.client.player.inventory.method_14153();
			}
		}
	}

	private void method_14599() {
		this.field_16056.method_14166();
		this.client.player.inventory.method_15921(this.field_16056);
		this.field_20450.method_15978(this.field_16056);
		this.method_14589(false);
	}

	public void method_14575(int i, int j, float f) {
		if (this.method_14590()) {
			DiffuseLighting.enable();
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 100.0F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int k = (this.field_16044 - 147) / 2 - this.field_16043;
			int l = (this.field_16045 - 166) / 2;
			this.drawTexture(k, l, 1, 1, 147, 166);
			this.field_16052.method_18385(i, j, f);
			DiffuseLighting.disable();

			for (class_3284 lv : this.field_16047) {
				lv.method_891(i, j, f);
			}

			this.field_16049.method_891(i, j, f);
			this.field_16055.method_14604(k, l, i, j, f);
			GlStateManager.popMatrix();
		}
	}

	public void method_14591(int i, int j, int k, int l) {
		if (this.method_14590()) {
			this.field_16055.method_14603(k, l);
			if (this.field_16049.isHovered()) {
				String string = this.method_18796();
				if (this.client.currentScreen != null) {
					this.client.currentScreen.renderTooltip(string, k, l);
				}
			}

			this.method_14595(i, j, k, l);
		}
	}

	protected String method_18796() {
		return I18n.translate(this.field_16049.method_14479() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
	}

	private void method_14595(int i, int j, int k, int l) {
		ItemStack itemStack = null;

		for (int m = 0; m < this.field_16046.method_14557(); m++) {
			class_3278.class_3279 lv = this.field_16046.method_14552(m);
			int n = lv.method_14559() + i;
			int o = lv.method_14560() + j;
			if (k >= n && l >= o && k < n + 16 && l < o + 16) {
				itemStack = lv.method_14561();
			}
		}

		if (itemStack != null && this.client.currentScreen != null) {
			this.client.currentScreen.renderTooltip(this.client.currentScreen.method_14502(itemStack), k, l);
		}
	}

	public void method_14578(int i, int j, boolean bl, float f) {
		this.field_16046.method_14555(this.client, i, j, bl, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.method_14590() && !this.client.player.isSpectator()) {
			if (this.field_16055.method_14605(d, e, i, (this.field_16044 - 147) / 2 - this.field_16043, (this.field_16045 - 166) / 2, 147, 166)) {
				RecipeType recipeType = this.field_16055.method_14602();
				class_3286 lv = this.field_16055.method_14610();
				if (recipeType != null && lv != null) {
					if (!lv.method_14627(recipeType) && this.field_16046.method_14558() == recipeType) {
						return false;
					}

					this.field_16046.method_14551();
					this.client.interactionManager.method_14674(this.client.player.openScreenHandler.syncId, recipeType, Screen.hasShiftDown());
					if (!this.method_14600()) {
						this.method_14584(false);
					}
				}

				return true;
			} else if (this.field_16052.mouseClicked(d, e, i)) {
				return true;
			} else if (this.field_16049.mouseClicked(d, e, i)) {
				boolean bl = this.method_18797();
				this.field_16049.method_14478(bl);
				this.method_14601();
				this.method_14589(false);
				return true;
			} else {
				for (class_3284 lv2 : this.field_16047) {
					if (lv2.mouseClicked(d, e, i)) {
						if (this.field_16048 != lv2) {
							this.field_16048.method_14478(false);
							this.field_16048 = lv2;
							this.field_16048.method_14478(true);
							this.method_14589(true);
						}

						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	protected boolean method_18797() {
		boolean bl = !this.field_20451.method_21398();
		this.field_20451.method_21401(bl);
		return bl;
	}

	public boolean method_18792(double d, double e, int i, int j, int k, int l, int m) {
		if (!this.method_14590()) {
			return true;
		} else {
			boolean bl = d < (double)i || e < (double)j || d >= (double)(i + k) || e >= (double)(j + l);
			boolean bl2 = (double)(i - 147) < d && d < (double)i && (double)j < e && e < (double)(j + l);
			return bl && !bl2 && !this.field_16048.isHovered();
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		this.field_20452 = false;
		if (!this.method_14590() || this.client.player.isSpectator()) {
			return false;
		} else if (i == 256 && !this.method_14600()) {
			this.method_14584(false);
			return true;
		} else if (this.field_16052.keyPressed(i, j, k)) {
			this.method_18798();
			return true;
		} else if (this.client.options.chatKey.method_18166(i, j) && !this.field_16052.isFocused()) {
			this.field_20452 = true;
			this.field_16052.setFocused(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k) {
		this.field_20452 = false;
		return class_4122.super.keyReleased(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_20452) {
			return false;
		} else if (!this.method_14590() || this.client.player.isSpectator()) {
			return false;
		} else if (this.field_16052.charTyped(c, i)) {
			this.method_18798();
			return true;
		} else {
			return class_4122.super.charTyped(c, i);
		}
	}

	private void method_18798() {
		String string = this.field_16052.getText().toLowerCase(Locale.ROOT);
		this.method_14583(string);
		if (!string.equals(this.field_16053)) {
			this.method_14589(false);
			this.field_16053 = string;
		}
	}

	private void method_14583(String string) {
		if ("excitedze".equals(string)) {
			LanguageManager languageManager = this.client.getLanguageManager();
			LanguageDefinition languageDefinition = languageManager.method_14698("en_pt");
			if (languageManager.getLanguage().compareTo(languageDefinition) == 0) {
				return;
			}

			languageManager.setLanguage(languageDefinition);
			this.client.options.language = languageDefinition.getCode();
			this.client.reloadResources();
			this.client.textRenderer.setRightToLeft(languageManager.isRightToLeft());
			this.client.options.save();
		}
	}

	private boolean method_14600() {
		return this.field_16043 == 86;
	}

	public void method_14597() {
		this.method_14598();
		if (this.method_14590()) {
			this.method_14589(false);
		}
	}

	@Override
	public void method_14636(List<RecipeType> list) {
		for (RecipeType recipeType : list) {
			this.client.player.method_14676(recipeType);
		}
	}

	public void method_14580(RecipeType recipeType, List<Slot> list) {
		ItemStack itemStack = recipeType.getOutput();
		this.field_16046.method_14554(recipeType);
		this.field_16046.method_14553(Ingredient.method_14248(itemStack), ((Slot)list.get(0)).x, ((Slot)list.get(0)).y);
		this.method_20429(
			this.field_20450.method_15982(), this.field_20450.method_15983(), this.field_20450.method_15981(), recipeType, recipeType.method_14252().iterator(), 0
		);
	}

	@Override
	public void method_20430(Iterator<Ingredient> iterator, int i, int j, int k, int l) {
		Ingredient ingredient = (Ingredient)iterator.next();
		if (!ingredient.method_16196()) {
			Slot slot = (Slot)this.field_20450.slots.get(i);
			this.field_16046.method_14553(ingredient, slot.x, slot.y);
		}
	}

	protected void method_14601() {
		if (this.client.getNetworkHandler() != null) {
			this.client
				.getNetworkHandler()
				.sendPacket(
					new CraftingBlockData(this.field_20451.method_21392(), this.field_20451.method_21398(), this.field_20451.method_21402(), this.field_20451.method_21406())
				);
		}
	}
}
