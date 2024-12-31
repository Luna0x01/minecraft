package net.minecraft.client.render.item;

import com.google.common.base.Objects;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class HeldItemRenderer {
	private static final Identifier MAP_BACKGROUND = new Identifier("textures/map/map_background.png");
	private static final Identifier UNDERWATER_TEXTURE = new Identifier("textures/misc/underwater.png");
	private final MinecraftClient client;
	private ItemStack field_13531 = ItemStack.EMPTY;
	private ItemStack field_13532 = ItemStack.EMPTY;
	private float field_13533;
	private float lastEquipProgress;
	private float field_13534;
	private float field_13535;
	private final EntityRenderDispatcher entityRenderer;
	private final ItemRenderer field_13536;

	public HeldItemRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.entityRenderer = minecraftClient.getEntityRenderManager();
		this.field_13536 = minecraftClient.getItemRenderer();
	}

	public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode) {
		this.method_12333(entity, stack, renderMode, false);
	}

	public void method_12333(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Mode mode, boolean bl) {
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean bl2 = this.field_13536.hasDepth(itemStack) && block.getRenderLayerType() == RenderLayer.TRANSLUCENT;
			if (bl2) {
				GlStateManager.depthMask(false);
			}

			this.field_13536.method_12460(itemStack, livingEntity, mode, bl);
			if (bl2) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	private void rotate(float pitch, float yaw) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
		DiffuseLighting.enableNormally();
		GlStateManager.popMatrix();
	}

	private void method_12334() {
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		int i = this.client
			.world
			.getLight(
				new BlockPos(abstractClientPlayerEntity.x, abstractClientPlayerEntity.y + (double)abstractClientPlayerEntity.getEyeHeight(), abstractClientPlayerEntity.z),
				0
			);
		float f = (float)(i & 65535);
		float g = (float)(i >> 16);
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, f, g);
	}

	private void method_9870(float f) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		float g = clientPlayerEntity.lastRenderPitch + (clientPlayerEntity.renderPitch - clientPlayerEntity.lastRenderPitch) * f;
		float h = clientPlayerEntity.lastRenderYaw + (clientPlayerEntity.renderYaw - clientPlayerEntity.lastRenderYaw) * f;
		GlStateManager.rotate((clientPlayerEntity.pitch - g) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((clientPlayerEntity.yaw - h) * 0.1F, 0.0F, 1.0F, 0.0F);
	}

	private float getMapAngle(float tickDelta) {
		float f = 1.0F - tickDelta / 45.0F + 0.1F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void method_12336() {
		if (!this.client.player.isInvisible()) {
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			this.method_12331(HandOption.RIGHT);
			this.method_12331(HandOption.LEFT);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}

	private void method_12331(HandOption handOption) {
		this.client.getTextureManager().bindTexture(this.client.player.getCapeId());
		EntityRenderer<AbstractClientPlayerEntity> entityRenderer = this.entityRenderer.getRenderer(this.client.player);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
		GlStateManager.pushMatrix();
		float f = handOption == HandOption.RIGHT ? 1.0F : -1.0F;
		GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);
		if (handOption == HandOption.RIGHT) {
			playerEntityRenderer.renderRightArm(this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(this.client.player);
		}

		GlStateManager.popMatrix();
	}

	private void method_12326(float f, HandOption handOption, float g, ItemStack itemStack) {
		float h = handOption == HandOption.RIGHT ? 1.0F : -1.0F;
		GlStateManager.translate(h * 0.125F, -0.125F, 0.0F);
		if (!this.client.player.isInvisible()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(h * 10.0F, 0.0F, 0.0F, 1.0F);
			this.method_12325(f, g, handOption);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(h * 0.51F, -0.08F + f * -1.2F, -0.75F);
		float i = MathHelper.sqrt(g);
		float j = MathHelper.sin(i * (float) Math.PI);
		float k = -0.5F * j;
		float l = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float m = -0.3F * MathHelper.sin(g * (float) Math.PI);
		GlStateManager.translate(h * k, l - 0.3F * j, m);
		GlStateManager.rotate(j * -45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(h * j * -30.0F, 0.0F, 1.0F, 0.0F);
		this.method_12328(itemStack);
		GlStateManager.popMatrix();
	}

	private void method_12324(float f, float g, float h) {
		float i = MathHelper.sqrt(h);
		float j = -0.2F * MathHelper.sin(h * (float) Math.PI);
		float k = -0.4F * MathHelper.sin(i * (float) Math.PI);
		GlStateManager.translate(0.0F, -j / 2.0F, k);
		float l = this.getMapAngle(f);
		GlStateManager.translate(0.0F, 0.04F + g * -1.2F + l * -0.5F, -0.72F);
		GlStateManager.rotate(l * -85.0F, 1.0F, 0.0F, 0.0F);
		this.method_12336();
		float m = MathHelper.sin(i * (float) Math.PI);
		GlStateManager.rotate(m * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		this.method_12328(this.field_13531);
	}

	private void method_12328(ItemStack itemStack) {
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scale(0.38F, 0.38F, 0.38F);
		GlStateManager.disableLighting();
		this.client.getTextureManager().bindTexture(MAP_BACKGROUND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.translate(-0.5F, -0.5F, 0.0F);
		GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-7.0, 135.0, 0.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex(135.0, 135.0, 0.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex(135.0, -7.0, 0.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(-7.0, -7.0, 0.0).texture(0.0, 0.0).next();
		tessellator.draw();
		MapState mapState = Items.FILLED_MAP.getMapState(itemStack, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(mapState, false);
		}

		GlStateManager.enableLighting();
	}

	private void method_12325(float f, float g, HandOption handOption) {
		boolean bl = handOption != HandOption.LEFT;
		float h = bl ? 1.0F : -1.0F;
		float i = MathHelper.sqrt(g);
		float j = -0.3F * MathHelper.sin(i * (float) Math.PI);
		float k = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float l = -0.4F * MathHelper.sin(g * (float) Math.PI);
		GlStateManager.translate(h * (j + 0.64000005F), k + -0.6F + f * -0.6F, l + -0.71999997F);
		GlStateManager.rotate(h * 45.0F, 0.0F, 1.0F, 0.0F);
		float m = MathHelper.sin(g * g * (float) Math.PI);
		float n = MathHelper.sin(i * (float) Math.PI);
		GlStateManager.rotate(h * n * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(h * m * -20.0F, 0.0F, 0.0F, 1.0F);
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getCapeId());
		GlStateManager.translate(h * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotate(h * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(h * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(h * 5.6F, 0.0F, 0.0F);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.entityRenderer.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		GlStateManager.disableCull();
		if (bl) {
			playerEntityRenderer.renderRightArm(abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(abstractClientPlayerEntity);
		}

		GlStateManager.enableCull();
	}

	private void method_12327(float f, HandOption handOption, ItemStack itemStack) {
		float g = (float)this.client.player.method_13065() - f + 1.0F;
		float h = g / (float)itemStack.getMaxUseTime();
		if (h < 0.8F) {
			float i = MathHelper.abs(MathHelper.cos(g / 4.0F * (float) Math.PI) * 0.1F);
			GlStateManager.translate(0.0F, i, 0.0F);
		}

		float j = 1.0F - (float)Math.pow((double)h, 27.0);
		int k = handOption == HandOption.RIGHT ? 1 : -1;
		GlStateManager.translate(j * 0.6F * (float)k, j * -0.5F, j * 0.0F);
		GlStateManager.rotate((float)k * j * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(j * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)k * j * 30.0F, 0.0F, 0.0F, 1.0F);
	}

	private void method_12332(HandOption handOption, float f) {
		int i = handOption == HandOption.RIGHT ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		GlStateManager.rotate((float)i * (45.0F + g * -20.0F), 0.0F, 1.0F, 0.0F);
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		GlStateManager.rotate((float)i * h * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(h * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	private void method_12335(HandOption handOption, float f) {
		int i = handOption == HandOption.RIGHT ? 1 : -1;
		GlStateManager.translate((float)i * 0.56F, -0.52F + f * -0.6F, -0.72F);
	}

	public void renderArmHoldingItem(float tickDelta) {
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		float f = abstractClientPlayerEntity.getHandSwingProgress(tickDelta);
		Hand hand = (Hand)Objects.firstNonNull(abstractClientPlayerEntity.mainHand, Hand.MAIN_HAND);
		float g = abstractClientPlayerEntity.prevPitch + (abstractClientPlayerEntity.pitch - abstractClientPlayerEntity.prevPitch) * tickDelta;
		float h = abstractClientPlayerEntity.prevYaw + (abstractClientPlayerEntity.yaw - abstractClientPlayerEntity.prevYaw) * tickDelta;
		boolean bl = true;
		boolean bl2 = true;
		if (abstractClientPlayerEntity.method_13061()) {
			ItemStack itemStack = abstractClientPlayerEntity.method_13064();
			if (itemStack.getItem() == Items.BOW) {
				Hand hand2 = abstractClientPlayerEntity.method_13062();
				bl = hand2 == Hand.MAIN_HAND;
				bl2 = !bl;
			}
		}

		this.rotate(g, h);
		this.method_12334();
		this.method_9870(tickDelta);
		GlStateManager.enableRescaleNormal();
		if (bl) {
			float i = hand == Hand.MAIN_HAND ? f : 0.0F;
			float j = 1.0F - (this.lastEquipProgress + (this.field_13533 - this.lastEquipProgress) * tickDelta);
			this.method_12329(abstractClientPlayerEntity, tickDelta, g, Hand.MAIN_HAND, i, this.field_13531, j);
		}

		if (bl2) {
			float k = hand == Hand.OFF_HAND ? f : 0.0F;
			float l = 1.0F - (this.field_13535 + (this.field_13534 - this.field_13535) * tickDelta);
			this.method_12329(abstractClientPlayerEntity, tickDelta, g, Hand.OFF_HAND, k, this.field_13532, l);
		}

		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
	}

	public void method_12329(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i) {
		boolean bl = hand == Hand.MAIN_HAND;
		HandOption handOption = bl ? abstractClientPlayerEntity.getDurability() : abstractClientPlayerEntity.getDurability().method_13037();
		GlStateManager.pushMatrix();
		if (itemStack.isEmpty()) {
			if (bl && !abstractClientPlayerEntity.isInvisible()) {
				this.method_12325(i, h, handOption);
			}
		} else if (itemStack.getItem() == Items.FILLED_MAP) {
			if (bl && this.field_13532.isEmpty()) {
				this.method_12324(g, i, h);
			} else {
				this.method_12326(i, handOption, h, itemStack);
			}
		} else {
			boolean bl2 = handOption == HandOption.RIGHT;
			if (abstractClientPlayerEntity.method_13061() && abstractClientPlayerEntity.method_13065() > 0 && abstractClientPlayerEntity.method_13062() == hand) {
				int j = bl2 ? 1 : -1;
				switch (itemStack.getUseAction()) {
					case NONE:
						this.method_12335(handOption, i);
						break;
					case EAT:
					case DRINK:
						this.method_12327(f, handOption, itemStack);
						this.method_12335(handOption, i);
						break;
					case BLOCK:
						this.method_12335(handOption, i);
						break;
					case BOW:
						this.method_12335(handOption, i);
						GlStateManager.translate((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
						GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
						float k = (float)itemStack.getMaxUseTime() - ((float)this.client.player.method_13065() - f + 1.0F);
						float l = k / 20.0F;
						l = (l * l + l * 2.0F) / 3.0F;
						if (l > 1.0F) {
							l = 1.0F;
						}

						if (l > 0.1F) {
							float m = MathHelper.sin((k - 0.1F) * 1.3F);
							float n = l - 0.1F;
							float o = m * n;
							GlStateManager.translate(o * 0.0F, o * 0.004F, o * 0.0F);
						}

						GlStateManager.translate(l * 0.0F, l * 0.0F, l * 0.04F);
						GlStateManager.scale(1.0F, 1.0F, 1.0F + l * 0.2F);
						GlStateManager.rotate((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
				}
			} else {
				float p = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float q = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float r = -0.2F * MathHelper.sin(h * (float) Math.PI);
				int s = bl2 ? 1 : -1;
				GlStateManager.translate((float)s * p, q, r);
				this.method_12335(handOption, i);
				this.method_12332(handOption, h);
			}

			this.method_12333(
				abstractClientPlayerEntity, itemStack, bl2 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl2
			);
		}

		GlStateManager.popMatrix();
	}

	public void renderOverlays(float tickDelta) {
		GlStateManager.disableAlphaTest();
		if (this.client.player.isInsideWall()) {
			BlockState blockState = this.client.world.getBlockState(new BlockPos(this.client.player));
			PlayerEntity playerEntity = this.client.player;

			for (int i = 0; i < 8; i++) {
				double d = playerEntity.x + (double)(((float)((i >> 0) % 2) - 0.5F) * playerEntity.width * 0.8F);
				double e = playerEntity.y + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
				double f = playerEntity.z + (double)(((float)((i >> 2) % 2) - 0.5F) * playerEntity.width * 0.8F);
				BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getEyeHeight(), f);
				BlockState blockState2 = this.client.world.getBlockState(blockPos);
				if (blockState2.method_13763()) {
					blockState = blockState2;
				}
			}

			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				this.renderInWallOverlay(tickDelta, this.client.getBlockRenderManager().getModels().getParticleSprite(blockState));
			}
		}

		if (!this.client.player.isSpectator()) {
			if (this.client.player.isSubmergedIn(Material.WATER)) {
				this.renderUnderwaterOverlay(tickDelta);
			}

			if (this.client.player.isOnFire()) {
				this.renderFireOverlay(tickDelta);
			}
		}

		GlStateManager.enableAlphaTest();
	}

	private void renderInWallOverlay(float tickDelta, Sprite sprite) {
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float f = 0.1F;
		GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
		GlStateManager.pushMatrix();
		float g = -1.0F;
		float h = 1.0F;
		float i = -1.0F;
		float j = 1.0F;
		float k = -0.5F;
		float l = sprite.getMinU();
		float m = sprite.getMaxU();
		float n = sprite.getMinV();
		float o = sprite.getMaxV();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-1.0, -1.0, -0.5).texture((double)m, (double)o).next();
		bufferBuilder.vertex(1.0, -1.0, -0.5).texture((double)l, (double)o).next();
		bufferBuilder.vertex(1.0, 1.0, -0.5).texture((double)l, (double)n).next();
		bufferBuilder.vertex(-1.0, 1.0, -0.5).texture((double)m, (double)n).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderUnderwaterOverlay(float tickDelta) {
		this.client.getTextureManager().bindTexture(UNDERWATER_TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float f = this.client.player.getBrightnessAtEyes(tickDelta);
		GlStateManager.color(f, f, f, 0.5F);
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.pushMatrix();
		float g = 4.0F;
		float h = -1.0F;
		float i = 1.0F;
		float j = -1.0F;
		float k = 1.0F;
		float l = -0.5F;
		float m = -this.client.player.yaw / 64.0F;
		float n = this.client.player.pitch / 64.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-1.0, -1.0, -0.5).texture((double)(4.0F + m), (double)(4.0F + n)).next();
		bufferBuilder.vertex(1.0, -1.0, -0.5).texture((double)(0.0F + m), (double)(4.0F + n)).next();
		bufferBuilder.vertex(1.0, 1.0, -0.5).texture((double)(0.0F + m), (double)(0.0F + n)).next();
		bufferBuilder.vertex(-1.0, 1.0, -0.5).texture((double)(4.0F + m), (double)(0.0F + n)).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
	}

	private void renderFireOverlay(float tickDelta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
		GlStateManager.depthFunc(519);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		float f = 1.0F;

		for (int i = 0; i < 2; i++) {
			GlStateManager.pushMatrix();
			Sprite sprite = this.client.getSpriteAtlasTexture().getSprite("minecraft:blocks/fire_layer_1");
			this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float g = sprite.getMinU();
			float h = sprite.getMaxU();
			float j = sprite.getMinV();
			float k = sprite.getMaxV();
			float l = -0.5F;
			float m = 0.5F;
			float n = -0.5F;
			float o = 0.5F;
			float p = -0.5F;
			GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
			GlStateManager.rotate((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(-0.5, -0.5, -0.5).texture((double)h, (double)k).next();
			bufferBuilder.vertex(0.5, -0.5, -0.5).texture((double)g, (double)k).next();
			bufferBuilder.vertex(0.5, 0.5, -0.5).texture((double)g, (double)j).next();
			bufferBuilder.vertex(-0.5, 0.5, -0.5).texture((double)h, (double)j).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
	}

	public void updateHeldItems() {
		this.lastEquipProgress = this.field_13533;
		this.field_13535 = this.field_13534;
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		ItemStack itemStack = clientPlayerEntity.getMainHandStack();
		ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
		if (clientPlayerEntity.method_12266()) {
			this.field_13533 = MathHelper.clamp(this.field_13533 - 0.4F, 0.0F, 1.0F);
			this.field_13534 = MathHelper.clamp(this.field_13534 - 0.4F, 0.0F, 1.0F);
		} else {
			float f = clientPlayerEntity.method_13275(1.0F);
			this.field_13533 = this.field_13533 + MathHelper.clamp((Objects.equal(this.field_13531, itemStack) ? f * f * f : 0.0F) - this.field_13533, -0.4F, 0.4F);
			this.field_13534 = this.field_13534 + MathHelper.clamp((float)(Objects.equal(this.field_13532, itemStack2) ? 1 : 0) - this.field_13534, -0.4F, 0.4F);
		}

		if (this.field_13533 < 0.1F) {
			this.field_13531 = itemStack;
		}

		if (this.field_13534 < 0.1F) {
			this.field_13532 = itemStack2;
		}
	}

	public void method_12330(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			this.field_13533 = 0.0F;
		} else {
			this.field_13534 = 0.0F;
		}
	}
}
