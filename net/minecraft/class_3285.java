package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_3285 extends ButtonWidget {
	private static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private class_3536 field_20455;
	private class_4471 field_16074;
	private class_3286 field_16075;
	private float field_16076;
	private float field_16077;
	private int field_16078;

	public class_3285() {
		super(0, 0, 0, 25, 25, "");
	}

	public void method_18803(class_3286 arg, class_3283 arg2) {
		this.field_16075 = arg;
		this.field_20455 = (class_3536)arg2.method_18799().player.openScreenHandler;
		this.field_16074 = arg2.method_18800();
		List<RecipeType> list = arg.method_14629(this.field_16074.method_21393(this.field_20455));

		for (RecipeType recipeType : list) {
			if (this.field_16074.method_21407(recipeType)) {
				arg2.method_14608(list);
				this.field_16077 = 15.0F;
				break;
			}
		}
	}

	public class_3286 method_14620() {
		return this.field_16075;
	}

	public void method_14621(int i, int j) {
		this.x = i;
		this.y = j;
	}

	@Override
	public void method_891(int i, int j, float f) {
		if (this.visible) {
			if (!Screen.hasControlDown()) {
				this.field_16076 += f;
			}

			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			DiffuseLighting.enable();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.disableLighting();
			int k = 29;
			if (!this.field_16075.method_14630()) {
				k += 25;
			}

			int l = 206;
			if (this.field_16075.method_14629(this.field_16074.method_21393(this.field_20455)).size() > 1) {
				l += 25;
			}

			boolean bl = this.field_16077 > 0.0F;
			if (bl) {
				float g = 1.0F + 0.1F * (float)Math.sin((double)(this.field_16077 / 15.0F * (float) Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)(this.x + 8), (float)(this.y + 12), 0.0F);
				GlStateManager.scale(g, g, 1.0F);
				GlStateManager.translate((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
				this.field_16077 -= f;
			}

			this.drawTexture(this.x, this.y, k, l, this.width, this.height);
			List<RecipeType> list = this.method_14624();
			this.field_16078 = MathHelper.floor(this.field_16076 / 30.0F) % list.size();
			ItemStack itemStack = ((RecipeType)list.get(this.field_16078)).getOutput();
			int m = 4;
			if (this.field_16075.method_14635() && this.method_14624().size() > 1) {
				minecraftClient.getHeldItemRenderer().method_19397(itemStack, this.x + m + 1, this.y + m + 1);
				m--;
			}

			minecraftClient.getHeldItemRenderer().method_19397(itemStack, this.x + m, this.y + m);
			if (bl) {
				GlStateManager.popMatrix();
			}

			GlStateManager.enableLighting();
			DiffuseLighting.disable();
		}
	}

	private List<RecipeType> method_14624() {
		List<RecipeType> list = this.field_16075.method_14632(true);
		if (!this.field_16074.method_21393(this.field_20455)) {
			list.addAll(this.field_16075.method_14632(false));
		}

		return list;
	}

	public boolean method_14622() {
		return this.method_14624().size() == 1;
	}

	public RecipeType method_14623() {
		List<RecipeType> list = this.method_14624();
		return (RecipeType)list.get(this.field_16078);
	}

	public List<String> method_14618(Screen screen) {
		ItemStack itemStack = ((RecipeType)this.method_14624().get(this.field_16078)).getOutput();
		List<String> list = screen.method_14502(itemStack);
		if (this.field_16075.method_14629(this.field_16074.method_21393(this.field_20455)).size() > 1) {
			list.add(I18n.translate("gui.recipebook.moreRecipes"));
		}

		return list;
	}

	@Override
	public int getWidth() {
		return 25;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0 || i == 1) {
			boolean bl = this.method_18377(d, e);
			if (bl) {
				this.playDownSound(MinecraftClient.getInstance().getSoundManager());
				if (i == 0) {
					this.method_18374(d, e);
				}

				return true;
			}
		}

		return false;
	}
}
