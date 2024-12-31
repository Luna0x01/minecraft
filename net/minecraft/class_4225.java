package net.minecraft;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
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
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class class_4225 {
	private static final Identifier field_20728 = new Identifier("textures/map/map_background.png");
	private static final Identifier field_20729 = new Identifier("textures/misc/underwater.png");
	private final MinecraftClient field_20730;
	private ItemStack field_20731 = ItemStack.EMPTY;
	private ItemStack field_20732 = ItemStack.EMPTY;
	private float field_20733;
	private float field_20734;
	private float field_20735;
	private float field_20736;
	private final EntityRenderDispatcher field_20737;
	private final HeldItemRenderer field_20738;

	public class_4225(MinecraftClient minecraftClient) {
		this.field_20730 = minecraftClient;
		this.field_20737 = minecraftClient.getEntityRenderManager();
		this.field_20738 = minecraftClient.getHeldItemRenderer();
	}

	public void method_19139(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Mode mode) {
		this.method_19140(livingEntity, itemStack, mode, false);
	}

	public void method_19140(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Mode mode, boolean bl) {
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean bl2 = this.field_20738.method_19375(itemStack) && block.getRenderLayerType() == RenderLayer.TRANSLUCENT;
			if (bl2) {
				GlStateManager.depthMask(false);
			}

			this.field_20738.method_19378(itemStack, livingEntity, mode, bl);
			if (bl2) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	private void method_19131(float f, float g) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(f, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(g, 0.0F, 1.0F, 0.0F);
		DiffuseLighting.enableNormally();
		GlStateManager.popMatrix();
	}

	private void method_19144() {
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.field_20730.player;
		int i = this.field_20730
			.world
			.method_8578(
				new BlockPos(abstractClientPlayerEntity.x, abstractClientPlayerEntity.y + (double)abstractClientPlayerEntity.getEyeHeight(), abstractClientPlayerEntity.z),
				0
			);
		float f = (float)(i & 65535);
		float g = (float)(i >> 16);
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, f, g);
	}

	private void method_19148(float f) {
		ClientPlayerEntity clientPlayerEntity = this.field_20730.player;
		float g = clientPlayerEntity.lastRenderPitch + (clientPlayerEntity.renderPitch - clientPlayerEntity.lastRenderPitch) * f;
		float h = clientPlayerEntity.lastRenderYaw + (clientPlayerEntity.renderYaw - clientPlayerEntity.lastRenderYaw) * f;
		GlStateManager.rotate((clientPlayerEntity.pitch - g) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((clientPlayerEntity.yaw - h) * 0.1F, 0.0F, 1.0F, 0.0F);
	}

	private float method_19150(float f) {
		float g = 1.0F - f / 45.0F + 0.1F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		return -MathHelper.cos(g * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void method_19147() {
		if (!this.field_20730.player.isInvisible()) {
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			this.method_19137(HandOption.RIGHT);
			this.method_19137(HandOption.LEFT);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}

	private void method_19137(HandOption handOption) {
		this.field_20730.getTextureManager().bindTexture(this.field_20730.player.getCapeId());
		EntityRenderer<AbstractClientPlayerEntity> entityRenderer = this.field_20737.getRenderer(this.field_20730.player);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
		GlStateManager.pushMatrix();
		float f = handOption == HandOption.RIGHT ? 1.0F : -1.0F;
		GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);
		if (handOption == HandOption.RIGHT) {
			playerEntityRenderer.renderRightArm(this.field_20730.player);
		} else {
			playerEntityRenderer.renderLeftArm(this.field_20730.player);
		}

		GlStateManager.popMatrix();
	}

	private void method_19134(float f, HandOption handOption, float g, ItemStack itemStack) {
		float h = handOption == HandOption.RIGHT ? 1.0F : -1.0F;
		GlStateManager.translate(h * 0.125F, -0.125F, 0.0F);
		if (!this.field_20730.player.isInvisible()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(h * 10.0F, 0.0F, 0.0F, 1.0F);
			this.method_19133(f, g, handOption);
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
		this.method_19141(itemStack);
		GlStateManager.popMatrix();
	}

	private void method_19132(float f, float g, float h) {
		float i = MathHelper.sqrt(h);
		float j = -0.2F * MathHelper.sin(h * (float) Math.PI);
		float k = -0.4F * MathHelper.sin(i * (float) Math.PI);
		GlStateManager.translate(0.0F, -j / 2.0F, k);
		float l = this.method_19150(f);
		GlStateManager.translate(0.0F, 0.04F + g * -1.2F + l * -0.5F, -0.72F);
		GlStateManager.rotate(l * -85.0F, 1.0F, 0.0F, 0.0F);
		this.method_19147();
		float m = MathHelper.sin(i * (float) Math.PI);
		GlStateManager.rotate(m * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		this.method_19141(this.field_20731);
	}

	private void method_19141(ItemStack itemStack) {
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scale(0.38F, 0.38F, 0.38F);
		GlStateManager.disableLighting();
		this.field_20730.getTextureManager().bindTexture(field_20728);
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
		MapState mapState = FilledMapItem.method_16111(itemStack, this.field_20730.world);
		if (mapState != null) {
			this.field_20730.field_3818.method_19090().draw(mapState, false);
		}

		GlStateManager.enableLighting();
	}

	private void method_19133(float f, float g, HandOption handOption) {
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
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.field_20730.player;
		this.field_20730.getTextureManager().bindTexture(abstractClientPlayerEntity.getCapeId());
		GlStateManager.translate(h * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotate(h * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(h * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(h * 5.6F, 0.0F, 0.0F);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.field_20737.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		GlStateManager.disableCull();
		if (bl) {
			playerEntityRenderer.renderRightArm(abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(abstractClientPlayerEntity);
		}

		GlStateManager.enableCull();
	}

	private void method_19135(float f, HandOption handOption, ItemStack itemStack) {
		float g = (float)this.field_20730.player.method_13065() - f + 1.0F;
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

	private void method_19138(HandOption handOption, float f) {
		int i = handOption == HandOption.RIGHT ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		GlStateManager.rotate((float)i * (45.0F + g * -20.0F), 0.0F, 1.0F, 0.0F);
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		GlStateManager.rotate((float)i * h * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(h * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	private void method_19146(HandOption handOption, float f) {
		int i = handOption == HandOption.RIGHT ? 1 : -1;
		GlStateManager.translate((float)i * 0.56F, -0.52F + f * -0.6F, -0.72F);
	}

	public void method_19130(float f) {
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.field_20730.player;
		float g = abstractClientPlayerEntity.getHandSwingProgress(f);
		Hand hand = (Hand)MoreObjects.firstNonNull(abstractClientPlayerEntity.mainHand, Hand.MAIN_HAND);
		float h = abstractClientPlayerEntity.prevPitch + (abstractClientPlayerEntity.pitch - abstractClientPlayerEntity.prevPitch) * f;
		float i = abstractClientPlayerEntity.prevYaw + (abstractClientPlayerEntity.yaw - abstractClientPlayerEntity.prevYaw) * f;
		boolean bl = true;
		boolean bl2 = true;
		if (abstractClientPlayerEntity.method_13061()) {
			ItemStack itemStack = abstractClientPlayerEntity.method_13064();
			if (itemStack.getItem() == Items.BOW) {
				bl = abstractClientPlayerEntity.method_13062() == Hand.MAIN_HAND;
				bl2 = !bl;
			}
		}

		this.method_19131(h, i);
		this.method_19144();
		this.method_19148(f);
		GlStateManager.enableRescaleNormal();
		if (bl) {
			float j = hand == Hand.MAIN_HAND ? g : 0.0F;
			float k = 1.0F - (this.field_20734 + (this.field_20733 - this.field_20734) * f);
			this.method_19142(abstractClientPlayerEntity, f, h, Hand.MAIN_HAND, j, this.field_20731, k);
		}

		if (bl2) {
			float l = hand == Hand.OFF_HAND ? g : 0.0F;
			float m = 1.0F - (this.field_20736 + (this.field_20735 - this.field_20736) * f);
			this.method_19142(abstractClientPlayerEntity, f, h, Hand.OFF_HAND, l, this.field_20732, m);
		}

		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
	}

	public void method_19142(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i) {
		boolean bl = hand == Hand.MAIN_HAND;
		HandOption handOption = bl ? abstractClientPlayerEntity.getDurability() : abstractClientPlayerEntity.getDurability().method_13037();
		GlStateManager.pushMatrix();
		if (itemStack.isEmpty()) {
			if (bl && !abstractClientPlayerEntity.isInvisible()) {
				this.method_19133(i, h, handOption);
			}
		} else if (itemStack.getItem() == Items.FILLED_MAP) {
			if (bl && this.field_20732.isEmpty()) {
				this.method_19132(g, i, h);
			} else {
				this.method_19134(i, handOption, h, itemStack);
			}
		} else {
			boolean bl2 = handOption == HandOption.RIGHT;
			if (abstractClientPlayerEntity.method_13061() && abstractClientPlayerEntity.method_13065() > 0 && abstractClientPlayerEntity.method_13062() == hand) {
				int j = bl2 ? 1 : -1;
				switch (itemStack.getUseAction()) {
					case NONE:
						this.method_19146(handOption, i);
						break;
					case EAT:
					case DRINK:
						this.method_19135(f, handOption, itemStack);
						this.method_19146(handOption, i);
						break;
					case BLOCK:
						this.method_19146(handOption, i);
						break;
					case BOW:
						this.method_19146(handOption, i);
						GlStateManager.translate((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
						GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
						float k = (float)itemStack.getMaxUseTime() - ((float)this.field_20730.player.method_13065() - f + 1.0F);
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
						break;
					case SPEAR:
						this.method_19146(handOption, i);
						GlStateManager.translate((float)j * -0.5F, 0.7F, 0.1F);
						GlStateManager.rotate(-55.0F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
						float p = (float)itemStack.getMaxUseTime() - ((float)this.field_20730.player.method_13065() - f + 1.0F);
						float q = p / 10.0F;
						if (q > 1.0F) {
							q = 1.0F;
						}

						if (q > 0.1F) {
							float r = MathHelper.sin((p - 0.1F) * 1.3F);
							float s = q - 0.1F;
							float t = r * s;
							GlStateManager.translate(t * 0.0F, t * 0.004F, t * 0.0F);
						}

						GlStateManager.translate(0.0F, 0.0F, q * 0.2F);
						GlStateManager.scale(1.0F, 1.0F, 1.0F + q * 0.2F);
						GlStateManager.rotate((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
				}
			} else if (abstractClientPlayerEntity.method_15646()) {
				this.method_19146(handOption, i);
				int u = bl2 ? 1 : -1;
				GlStateManager.translate((float)u * -0.4F, 0.8F, 0.3F);
				GlStateManager.rotate((float)u * 65.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate((float)u * -85.0F, 0.0F, 0.0F, 1.0F);
			} else {
				float v = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float w = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float x = -0.2F * MathHelper.sin(h * (float) Math.PI);
				int y = bl2 ? 1 : -1;
				GlStateManager.translate((float)y * v, w, x);
				this.method_19146(handOption, i);
				this.method_19138(handOption, h);
			}

			this.method_19140(
				abstractClientPlayerEntity, itemStack, bl2 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl2
			);
		}

		GlStateManager.popMatrix();
	}

	public void method_19145(float f) {
		GlStateManager.disableAlphaTest();
		if (this.field_20730.player.isInsideWall()) {
			BlockState blockState = this.field_20730.world.getBlockState(new BlockPos(this.field_20730.player));
			PlayerEntity playerEntity = this.field_20730.player;

			for (int i = 0; i < 8; i++) {
				double d = playerEntity.x + (double)(((float)((i >> 0) % 2) - 0.5F) * playerEntity.width * 0.8F);
				double e = playerEntity.y + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
				double g = playerEntity.z + (double)(((float)((i >> 2) % 2) - 0.5F) * playerEntity.width * 0.8F);
				BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getEyeHeight(), g);
				BlockState blockState2 = this.field_20730.world.getBlockState(blockPos);
				if (blockState2.method_16914()) {
					blockState = blockState2;
				}
			}

			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				this.method_19143(this.field_20730.getBlockRenderManager().getModels().getParticleSprite(blockState));
			}
		}

		if (!this.field_20730.player.isSpectator()) {
			if (this.field_20730.player.method_15567(FluidTags.WATER)) {
				this.method_19151(f);
			}

			if (this.field_20730.player.isOnFire()) {
				this.method_19149();
			}
		}

		GlStateManager.enableAlphaTest();
	}

	private void method_19143(Sprite sprite) {
		this.field_20730.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
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

	private void method_19151(float f) {
		this.field_20730.getTextureManager().bindTexture(field_20729);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float g = this.field_20730.player.getBrightnessAtEyes();
		GlStateManager.color(g, g, g, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.pushMatrix();
		float h = 4.0F;
		float i = -1.0F;
		float j = 1.0F;
		float k = -1.0F;
		float l = 1.0F;
		float m = -0.5F;
		float n = -this.field_20730.player.yaw / 64.0F;
		float o = this.field_20730.player.pitch / 64.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-1.0, -1.0, -0.5).texture((double)(4.0F + n), (double)(4.0F + o)).next();
		bufferBuilder.vertex(1.0, -1.0, -0.5).texture((double)(0.0F + n), (double)(4.0F + o)).next();
		bufferBuilder.vertex(1.0, 1.0, -0.5).texture((double)(0.0F + n), (double)(0.0F + o)).next();
		bufferBuilder.vertex(-1.0, 1.0, -0.5).texture((double)(4.0F + n), (double)(0.0F + o)).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
	}

	private void method_19149() {
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
			Sprite sprite = this.field_20730.getSpriteAtlasTexture().method_19509(class_4288.field_21065);
			this.field_20730.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
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

	public void method_19129() {
		this.field_20734 = this.field_20733;
		this.field_20736 = this.field_20735;
		ClientPlayerEntity clientPlayerEntity = this.field_20730.player;
		ItemStack itemStack = clientPlayerEntity.getMainHandStack();
		ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
		if (clientPlayerEntity.method_12266()) {
			this.field_20733 = MathHelper.clamp(this.field_20733 - 0.4F, 0.0F, 1.0F);
			this.field_20735 = MathHelper.clamp(this.field_20735 - 0.4F, 0.0F, 1.0F);
		} else {
			float f = clientPlayerEntity.method_13275(1.0F);
			this.field_20733 = this.field_20733 + MathHelper.clamp((Objects.equals(this.field_20731, itemStack) ? f * f * f : 0.0F) - this.field_20733, -0.4F, 0.4F);
			this.field_20735 = this.field_20735 + MathHelper.clamp((float)(Objects.equals(this.field_20732, itemStack2) ? 1 : 0) - this.field_20735, -0.4F, 0.4F);
		}

		if (this.field_20733 < 0.1F) {
			this.field_20731 = itemStack;
		}

		if (this.field_20735 < 0.1F) {
			this.field_20732 = itemStack2;
		}
	}

	public void method_19136(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			this.field_20733 = 0.0F;
		} else {
			this.field_20735 = 0.0F;
		}
	}
}
