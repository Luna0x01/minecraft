package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_4290;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2838;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityItemStackRenderHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class HeldItemRenderer implements ResourceReloadListener {
	public static final Identifier field_20931 = new Identifier("textures/misc/enchanted_item_glint.png");
	private static final Set<Item> field_20933 = Sets.newHashSet(new Item[]{Items.AIR});
	public float field_20932;
	private final ItemModels field_20934;
	private final TextureManager field_20935;
	private final class_2838 field_20936;

	public HeldItemRenderer(TextureManager textureManager, BakedModelManager bakedModelManager, class_2838 arg) {
		this.field_20935 = textureManager;
		this.field_20934 = new ItemModels(bakedModelManager);

		for (Item item : Registry.ITEM) {
			if (!field_20933.contains(item)) {
				this.field_20934.method_19153(item, new class_4290(Registry.ITEM.getId(item), "inventory"));
			}
		}

		this.field_20936 = arg;
	}

	public ItemModels method_19372() {
		return this.field_20934;
	}

	private void method_19394(BakedModel bakedModel, ItemStack itemStack) {
		this.method_19393(bakedModel, -1, itemStack);
	}

	private void method_19392(BakedModel bakedModel, int i) {
		this.method_19393(bakedModel, i, ItemStack.EMPTY);
	}

	private void method_19393(BakedModel bakedModel, int i, ItemStack itemStack) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.BLOCK_NORMALS);
		Random random = new Random();
		long l = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.method_19388(bufferBuilder, bakedModel.method_19561(null, direction, random), i, itemStack);
		}

		random.setSeed(42L);
		this.method_19388(bufferBuilder, bakedModel.method_19561(null, null, random), i, itemStack);
		tessellator.draw();
	}

	public void method_19381(ItemStack itemStack, BakedModel bakedModel) {
		if (!itemStack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			if (bakedModel.isBuiltin()) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableRescaleNormal();
				BlockEntityItemStackRenderHelper.INSTANCE.renderItem(itemStack);
			} else {
				this.method_19394(bakedModel, itemStack);
				if (itemStack.hasEnchantmentGlint()) {
					method_19390(this.field_20935, () -> this.method_19392(bakedModel, -8372020), 8);
				}
			}

			GlStateManager.popMatrix();
		}
	}

	public static void method_19390(TextureManager textureManager, Runnable runnable, int i) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.method_12287(GlStateManager.class_2870.SRC_COLOR, GlStateManager.class_2866.ONE);
		textureManager.bindTexture(field_20931);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale((float)i, (float)i, (float)i);
		float f = (float)(Util.method_20227() % 3000L) / 3000.0F / (float)i;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		runnable.run();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale((float)i, (float)i, (float)i);
		float g = (float)(Util.method_20227() % 4873L) / 4873.0F / (float)i;
		GlStateManager.translate(-g, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		runnable.run();
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	}

	private void method_19386(BufferBuilder bufferBuilder, BakedQuad bakedQuad) {
		Vec3i vec3i = bakedQuad.getFace().getVector();
		bufferBuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
	}

	private void method_19387(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int i) {
		bufferBuilder.putArray(bakedQuad.getVertexData());
		bufferBuilder.putQuadColor(i);
		this.method_19386(bufferBuilder, bakedQuad);
	}

	private void method_19388(BufferBuilder bufferBuilder, List<BakedQuad> list, int i, ItemStack itemStack) {
		boolean bl = i == -1 && !itemStack.isEmpty();
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			int l = i;
			if (bl && bakedQuad.hasColor()) {
				l = this.field_20936.method_12160(itemStack, bakedQuad.getColorIndex());
				l |= -16777216;
			}

			this.method_19387(bufferBuilder, bakedQuad, l);
		}
	}

	public boolean method_19375(ItemStack itemStack) {
		BakedModel bakedModel = this.field_20934.getModel(itemStack);
		return bakedModel == null ? false : bakedModel.hasDepth();
	}

	public void method_19380(ItemStack itemStack, ModelTransformation.Mode mode) {
		if (!itemStack.isEmpty()) {
			BakedModel bakedModel = this.method_19396(itemStack);
			this.method_19382(itemStack, bakedModel, mode, false);
		}
	}

	public BakedModel method_19379(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		BakedModel bakedModel = this.field_20934.getModel(itemStack);
		Item item = itemStack.getItem();
		return !item.hasProperties() ? bakedModel : this.method_19395(bakedModel, itemStack, world, livingEntity);
	}

	public BakedModel method_19398(ItemStack itemStack, World world, LivingEntity livingEntity) {
		Item item = itemStack.getItem();
		BakedModel bakedModel;
		if (item == Items.TRIDENT) {
			bakedModel = this.field_20934.getModelManager().method_19594(new class_4290("minecraft:trident_in_hand#inventory"));
		} else {
			bakedModel = this.field_20934.getModel(itemStack);
		}

		return !item.hasProperties() ? bakedModel : this.method_19395(bakedModel, itemStack, world, livingEntity);
	}

	public BakedModel method_19396(ItemStack itemStack) {
		return this.method_19379(itemStack, null, null);
	}

	private BakedModel method_19395(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		BakedModel bakedModel2 = bakedModel.method_12503().method_19253(bakedModel, itemStack, world, livingEntity);
		return bakedModel2 == null ? this.field_20934.getModelManager().getBakedModel() : bakedModel2;
	}

	public void method_19378(ItemStack itemStack, LivingEntity livingEntity, ModelTransformation.Mode mode, boolean bl) {
		if (!itemStack.isEmpty() && livingEntity != null) {
			BakedModel bakedModel = this.method_19398(itemStack, livingEntity.world, livingEntity);
			this.method_19382(itemStack, bakedModel, mode, bl);
		}
	}

	protected void method_19382(ItemStack itemStack, BakedModel bakedModel, ModelTransformation.Mode mode, boolean bl) {
		if (!itemStack.isEmpty()) {
			this.field_20935.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.field_20935.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			GlStateManager.pushMatrix();
			ModelTransformation modelTransformation = bakedModel.getTransformation();
			ModelTransformation.method_19257(modelTransformation.getTransformation(mode), bl);
			if (this.method_19389(modelTransformation.getTransformation(mode))) {
				GlStateManager.method_12284(GlStateManager.class_2865.FRONT);
			}

			this.method_19381(itemStack, bakedModel);
			GlStateManager.method_12284(GlStateManager.class_2865.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			this.field_20935.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.field_20935.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
		}
	}

	private boolean method_19389(Transformation transformation) {
		return transformation.field_20809.method_19662() < 0.0F ^ transformation.field_20809.method_19667() < 0.0F ^ transformation.field_20809.method_19670() < 0.0F;
	}

	public void method_19376(ItemStack itemStack, int i, int j) {
		this.method_19377(itemStack, i, j, this.method_19396(itemStack));
	}

	protected void method_19377(ItemStack itemStack, int i, int j, BakedModel bakedModel) {
		GlStateManager.pushMatrix();
		this.field_20935.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.field_20935.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.method_19373(i, j, bakedModel.hasDepth());
		bakedModel.getTransformation().apply(ModelTransformation.Mode.GUI);
		this.method_19381(itemStack, bakedModel);
		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		this.field_20935.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.field_20935.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
	}

	private void method_19373(int i, int j, boolean bl) {
		GlStateManager.translate((float)i, (float)j, 100.0F + this.field_20932);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);
		if (bl) {
			GlStateManager.enableLighting();
		} else {
			GlStateManager.disableLighting();
		}
	}

	public void method_19397(ItemStack itemStack, int i, int j) {
		this.method_19374(MinecraftClient.getInstance().player, itemStack, i, j);
	}

	public void method_19374(@Nullable LivingEntity livingEntity, ItemStack itemStack, int i, int j) {
		if (!itemStack.isEmpty()) {
			this.field_20932 += 50.0F;

			try {
				this.method_19377(itemStack, i, j, this.method_19379(itemStack, null, livingEntity));
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Rendering item");
				CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
				crashReportSection.add("Item Type", (CrashCallable<String>)(() -> String.valueOf(itemStack.getItem())));
				crashReportSection.add("Item Damage", (CrashCallable<String>)(() -> String.valueOf(itemStack.getDamage())));
				crashReportSection.add("Item NBT", (CrashCallable<String>)(() -> String.valueOf(itemStack.getNbt())));
				crashReportSection.add("Item Foil", (CrashCallable<String>)(() -> String.valueOf(itemStack.hasEnchantmentGlint())));
				throw new CrashException(crashReport);
			}

			this.field_20932 -= 50.0F;
		}
	}

	public void method_19383(TextRenderer textRenderer, ItemStack itemStack, int i, int j) {
		this.method_19384(textRenderer, itemStack, i, j, null);
	}

	public void method_19384(TextRenderer textRenderer, ItemStack itemStack, int i, int j, @Nullable String string) {
		if (!itemStack.isEmpty()) {
			if (itemStack.getCount() != 1 || string != null) {
				String string2 = string == null ? String.valueOf(itemStack.getCount()) : string;
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableBlend();
				textRenderer.drawWithShadow(string2, (float)(i + 19 - 2 - textRenderer.getStringWidth(string2)), (float)(j + 6 + 3), 16777215);
				GlStateManager.enableBlend();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			if (itemStack.isDamaged()) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				float f = (float)itemStack.getDamage();
				float g = (float)itemStack.getMaxDamage();
				float h = Math.max(0.0F, (g - f) / g);
				int k = Math.round(13.0F - f * 13.0F / g);
				int l = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
				this.method_19385(bufferBuilder, i + 2, j + 13, 13, 2, 0, 0, 0, 255);
				this.method_19385(bufferBuilder, i + 2, j + 13, k, 1, l >> 16 & 0xFF, l >> 8 & 0xFF, l & 0xFF, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlphaTest();
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			float m = clientPlayerEntity == null
				? 0.0F
				: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(itemStack.getItem(), MinecraftClient.getInstance().method_12143());
			if (m > 0.0F) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
				this.method_19385(bufferBuilder2, i, j + MathHelper.floor(16.0F * (1.0F - m)), 16, MathHelper.ceil(16.0F * m), 255, 255, 255, 127);
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}
	}

	private void method_19385(BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o, int p) {
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)(i + 0), (double)(j + 0), 0.0).color(m, n, o, p).next();
		bufferBuilder.vertex((double)(i + 0), (double)(j + l), 0.0).color(m, n, o, p).next();
		bufferBuilder.vertex((double)(i + k), (double)(j + l), 0.0).color(m, n, o, p).next();
		bufferBuilder.vertex((double)(i + k), (double)(j + 0), 0.0).color(m, n, o, p).next();
		Tessellator.getInstance().draw();
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.field_20934.reloadModels();
	}
}
