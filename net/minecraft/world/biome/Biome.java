package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_3710;
import net.minecraft.class_3781;
import net.minecraft.class_3798;
import net.minecraft.class_3801;
import net.minecraft.class_3812;
import net.minecraft.class_3816;
import net.minecraft.class_3821;
import net.minecraft.class_3826;
import net.minecraft.class_3827;
import net.minecraft.class_3829;
import net.minecraft.class_3830;
import net.minecraft.class_3831;
import net.minecraft.class_3832;
import net.minecraft.class_3835;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.class_3847;
import net.minecraft.class_3848;
import net.minecraft.class_3855;
import net.minecraft.class_3859;
import net.minecraft.class_3866;
import net.minecraft.class_3867;
import net.minecraft.class_3870;
import net.minecraft.class_3872;
import net.minecraft.class_3874;
import net.minecraft.class_3877;
import net.minecraft.class_3890;
import net.minecraft.class_3900;
import net.minecraft.class_3902;
import net.minecraft.class_3905;
import net.minecraft.class_3911;
import net.minecraft.class_3914;
import net.minecraft.class_3918;
import net.minecraft.class_3919;
import net.minecraft.class_3920;
import net.minecraft.class_3921;
import net.minecraft.class_3922;
import net.minecraft.class_3923;
import net.minecraft.class_3924;
import net.minecraft.class_3925;
import net.minecraft.class_3926;
import net.minecraft.class_3927;
import net.minecraft.class_3928;
import net.minecraft.class_3929;
import net.minecraft.class_3930;
import net.minecraft.class_3931;
import net.minecraft.class_3932;
import net.minecraft.class_3933;
import net.minecraft.class_3934;
import net.minecraft.class_3935;
import net.minecraft.class_3936;
import net.minecraft.class_3937;
import net.minecraft.class_3939;
import net.minecraft.class_3940;
import net.minecraft.class_3941;
import net.minecraft.class_3942;
import net.minecraft.class_3943;
import net.minecraft.class_3944;
import net.minecraft.class_3945;
import net.minecraft.class_3946;
import net.minecraft.class_3947;
import net.minecraft.class_3948;
import net.minecraft.class_3949;
import net.minecraft.class_3950;
import net.minecraft.class_3951;
import net.minecraft.class_3952;
import net.minecraft.class_3953;
import net.minecraft.class_3954;
import net.minecraft.class_3955;
import net.minecraft.class_3956;
import net.minecraft.class_3957;
import net.minecraft.class_3958;
import net.minecraft.class_3959;
import net.minecraft.class_3960;
import net.minecraft.class_3961;
import net.minecraft.class_3962;
import net.minecraft.class_3963;
import net.minecraft.class_3964;
import net.minecraft.class_3967;
import net.minecraft.class_3968;
import net.minecraft.class_3969;
import net.minecraft.class_3971;
import net.minecraft.class_3972;
import net.minecraft.class_3973;
import net.minecraft.class_3975;
import net.minecraft.class_3983;
import net.minecraft.class_3994;
import net.minecraft.class_3995;
import net.minecraft.class_3997;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.class_4003;
import net.minecraft.class_4004;
import net.minecraft.class_4005;
import net.minecraft.class_4006;
import net.minecraft.class_4007;
import net.minecraft.class_4008;
import net.minecraft.class_4010;
import net.minecraft.class_4011;
import net.minecraft.class_4012;
import net.minecraft.class_4013;
import net.minecraft.class_4014;
import net.minecraft.class_4015;
import net.minecraft.class_4016;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.VillagePieces;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.RenderBlockView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final class_3969<class_3877> field_17600 = new class_3972();
	public static final class_3969<class_3877> field_17601 = new class_3975();
	public static final class_3969<class_3877> field_17602 = new class_3971();
	public static final class_3969<class_3877> field_17603 = new class_3994();
	public static final class_3969<class_3877> field_17604 = new class_3995();
	public static final class_3945<class_3935> field_17605 = new class_3929();
	public static final class_3945<class_3935> field_17606 = new class_3930();
	public static final class_3945<class_3935> field_17607 = new class_3928();
	public static final class_3945<class_3935> field_17608 = new class_3926();
	public static final class_3945<class_3935> field_17609 = new class_3927();
	public static final class_3945<class_3832> field_17610 = new class_3953();
	public static final class_3945<class_3832> field_17611 = new class_3954();
	public static final class_3945<class_3870> field_17612 = new class_3955();
	public static final class_3945<class_3934> field_17613 = new class_3919();
	public static final class_3945<class_3934> field_17614 = new class_3920();
	public static final class_3945<class_3934> field_17615 = new class_3921();
	public static final class_3945<class_3934> field_17616 = new class_3922();
	public static final class_3945<class_3937> field_17617 = new class_3932();
	public static final class_3945<class_3831> field_17618 = new class_3962();
	public static final class_3945<class_3831> field_17619 = new class_3924();
	public static final class_3945<class_3831> field_17620 = new class_3931();
	public static final class_3945<class_3831> field_17621 = new class_3968();
	public static final class_3945<class_3829> field_17622 = new class_3961();
	public static final class_3945<class_3936> field_17623 = new class_3826();
	public static final class_3945<class_3936> field_17624 = new class_3827();
	public static final class_3945<class_3941> field_17531 = new class_3925();
	public static final class_3945<class_3870> field_17532 = new class_3958();
	public static final class_3945<class_3940> field_17533 = new class_3960();
	public static final class_3945<class_3939> field_17534 = new class_3959();
	public static final class_3945<class_3933> field_17535 = new class_3918();
	public static final class_3945<class_3935> field_17536 = new class_3946();
	public static final class_3945<class_3935> field_17537 = new class_3963();
	public static final class_3945<class_3935> field_17538 = new class_3967();
	public static final class_3945<class_3870> field_17539 = new class_3942();
	public static final class_3945<class_3948> field_17540 = new class_3949();
	public static final class_3945<class_3948> field_17541 = new class_3950();
	public static final class_3945<class_3951> field_17542 = new class_3952();
	public static final class_3945<class_3870> field_17543 = new class_3956();
	public static final class_3945<class_3934> field_17544 = new class_3947();
	public static final class_3945<class_3935> field_17545 = new class_3964();
	public static final class_3945<class_3870> field_17546 = new class_3957();
	public static final class_3945<class_3870> field_17547 = new class_3944();
	public static final class_3945<class_3870> field_17548 = new class_3923();
	public static final class_3945<class_3870> field_17549 = new class_3943();
	protected static final BlockState field_17550 = Blocks.AIR.getDefaultState();
	protected static final BlockState field_17551 = Blocks.DIRT.getDefaultState();
	protected static final BlockState field_17552 = Blocks.GRASS_BLOCK.getDefaultState();
	protected static final BlockState field_17553 = Blocks.PODZOL.getDefaultState();
	protected static final BlockState field_17554 = Blocks.GRAVEL.getDefaultState();
	protected static final BlockState field_17555 = Blocks.STONE.getDefaultState();
	protected static final BlockState field_17556 = Blocks.COARSE_DIRT.getDefaultState();
	protected static final BlockState field_17574 = Blocks.SAND.getDefaultState();
	protected static final BlockState field_17575 = Blocks.RED_SAND.getDefaultState();
	protected static final BlockState field_17576 = Blocks.WHITE_TERRACOTTA.getDefaultState();
	protected static final BlockState field_17577 = Blocks.MYCELIUM.getDefaultState();
	protected static final BlockState field_17578 = Blocks.NETHERRACK.getDefaultState();
	protected static final BlockState field_17579 = Blocks.END_STONE.getDefaultState();
	public static final class_4013 field_17580 = new class_4013(field_17550, field_17550, field_17550);
	public static final class_4013 field_17581 = new class_4013(field_17551, field_17551, field_17554);
	public static final class_4013 field_17582 = new class_4013(field_17552, field_17551, field_17554);
	public static final class_4013 field_17583 = new class_4013(field_17555, field_17555, field_17554);
	public static final class_4013 field_17584 = new class_4013(field_17554, field_17554, field_17554);
	public static final class_4013 field_17585 = new class_4013(field_17556, field_17551, field_17554);
	public static final class_4013 field_17586 = new class_4013(field_17553, field_17551, field_17554);
	public static final class_4013 field_17587 = new class_4013(field_17574, field_17574, field_17574);
	public static final class_4013 field_17588 = new class_4013(field_17552, field_17551, field_17574);
	public static final class_4013 field_17589 = new class_4013(field_17574, field_17574, field_17554);
	public static final class_4013 field_17590 = new class_4013(field_17575, field_17576, field_17554);
	public static final class_4013 field_17591 = new class_4013(field_17577, field_17551, field_17554);
	public static final class_4013 field_17592 = new class_4013(field_17578, field_17578, field_17578);
	public static final class_4013 field_17593 = new class_4013(field_17579, field_17579, field_17579);
	public static final class_4012<class_4013> field_17594 = new class_4001();
	public static final class_4012<class_4013> field_17595 = new class_4003();
	public static final class_4012<class_4013> field_17596 = new class_4011();
	public static final class_4012<class_4013> field_17597 = new class_4002();
	public static final class_4012<class_4013> field_17598 = new class_4016();
	public static final class_4012<class_4013> field_17599 = new class_4015();
	public static final class_4012<class_4013> field_17557 = new class_4007();
	public static final class_4012<class_4013> field_17558 = new class_4006();
	public static final class_4012<class_4013> field_17559 = new class_4005();
	public static final class_4012<class_4013> field_17560 = new class_4004();
	public static final class_4012<class_4013> field_17561 = new class_4008();
	public static final class_4012<class_4013> field_17562 = new class_4010();
	public static final Set<Biome> ALL_BIOMES = Sets.newHashSet();
	public static final IdList<Biome> biomeList = new IdList<>();
	protected static final PerlinNoiseGenerator TEMPERATURE_NOISE = new PerlinNoiseGenerator(new Random(1234L), 1);
	public static final PerlinNoiseGenerator FOLIAGE_NOISE = new PerlinNoiseGenerator(new Random(2345L), 1);
	@Nullable
	protected String translationKey;
	protected final float depth;
	protected final float variationModifier;
	protected final float temperature;
	protected final float downfall;
	protected final int waterColor;
	protected final int waterFogColor;
	@Nullable
	protected final String parent;
	protected final SurfaceBuilder<?> surfaceBuilder;
	protected final Biome.Category category;
	protected final Biome.Precipitation precipitation;
	protected final Map<class_3801.class_3802, List<class_3973<?>>> field_17569 = Maps.newHashMap();
	protected final Map<class_3801.class_3803, List<class_3821<?, ?>>> field_17570 = Maps.newHashMap();
	protected final List<class_3847<?>> field_17571 = Lists.newArrayList();
	protected final Map<class_3902<?>, class_3845> field_17572 = Maps.newHashMap();
	private final Map<EntityCategory, List<Biome.SpawnEntry>> field_17573 = Maps.newHashMap();

	@Nullable
	public static Biome getBiomeFromList(Biome biome) {
		return biomeList.fromId(Registry.BIOME.getRawId(biome));
	}

	public static <C extends class_3845> class_3973<C> method_16437(class_3997<C> arg, C arg2) {
		return new class_3973<>(arg, arg2);
	}

	public static <F extends class_3845, D extends class_3830> class_3821<F, D> method_16433(class_3844<F> arg, F arg2, class_3945<D> arg3, D arg4) {
		return new class_3821<>(arg, arg2, arg3, arg4);
	}

	public static <D extends class_3830> class_3847<D> method_16434(class_3848 arg, class_3945<D> arg2, D arg3) {
		return new class_3847<>(arg, arg2, arg3);
	}

	protected Biome(Biome.Builder builder) {
		if (builder.surfaceBuilder != null
			&& builder.precipitation != null
			&& builder.category != null
			&& builder.depth != null
			&& builder.scale != null
			&& builder.temperature != null
			&& builder.downfall != null
			&& builder.waterColor != null
			&& builder.waterFogColor != null) {
			this.surfaceBuilder = builder.surfaceBuilder;
			this.precipitation = builder.precipitation;
			this.category = builder.category;
			this.depth = builder.depth;
			this.variationModifier = builder.scale;
			this.temperature = builder.temperature;
			this.downfall = builder.downfall;
			this.waterColor = builder.waterColor;
			this.waterFogColor = builder.waterFogColor;
			this.parent = builder.parent;

			for (class_3801.class_3803 lv : class_3801.class_3803.values()) {
				this.field_17570.put(lv, Lists.newArrayList());
			}

			for (EntityCategory entityCategory : EntityCategory.values()) {
				this.field_17573.put(entityCategory, Lists.newArrayList());
			}
		} else {
			throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + builder);
		}
	}

	protected void method_16424() {
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_STRUCTURES,
			method_16433(class_3844.field_19182, new class_3866(0.004F, class_3867.class_3014.NORMAL), field_17612, class_3830.field_19084)
		);
		this.method_16432(
			class_3801.class_3803.SURFACE_STRUCTURES,
			method_16433(class_3844.field_19181, new class_3911(0, VillagePieces.class_3996.OAK), field_17612, class_3830.field_19084)
		);
		this.method_16432(class_3801.class_3803.UNDERGROUND_STRUCTURES, method_16433(class_3844.field_19189, new class_3900(), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19188, new class_3905(), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19185, new class_3835(), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19184, new class_3859(), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19186, new class_3855(), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19187, new class_3890(false), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19190, new class_3872(), field_17612, class_3830.field_19084));
		this.method_16432(class_3801.class_3803.SURFACE_STRUCTURES, method_16433(class_3844.field_19183, new class_3914(), field_17612, class_3830.field_19084));
		this.method_16432(
			class_3801.class_3803.SURFACE_STRUCTURES,
			method_16433(class_3844.field_19191, new class_3874(class_3983.class_3985.COLD, 0.3F, 0.9F), field_17612, class_3830.field_19084)
		);
		this.method_16432(
			class_3801.class_3803.UNDERGROUND_STRUCTURES, method_16433(class_3844.field_19194, new class_3816(0.01F), field_17612, class_3830.field_19084)
		);
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public int getSkyColor(float temperature) {
		temperature /= 3.0F;
		temperature = MathHelper.clamp(temperature, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - temperature * 0.05F, 0.5F + temperature * 0.1F, 1.0F);
	}

	protected void method_16425(EntityCategory entityCategory, Biome.SpawnEntry spawnEntry) {
		((List)this.field_17573.get(entityCategory)).add(spawnEntry);
	}

	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category) {
		return (List<Biome.SpawnEntry>)this.field_17573.get(category);
	}

	public Biome.Precipitation getPrecipitation() {
		return this.precipitation;
	}

	public boolean hasHighHumidity() {
		return this.getRainfall() > 0.85F;
	}

	public float getMaxSpawnLimit() {
		return 0.1F;
	}

	public float getTemperature(BlockPos pos) {
		if (pos.getY() > 64) {
			float f = (float)(TEMPERATURE_NOISE.noise((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 4.0);
			return this.getTemperature() - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return this.getTemperature();
		}
	}

	public boolean method_16426(RenderBlockView renderBlockView, BlockPos blockPos) {
		return this.method_16427(renderBlockView, blockPos, true);
	}

	public boolean method_16427(RenderBlockView renderBlockView, BlockPos blockPos, boolean bl) {
		if (this.getTemperature(blockPos) >= 0.15F) {
			return false;
		} else {
			if (blockPos.getY() >= 0 && blockPos.getY() < 256 && renderBlockView.method_16370(LightType.BLOCK, blockPos) < 10) {
				BlockState blockState = renderBlockView.getBlockState(blockPos);
				FluidState fluidState = renderBlockView.getFluidState(blockPos);
				if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof class_3710) {
					if (!bl) {
						return true;
					}

					boolean bl2 = renderBlockView.method_16357(blockPos.west())
						&& renderBlockView.method_16357(blockPos.east())
						&& renderBlockView.method_16357(blockPos.north())
						&& renderBlockView.method_16357(blockPos.south());
					if (!bl2) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean method_16439(RenderBlockView renderBlockView, BlockPos blockPos) {
		if (this.getTemperature(blockPos) >= 0.15F) {
			return false;
		} else {
			if (blockPos.getY() >= 0 && blockPos.getY() < 256 && renderBlockView.method_16370(LightType.BLOCK, blockPos) < 10) {
				BlockState blockState = renderBlockView.getBlockState(blockPos);
				if (blockState.isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(renderBlockView, blockPos)) {
					return true;
				}
			}

			return false;
		}
	}

	public void method_16432(class_3801.class_3803 arg, class_3821<?, ?> arg2) {
		if (arg2 instanceof class_3847) {
			this.field_17571.add(arg2);
		}

		((List)this.field_17570.get(arg)).add(arg2);
	}

	public <C extends class_3845> void method_16429(class_3801.class_3802 arg, class_3973<C> arg2) {
		((List)this.field_17569.computeIfAbsent(arg, argx -> Lists.newArrayList())).add(arg2);
	}

	public List<class_3973<?>> method_16428(class_3801.class_3802 arg) {
		return (List<class_3973<?>>)this.field_17569.computeIfAbsent(arg, argx -> Lists.newArrayList());
	}

	public <C extends class_3845> void method_16436(class_3902<C> arg, C arg2) {
		this.field_17572.put(arg, arg2);
	}

	public <C extends class_3845> boolean method_16435(class_3902<C> arg) {
		return this.field_17572.containsKey(arg);
	}

	@Nullable
	public <C extends class_3845> class_3845 method_16441(class_3902<C> arg) {
		return (class_3845)this.field_17572.get(arg);
	}

	public List<class_3847<?>> method_16444() {
		return this.field_17571;
	}

	public List<class_3821<?, ?>> method_16430(class_3801.class_3803 arg) {
		return (List<class_3821<?, ?>>)this.field_17570.get(arg);
	}

	public void method_16431(
		class_3801.class_3803 arg, ChunkGenerator<? extends class_3798> chunkGenerator, IWorld iWorld, long l, class_3812 arg2, BlockPos blockPos
	) {
		int i = 0;

		for (class_3821<?, ?> lv : (List)this.field_17570.get(arg)) {
			arg2.method_17290(l, i, arg.ordinal());
			lv.method_17343(iWorld, chunkGenerator, arg2, blockPos, class_3845.field_19203);
			i++;
		}
	}

	public int getGrassColor(BlockPos pos) {
		double d = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return GrassColors.getColor(d, e);
	}

	public int getFoliageColor(BlockPos pos) {
		double d = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return FoliageColors.getColor(d, e);
	}

	public void method_16438(Random random, class_3781 arg, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m) {
		this.surfaceBuilder.method_17717(m);
		this.surfaceBuilder.method_17718(random, arg, this, i, j, k, d, blockState, blockState2, l, m, field_17580);
	}

	public Biome.Temperature getBiomeTemperature() {
		if (this.category == Biome.Category.OCEAN) {
			return Biome.Temperature.OCEAN;
		} else if ((double)this.getTemperature() < 0.2) {
			return Biome.Temperature.COLD;
		} else {
			return (double)this.getTemperature() < 1.0 ? Biome.Temperature.MEDIUM : Biome.Temperature.WARM;
		}
	}

	public static Biome getByRawIdOrDefault(int id, Biome biome) {
		Biome biome2 = Registry.BIOME.getByRawId(id);
		return biome2 == null ? biome : biome2;
	}

	public final float getDepth() {
		return this.depth;
	}

	public final float getRainfall() {
		return this.downfall;
	}

	public Text method_16445() {
		return new TranslatableText(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("biome", Registry.BIOME.getId(this));
		}

		return this.translationKey;
	}

	public final float getVariationModifier() {
		return this.variationModifier;
	}

	public final float getTemperature() {
		return this.temperature;
	}

	public final int getWaterColor() {
		return this.waterColor;
	}

	public final int method_16447() {
		return this.waterFogColor;
	}

	public final Biome.Category getCategory() {
		return this.category;
	}

	public SurfaceBuilder<?> getSurfaceBuilder() {
		return this.surfaceBuilder;
	}

	public class_4014 method_16450() {
		return this.surfaceBuilder.method_17700();
	}

	@Nullable
	public String getParent() {
		return this.parent;
	}

	public static void register() {
		register(0, "ocean", new OceanBiome());
		register(1, "plains", new PlainsBiome());
		register(2, "desert", new DesertBiome());
		register(3, "mountains", new MountainsBiome());
		register(4, "forest", new ForestBiome());
		register(5, "taiga", new TaigaBiome());
		register(6, "swamp", new SwampBiome());
		register(7, "river", new RiverBiome());
		register(8, "nether", new NetherBiome());
		register(9, "the_end", new EndBiome());
		register(10, "frozen_ocean", new FrozenOceanBiome());
		register(11, "frozen_river", new FrozenRiverBiome());
		register(12, "snowy_tundra", new SnowyTundraBiome());
		register(13, "snowy_mountains", new SnowyMountainsBiome());
		register(14, "mushroom_fields", new MushroomFieldsBiome());
		register(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
		register(16, "beach", new BeachBiome());
		register(17, "desert_hills", new DesertHillsBiome());
		register(18, "wooded_hills", new ExtremeHillsBiome());
		register(19, "taiga_hills", new TaigaHillsBiome());
		register(20, "mountain_edge", new MountainEdgeBiome());
		register(21, "jungle", new JungleBiome());
		register(22, "jungle_hills", new JungleHillsBiome());
		register(23, "jungle_edge", new JungleEdgeBiome());
		register(24, "deep_ocean", new DeepOceanBiome());
		register(25, "stone_shore", new StoneShoreBiome());
		register(26, "snowy_beach", new SnowyBeachBiome());
		register(27, "birch_forest", new BirchForestBiome());
		register(28, "birch_forest_hills", new BirchForestHillsBiome());
		register(29, "dark_forest", new DarkForestBiome());
		register(30, "snowy_taiga", new SnowyTaigaBiome());
		register(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
		register(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
		register(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
		register(34, "wooded_mountains", new WoodedMountainsBiome());
		register(35, "savanna", new SavannaBiome());
		register(36, "savanna_plateau", new SavannaPlateauBiome());
		register(37, "badlands", new BadlandsBiome());
		register(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
		register(39, "badlands_plateau", new BadlandsPlateauBiome());
		register(40, "small_end_islands", new SmallEndIslandsBiome());
		register(41, "end_midlands", new EndMidlandsBiome());
		register(42, "end_highlands", new EndHighlandsBiome());
		register(43, "end_barrens", new EndBarrensBiome());
		register(44, "warm_ocean", new WarmOceanBiome());
		register(45, "lukewarm_ocean", new LukewarmOceanBiome());
		register(46, "cold_ocean", new ColdOceanBiome());
		register(47, "deep_warm_ocean", new DeepWarmOceanBiome());
		register(48, "deep_lukewarm_ocean", new DeepLukewarmOceanBiome());
		register(49, "deep_cold_ocean", new DeepColdOceanBiome());
		register(50, "deep_frozen_ocean", new DeepFrozenOceanBiome());
		register(127, "the_void", new VoidBiome());
		register(129, "sunflower_plains", new SunflowerPlainsBiome());
		register(130, "desert_lakes", new DesertLakesBiome());
		register(131, "gravelly_mountains", new GravellyMountainsBiome());
		register(132, "flower_forest", new FlowerForestBiome());
		register(133, "taiga_mountains", new TaigaMountainsBiome());
		register(134, "swamp_hills", new SwampHillsBiome());
		register(140, "ice_spikes", new IceBiome());
		register(149, "modified_jungle", new ModifiedJungleBiome());
		register(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
		register(155, "tall_birch_forest", new TallBirchForestBiome());
		register(156, "tall_birch_hills", new TallBirchHillsBiome());
		register(157, "dark_forest_hills", new DarkForestHillsBiome());
		register(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
		register(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
		register(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
		register(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
		register(163, "shattered_savanna", new ShatteredSavannaBiome());
		register(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
		register(165, "eroded_badlands", new ErodedBadlandsBiome());
		register(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
		register(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
		Collections.addAll(
			ALL_BIOMES,
			new Biome[]{
				Biomes.OCEAN,
				Biomes.PLAINS,
				Biomes.DESERT,
				Biomes.EXTREME_HILLS,
				Biomes.FOREST,
				Biomes.TAIGA,
				Biomes.SWAMP,
				Biomes.RIVER,
				Biomes.FROZEN_RIVER,
				Biomes.ICE_FLATS,
				Biomes.ICE_MOUNTAINS,
				Biomes.MUSHROOM_ISLAND,
				Biomes.MUSHROOM_ISLAND_SHORE,
				Biomes.BEACH,
				Biomes.DESERT_HILLS,
				Biomes.FOREST_HILLS,
				Biomes.TAIGA_HILLS,
				Biomes.JUNGLE,
				Biomes.JUNGLE_HILLS,
				Biomes.JUNGLE_EDGE,
				Biomes.DEEP_OCEAN,
				Biomes.STONE_BEACH,
				Biomes.COLD_BEACH,
				Biomes.BIRCH_FOREST,
				Biomes.BIRCH_FOREST_HILLS,
				Biomes.ROOFED_FOREST,
				Biomes.TAIGA_COLD,
				Biomes.TAIGA_COLD_HILLS,
				Biomes.GIANT_TREE_TAIGA,
				Biomes.GIANT_TREE_TAIGA_HILLS,
				Biomes.EXTREME_HILLS_WITH_TREES,
				Biomes.SAVANNA,
				Biomes.SAVANNA_PLATEAU,
				Biomes.MESA,
				Biomes.WOODED_BADLANDS_PLATEAU,
				Biomes.BADLANDS_PLATEAU
			}
		);
	}

	private static void register(int numId, String stringId, Biome biome) {
		Registry.BIOME.set(numId, new Identifier(stringId), biome);
		if (biome.hasParent()) {
			biomeList.set(biome, Registry.BIOME.getRawId(Registry.BIOME.getByIdentifier(new Identifier(biome.parent))));
		}
	}

	public static class Builder {
		@Nullable
		private SurfaceBuilder<?> surfaceBuilder;
		@Nullable
		private Biome.Precipitation precipitation;
		@Nullable
		private Biome.Category category;
		@Nullable
		private Float depth;
		@Nullable
		private Float scale;
		@Nullable
		private Float temperature;
		@Nullable
		private Float downfall;
		@Nullable
		private Integer waterColor;
		@Nullable
		private Integer waterFogColor;
		@Nullable
		private String parent;

		public Biome.Builder setSurfaceBuilder(SurfaceBuilder<?> surfaceBuilder) {
			this.surfaceBuilder = surfaceBuilder;
			return this;
		}

		public Biome.Builder setPrecipitation(Biome.Precipitation precipitation) {
			this.precipitation = precipitation;
			return this;
		}

		public Biome.Builder setCategory(Biome.Category category) {
			this.category = category;
			return this;
		}

		public Biome.Builder setDepth(float depth) {
			this.depth = depth;
			return this;
		}

		public Biome.Builder setScale(float scale) {
			this.scale = scale;
			return this;
		}

		public Biome.Builder setTemperature(float temperature) {
			this.temperature = temperature;
			return this;
		}

		public Biome.Builder setDownfall(float downfall) {
			this.downfall = downfall;
			return this;
		}

		public Biome.Builder setWaterColor(int waterColor) {
			this.waterColor = waterColor;
			return this;
		}

		public Biome.Builder setWaterFogColor(int waterFogColor) {
			this.waterFogColor = waterFogColor;
			return this;
		}

		public Biome.Builder setParent(@Nullable String parent) {
			this.parent = parent;
			return this;
		}

		public String toString() {
			return "BiomeBuilder{\nsurfaceBuilder="
				+ this.surfaceBuilder
				+ ",\nprecipitation="
				+ this.precipitation
				+ ",\nbiomeCategory="
				+ this.category
				+ ",\ndepth="
				+ this.depth
				+ ",\nscale="
				+ this.scale
				+ ",\ntemperature="
				+ this.temperature
				+ ",\ndownfall="
				+ this.downfall
				+ ",\nwaterColor="
				+ this.waterColor
				+ ",\nwaterFogColor="
				+ this.waterFogColor
				+ ",\nparent='"
				+ this.parent
				+ '\''
				+ "\n"
				+ '}';
		}
	}

	public static enum Category {
		NONE,
		TAIGA,
		EXTREME_HILLS,
		JUNGLE,
		MESA,
		PLAINS,
		SAVANNA,
		ICY,
		THEEND,
		BEACH,
		FOREST,
		OCEAN,
		DESERT,
		RIVER,
		SWAMP,
		MUSHROOM,
		NETHER;
	}

	public static enum Precipitation {
		NONE,
		RAIN,
		SNOW;
	}

	public static class SpawnEntry extends Weighting.Weight {
		public EntityType<? extends MobEntity> field_17657;
		public int minGroupSize;
		public int maxGroupSize;

		public SpawnEntry(EntityType<? extends MobEntity> entityType, int i, int j, int k) {
			super(i);
			this.field_17657 = entityType;
			this.minGroupSize = j;
			this.maxGroupSize = k;
		}

		public String toString() {
			return EntityType.getId(this.field_17657) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
		}
	}

	public static enum Temperature {
		OCEAN,
		COLD,
		MEDIUM,
		WARM;
	}
}
