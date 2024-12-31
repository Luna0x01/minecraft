package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

public abstract class RenderLayer extends RenderPhase {
	private static final RenderLayer field_9178 = of(
		"solid",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		7,
		2097152,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer field_9175 = of(
		"cutout_mipped",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		7,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.shadeModel(SMOOTH_SHADE_MODEL)
			.lightmap(ENABLE_LIGHTMAP)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.alpha(HALF_ALPHA)
			.build(true)
	);
	private static final RenderLayer field_9174 = of(
		"cutout",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		7,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.shadeModel(SMOOTH_SHADE_MODEL)
			.lightmap(ENABLE_LIGHTMAP)
			.texture(BLOCK_ATLAS_TEXTURE)
			.alpha(HALF_ALPHA)
			.build(true)
	);
	private static final RenderLayer field_9179 = of(
		"translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, true, true, createTranslucentPhaseData()
	);
	private static final RenderLayer field_20963 = of(
		"translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, false, true, createTranslucentPhaseData()
	);
	private static final RenderLayer field_20964 = of(
		"leash",
		VertexFormats.POSITION_COLOR_LIGHT,
		7,
		256,
		RenderLayer.MultiPhaseParameters.builder().texture(NO_TEXTURE).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false)
	);
	private static final RenderLayer field_20965 = of(
		"water_mask", VertexFormats.POSITION, 7, 256, RenderLayer.MultiPhaseParameters.builder().texture(NO_TEXTURE).writeMaskState(DEPTH_MASK).build(false)
	);
	private static final RenderLayer field_20967 = of(
		"glint",
		VertexFormats.POSITION_TEXTURE,
		7,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer field_20968 = of(
		"entity_glint",
		VertexFormats.POSITION_TEXTURE,
		7,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer field_20970 = of(
		"lightning",
		VertexFormats.POSITION_COLOR,
		7,
		256,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder().writeMaskState(COLOR_MASK).transparency(LIGHTNING_TRANSPARENCY).shadeModel(SMOOTH_SHADE_MODEL).build(false)
	);
	public static final RenderLayer.MultiPhase field_21695 = of(
		"lines",
		VertexFormats.POSITION_COLOR,
		1,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(PROJECTION_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.writeMaskState(COLOR_MASK)
			.build(false)
	);
	private final VertexFormat vertexFormat;
	private final int drawMode;
	private final int expectedBufferSize;
	private final boolean hasCrumbling;
	private final boolean translucent;
	private final Optional<RenderLayer> optionalThis;

	public static RenderLayer getSolid() {
		return field_9178;
	}

	public static RenderLayer getCutoutMipped() {
		return field_9175;
	}

	public static RenderLayer getCutout() {
		return field_9174;
	}

	private static RenderLayer.MultiPhaseParameters createTranslucentPhaseData() {
		return RenderLayer.MultiPhaseParameters.builder()
			.shadeModel(SMOOTH_SHADE_MODEL)
			.lightmap(ENABLE_LIGHTMAP)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.build(true);
	}

	public static RenderLayer getTranslucent() {
		return field_9179;
	}

	public static RenderLayer getTranslucentNoCrumbling() {
		return field_20963;
	}

	public static RenderLayer getEntitySolid(Identifier identifier) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(NO_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseParameters);
	}

	public static RenderLayer getEntityCutout(Identifier identifier) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(NO_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseParameters);
	}

	public static RenderLayer getCutoutNoCull(Identifier identifier, boolean bl) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(NO_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(bl);
		return of("entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseParameters);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier identifier) {
		return getCutoutNoCull(identifier, true);
	}

	public static RenderLayer getEntityTranslucentCull(Identifier identifier) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return of("entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, multiPhaseParameters);
	}

	public static RenderLayer getEntityTranslucent(Identifier identifier, boolean bl) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(bl);
		return of("entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, multiPhaseParameters);
	}

	public static RenderLayer getEntityTranslucent(Identifier identifier) {
		return getEntityTranslucent(identifier, true);
	}

	public static RenderLayer getEntitySmoothCutout(Identifier identifier) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.alpha(HALF_ALPHA)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.shadeModel(SMOOTH_SHADE_MODEL)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.build(true);
		return of("entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, multiPhaseParameters);
	}

	public static RenderLayer getBeaconBeam(Identifier identifier, boolean bl) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(bl ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
			.writeMaskState(bl ? COLOR_MASK : ALL_MASK)
			.fog(NO_FOG)
			.build(false);
		return of("beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, false, true, multiPhaseParameters);
	}

	public static RenderLayer getEntityDecal(Identifier identifier) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.depthTest(EQUAL_DEPTH_TEST)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(false);
		return of("entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, multiPhaseParameters);
	}

	public static RenderLayer getEntityNoOutline(Identifier identifier) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.writeMaskState(COLOR_MASK)
			.build(false);
		return of("entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, multiPhaseParameters);
	}

	public static RenderLayer getEntityAlpha(Identifier identifier, float f) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.alpha(new RenderPhase.Alpha(f))
			.cull(DISABLE_CULLING)
			.build(true);
		return of("entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, multiPhaseParameters);
	}

	public static RenderLayer getEyes(Identifier identifier) {
		RenderPhase.Texture texture = new RenderPhase.Texture(identifier, false, false);
		return of(
			"eyes",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder().texture(texture).transparency(ADDITIVE_TRANSPARENCY).writeMaskState(COLOR_MASK).fog(BLACK_FOG).build(false)
		);
	}

	public static RenderLayer getEnergySwirl(Identifier identifier, float f, float g) {
		return of(
			"energy_swirl",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(identifier, false, false))
				.texturing(new RenderPhase.OffsetTexturing(f, g))
				.fog(BLACK_FOG)
				.transparency(ADDITIVE_TRANSPARENCY)
				.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
				.alpha(ONE_TENTH_ALPHA)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(false)
		);
	}

	public static RenderLayer getLeash() {
		return field_20964;
	}

	public static RenderLayer getWaterMask() {
		return field_20965;
	}

	public static RenderLayer getOutline(Identifier identifier) {
		return of(
			"outline",
			VertexFormats.POSITION_COLOR_TEXTURE,
			7,
			256,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(identifier, false, false))
				.cull(DISABLE_CULLING)
				.depthTest(ALWAYS_DEPTH_TEST)
				.alpha(ONE_TENTH_ALPHA)
				.texturing(OUTLINE_TEXTURING)
				.fog(NO_FOG)
				.target(OUTLINE_TARGET)
				.build(RenderLayer.OutlineMode.field_21854)
		);
	}

	public static RenderLayer getGlint() {
		return field_20967;
	}

	public static RenderLayer getEntityGlint() {
		return field_20968;
	}

	public static RenderLayer getBlockBreaking(Identifier identifier) {
		RenderPhase.Texture texture = new RenderPhase.Texture(identifier, false, false);
		return of(
			"crumbling",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(texture)
				.alpha(ONE_TENTH_ALPHA)
				.transparency(CRUMBLING_TRANSPARENCY)
				.writeMaskState(COLOR_MASK)
				.layering(POLYGON_OFFSET_LAYERING)
				.build(false)
		);
	}

	public static RenderLayer getText(Identifier identifier) {
		return of(
			"text",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(identifier, false, false))
				.alpha(ONE_TENTH_ALPHA)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.build(false)
		);
	}

