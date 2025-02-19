package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public abstract class RenderLayer extends RenderPhase {
	private static final int field_32776 = 4;
	private static final int field_32777 = 1048576;
	public static final int SOLID_BUFFER_SIZE = 2097152;
	public static final int TRANSLUCENT_BUFFER_SIZE = 262144;
	public static final int CUTOUT_BUFFER_SIZE = 131072;
	public static final int DEFAULT_BUFFER_SIZE = 256;
	private static final RenderLayer SOLID = of(
		"solid",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		2097152,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).shader(SOLID_SHADER).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer CUTOUT_MIPPED = of(
		"cutout_mipped",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).shader(CUTOUT_MIPPED_SHADER).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer CUTOUT = of(
		"cutout",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).shader(CUTOUT_SHADER).texture(BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer TRANSLUCENT = of(
		"translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 2097152, true, true, of(TRANSLUCENT_SHADER)
	);
	private static final RenderLayer TRANSLUCENT_MOVING_BLOCK = of(
		"translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 262144, false, true, getItemPhaseData()
	);
	private static final RenderLayer TRANSLUCENT_NO_CRUMBLING = of(
		"translucent_no_crumbling",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		262144,
		false,
		true,
		of(TRANSLUCENT_NO_CRUMBLING_SHADER)
	);
	private static final Function<Identifier, RenderLayer> ARMOR_CUTOUT_NO_CULL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ARMOR_CUTOUT_NO_CULL_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.build(true);
			return of(
				"armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_SOLID = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_SOLID_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true);
			return of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_CUTOUT = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_CUTOUT_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true);
			return of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_CUTOUT_NO_CULL = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_CUTOUT_NONULL_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(affectsOutline);
			return of(
				"entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters
			);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_CUTOUT_NONULL_OFFSET_Z_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.build(affectsOutline);
			return of(
				"entity_cutout_no_cull_z_offset",
				VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				256,
				true,
				false,
				multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.target(ITEM_TARGET)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.writeMaskState(RenderPhase.ALL_MASK)
				.build(true);
			return of(
				"item_entity_translucent_cull",
				VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				256,
				true,
				true,
				multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_TRANSLUCENT_CULL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_TRANSLUCENT_CULL_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true);
			return of(
				"entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters
			);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_TRANSLUCENT = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_TRANSLUCENT_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(affectsOutline);
			return of(
				"entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_SMOOTH_CUTOUT = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_SMOOTH_CUTOUT_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.build(true);
			return of("entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, multiPhaseParameters);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> BEACON_BEAM = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(BEACON_BEAM_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(affectsOutline ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
				.writeMaskState(affectsOutline ? COLOR_MASK : ALL_MASK)
				.build(false);
			return of("beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_DECAL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_DECAL_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.depthTest(EQUAL_DEPTH_TEST)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(false);
			return of("entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_NO_OUTLINE = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_NO_OUTLINE_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.writeMaskState(COLOR_MASK)
				.build(false);
			return of(
				"entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_SHADOW = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_SHADOW_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(ENABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.writeMaskState(COLOR_MASK)
				.depthTest(LEQUAL_DEPTH_TEST)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.build(false);
			return of("entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, false, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_ALPHA = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(ENTITY_ALPHA_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.cull(DISABLE_CULLING)
				.build(true);
			return of("entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> EYES = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
			return of(
				"eyes",
				VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(EYES_SHADER)
					.texture(texture2)
					.transparency(ADDITIVE_TRANSPARENCY)
					.writeMaskState(COLOR_MASK)
					.build(false)
			);
		})
	);
	private static final RenderLayer LEASH = of(
		"leash",
		VertexFormats.POSITION_COLOR_LIGHT,
		VertexFormat.DrawMode.TRIANGLE_STRIP,
		256,
		RenderLayer.MultiPhaseParameters.builder().shader(LEASH_SHADER).texture(NO_TEXTURE).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false)
	);
	private static final RenderLayer WATER_MASK = of(
		"water_mask",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder().shader(WATER_MASK_SHADER).texture(NO_TEXTURE).writeMaskState(DEPTH_MASK).build(false)
	);
	private static final RenderLayer ARMOR_GLINT = of(
		"armor_glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(ARMOR_GLINT_SHADER)
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.build(false)
	);
	private static final RenderLayer ARMOR_ENTITY_GLINT = of(
		"armor_entity_glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(ARMOR_ENTITY_GLINT_SHADER)
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(ENTITY_GLINT_TEXTURING)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.build(false)
	);
	private static final RenderLayer GLINT_TRANSLUCENT = of(
		"glint_translucent",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(TRANSLUCENT_GLINT_SHADER)
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.target(ITEM_TARGET)
			.build(false)
	);
	private static final RenderLayer GLINT = of(
		"glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(GLINT_SHADER)
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer DIRECT_GLINT = of(
		"glint_direct",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(DIRECT_GLINT_SHADER)
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer ENTITY_GLINT = of(
		"entity_glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(ENTITY_GLINT_SHADER)
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer DIRECT_ENTITY_GLINT = of(
		"entity_glint_direct",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(DIRECT_ENTITY_GLINT_SHADER)
			.texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final Function<Identifier, RenderLayer> CRUMBLING = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
			return of(
				"crumbling",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(CRUMBLING_SHADER)
					.texture(texture2)
					.transparency(CRUMBLING_TRANSPARENCY)
					.writeMaskState(COLOR_MASK)
					.layering(POLYGON_OFFSET_LAYERING)
					.build(false)
			);
		})
	);
	private static final Function<Identifier, RenderLayer> TEXT = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(TEXT_SHADER)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_INTENSITY = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_intensity",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(TEXT_INTENSITY_SHADER)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_POLYGON_OFFSET = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_polygon_offset",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(TEXT_SHADER)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.layering(POLYGON_OFFSET_LAYERING)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_intensity_polygon_offset",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(TEXT_INTENSITY_SHADER)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.layering(POLYGON_OFFSET_LAYERING)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_SEE_THROUGH = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_see_through",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(TRANSPARENT_TEXT_SHADER)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.depthTest(ALWAYS_DEPTH_TEST)
					.writeMaskState(COLOR_MASK)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_INTENSITY_SEE_THROUGH = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_intensity_see_through",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.shader(TRANSPARENT_TEXT_INTENSITY_SHADER)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.depthTest(ALWAYS_DEPTH_TEST)
					.writeMaskState(COLOR_MASK)
					.build(false)
			))
	);
	private static final RenderLayer LIGHTNING = of(
		"lightning",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		256,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(LIGHTNING_SHADER)
			.writeMaskState(ALL_MASK)
			.transparency(LIGHTNING_TRANSPARENCY)
			.target(WEATHER_TARGET)
			.build(false)
	);
	private static final RenderLayer TRIPWIRE = of(
		"tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 262144, true, true, getTripwirePhaseData()
	);
	private static final RenderLayer END_PORTAL = of(
		"end_portal",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		256,
		false,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(END_PORTAL_SHADER)
			.texture(
				RenderPhase.Textures.create()
					.add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
					.add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false)
					.build()
			)
			.build(false)
	);
	private static final RenderLayer END_GATEWAY = of(
		"end_gateway",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		256,
		false,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(END_GATEWAY_SHADER)
			.texture(
				RenderPhase.Textures.create()
					.add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
					.add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false)
					.build()
			)
			.build(false)
	);
	public static final RenderLayer.MultiPhase LINES = of(
		"lines",
		VertexFormats.LINES,
		VertexFormat.DrawMode.LINES,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(LINES_SHADER)
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.writeMaskState(ALL_MASK)
			.cull(DISABLE_CULLING)
			.build(false)
	);
	public static final RenderLayer.MultiPhase LINE_STRIP = of(
		"line_strip",
		VertexFormats.LINES,
		VertexFormat.DrawMode.LINE_STRIP,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.shader(LINES_SHADER)
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.writeMaskState(ALL_MASK)
			.cull(DISABLE_CULLING)
			.build(false)
	);
	private final VertexFormat vertexFormat;
	private final VertexFormat.DrawMode drawMode;
	private final int expectedBufferSize;
	private final boolean hasCrumbling;
	private final boolean translucent;
	private final Optional<RenderLayer> optionalThis;

	public static RenderLayer getSolid() {
		return SOLID;
	}

	public static RenderLayer getCutoutMipped() {
		return CUTOUT_MIPPED;
	}

	public static RenderLayer getCutout() {
		return CUTOUT;
	}

	private static RenderLayer.MultiPhaseParameters of(RenderPhase.Shader shader) {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.shader(shader)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(TRANSLUCENT_TARGET)
			.build(true);
	}

	public static RenderLayer getTranslucent() {
		return TRANSLUCENT;
	}

	private static RenderLayer.MultiPhaseParameters getItemPhaseData() {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.shader(TRANSLUCENT_MOVING_BLOCK_SHADER)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.build(true);
	}

	public static RenderLayer getTranslucentMovingBlock() {
		return TRANSLUCENT_MOVING_BLOCK;
	}

	public static RenderLayer getTranslucentNoCrumbling() {
		return TRANSLUCENT_NO_CRUMBLING;
	}

	public static RenderLayer getArmorCutoutNoCull(Identifier texture) {
		return (RenderLayer)ARMOR_CUTOUT_NO_CULL.apply(texture);
	}

	public static RenderLayer getEntitySolid(Identifier texture) {
		return (RenderLayer)ENTITY_SOLID.apply(texture);
	}

	public static RenderLayer getEntityCutout(Identifier texture) {
		return (RenderLayer)ENTITY_CUTOUT.apply(texture);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier texture, boolean affectsOutline) {
		return (RenderLayer)ENTITY_CUTOUT_NO_CULL.apply(texture, affectsOutline);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier texture) {
		return getEntityCutoutNoCull(texture, true);
	}

	public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture, boolean affectsOutline) {
		return (RenderLayer)ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(texture, affectsOutline);
	}

	public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture) {
		return getEntityCutoutNoCullZOffset(texture, true);
	}

	public static RenderLayer getItemEntityTranslucentCull(Identifier texture) {
		return (RenderLayer)ITEM_ENTITY_TRANSLUCENT_CULL.apply(texture);
	}

	public static RenderLayer getEntityTranslucentCull(Identifier texture) {
		return (RenderLayer)ENTITY_TRANSLUCENT_CULL.apply(texture);
	}

	public static RenderLayer getEntityTranslucent(Identifier texture, boolean affectsOutline) {
		return (RenderLayer)ENTITY_TRANSLUCENT.apply(texture, affectsOutline);
	}

	public static RenderLayer getEntityTranslucent(Identifier texture) {
		return getEntityTranslucent(texture, true);
	}

	public static RenderLayer getEntitySmoothCutout(Identifier texture) {
		return (RenderLayer)ENTITY_SMOOTH_CUTOUT.apply(texture);
	}

	public static RenderLayer getBeaconBeam(Identifier texture, boolean translucent) {
		return (RenderLayer)BEACON_BEAM.apply(texture, translucent);
	}

	public static RenderLayer getEntityDecal(Identifier texture) {
		return (RenderLayer)ENTITY_DECAL.apply(texture);
	}

	public static RenderLayer getEntityNoOutline(Identifier texture) {
		return (RenderLayer)ENTITY_NO_OUTLINE.apply(texture);
	}

	public static RenderLayer getEntityShadow(Identifier texture) {
		return (RenderLayer)ENTITY_SHADOW.apply(texture);
	}

	public static RenderLayer getEntityAlpha(Identifier texture) {
		return (RenderLayer)ENTITY_ALPHA.apply(texture);
	}

	public static RenderLayer getEyes(Identifier texture) {
		return (RenderLayer)EYES.apply(texture);
	}

	public static RenderLayer getEnergySwirl(Identifier texture, float x, float y) {
		return of(
			"energy_swirl",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.shader(ENERGY_SWIRL_SHADER)
				.texture(new RenderPhase.Texture(texture, false, false))
				.texturing(new RenderPhase.OffsetTexturing(x, y))
				.transparency(ADDITIVE_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(false)
		);
	}

	public static RenderLayer getLeash() {
		return LEASH;
	}

	public static RenderLayer getWaterMask() {
		return WATER_MASK;
	}

	public static RenderLayer getOutline(Identifier texture) {
		return (RenderLayer)RenderLayer.MultiPhase.CULLING_LAYERS.apply(texture, DISABLE_CULLING);
	}

	public static RenderLayer getArmorGlint() {
		return ARMOR_GLINT;
	}

	public static RenderLayer getArmorEntityGlint() {
		return ARMOR_ENTITY_GLINT;
	}

	public static RenderLayer getGlintTranslucent() {
		return GLINT_TRANSLUCENT;
	}

	public static RenderLayer getGlint() {
		return GLINT;
	}

	public static RenderLayer getDirectGlint() {
		return DIRECT_GLINT;
	}

	public static RenderLayer getEntityGlint() {
		return ENTITY_GLINT;
	}

	public static RenderLayer getDirectEntityGlint() {
		return DIRECT_ENTITY_GLINT;
	}

	public static RenderLayer getBlockBreaking(Identifier texture) {
		return (RenderLayer)CRUMBLING.apply(texture);
	}

	public static RenderLayer getText(Identifier texture) {
		return (RenderLayer)TEXT.apply(texture);
	}

	public static RenderLayer getTextIntensity(Identifier texture) {
		return (RenderLayer)TEXT_INTENSITY.apply(texture);
	}

	public static RenderLayer getTextPolygonOffset(Identifier texture) {
		return (RenderLayer)TEXT_POLYGON_OFFSET.apply(texture);
	}

	public static RenderLayer getTextIntensityPolygonOffset(Identifier texture) {
		return (RenderLayer)TEXT_INTENSITY_POLYGON_OFFSET.apply(texture);
	}

	public static RenderLayer getTextSeeThrough(Identifier texture) {
		return (RenderLayer)TEXT_SEE_THROUGH.apply(texture);
	}

	public static RenderLayer getTextIntensitySeeThrough(Identifier texture) {
		return (RenderLayer)TEXT_INTENSITY_SEE_THROUGH.apply(texture);
	}

	public static RenderLayer getLightning() {
		return LIGHTNING;
	}

	private static RenderLayer.MultiPhaseParameters getTripwirePhaseData() {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.shader(TRIPWIRE_SHADER)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(WEATHER_TARGET)
			.build(true);
	}

	public static RenderLayer getTripwire() {
		return TRIPWIRE;
	}

	public static RenderLayer getEndPortal() {
		return END_PORTAL;
	}

	public static RenderLayer getEndGateway() {
		return END_GATEWAY;
	}

	public static RenderLayer getLines() {
		return LINES;
	}

	public static RenderLayer getLineStrip() {
		return LINE_STRIP;
	}

	public RenderLayer(
		String name,
		VertexFormat vertexFormat,
		VertexFormat.DrawMode drawMode,
		int expectedBufferSize,
		boolean hasCrumbling,
		boolean translucent,
		Runnable startAction,
		Runnable endAction
	) {
		super(name, startAction, endAction);
		this.vertexFormat = vertexFormat;
		this.drawMode = drawMode;
		this.expectedBufferSize = expectedBufferSize;
		this.hasCrumbling = hasCrumbling;
		this.translucent = translucent;
		this.optionalThis = Optional.of(this);
	}

	static RenderLayer.MultiPhase of(
		String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, RenderLayer.MultiPhaseParameters phaseData
	) {
		return of(name, vertexFormat, drawMode, expectedBufferSize, false, false, phaseData);
	}

	private static RenderLayer.MultiPhase of(
		String name,
		VertexFormat vertexFormat,
		VertexFormat.DrawMode drawMode,
		int expectedBufferSize,
		boolean hasCrumbling,
		boolean translucent,
		RenderLayer.MultiPhaseParameters phases
	) {
		return new RenderLayer.MultiPhase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
	}

	public void draw(BufferBuilder buffer, int cameraX, int cameraY, int cameraZ) {
		if (buffer.isBuilding()) {
			if (this.translucent) {
				buffer.setCameraPosition((float)cameraX, (float)cameraY, (float)cameraZ);
			}

			buffer.end();
			this.startDrawing();
			BufferRenderer.draw(buffer);
			this.endDrawing();
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static List<RenderLayer> getBlockLayers() {
		return ImmutableList.of(getSolid(), getCutoutMipped(), getCutout(), getTranslucent(), getTripwire());
	}

	public int getExpectedBufferSize() {
		return this.expectedBufferSize;
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public VertexFormat.DrawMode getDrawMode() {
		return this.drawMode;
	}

	public Optional<RenderLayer> getAffectedOutline() {
		return Optional.empty();
	}

	public boolean isOutline() {
		return false;
	}

	public boolean hasCrumbling() {
		return this.hasCrumbling;
	}

	public Optional<RenderLayer> asOptional() {
		return this.optionalThis;
	}

	static final class MultiPhase extends RenderLayer {
		static final BiFunction<Identifier, RenderPhase.Cull, RenderLayer> CULLING_LAYERS = Util.memoize(
			(BiFunction<Identifier, RenderPhase.Cull, RenderLayer>)((texture, culling) -> RenderLayer.of(
					"outline",
					VertexFormats.POSITION_COLOR_TEXTURE,
					VertexFormat.DrawMode.QUADS,
					256,
					RenderLayer.MultiPhaseParameters.builder()
						.shader(OUTLINE_SHADER)
						.texture(new RenderPhase.Texture(texture, false, false))
						.cull(culling)
						.depthTest(ALWAYS_DEPTH_TEST)
						.target(OUTLINE_TARGET)
						.build(RenderLayer.OutlineMode.IS_OUTLINE)
				))
		);
		private final RenderLayer.MultiPhaseParameters phases;
		private final Optional<RenderLayer> affectedOutline;
		private final boolean outline;

		MultiPhase(
			String name,
			VertexFormat vertexFormat,
			VertexFormat.DrawMode drawMode,
			int expectedBufferSize,
			boolean hasCrumbling,
			boolean translucent,
			RenderLayer.MultiPhaseParameters phases
		) {
			super(
				name,
				vertexFormat,
				drawMode,
				expectedBufferSize,
				hasCrumbling,
				translucent,
				() -> phases.phases.forEach(RenderPhase::startDrawing),
				() -> phases.phases.forEach(RenderPhase::endDrawing)
			);
			this.phases = phases;
			this.affectedOutline = phases.outlineMode == RenderLayer.OutlineMode.AFFECTS_OUTLINE
				? phases.texture.getId().map(texture -> (RenderLayer)CULLING_LAYERS.apply(texture, phases.cull))
				: Optional.empty();
			this.outline = phases.outlineMode == RenderLayer.OutlineMode.IS_OUTLINE;
		}

		@Override
		public Optional<RenderLayer> getAffectedOutline() {
			return this.affectedOutline;
		}

		@Override
		public boolean isOutline() {
			return this.outline;
		}

		protected final RenderLayer.MultiPhaseParameters getPhases() {
			return this.phases;
		}

		@Override
		public String toString() {
			return "RenderType[" + this.name + ":" + this.phases + "]";
		}
	}

	protected static final class MultiPhaseParameters {
		final RenderPhase.TextureBase texture;
		private final RenderPhase.Shader shader;
		private final RenderPhase.Transparency transparency;
		private final RenderPhase.DepthTest depthTest;
		final RenderPhase.Cull cull;
		private final RenderPhase.Lightmap lightmap;
		private final RenderPhase.Overlay overlay;
		private final RenderPhase.Layering layering;
		private final RenderPhase.Target target;
		private final RenderPhase.Texturing texturing;
		private final RenderPhase.WriteMaskState writeMaskState;
		private final RenderPhase.LineWidth lineWidth;
		final RenderLayer.OutlineMode outlineMode;
		final ImmutableList<RenderPhase> phases;

		MultiPhaseParameters(
			RenderPhase.TextureBase texture,
			RenderPhase.Shader shader,
			RenderPhase.Transparency transparency,
			RenderPhase.DepthTest depthTest,
			RenderPhase.Cull cull,
			RenderPhase.Lightmap lightmap,
			RenderPhase.Overlay overlay,
			RenderPhase.Layering layering,
			RenderPhase.Target target,
			RenderPhase.Texturing texturing,
			RenderPhase.WriteMaskState writeMaskState,
			RenderPhase.LineWidth lineWidth,
			RenderLayer.OutlineMode outlineMode
		) {
			this.texture = texture;
			this.shader = shader;
			this.transparency = transparency;
			this.depthTest = depthTest;
			this.cull = cull;
			this.lightmap = lightmap;
			this.overlay = overlay;
			this.layering = layering;
			this.target = target;
			this.texturing = texturing;
			this.writeMaskState = writeMaskState;
			this.lineWidth = lineWidth;
			this.outlineMode = outlineMode;
			this.phases = ImmutableList.of(
				this.texture,
				this.shader,
				this.transparency,
				this.depthTest,
				this.cull,
				this.lightmap,
				this.overlay,
				this.layering,
				this.target,
				this.texturing,
				this.writeMaskState,
				this.lineWidth,
				new RenderPhase[0]
			);
		}

		public String toString() {
			return "CompositeState[" + this.phases + ", outlineProperty=" + this.outlineMode + "]";
		}

		public static RenderLayer.MultiPhaseParameters.Builder builder() {
			return new RenderLayer.MultiPhaseParameters.Builder();
		}

		public static class Builder {
			private RenderPhase.TextureBase texture = RenderPhase.NO_TEXTURE;
			private RenderPhase.Shader shader = RenderPhase.NO_SHADER;
			private RenderPhase.Transparency transparency = RenderPhase.NO_TRANSPARENCY;
			private RenderPhase.DepthTest depthTest = RenderPhase.LEQUAL_DEPTH_TEST;
			private RenderPhase.Cull cull = RenderPhase.ENABLE_CULLING;
			private RenderPhase.Lightmap lightmap = RenderPhase.DISABLE_LIGHTMAP;
			private RenderPhase.Overlay overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
			private RenderPhase.Layering layering = RenderPhase.NO_LAYERING;
			private RenderPhase.Target target = RenderPhase.MAIN_TARGET;
			private RenderPhase.Texturing texturing = RenderPhase.DEFAULT_TEXTURING;
			private RenderPhase.WriteMaskState writeMaskState = RenderPhase.ALL_MASK;
			private RenderPhase.LineWidth lineWidth = RenderPhase.FULL_LINE_WIDTH;

			Builder() {
			}

			public RenderLayer.MultiPhaseParameters.Builder texture(RenderPhase.TextureBase texture) {
				this.texture = texture;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder shader(RenderPhase.Shader shader) {
				this.shader = shader;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder transparency(RenderPhase.Transparency transparency) {
				this.transparency = transparency;
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

			public RenderLayer.MultiPhaseParameters build(boolean affectsOutline) {
				return this.build(affectsOutline ? RenderLayer.OutlineMode.AFFECTS_OUTLINE : RenderLayer.OutlineMode.NONE);
			}

			public RenderLayer.MultiPhaseParameters build(RenderLayer.OutlineMode outlineMode) {
				return new RenderLayer.MultiPhaseParameters(
					this.texture,
					this.shader,
					this.transparency,
					this.depthTest,
					this.cull,
					this.lightmap,
					this.overlay,
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
		NONE("none"),
		IS_OUTLINE("is_outline"),
		AFFECTS_OUTLINE("affects_outline");

		private final String name;

		private OutlineMode(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}
	}
}
