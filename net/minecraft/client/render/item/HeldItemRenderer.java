package net.minecraft.client.render.item;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class HeldItemRenderer {
	private static final Identifier MAP_BACKGROUND = new Identifier("textures/map/map_background.png");
	private static final Identifier UNDERWATER_TEXTURE = new Identifier("textures/misc/underwater.png");
	private final MinecraftClient client;
	private ItemStack mainHand;
	private float equipProgress;
	private float lastEquipProgress;
	private final EntityRenderDispatcher entityRenderer;
	private final ItemRenderer itemRenderer;
	private int selectedSlot = -1;

	public HeldItemRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.entityRenderer = minecraftClient.getEntityRenderManager();
		this.itemRenderer = minecraftClient.getItemRenderer();
	}

	public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode) {
		if (stack != null) {
			Item item = stack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			if (this.itemRenderer.hasDepth(stack)) {
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				if (this.isTranslucent(block)) {
					GlStateManager.depthMask(false);
				}
			}

			this.itemRenderer.renderItem(stack, entity, renderMode);
			if (this.isTranslucent(block)) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	private boolean isTranslucent(Block block) {
		return block != null && block.getRenderLayerType() == RenderLayer.TRANSLUCENT;
	}

	private void rotate(float pitch, float yaw) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
		DiffuseLighting.enableNormally();
		GlStateManager.popMatrix();
	}

	private void applyPlayerLighting(AbstractClientPlayerEntity player) {
		int i = this.client.world.getLight(new BlockPos(player.x, player.y + (double)player.getEyeHeight(), player.z), 0);
		float f = (float)(i & 65535);
		float g = (float)(i >> 16);
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, f, g);
	}

	private void applyPlayerRotation(ClientPlayerEntity player, float tickDelta) {
		float f = player.lastRenderPitch + (player.renderPitch - player.lastRenderPitch) * tickDelta;
		float g = player.lastRenderYaw + (player.renderYaw - player.lastRenderYaw) * tickDelta;
		GlStateManager.rotate((player.pitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((player.yaw - g) * 0.1F, 0.0F, 1.0F, 0.0F);
	}

	private float getMapAngle(float tickDelta) {
		float f = 1.0F - tickDelta / 45.0F + 0.1F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderRightMapArm(PlayerEntityRenderer playerRenderer) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0.25F, -0.85F, 0.75F);
		playerRenderer.renderRightArm(this.client.player);
		GlStateManager.popMatrix();
	}

	private void renderLeftMapArm(PlayerEntityRenderer playerRenderer) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-0.3F, -1.1F, 0.45F);
		playerRenderer.renderLeftArm(this.client.player);
		GlStateManager.popMatrix();
	}

	private void renderMapArms(AbstractClientPlayerEntity player) {
		this.client.getTextureManager().bindTexture(player.getCapeId());
		EntityRenderer<AbstractClientPlayerEntity> entityRenderer = this.entityRenderer.getRenderer(this.client.player);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
		if (!player.isInvisible()) {
			GlStateManager.disableCull();
			this.renderRightMapArm(playerEntityRenderer);
			this.renderLeftMapArm(playerEntityRenderer);
			GlStateManager.enableCull();
		}
	}

	private void renderMap(AbstractClientPlayerEntity player, float pitch, float equipProgress, float swingProgress) {
		float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		float g = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI * 2.0F);
		float h = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
		GlStateManager.translate(f, g, h);
		float i = this.getMapAngle(pitch);
		GlStateManager.translate(0.0F, 0.04F, -0.72F);
		GlStateManager.translate(0.0F, equipProgress * -1.2F, 0.0F);
		GlStateManager.translate(0.0F, i * -0.5F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(i * -85.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
		this.renderMapArms(player);
		float j = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		float k = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		GlStateManager.rotate(j * -20.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(k * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(k * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(0.38F, 0.38F, 0.38F);
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(-1.0F, -1.0F, 0.0F);
		GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
		this.client.getTextureManager().bindTexture(MAP_BACKGROUND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GL11.glNormal3f(0.0F, 0.0F, -1.0F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-7.0, 135.0, 0.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex(135.0, 135.0, 0.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex(135.0, -7.0, 0.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(-7.0, -7.0, 0.0).texture(0.0, 0.0).next();
		tessellator.draw();
		MapState mapState = Items.FILLED_MAP.getMapState(this.mainHand, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(mapState, false);
		}
	}

	private void renderArm(AbstractClientPlayerEntity player, float equipProgress, float swingProgress) {
		float f = -0.3F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		float g = 0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI * 2.0F);
		float h = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
		GlStateManager.translate(f, g, h);
		GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
		GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
		GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		float i = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		float j = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		GlStateManager.rotate(j * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(i * -20.0F, 0.0F, 0.0F, 1.0F);
		this.client.getTextureManager().bindTexture(player.getCapeId());
		GlStateManager.translate(-1.0F, 3.6F, 3.5F);
		GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(1.0F, 1.0F, 1.0F);
		GlStateManager.translate(5.6F, 0.0F, 0.0F);
		EntityRenderer<AbstractClientPlayerEntity> entityRenderer = this.entityRenderer.getRenderer(this.client.player);
		GlStateManager.disableCull();
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
		playerEntityRenderer.renderRightArm(this.client.player);
		GlStateManager.enableCull();
	}

	private void translateSwingProgress(float swingProgress) {
		float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		float g = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI * 2.0F);
		float h = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
		GlStateManager.translate(f, g, h);
	}

	private void applyEatOrDrinkTransformation(AbstractClientPlayerEntity player, float tickDelta) {
		float f = (float)player.getItemUseTicks() - tickDelta + 1.0F;
		float g = f / (float)this.mainHand.getMaxUseTime();
		float h = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
		if (g >= 0.8F) {
			h = 0.0F;
		}

		GlStateManager.translate(0.0F, h, 0.0F);
		float i = 1.0F - (float)Math.pow((double)g, 27.0);
		GlStateManager.translate(i * 0.6F, i * -0.5F, i * 0.0F);
		GlStateManager.rotate(i * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(i * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(i * 30.0F, 0.0F, 0.0F, 1.0F);
	}

	private void applyEquipAndSwingOffset(float equipProgress, float swingProgress) {
		GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
		GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
		GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(g * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(g * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(0.4F, 0.4F, 0.4F);
	}

	private void applyBowTransformation(float tickDelta, AbstractClientPlayerEntity player) {
		GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(-0.9F, 0.2F, 0.0F);
		float f = (float)this.mainHand.getMaxUseTime() - ((float)player.getItemUseTicks() - tickDelta + 1.0F);
		float g = f / 20.0F;
		g = (g * g + g * 2.0F) / 3.0F;
		if (g > 1.0F) {
			g = 1.0F;
		}

		if (g > 0.1F) {
			float h = MathHelper.sin((f - 0.1F) * 1.3F);
			float i = g - 0.1F;
			float j = h * i;
			GlStateManager.translate(j * 0.0F, j * 0.01F, j * 0.0F);
		}

		GlStateManager.translate(g * 0.0F, g * 0.0F, g * 0.1F);
		GlStateManager.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
	}

	private void applySwordBlockTransformation() {
		GlStateManager.translate(-0.5F, 0.2F, 0.0F);
		GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
	}

	public void renderArmHoldingItem(float tickDelta) {
		float f = 1.0F - (this.lastEquipProgress + (this.equipProgress - this.lastEquipProgress) * tickDelta);
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		float g = abstractClientPlayerEntity.getHandSwingProgress(tickDelta);
		float h = abstractClientPlayerEntity.prevPitch + (abstractClientPlayerEntity.pitch - abstractClientPlayerEntity.prevPitch) * tickDelta;
		float i = abstractClientPlayerEntity.prevYaw + (abstractClientPlayerEntity.yaw - abstractClientPlayerEntity.prevYaw) * tickDelta;
		this.rotate(h, i);
		this.applyPlayerLighting(abstractClientPlayerEntity);
		this.applyPlayerRotation((ClientPlayerEntity)abstractClientPlayerEntity, tickDelta);
		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		if (this.mainHand != null) {
			if (this.mainHand.getItem() == Items.FILLED_MAP) {
				this.renderMap(abstractClientPlayerEntity, h, f, g);
			} else if (abstractClientPlayerEntity.getItemUseTicks() > 0) {
				UseAction useAction = this.mainHand.getUseAction();
				switch (useAction) {
					case NONE:
						this.applyEquipAndSwingOffset(f, 0.0F);
						break;
					case EAT:
					case DRINK:
						this.applyEatOrDrinkTransformation(abstractClientPlayerEntity, tickDelta);
						this.applyEquipAndSwingOffset(f, 0.0F);
						break;
					case BLOCK:
						this.applyEquipAndSwingOffset(f, 0.0F);
						this.applySwordBlockTransformation();
						break;
					case BOW:
						this.applyEquipAndSwingOffset(f, 0.0F);
						this.applyBowTransformation(tickDelta, abstractClientPlayerEntity);
				}
			} else {
				this.translateSwingProgress(g);
				this.applyEquipAndSwingOffset(f, g);
			}

			this.renderItem(abstractClientPlayerEntity, this.mainHand, ModelTransformation.Mode.FIRST_PERSON);
		} else if (!abstractClientPlayerEntity.isInvisible()) {
			this.renderArm(abstractClientPlayerEntity, f, g);
		}

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
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
				if (blockState2.getBlock().isLeafBlock()) {
					blockState = blockState2;
				}
			}

			if (blockState.getBlock().getBlockType() != -1) {
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
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
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
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		float f = 1.0F;

		for (int i = 0; i < 2; i++) {
			GlStateManager.pushMatrix();
			Sprite sprite = this.client.getSpriteAtlasTexture().getSprite("minecraft:blocks/fire_layer_1");
			this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float g = sprite.getMinU();
			float h = sprite.getMaxU();
			float j = sprite.getMinV();
			float k = sprite.getMaxV();
			float l = (0.0F - f) / 2.0F;
			float m = l + f;
			float n = 0.0F - f / 2.0F;
			float o = n + f;
			float p = -0.5F;
			GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
			GlStateManager.rotate((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex((double)l, (double)n, (double)p).texture((double)h, (double)k).next();
			bufferBuilder.vertex((double)m, (double)n, (double)p).texture((double)g, (double)k).next();
			bufferBuilder.vertex((double)m, (double)o, (double)p).texture((double)g, (double)j).next();
			bufferBuilder.vertex((double)l, (double)o, (double)p).texture((double)h, (double)j).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
	}

	public void updateHeldItems() {
		this.lastEquipProgress = this.equipProgress;
		PlayerEntity playerEntity = this.client.player;
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		boolean bl = false;
		if (this.mainHand != null && itemStack != null) {
			if (!this.mainHand.equalsAllClient(itemStack)) {
				bl = true;
			}
		} else if (this.mainHand == null && itemStack == null) {
			bl = false;
		} else {
			bl = true;
		}

		float f = 0.4F;
		float g = bl ? 0.0F : 1.0F;
		float h = MathHelper.clamp(g - this.equipProgress, -f, f);
		this.equipProgress += h;
		if (this.equipProgress < 0.1F) {
			this.mainHand = itemStack;
			this.selectedSlot = playerEntity.inventory.selectedSlot;
		}
	}

	public void resetEquipProgress() {
		this.equipProgress = 0.0F;
	}

	public void resetEquipProgress2() {
		this.equipProgress = 0.0F;
	}
}