	public static RenderLayer getTextSeeThrough(Identifier identifier) {
		return of(
			"text_see_through",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(identifier, false, false))
				.alpha(ONE_TENTH_ALPHA)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.depthTest(ALWAYS_DEPTH_TEST)
				.writeMaskState(COLOR_MASK)
				.build(false)
		);
	}

	public static RenderLayer getLightning() {
		return field_20970;
	}

	public static RenderLayer getEndPortal(int i) {
		RenderPhase.Transparency transparency;
		RenderPhase.Texture texture;
		if (i <= 1) {
			transparency = TRANSLUCENT_TRANSPARENCY;
			texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.SKY_TEX, false, false);
		} else {
			transparency = ADDITIVE_TRANSPARENCY;
			texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.PORTAL_TEX, false, false);
		}

		return of(
			"end_portal",
			VertexFormats.POSITION_COLOR,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.transparency(transparency)
				.texture(texture)
				.texturing(new RenderPhase.PortalTexturing(i))
				.fog(BLACK_FOG)
				.build(false)
		);
	}

	public static RenderLayer getLines() {
		return field_21695;
	}

	public RenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
		super(string, runnable, runnable2);
		this.vertexFormat = vertexFormat;
		this.drawMode = i;
		this.expectedBufferSize = j;
		this.hasCrumbling = bl;
		this.translucent = bl2;
		this.optionalThis = Optional.of(this);
	}

	public static RenderLayer.MultiPhase of(String string, VertexFormat vertexFormat, int i, int j, RenderLayer.MultiPhaseParameters multiPhaseParameters) {
		return of(string, vertexFormat, i, j, false, false, multiPhaseParameters);
	}

	public static RenderLayer.MultiPhase of(
		String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, RenderLayer.MultiPhaseParameters multiPhaseParameters
	) {
		return RenderLayer.MultiPhase.of(string, vertexFormat, i, j, bl, bl2, multiPhaseParameters);
	}

	public void draw(BufferBuilder bufferBuilder, int i, int j, int k) {
		if (bufferBuilder.isBuilding()) {
			if (this.translucent) {
				bufferBuilder.sortQuads((float)i, (float)j, (float)k);
			}

			bufferBuilder.end();
			this.startDrawing();
			BufferRenderer.draw(bufferBuilder);
			this.endDrawing();
		}
	}

	public String toString() {
		return this.name;
	}

	public static List<RenderLayer> getBlockLayers() {
		return ImmutableList.of(getSolid(), getCutoutMipped(), getCutout(), getTranslucent());
	}

	public int getExpectedBufferSize() {
		return this.expectedBufferSize;
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public int getDrawMode() {
		return this.drawMode;
	}

	public Optional<RenderLayer> getAffectedOutline() {
		return Optional.empty();
	}

	public boolean isOutline() {
		return false;
	}

	public boolean method_23037() {
		return this.hasCrumbling;
	}

	public Optional<RenderLayer> asOptional() {
		return this.optionalThis;
	}

	static final class MultiPhase extends RenderLayer {
		private static final ObjectOpenCustomHashSet<RenderLayer.MultiPhase> CACHE = new ObjectOpenCustomHashSet(RenderLayer.MultiPhase.HashStrategy.field_21698);
		private final RenderLayer.MultiPhaseParameters phases;
		private final int hash;
		private final Optional<RenderLayer> affectedOutline;
		private final boolean outline;

		private MultiPhase(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, RenderLayer.MultiPhaseParameters multiPhaseParameters) {
			super(
				string,
				vertexFormat,
				i,
				j,
				bl,
				bl2,
				() -> multiPhaseParameters.phases.forEach(RenderPhase::startDrawing),
				() -> multiPhaseParameters.phases.forEach(RenderPhase::endDrawing)
			);
			this.phases = multiPhaseParameters;
			this.affectedOutline = multiPhaseParameters.outlineMode == RenderLayer.OutlineMode.field_21855
				? multiPhaseParameters.texture.getId().map(RenderLayer::getOutline)
				: Optional.empty();
			this.outline = multiPhaseParameters.outlineMode == RenderLayer.OutlineMode.field_21854;
			this.hash = Objects.hash(new Object[]{super.hashCode(), multiPhaseParameters});
		}

		private static RenderLayer.MultiPhase of(
			String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, RenderLayer.MultiPhaseParameters multiPhaseParameters
		) {
			return (RenderLayer.MultiPhase)CACHE.addOrGet(new RenderLayer.MultiPhase(string, vertexFormat, i, j, bl, bl2, multiPhaseParameters));
		}

		@Override
		public Optional<RenderLayer> getAffectedOutline() {
			return this.affectedOutline;
		}

		@Override
		public boolean isOutline() {
			return this.outline;
		}

		@Override
		public boolean equals(@Nullable Object object) {
			return this == object;
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		static enum HashStrategy implements Strategy<RenderLayer.MultiPhase> {
			field_21698;

			public int hashCode(@Nullable RenderLayer.MultiPhase multiPhase) {
				return multiPhase == null ? 0 : multiPhase.hash;
			}

			public boolean equals(@Nullable RenderLayer.MultiPhase multiPhase, @Nullable RenderLayer.MultiPhase multiPhase2) {
				if (multiPhase == multiPhase2) {
					return true;
				} else {
					return multiPhase != null && multiPhase2 != null ? Objects.equals(multiPhase.phases, multiPhase2.phases) : false;
				}
			}
		}
	}

	public static final class MultiPhaseParameters {
		private final RenderPhase.Texture texture;
		private final RenderPhase.Transparency transparency;
		private final RenderPhase.DiffuseLighting diffuseLighting;
		private final RenderPhase.ShadeModel shadeModel;
		private final RenderPhase.Alpha alpha;
		private final RenderPhase.DepthTest depthTest;
		private final RenderPhase.Cull cull;
		private final RenderPhase.Lightmap lightmap;
		private final RenderPhase.Overlay overlay;
		private final RenderPhase.Fog fog;
		private final RenderPhase.Layering layering;
		private final RenderPhase.Target target;
		private final RenderPhase.Texturing texturing;
		private final RenderPhase.WriteMaskState writeMaskState;
		private final RenderPhase.LineWidth lineWidth;
		private final RenderLayer.OutlineMode outlineMode;
		private final ImmutableList<RenderPhase> phases;

		private MultiPhaseParameters(
			RenderPhase.Texture texture,
			RenderPhase.Transparency transparency,
			RenderPhase.DiffuseLighting diffuseLighting,
			RenderPhase.ShadeModel shadeModel,
			RenderPhase.Alpha alpha,
			RenderPhase.DepthTest depthTest,
			RenderPhase.Cull cull,
			RenderPhase.Lightmap lightmap,
			RenderPhase.Overlay overlay,
			RenderPhase.Fog fog,
			RenderPhase.Layering layering,
			RenderPhase.Target target,
			RenderPhase.Texturing texturing,
			RenderPhase.WriteMaskState writeMaskState,
			RenderPhase.LineWidth lineWidth,
			RenderLayer.OutlineMode outlineMode
		) {
			this.texture = texture;
			this.transparency = transparency;
			this.diffuseLighting = diffuseLighting;
			this.shadeModel = shadeModel;
			this.alpha = alpha;
			this.depthTest = depthTest;
			this.cull = cull;
			this.lightmap = lightmap;
			this.overlay = overlay;
			this.fog = fog;
			this.layering = layering;
			this.target = target;
			this.texturing = texturing;
			this.writeMaskState = writeMaskState;
			this.lineWidth = lineWidth;
			this.outlineMode = outlineMode;
			this.phases = ImmutableList.of(
				this.texture,
				this.transparency,
				this.diffuseLighting,
				this.shadeModel,
				this.alpha,
				this.depthTest,
				this.cull,
				this.lightmap,
				this.overlay,
				this.fog,
				this.layering,
				this.target,
				new RenderPhase[]{this.texturing, this.writeMaskState, this.lineWidth}
			);
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderLayer.MultiPhaseParameters multiPhaseParameters = (RenderLayer.MultiPhaseParameters)object;
				return this.outlineMode == multiPhaseParameters.outlineMode && this.phases.equals(multiPhaseParameters.phases);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.phases, this.outlineMode});
		}

		public static RenderLayer.MultiPhaseParameters.Builder builder() {
			return new RenderLayer.MultiPhaseParameters.Builder();
		}

		public static class Builder {
			private RenderPhase.Texture texture = RenderPhase.NO_TEXTURE;
			private RenderPhase.Transparency transparency = RenderPhase.NO_TRANSPARENCY;
			private RenderPhase.DiffuseLighting diffuseLighting = RenderPhase.DISABLE_DIFFUSE_LIGHTING;
			private RenderPhase.ShadeModel shadeModel = RenderPhase.SHADE_MODEL;
			private RenderPhase.Alpha alpha = RenderPhase.ZERO_ALPHA;
			private RenderPhase.DepthTest depthTest = RenderPhase.LEQUAL_DEPTH_TEST;
			private RenderPhase.Cull cull = RenderPhase.ENABLE_CULLING;
			private RenderPhase.Lightmap lightmap = RenderPhase.DISABLE_LIGHTMAP;
			private RenderPhase.Overlay overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
			private RenderPhase.Fog fog = RenderPhase.FOG;
			private RenderPhase.Layering layering = RenderPhase.NO_LAYERING;
			private RenderPhase.Target target = RenderPhase.MAIN_TARGET;
			private RenderPhase.Texturing texturing = RenderPhase.DEFAULT_TEXTURING;
			private RenderPhase.WriteMaskState writeMaskState = RenderPhase.ALL_MASK;
			private RenderPhase.LineWidth lineWidth = RenderPhase.FULL_LINEWIDTH;

			private Builder() {
			}

			public RenderLayer.MultiPhaseParameters.Builder texture(RenderPhase.Texture texture) {
				this.texture = texture;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder transparency(RenderPhase.Transparency transparency) {
				this.transparency = transparency;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder diffuseLighting(RenderPhase.DiffuseLighting diffuseLighting) {
				this.diffuseLighting = diffuseLighting;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder shadeModel(RenderPhase.ShadeModel shadeModel) {
				this.shadeModel = shadeModel;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder alpha(RenderPhase.Alpha alpha) {
				this.alpha = alpha;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder depthTest(RenderPhase.DepthTest depthTest) {
				this.depthTest = depthTest;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder cull(RenderPhase.Cull cull) {
				this.cull = cull;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder lightmap(RenderPhase.Lightmap lightmap) {
				this.lightmap = lightmap;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder overlay(RenderPhase.Overlay overlay) {
				this.overlay = overlay;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder fog(RenderPhase.Fog fog) {
				this.fog = fog;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder layering(RenderPhase.Layering layering) {
				this.layering = layering;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder target(RenderPhase.Target target) {
				this.target = target;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder texturing(RenderPhase.Texturing texturing) {
				this.texturing = texturing;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder writeMaskState(RenderPhase.WriteMaskState writeMaskState) {
				this.writeMaskState = writeMaskState;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder lineWidth(RenderPhase.LineWidth lineWidth) {
				this.lineWidth = lineWidth;
				return this;
			}

			public RenderLayer.MultiPhaseParameters build(boolean bl) {
				return this.build(bl ? RenderLayer.OutlineMode.field_21855 : RenderLayer.OutlineMode.field_21853);
			}

			public RenderLayer.MultiPhaseParameters build(RenderLayer.OutlineMode outlineMode) {
				return new RenderLayer.MultiPhaseParameters(
					this.texture,
					this.transparency,
					this.diffuseLighting,
					this.shadeModel,
					this.alpha,
					this.depthTest,
					this.cull,
					this.lightmap,
					this.overlay,
					this.fog,
					this.layering,
					this.target,
					this.texturing,
					this.writeMaskState,
					this.lineWidth,
					outlineMode
				);
			}
		}
	}

	static enum OutlineMode {
		field_21853,
		field_21854,
		field_21855;
	}
}
