package net.minecraft.client.render.item;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.QuartzBlock;
import net.minecraft.block.RedSandstoneBlock;
import net.minecraft.block.RedSandstoneSlabBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityItemStackRenderHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.MeshDefinition;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class ItemRenderer implements ResourceReloadListener {
	private static final Identifier ITEM_GLINT_TEXTURE = new Identifier("textures/misc/enchanted_item_glint.png");
	private boolean renderingAchievement = true;
	public float zOffset;
	private final ItemModels models;
	private final TextureManager textureManager;

	public ItemRenderer(TextureManager textureManager, BakedModelManager bakedModelManager) {
		this.textureManager = textureManager;
		this.models = new ItemModels(bakedModelManager);
		this.initModels();
	}

	public void setRenderingAchievement(boolean renderingAchievement) {
		this.renderingAchievement = renderingAchievement;
	}

	public ItemModels getModels() {
		return this.models;
	}

	protected void addModel(Item item, int metadata, String id) {
		this.models.putModel(item, metadata, new ModelIdentifier(id, "inventory"));
	}

	protected void addModel(Block block, int metadata, String id) {
		this.addModel(Item.fromBlock(block), metadata, id);
	}

	private void addModel(Block block, String id) {
		this.addModel(block, 0, id);
	}

	private void addModel(Item item, String id) {
		this.addModel(item, 0, id);
	}

	private void renderBakedItemModel(BakedModel model, ItemStack stack) {
		this.renderBakedItemModel(model, -1, stack);
	}

	private void renderBakedItemModel(BakedModel model, int color) {
		this.renderBakedItemModel(model, color, null);
	}

	private void renderBakedItemModel(BakedModel model, int color, ItemStack stack) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.BLOCK_NORMALS);

		for (Direction direction : Direction.values()) {
			this.renderBakedItemQuads(bufferBuilder, model.getByDirection(direction), color, stack);
		}

		this.renderBakedItemQuads(bufferBuilder, model.getQuads(), color, stack);
		tessellator.draw();
	}

	public void renderItem(ItemStack stack, BakedModel model) {
		if (stack != null) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			if (model.isBuiltin()) {
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableRescaleNormal();
				BlockEntityItemStackRenderHelper.INSTANCE.renderItem(stack);
			} else {
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);
				this.renderBakedItemModel(model, stack);
				if (stack.hasEnchantmentGlint()) {
					this.renderGlint(model);
				}
			}

			GlStateManager.popMatrix();
		}
	}

	private void renderGlint(BakedModel model) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(768, 1);
		this.textureManager.bindTexture(ITEM_GLINT_TEXTURE);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = (float)(MinecraftClient.getTime() % 3000L) / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		this.renderBakedItemModel(model, -8372020);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float g = (float)(MinecraftClient.getTime() % 4873L) / 4873.0F / 8.0F;
		GlStateManager.translate(-g, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		this.renderBakedItemModel(model, -8372020);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	}

	private void renderQuad(BufferBuilder bufferBuilder, BakedQuad quad) {
		Vec3i vec3i = quad.getFace().getVector();
		bufferBuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
	}

	private void renderQuad(BufferBuilder bufferBuilder, BakedQuad quad, int color) {
		bufferBuilder.putArray(quad.getVertexData());
		bufferBuilder.putQuadColor(color);
		this.renderQuad(bufferBuilder, quad);
	}

	private void renderBakedItemQuads(BufferBuilder bufferBuilder, List<BakedQuad> quads, int color, ItemStack stack) {
		boolean bl = color == -1 && stack != null;
		int i = 0;

		for (int j = quads.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)quads.get(i);
			int k = color;
			if (bl && bakedQuad.hasColor()) {
				k = stack.getItem().getDisplayColor(stack, bakedQuad.getColorIndex());
				if (GameRenderer.anaglyphEnabled) {
					k = TextureUtil.getAnaglyphColor(k);
				}

				k |= -16777216;
			}

			this.renderQuad(bufferBuilder, bakedQuad, k);
		}
	}

	public boolean hasDepth(ItemStack stack) {
		BakedModel bakedModel = this.models.getModel(stack);
		return bakedModel == null ? false : bakedModel.hasDepth();
	}

	private void preRenderGuiItemModel(ItemStack stack) {
		BakedModel bakedModel = this.models.getModel(stack);
		Item item = stack.getItem();
		if (item != null) {
			boolean bl = bakedModel.hasDepth();
			if (!bl) {
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	public void renderItem(ItemStack stack, ModelTransformation.Mode mode) {
		if (stack != null) {
			BakedModel bakedModel = this.models.getModel(stack);
			this.renderItem(stack, bakedModel, mode);
		}
	}

	public void renderItem(ItemStack stack, LivingEntity entity, ModelTransformation.Mode mode) {
		if (stack != null && entity != null) {
			BakedModel bakedModel = this.models.getModel(stack);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				Item item = stack.getItem();
				ModelIdentifier modelIdentifier = null;
				if (item == Items.FISHING_ROD && playerEntity.fishHook != null) {
					modelIdentifier = new ModelIdentifier("fishing_rod_cast", "inventory");
				} else if (item == Items.BOW && playerEntity.getUsedItem() != null) {
					int i = stack.getMaxUseTime() - playerEntity.getItemUseTicks();
					if (i >= 18) {
						modelIdentifier = new ModelIdentifier("bow_pulling_2", "inventory");
					} else if (i > 13) {
						modelIdentifier = new ModelIdentifier("bow_pulling_1", "inventory");
					} else if (i > 0) {
						modelIdentifier = new ModelIdentifier("bow_pulling_0", "inventory");
					}
				}

				if (modelIdentifier != null) {
					bakedModel = this.models.getModelManager().getByIdentifier(modelIdentifier);
				}
			}

			this.renderItem(stack, bakedModel, mode);
		}
	}

	protected void renderItem(ItemStack stack, BakedModel model, ModelTransformation.Mode transformation) {
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		this.preRenderGuiItemModel(stack);
		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.pushMatrix();
		ModelTransformation modelTransformation = model.getTransformation();
		modelTransformation.apply(transformation);
		if (this.isNegativeScale(modelTransformation.getTransformation(transformation))) {
			GlStateManager.cullFace(1028);
		}

		this.renderItem(stack, model);
		GlStateManager.cullFace(1029);
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
	}

	private boolean isNegativeScale(Transformation transformation) {
		return transformation.scale.x < 0.0F ^ transformation.scale.y < 0.0F ^ transformation.scale.z < 0.0F;
	}

	public void renderGuiItemModel(ItemStack stack, int x, int y) {
		BakedModel bakedModel = this.models.getModel(stack);
		GlStateManager.pushMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.renderGuiItemModelTransformations(x, y, bakedModel.hasDepth());
		bakedModel.getTransformation().apply(ModelTransformation.Mode.GUI);
		this.renderItem(stack, bakedModel);
		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
	}

	private void renderGuiItemModelTransformations(int x, int y, boolean depth) {
		GlStateManager.translate((float)x, (float)y, 100.0F + this.zOffset);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, 1.0F, -1.0F);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		if (depth) {
			GlStateManager.scale(40.0F, 40.0F, 40.0F);
			GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.enableLighting();
		} else {
			GlStateManager.scale(64.0F, 64.0F, 64.0F);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
		}
	}

	public void renderInGuiWithOverrides(ItemStack stack, int x, int y) {
		if (stack != null && stack.getItem() != null) {
			this.zOffset += 50.0F;

			try {
				this.renderGuiItemModel(stack, x, y);
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Rendering item");
				CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
				crashReportSection.add("Item Type", new Callable<String>() {
					public String call() throws Exception {
						return String.valueOf(stack.getItem());
					}
				});
				crashReportSection.add("Item Aux", new Callable<String>() {
					public String call() throws Exception {
						return String.valueOf(stack.getData());
					}
				});
				crashReportSection.add("Item NBT", new Callable<String>() {
					public String call() throws Exception {
						return String.valueOf(stack.getNbt());
					}
				});
				crashReportSection.add("Item Foil", new Callable<String>() {
					public String call() throws Exception {
						return String.valueOf(stack.hasEnchantmentGlint());
					}
				});
				throw new CrashException(crashReport);
			}

			this.zOffset -= 50.0F;
		}
	}

	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y) {
		this.renderGuiItemOverlay(renderer, stack, x, y, null);
	}

	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		if (stack != null) {
			if (stack.count != 1 || countLabel != null) {
				String string = countLabel == null ? String.valueOf(stack.count) : countLabel;
				if (countLabel == null && stack.count < 1) {
					string = Formatting.RED + String.valueOf(stack.count);
				}

				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableBlend();
				renderer.drawWithShadow(string, (float)(x + 19 - 2 - renderer.getStringWidth(string)), (float)(y + 6 + 3), 16777215);
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			if (stack.isDamaged()) {
				int i = (int)Math.round(13.0 - (double)stack.getDamage() * 13.0 / (double)stack.getMaxDamage());
				int j = (int)Math.round(255.0 - (double)stack.getDamage() * 255.0 / (double)stack.getMaxDamage());
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 12, 1, (255 - j) / 4, 64, 0, 255);
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, 255 - j, j, 0, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlphaTest();
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}
	}

	private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		buffer.begin(7, VertexFormats.POSITION_COLOR);
		buffer.vertex((double)(x + 0), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + 0), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + width), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + width), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	private void initModels() {
		this.addModel(Blocks.ANVIL, "anvil_intact");
		this.addModel(Blocks.ANVIL, 1, "anvil_slightly_damaged");
		this.addModel(Blocks.ANVIL, 2, "anvil_very_damaged");
		this.addModel(Blocks.CARPET, DyeColor.BLACK.getId(), "black_carpet");
		this.addModel(Blocks.CARPET, DyeColor.BLUE.getId(), "blue_carpet");
		this.addModel(Blocks.CARPET, DyeColor.BROWN.getId(), "brown_carpet");
		this.addModel(Blocks.CARPET, DyeColor.CYAN.getId(), "cyan_carpet");
		this.addModel(Blocks.CARPET, DyeColor.GRAY.getId(), "gray_carpet");
		this.addModel(Blocks.CARPET, DyeColor.GREEN.getId(), "green_carpet");
		this.addModel(Blocks.CARPET, DyeColor.LIGHT_BLUE.getId(), "light_blue_carpet");
		this.addModel(Blocks.CARPET, DyeColor.LIME.getId(), "lime_carpet");
		this.addModel(Blocks.CARPET, DyeColor.MAGENTA.getId(), "magenta_carpet");
		this.addModel(Blocks.CARPET, DyeColor.ORANGE.getId(), "orange_carpet");
		this.addModel(Blocks.CARPET, DyeColor.PINK.getId(), "pink_carpet");
		this.addModel(Blocks.CARPET, DyeColor.PURPLE.getId(), "purple_carpet");
		this.addModel(Blocks.CARPET, DyeColor.RED.getId(), "red_carpet");
		this.addModel(Blocks.CARPET, DyeColor.SILVER.getId(), "silver_carpet");
		this.addModel(Blocks.CARPET, DyeColor.WHITE.getId(), "white_carpet");
		this.addModel(Blocks.CARPET, DyeColor.YELLOW.getId(), "yellow_carpet");
		this.addModel(Blocks.COBBLESTONE_WALL, WallBlock.WallType.MOSSY.getId(), "mossy_cobblestone_wall");
		this.addModel(Blocks.COBBLESTONE_WALL, WallBlock.WallType.NORMAL.getId(), "cobblestone_wall");
		this.addModel(Blocks.DIRT, DirtBlock.DirtType.COARSE_DIRT.getId(), "coarse_dirt");
		this.addModel(Blocks.DIRT, DirtBlock.DirtType.DIRT.getId(), "dirt");
		this.addModel(Blocks.DIRT, DirtBlock.DirtType.PODZOL.getId(), "podzol");
		this.addModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.DoublePlantType.FERN.getId(), "double_fern");
		this.addModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.DoublePlantType.GRASS.getId(), "double_grass");
		this.addModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.DoublePlantType.PAEONIA.getId(), "paeonia");
		this.addModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.DoublePlantType.ROSE.getId(), "double_rose");
		this.addModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.DoublePlantType.SUNFLOWER.getId(), "sunflower");
		this.addModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.DoublePlantType.SYRINGA.getId(), "syringa");
		this.addModel(Blocks.LEAVES, PlanksBlock.WoodType.BIRCH.getId(), "birch_leaves");
		this.addModel(Blocks.LEAVES, PlanksBlock.WoodType.JUNGLE.getId(), "jungle_leaves");
		this.addModel(Blocks.LEAVES, PlanksBlock.WoodType.OAK.getId(), "oak_leaves");
		this.addModel(Blocks.LEAVES, PlanksBlock.WoodType.SPRUCE.getId(), "spruce_leaves");
		this.addModel(Blocks.LEAVES2, PlanksBlock.WoodType.ACACIA.getId() - 4, "acacia_leaves");
		this.addModel(Blocks.LEAVES2, PlanksBlock.WoodType.DARK_OAK.getId() - 4, "dark_oak_leaves");
		this.addModel(Blocks.LOG, PlanksBlock.WoodType.BIRCH.getId(), "birch_log");
		this.addModel(Blocks.LOG, PlanksBlock.WoodType.JUNGLE.getId(), "jungle_log");
		this.addModel(Blocks.LOG, PlanksBlock.WoodType.OAK.getId(), "oak_log");
		this.addModel(Blocks.LOG, PlanksBlock.WoodType.SPRUCE.getId(), "spruce_log");
		this.addModel(Blocks.LOG2, PlanksBlock.WoodType.ACACIA.getId() - 4, "acacia_log");
		this.addModel(Blocks.LOG2, PlanksBlock.WoodType.DARK_OAK.getId() - 4, "dark_oak_log");
		this.addModel(Blocks.MONSTER_EGG, InfestedBlock.Variants.CHISELED_STONE_BRICK.getId(), "chiseled_brick_monster_egg");
		this.addModel(Blocks.MONSTER_EGG, InfestedBlock.Variants.COBBLESTONE.getId(), "cobblestone_monster_egg");
		this.addModel(Blocks.MONSTER_EGG, InfestedBlock.Variants.CRACKED_STONE_BRICK.getId(), "cracked_brick_monster_egg");
		this.addModel(Blocks.MONSTER_EGG, InfestedBlock.Variants.MOSSY_STONE_BRICK.getId(), "mossy_brick_monster_egg");
		this.addModel(Blocks.MONSTER_EGG, InfestedBlock.Variants.STONE.getId(), "stone_monster_egg");
		this.addModel(Blocks.MONSTER_EGG, InfestedBlock.Variants.STONE_BRICK.getId(), "stone_brick_monster_egg");
		this.addModel(Blocks.PLANKS, PlanksBlock.WoodType.ACACIA.getId(), "acacia_planks");
		this.addModel(Blocks.PLANKS, PlanksBlock.WoodType.BIRCH.getId(), "birch_planks");
		this.addModel(Blocks.PLANKS, PlanksBlock.WoodType.DARK_OAK.getId(), "dark_oak_planks");
		this.addModel(Blocks.PLANKS, PlanksBlock.WoodType.JUNGLE.getId(), "jungle_planks");
		this.addModel(Blocks.PLANKS, PlanksBlock.WoodType.OAK.getId(), "oak_planks");
		this.addModel(Blocks.PLANKS, PlanksBlock.WoodType.SPRUCE.getId(), "spruce_planks");
		this.addModel(Blocks.PRISMARINE, PrismarineBlock.PrismarineType.BRICKS.getId(), "prismarine_bricks");
		this.addModel(Blocks.PRISMARINE, PrismarineBlock.PrismarineType.DARK.getId(), "dark_prismarine");
		this.addModel(Blocks.PRISMARINE, PrismarineBlock.PrismarineType.ROUGH.getId(), "prismarine");
		this.addModel(Blocks.QUARTZ_BLOCK, QuartzBlock.QuartzType.CHISELED.getId(), "chiseled_quartz_block");
		this.addModel(Blocks.QUARTZ_BLOCK, QuartzBlock.QuartzType.DEFAULT.getId(), "quartz_block");
		this.addModel(Blocks.QUARTZ_BLOCK, QuartzBlock.QuartzType.LINES_X.getId(), "quartz_column");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.ALLIUM.getDataIndex(), "allium");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.BLUE_ORCHID.getDataIndex(), "blue_orchid");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.HOUSTONIA.getDataIndex(), "houstonia");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.ORANGE_TULIP.getDataIndex(), "orange_tulip");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.OXEYE_DAISY.getDataIndex(), "oxeye_daisy");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.PINK_TULIP.getDataIndex(), "pink_tulip");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.POPPY.getDataIndex(), "poppy");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.RED_TULIP.getDataIndex(), "red_tulip");
		this.addModel(Blocks.RED_FLOWER, FlowerBlock.FlowerType.WHITE_TULIP.getDataIndex(), "white_tulip");
		this.addModel(Blocks.SAND, SandBlock.SandType.RED_SAND.getId(), "red_sand");
		this.addModel(Blocks.SAND, SandBlock.SandType.SAND.getId(), "sand");
		this.addModel(Blocks.SANDSTONE, SandstoneBlock.SandstoneType.CHISELED.getId(), "chiseled_sandstone");
		this.addModel(Blocks.SANDSTONE, SandstoneBlock.SandstoneType.DEFAULT.getId(), "sandstone");
		this.addModel(Blocks.SANDSTONE, SandstoneBlock.SandstoneType.SMOOTH.getId(), "smooth_sandstone");
		this.addModel(Blocks.RED_SANDSTONE, RedSandstoneBlock.RedSandstoneType.CHISELED.getId(), "chiseled_red_sandstone");
		this.addModel(Blocks.RED_SANDSTONE, RedSandstoneBlock.RedSandstoneType.DEFAULT.getId(), "red_sandstone");
		this.addModel(Blocks.RED_SANDSTONE, RedSandstoneBlock.RedSandstoneType.SMOOTH.getId(), "smooth_red_sandstone");
		this.addModel(Blocks.SAPLING, PlanksBlock.WoodType.ACACIA.getId(), "acacia_sapling");
		this.addModel(Blocks.SAPLING, PlanksBlock.WoodType.BIRCH.getId(), "birch_sapling");
		this.addModel(Blocks.SAPLING, PlanksBlock.WoodType.DARK_OAK.getId(), "dark_oak_sapling");
		this.addModel(Blocks.SAPLING, PlanksBlock.WoodType.JUNGLE.getId(), "jungle_sapling");
		this.addModel(Blocks.SAPLING, PlanksBlock.WoodType.OAK.getId(), "oak_sapling");
		this.addModel(Blocks.SAPLING, PlanksBlock.WoodType.SPRUCE.getId(), "spruce_sapling");
		this.addModel(Blocks.SPONGE, 0, "sponge");
		this.addModel(Blocks.SPONGE, 1, "sponge_wet");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.BLACK.getId(), "black_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.BLUE.getId(), "blue_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.BROWN.getId(), "brown_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.CYAN.getId(), "cyan_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.GRAY.getId(), "gray_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.GREEN.getId(), "green_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.LIGHT_BLUE.getId(), "light_blue_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.LIME.getId(), "lime_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.MAGENTA.getId(), "magenta_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.ORANGE.getId(), "orange_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.PINK.getId(), "pink_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.PURPLE.getId(), "purple_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.RED.getId(), "red_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.SILVER.getId(), "silver_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.WHITE.getId(), "white_stained_glass");
		this.addModel(Blocks.STAINED_GLASS, DyeColor.YELLOW.getId(), "yellow_stained_glass");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.BLACK.getId(), "black_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.BLUE.getId(), "blue_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.BROWN.getId(), "brown_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.CYAN.getId(), "cyan_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.GRAY.getId(), "gray_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.GREEN.getId(), "green_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.LIGHT_BLUE.getId(), "light_blue_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.LIME.getId(), "lime_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.MAGENTA.getId(), "magenta_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.ORANGE.getId(), "orange_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.PINK.getId(), "pink_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.PURPLE.getId(), "purple_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.RED.getId(), "red_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.SILVER.getId(), "silver_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.WHITE.getId(), "white_stained_glass_pane");
		this.addModel(Blocks.STAINED_GLASS_PANE, DyeColor.YELLOW.getId(), "yellow_stained_glass_pane");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.BLACK.getId(), "black_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.BLUE.getId(), "blue_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.BROWN.getId(), "brown_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.CYAN.getId(), "cyan_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.GRAY.getId(), "gray_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.GREEN.getId(), "green_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.LIGHT_BLUE.getId(), "light_blue_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.LIME.getId(), "lime_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.MAGENTA.getId(), "magenta_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.ORANGE.getId(), "orange_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.PINK.getId(), "pink_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.PURPLE.getId(), "purple_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.RED.getId(), "red_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.SILVER.getId(), "silver_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.WHITE.getId(), "white_stained_hardened_clay");
		this.addModel(Blocks.STAINED_TERRACOTTA, DyeColor.YELLOW.getId(), "yellow_stained_hardened_clay");
		this.addModel(Blocks.STONE, StoneBlock.StoneType.ANDESITE.byId(), "andesite");
		this.addModel(Blocks.STONE, StoneBlock.StoneType.POLISHED_ANDESITE.byId(), "andesite_smooth");
		this.addModel(Blocks.STONE, StoneBlock.StoneType.DIORITE.byId(), "diorite");
		this.addModel(Blocks.STONE, StoneBlock.StoneType.POLISHED_DIORITE.byId(), "diorite_smooth");
		this.addModel(Blocks.STONE, StoneBlock.StoneType.GRANITE.byId(), "granite");
		this.addModel(Blocks.STONE, StoneBlock.StoneType.POLISHED_GRANITE.byId(), "granite_smooth");
		this.addModel(Blocks.STONE, StoneBlock.StoneType.STONE.byId(), "stone");
		this.addModel(Blocks.STONE_BRICKS, StoneBrickBlock.Type.CRACKED.byId(), "cracked_stonebrick");
		this.addModel(Blocks.STONE_BRICKS, StoneBrickBlock.Type.DEFAULT.byId(), "stonebrick");
		this.addModel(Blocks.STONE_BRICKS, StoneBrickBlock.Type.CHISELED.byId(), "chiseled_stonebrick");
		this.addModel(Blocks.STONE_BRICKS, StoneBrickBlock.Type.MOSSY.byId(), "mossy_stonebrick");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.BRICK.getId(), "brick_slab");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.COBBLESTONE.getId(), "cobblestone_slab");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.WOOD.getId(), "old_wood_slab");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.NETHER_BRICK.getId(), "nether_brick_slab");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.QUARTZ.getId(), "quartz_slab");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.SANDSTONE.getId(), "sandstone_slab");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.STONE_BRICK.getId(), "stone_brick_slab");
		this.addModel(Blocks.STONE_SLAB, StoneSlabBlock.SlabType.STONE.getId(), "stone_slab");
		this.addModel(Blocks.STONE_SLAB2, RedSandstoneSlabBlock.SlabType.RED_SANDSTONE.getId(), "red_sandstone_slab");
		this.addModel(Blocks.TALLGRASS, TallPlantBlock.GrassType.DEAD_BUSH.getId(), "dead_bush");
		this.addModel(Blocks.TALLGRASS, TallPlantBlock.GrassType.FERN.getId(), "fern");
		this.addModel(Blocks.TALLGRASS, TallPlantBlock.GrassType.GRASS.getId(), "tall_grass");
		this.addModel(Blocks.WOODEN_SLAB, PlanksBlock.WoodType.ACACIA.getId(), "acacia_slab");
		this.addModel(Blocks.WOODEN_SLAB, PlanksBlock.WoodType.BIRCH.getId(), "birch_slab");
		this.addModel(Blocks.WOODEN_SLAB, PlanksBlock.WoodType.DARK_OAK.getId(), "dark_oak_slab");
		this.addModel(Blocks.WOODEN_SLAB, PlanksBlock.WoodType.JUNGLE.getId(), "jungle_slab");
		this.addModel(Blocks.WOODEN_SLAB, PlanksBlock.WoodType.OAK.getId(), "oak_slab");
		this.addModel(Blocks.WOODEN_SLAB, PlanksBlock.WoodType.SPRUCE.getId(), "spruce_slab");
		this.addModel(Blocks.WOOL, DyeColor.BLACK.getId(), "black_wool");
		this.addModel(Blocks.WOOL, DyeColor.BLUE.getId(), "blue_wool");
		this.addModel(Blocks.WOOL, DyeColor.BROWN.getId(), "brown_wool");
		this.addModel(Blocks.WOOL, DyeColor.CYAN.getId(), "cyan_wool");
		this.addModel(Blocks.WOOL, DyeColor.GRAY.getId(), "gray_wool");
		this.addModel(Blocks.WOOL, DyeColor.GREEN.getId(), "green_wool");
		this.addModel(Blocks.WOOL, DyeColor.LIGHT_BLUE.getId(), "light_blue_wool");
		this.addModel(Blocks.WOOL, DyeColor.LIME.getId(), "lime_wool");
		this.addModel(Blocks.WOOL, DyeColor.MAGENTA.getId(), "magenta_wool");
		this.addModel(Blocks.WOOL, DyeColor.ORANGE.getId(), "orange_wool");
		this.addModel(Blocks.WOOL, DyeColor.PINK.getId(), "pink_wool");
		this.addModel(Blocks.WOOL, DyeColor.PURPLE.getId(), "purple_wool");
		this.addModel(Blocks.WOOL, DyeColor.RED.getId(), "red_wool");
		this.addModel(Blocks.WOOL, DyeColor.SILVER.getId(), "silver_wool");
		this.addModel(Blocks.WOOL, DyeColor.WHITE.getId(), "white_wool");
		this.addModel(Blocks.WOOL, DyeColor.YELLOW.getId(), "yellow_wool");
		this.addModel(Blocks.ACACIA_STAIRS, "acacia_stairs");
		this.addModel(Blocks.ACTIVATOR_RAIL, "activator_rail");
		this.addModel(Blocks.BEACON, "beacon");
		this.addModel(Blocks.BEDROCK, "bedrock");
		this.addModel(Blocks.BIRCH_STAIRS, "birch_stairs");
		this.addModel(Blocks.BOOKSHELF, "bookshelf");
		this.addModel(Blocks.BRICKS, "brick_block");
		this.addModel(Blocks.BRICKS, "brick_block");
		this.addModel(Blocks.BRICK_STAIRS, "brick_stairs");
		this.addModel(Blocks.BROWN_MUSHROOM, "brown_mushroom");
		this.addModel(Blocks.CACTUS, "cactus");
		this.addModel(Blocks.CLAY, "clay");
		this.addModel(Blocks.COAL_BLOCK, "coal_block");
		this.addModel(Blocks.COAL_ORE, "coal_ore");
		this.addModel(Blocks.COBBLESTONE, "cobblestone");
		this.addModel(Blocks.CRAFTING_TABLE, "crafting_table");
		this.addModel(Blocks.DARK_OAK_STAIRS, "dark_oak_stairs");
		this.addModel(Blocks.DAYLIGHT_DETECTOR, "daylight_detector");
		this.addModel(Blocks.DEADBUSH, "dead_bush");
		this.addModel(Blocks.DETECTOR_RAIL, "detector_rail");
		this.addModel(Blocks.DIAMOND_BLOCK, "diamond_block");
		this.addModel(Blocks.DIAMOND_ORE, "diamond_ore");
		this.addModel(Blocks.DISPENSER, "dispenser");
		this.addModel(Blocks.DROPPER, "dropper");
		this.addModel(Blocks.EMERALD_BLOCK, "emerald_block");
		this.addModel(Blocks.EMERALD_ORE, "emerald_ore");
		this.addModel(Blocks.ENCHANTING_TABLE, "enchanting_table");
		this.addModel(Blocks.END_PORTAL_FRAME, "end_portal_frame");
		this.addModel(Blocks.END_STONE, "end_stone");
		this.addModel(Blocks.OAK_FENCE, "oak_fence");
		this.addModel(Blocks.SPRUCE_FENCE, "spruce_fence");
		this.addModel(Blocks.BIRCH_FENCE, "birch_fence");
		this.addModel(Blocks.JUNGLE_FENCE, "jungle_fence");
		this.addModel(Blocks.DARK_OAK_FENCE, "dark_oak_fence");
		this.addModel(Blocks.ACACIA_FENCE, "acacia_fence");
		this.addModel(Blocks.OAK_FENCE_GATE, "oak_fence_gate");
		this.addModel(Blocks.SPRUCE_FENCE_GATE, "spruce_fence_gate");
		this.addModel(Blocks.BIRCH_FENCE_GATE, "birch_fence_gate");
		this.addModel(Blocks.JUNGLE_FENCE_GATE, "jungle_fence_gate");
		this.addModel(Blocks.DARK_OAK_FENCE_GATE, "dark_oak_fence_gate");
		this.addModel(Blocks.ACACIA_FENCE_GATE, "acacia_fence_gate");
		this.addModel(Blocks.FURNACE, "furnace");
		this.addModel(Blocks.GLASS, "glass");
		this.addModel(Blocks.GLASS_PANE, "glass_pane");
		this.addModel(Blocks.GLOWSTONE, "glowstone");
		this.addModel(Blocks.POWERED_RAIL, "golden_rail");
		this.addModel(Blocks.GOLD_BLOCK, "gold_block");
		this.addModel(Blocks.GOLD_ORE, "gold_ore");
		this.addModel(Blocks.GRASS, "grass");
		this.addModel(Blocks.GRAVEL, "gravel");
		this.addModel(Blocks.TERRACOTTA, "hardened_clay");
		this.addModel(Blocks.HAY_BALE, "hay_block");
		this.addModel(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, "heavy_weighted_pressure_plate");
		this.addModel(Blocks.HOPPER, "hopper");
		this.addModel(Blocks.ICE, "ice");
		this.addModel(Blocks.IRON_BARS, "iron_bars");
		this.addModel(Blocks.IRON_BLOCK, "iron_block");
		this.addModel(Blocks.IRON_ORE, "iron_ore");
		this.addModel(Blocks.IRON_TRAPDOOR, "iron_trapdoor");
		this.addModel(Blocks.JUKEBOX, "jukebox");
		this.addModel(Blocks.JUNGLE_STAIRS, "jungle_stairs");
		this.addModel(Blocks.LADDER, "ladder");
		this.addModel(Blocks.LAPIS_LAZULI_BLOCK, "lapis_block");
		this.addModel(Blocks.LAPIS_LAZULI_ORE, "lapis_ore");
		this.addModel(Blocks.LEVER, "lever");
		this.addModel(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, "light_weighted_pressure_plate");
		this.addModel(Blocks.JACK_O_LANTERN, "lit_pumpkin");
		this.addModel(Blocks.MELON_BLOCK, "melon_block");
		this.addModel(Blocks.MOSSY_COBBLESTONE, "mossy_cobblestone");
		this.addModel(Blocks.MYCELIUM, "mycelium");
		this.addModel(Blocks.NETHERRACK, "netherrack");
		this.addModel(Blocks.NETHER_BRICKS, "nether_brick");
		this.addModel(Blocks.NETHER_BRICK_FENCE, "nether_brick_fence");
		this.addModel(Blocks.NETHER_BRICK_STAIRS, "nether_brick_stairs");
		this.addModel(Blocks.NOTEBLOCK, "noteblock");
		this.addModel(Blocks.WOODEN_STAIRS, "oak_stairs");
		this.addModel(Blocks.OBSIDIAN, "obsidian");
		this.addModel(Blocks.PACKED_ICE, "packed_ice");
		this.addModel(Blocks.PISTON, "piston");
		this.addModel(Blocks.PUMPKIN, "pumpkin");
		this.addModel(Blocks.NETHER_QUARTZ_ORE, "quartz_ore");
		this.addModel(Blocks.QUARTZ_STAIRS, "quartz_stairs");
		this.addModel(Blocks.RAIL, "rail");
		this.addModel(Blocks.REDSTONE_BLOCK, "redstone_block");
		this.addModel(Blocks.REDSTONE_LAMP, "redstone_lamp");
		this.addModel(Blocks.REDSTONE_ORE, "redstone_ore");
		this.addModel(Blocks.REDSTONE_TORCH, "redstone_torch");
		this.addModel(Blocks.RED_MUSHROOM, "red_mushroom");
		this.addModel(Blocks.SANDSTONE_STAIRS, "sandstone_stairs");
		this.addModel(Blocks.RED_SANDSTONE_STAIRS, "red_sandstone_stairs");
		this.addModel(Blocks.SEA_LANTERN, "sea_lantern");
		this.addModel(Blocks.SLIME_BLOCK, "slime");
		this.addModel(Blocks.SNOW, "snow");
		this.addModel(Blocks.SNOW_LAYER, "snow_layer");
		this.addModel(Blocks.SOULSAND, "soul_sand");
		this.addModel(Blocks.SPRUCE_STAIRS, "spruce_stairs");
		this.addModel(Blocks.STICKY_PISTON, "sticky_piston");
		this.addModel(Blocks.STONE_BRICK_STAIRS, "stone_brick_stairs");
		this.addModel(Blocks.STONE_BUTTON, "stone_button");
		this.addModel(Blocks.STONE_PRESSURE_PLATE, "stone_pressure_plate");
		this.addModel(Blocks.STONE_STAIRS, "stone_stairs");
		this.addModel(Blocks.TNT, "tnt");
		this.addModel(Blocks.TORCH, "torch");
		this.addModel(Blocks.TRAPDOOR, "trapdoor");
		this.addModel(Blocks.TRIPWIRE_HOOK, "tripwire_hook");
		this.addModel(Blocks.VINE, "vine");
		this.addModel(Blocks.LILY_PAD, "waterlily");
		this.addModel(Blocks.COBWEB, "web");
		this.addModel(Blocks.WOODEN_BUTTON, "wooden_button");
		this.addModel(Blocks.WOODEN_PRESSURE_PLATE, "wooden_pressure_plate");
		this.addModel(Blocks.YELLOW_FLOWER, FlowerBlock.FlowerType.DANDELION.getDataIndex(), "dandelion");
		this.addModel(Blocks.CHEST, "chest");
		this.addModel(Blocks.TRAPPED_CHEST, "trapped_chest");
		this.addModel(Blocks.ENDERCHEST, "ender_chest");
		this.addModel(Items.IRON_SHOVEL, "iron_shovel");
		this.addModel(Items.IRON_PICKAXE, "iron_pickaxe");
		this.addModel(Items.IRON_AXE, "iron_axe");
		this.addModel(Items.FLINT_AND_STEEL, "flint_and_steel");
		this.addModel(Items.APPLE, "apple");
		this.addModel(Items.BOW, 0, "bow");
		this.addModel(Items.BOW, 1, "bow_pulling_0");
		this.addModel(Items.BOW, 2, "bow_pulling_1");
		this.addModel(Items.BOW, 3, "bow_pulling_2");
		this.addModel(Items.ARROW, "arrow");
		this.addModel(Items.COAL, 0, "coal");
		this.addModel(Items.COAL, 1, "charcoal");
		this.addModel(Items.DIAMOND, "diamond");
		this.addModel(Items.IRON_INGOT, "iron_ingot");
		this.addModel(Items.GOLD_INGOT, "gold_ingot");
		this.addModel(Items.IRON_SWORD, "iron_sword");
		this.addModel(Items.WOODEN_SWORD, "wooden_sword");
		this.addModel(Items.WOODEN_SHOVEL, "wooden_shovel");
		this.addModel(Items.WOODEN_PICKAXE, "wooden_pickaxe");
		this.addModel(Items.WOODEN_AXE, "wooden_axe");
		this.addModel(Items.STONE_SWORD, "stone_sword");
		this.addModel(Items.STONE_SHOVEL, "stone_shovel");
		this.addModel(Items.STONE_PICKAXE, "stone_pickaxe");
		this.addModel(Items.STONE_AXE, "stone_axe");
		this.addModel(Items.DIAMOND_SWORD, "diamond_sword");
		this.addModel(Items.DIAMOND_SHOVEL, "diamond_shovel");
		this.addModel(Items.DIAMOND_PICKAXE, "diamond_pickaxe");
		this.addModel(Items.DIAMOND_AXE, "diamond_axe");
		this.addModel(Items.STICK, "stick");
		this.addModel(Items.BOWL, "bowl");
		this.addModel(Items.MUSHROOM_STEW, "mushroom_stew");
		this.addModel(Items.GOLDEN_SWORD, "golden_sword");
		this.addModel(Items.GOLDEN_SHOVEL, "golden_shovel");
		this.addModel(Items.GOLDEN_PICKAXE, "golden_pickaxe");
		this.addModel(Items.GOLDEN_AXE, "golden_axe");
		this.addModel(Items.STRING, "string");
		this.addModel(Items.FEATHER, "feather");
		this.addModel(Items.GUNPOWDER, "gunpowder");
		this.addModel(Items.WOODEN_HOE, "wooden_hoe");
		this.addModel(Items.STONE_HOE, "stone_hoe");
		this.addModel(Items.IRON_HOE, "iron_hoe");
		this.addModel(Items.DIAMOND_HOE, "diamond_hoe");
		this.addModel(Items.GOLDEN_HOE, "golden_hoe");
		this.addModel(Items.WHEAT_SEEDS, "wheat_seeds");
		this.addModel(Items.WHEAT, "wheat");
		this.addModel(Items.BREAD, "bread");
		this.addModel(Items.LEATHER_HELMET, "leather_helmet");
		this.addModel(Items.LEATHER_CHESTPLATE, "leather_chestplate");
		this.addModel(Items.LEATHER_LEGGINGS, "leather_leggings");
		this.addModel(Items.LEATHER_BOOTS, "leather_boots");
		this.addModel(Items.CHAINMAIL_HELMET, "chainmail_helmet");
		this.addModel(Items.CHAINMAIL_CHESTPLATE, "chainmail_chestplate");
		this.addModel(Items.CHAINMAIL_LEGGINGS, "chainmail_leggings");
		this.addModel(Items.CHAINMAIL_BOOTS, "chainmail_boots");
		this.addModel(Items.IRON_HELMET, "iron_helmet");
		this.addModel(Items.IRON_CHESTPLATE, "iron_chestplate");
		this.addModel(Items.IRON_LEGGINGS, "iron_leggings");
		this.addModel(Items.IRON_BOOTS, "iron_boots");
		this.addModel(Items.DIAMOND_HELMET, "diamond_helmet");
		this.addModel(Items.DIAMOND_CHESTPLATE, "diamond_chestplate");
		this.addModel(Items.DIAMOND_LEGGINGS, "diamond_leggings");
		this.addModel(Items.DIAMOND_BOOTS, "diamond_boots");
		this.addModel(Items.GOLDEN_HELMET, "golden_helmet");
		this.addModel(Items.GOLDEN_CHESTPLATE, "golden_chestplate");
		this.addModel(Items.GOLDEN_LEGGINGS, "golden_leggings");
		this.addModel(Items.GOLDEN_BOOTS, "golden_boots");
		this.addModel(Items.FLINT, "flint");
		this.addModel(Items.RAW_PORKCHOP, "porkchop");
		this.addModel(Items.COOKED_PORKCHOP, "cooked_porkchop");
		this.addModel(Items.PAINTING, "painting");
		this.addModel(Items.GOLDEN_APPLE, "golden_apple");
		this.addModel(Items.GOLDEN_APPLE, 1, "golden_apple");
		this.addModel(Items.SIGN, "sign");
		this.addModel(Items.OAK_DOOR, "oak_door");
		this.addModel(Items.SPRUCE_DOOR, "spruce_door");
		this.addModel(Items.BIRCH_DOOR, "birch_door");
		this.addModel(Items.JUNGLE_DOOR, "jungle_door");
		this.addModel(Items.ACACIA_DOOR, "acacia_door");
		this.addModel(Items.DARK_OAK_DOOR, "dark_oak_door");
		this.addModel(Items.BUCKET, "bucket");
		this.addModel(Items.WATER_BUCKET, "water_bucket");
		this.addModel(Items.LAVA_BUCKET, "lava_bucket");
		this.addModel(Items.MINECART, "minecart");
		this.addModel(Items.SADDLE, "saddle");
		this.addModel(Items.IRON_DOOR, "iron_door");
		this.addModel(Items.REDSTONE, "redstone");
		this.addModel(Items.SNOWBALL, "snowball");
		this.addModel(Items.BOAT, "boat");
		this.addModel(Items.LEATHER, "leather");
		this.addModel(Items.MILK_BUCKET, "milk_bucket");
		this.addModel(Items.BRICK, "brick");
		this.addModel(Items.CLAY_BALL, "clay_ball");
		this.addModel(Items.SUGARCANE, "reeds");
		this.addModel(Items.PAPER, "paper");
		this.addModel(Items.BOOK, "book");
		this.addModel(Items.SLIME_BALL, "slime_ball");
		this.addModel(Items.MINECART_WITH_CHEST, "chest_minecart");
		this.addModel(Items.MINECART_WITH_FURNACE, "furnace_minecart");
		this.addModel(Items.EGG, "egg");
		this.addModel(Items.COMPASS, "compass");
		this.addModel(Items.FISHING_ROD, "fishing_rod");
		this.addModel(Items.FISHING_ROD, 1, "fishing_rod_cast");
		this.addModel(Items.CLOCK, "clock");
		this.addModel(Items.GLOWSTONE_DUST, "glowstone_dust");
		this.addModel(Items.RAW_FISH, FishItem.FishType.COD.getId(), "cod");
		this.addModel(Items.RAW_FISH, FishItem.FishType.SALMON.getId(), "salmon");
		this.addModel(Items.RAW_FISH, FishItem.FishType.CLOWNFISH.getId(), "clownfish");
		this.addModel(Items.RAW_FISH, FishItem.FishType.PUFFERFISH.getId(), "pufferfish");
		this.addModel(Items.COOKED_FISH, FishItem.FishType.COD.getId(), "cooked_cod");
		this.addModel(Items.COOKED_FISH, FishItem.FishType.SALMON.getId(), "cooked_salmon");
		this.addModel(Items.DYE, DyeColor.BLACK.getSwappedId(), "dye_black");
		this.addModel(Items.DYE, DyeColor.RED.getSwappedId(), "dye_red");
		this.addModel(Items.DYE, DyeColor.GREEN.getSwappedId(), "dye_green");
		this.addModel(Items.DYE, DyeColor.BROWN.getSwappedId(), "dye_brown");
		this.addModel(Items.DYE, DyeColor.BLUE.getSwappedId(), "dye_blue");
		this.addModel(Items.DYE, DyeColor.PURPLE.getSwappedId(), "dye_purple");
		this.addModel(Items.DYE, DyeColor.CYAN.getSwappedId(), "dye_cyan");
		this.addModel(Items.DYE, DyeColor.SILVER.getSwappedId(), "dye_silver");
		this.addModel(Items.DYE, DyeColor.GRAY.getSwappedId(), "dye_gray");
		this.addModel(Items.DYE, DyeColor.PINK.getSwappedId(), "dye_pink");
		this.addModel(Items.DYE, DyeColor.LIME.getSwappedId(), "dye_lime");
		this.addModel(Items.DYE, DyeColor.YELLOW.getSwappedId(), "dye_yellow");
		this.addModel(Items.DYE, DyeColor.LIGHT_BLUE.getSwappedId(), "dye_light_blue");
		this.addModel(Items.DYE, DyeColor.MAGENTA.getSwappedId(), "dye_magenta");
		this.addModel(Items.DYE, DyeColor.ORANGE.getSwappedId(), "dye_orange");
		this.addModel(Items.DYE, DyeColor.WHITE.getSwappedId(), "dye_white");
		this.addModel(Items.BONE, "bone");
		this.addModel(Items.SUGAR, "sugar");
		this.addModel(Items.CAKE, "cake");
		this.addModel(Items.BED, "bed");
		this.addModel(Items.REPEATER, "repeater");
		this.addModel(Items.COOKIE, "cookie");
		this.addModel(Items.SHEARS, "shears");
		this.addModel(Items.MELON, "melon");
		this.addModel(Items.PUMPKIN_SEEDS, "pumpkin_seeds");
		this.addModel(Items.MELON_SEEDS, "melon_seeds");
		this.addModel(Items.BEEF, "beef");
		this.addModel(Items.COOKED_BEEF, "cooked_beef");
		this.addModel(Items.CHICKEN, "chicken");
		this.addModel(Items.COOKED_CHICKEN, "cooked_chicken");
		this.addModel(Items.RAW_RABBIT, "rabbit");
		this.addModel(Items.COOKED_RABBIT, "cooked_rabbit");
		this.addModel(Items.MUTTON, "mutton");
		this.addModel(Items.COOKED_MUTTON, "cooked_mutton");
		this.addModel(Items.RABBIT_FOOT, "rabbit_foot");
		this.addModel(Items.RABBIT_HIDE, "rabbit_hide");
		this.addModel(Items.RABBIT_STEW, "rabbit_stew");
		this.addModel(Items.ROTTEN_FLESH, "rotten_flesh");
		this.addModel(Items.ENDER_PEARL, "ender_pearl");
		this.addModel(Items.BLAZE_ROD, "blaze_rod");
		this.addModel(Items.GHAST_TEAR, "ghast_tear");
		this.addModel(Items.GOLD_NUGGET, "gold_nugget");
		this.addModel(Items.NETHER_WART, "nether_wart");
		this.models.putMesh(Items.POTION, new MeshDefinition() {
			@Override
			public ModelIdentifier getIdentifier(ItemStack stack) {
				return PotionItem.isThrowable(stack.getData()) ? new ModelIdentifier("bottle_splash", "inventory") : new ModelIdentifier("bottle_drinkable", "inventory");
			}
		});
		this.addModel(Items.GLASS_BOTTLE, "glass_bottle");
		this.addModel(Items.SPIDER_EYE, "spider_eye");
		this.addModel(Items.FERMENTED_SPIDER_EYE, "fermented_spider_eye");
		this.addModel(Items.BLAZE_POWDER, "blaze_powder");
		this.addModel(Items.MAGMA_CREAM, "magma_cream");
		this.addModel(Items.BREWING_STAND, "brewing_stand");
		this.addModel(Items.CAULDRON, "cauldron");
		this.addModel(Items.EYE_OF_ENDER, "ender_eye");
		this.addModel(Items.GLISTERING_MELON, "speckled_melon");
		this.models.putMesh(Items.SPAWN_EGG, new MeshDefinition() {
			@Override
			public ModelIdentifier getIdentifier(ItemStack stack) {
				return new ModelIdentifier("spawn_egg", "inventory");
			}
		});
		this.addModel(Items.EXPERIENCE_BOTTLE, "experience_bottle");
		this.addModel(Items.FIRE_CHARGE, "fire_charge");
		this.addModel(Items.WRITABLE_BOOK, "writable_book");
		this.addModel(Items.EMERALD, "emerald");
		this.addModel(Items.ITEM_FRAME, "item_frame");
		this.addModel(Items.FLOWER_POT, "flower_pot");
		this.addModel(Items.CARROT, "carrot");
		this.addModel(Items.POTATO, "potato");
		this.addModel(Items.BAKED_POTATO, "baked_potato");
		this.addModel(Items.POISONOUS_POTATO, "poisonous_potato");
		this.addModel(Items.MAP, "map");
		this.addModel(Items.GOLDEN_CARROT, "golden_carrot");
		this.addModel(Items.SKULL, 0, "skull_skeleton");
		this.addModel(Items.SKULL, 1, "skull_wither");
		this.addModel(Items.SKULL, 2, "skull_zombie");
		this.addModel(Items.SKULL, 3, "skull_char");
		this.addModel(Items.SKULL, 4, "skull_creeper");
		this.addModel(Items.CARROT_ON_A_STICK, "carrot_on_a_stick");
		this.addModel(Items.NETHER_STAR, "nether_star");
		this.addModel(Items.PUMPKIN_PIE, "pumpkin_pie");
		this.addModel(Items.FIREWORK_CHARGE, "firework_charge");
		this.addModel(Items.COMPARATOR, "comparator");
		this.addModel(Items.NETHERBRICK, "netherbrick");
		this.addModel(Items.QUARTZ, "quartz");
		this.addModel(Items.MINECART_WITH_TNT, "tnt_minecart");
		this.addModel(Items.MINECART_WITH_HOPPER, "hopper_minecart");
		this.addModel(Items.ARMOR_STAND, "armor_stand");
		this.addModel(Items.IRON_HORSE_ARMOR, "iron_horse_armor");
		this.addModel(Items.GOLDEN_HORSE_ARMOR, "golden_horse_armor");
		this.addModel(Items.DIAMOND_HORSE_ARMOR, "diamond_horse_armor");
		this.addModel(Items.LEAD, "lead");
		this.addModel(Items.NAME_TAG, "name_tag");
		this.models.putMesh(Items.BANNER, new MeshDefinition() {
			@Override
			public ModelIdentifier getIdentifier(ItemStack stack) {
				return new ModelIdentifier("banner", "inventory");
			}
		});
		this.addModel(Items.RECORD_13, "record_13");
		this.addModel(Items.RECORD_CAT, "record_cat");
		this.addModel(Items.RECORD_BLOCKS, "record_blocks");
		this.addModel(Items.RECORD_CHIRP, "record_chirp");
		this.addModel(Items.RECORD_FAR, "record_far");
		this.addModel(Items.RECORD_MALL, "record_mall");
		this.addModel(Items.RECORD_MELLOHI, "record_mellohi");
		this.addModel(Items.RECORD_STAL, "record_stal");
		this.addModel(Items.RECORD_STRAD, "record_strad");
		this.addModel(Items.RECORD_WARD, "record_ward");
		this.addModel(Items.RECORD_11, "record_11");
		this.addModel(Items.RECORD_WAIT, "record_wait");
		this.addModel(Items.PRISMARINE_SHARD, "prismarine_shard");
		this.addModel(Items.PRISMARINE_CRYSTALS, "prismarine_crystals");
		this.models.putMesh(Items.ENCHANTED_BOOK, new MeshDefinition() {
			@Override
			public ModelIdentifier getIdentifier(ItemStack stack) {
				return new ModelIdentifier("enchanted_book", "inventory");
			}
		});
		this.models.putMesh(Items.FILLED_MAP, new MeshDefinition() {
			@Override
			public ModelIdentifier getIdentifier(ItemStack stack) {
				return new ModelIdentifier("filled_map", "inventory");
			}
		});
		this.addModel(Blocks.COMMAND_BLOCK, "command_block");
		this.addModel(Items.FIREWORKS, "fireworks");
		this.addModel(Items.MINECART_WITH_COMMAND_BLOCK, "command_block_minecart");
		this.addModel(Blocks.BARRIER, "barrier");
		this.addModel(Blocks.SPAWNER, "mob_spawner");
		this.addModel(Items.WRITTEN_BOOK, "written_book");
		this.addModel(Blocks.BROWN_MUSHROOM_BLOCK, MushroomBlock.MushroomType.ALL_INSIDE.getId(), "brown_mushroom_block");
		this.addModel(Blocks.RED_MUSHROOM_BLOCK, MushroomBlock.MushroomType.ALL_INSIDE.getId(), "red_mushroom_block");
		this.addModel(Blocks.DRAGON_EGG, "dragon_egg");
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.models.reloadModels();
	}
}
