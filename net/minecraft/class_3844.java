package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.AbstractGiantTreeFeature;
import net.minecraft.world.gen.feature.AcaciaTreeFeature;
import net.minecraft.world.gen.feature.BigTreeFeature;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.BlockFeature;
import net.minecraft.world.gen.feature.CactusFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.DeadbushFeature;
import net.minecraft.world.gen.feature.DesertWellFeature;
import net.minecraft.world.gen.feature.DoublePlantFeature;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.EndGatewayFeature;
import net.minecraft.world.gen.feature.FillerBlockFeature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.GiantJungleTreeFeature;
import net.minecraft.world.gen.feature.GiantSpruceTreeFeature;
import net.minecraft.world.gen.feature.IceDiskFeature;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.JungleBushFeature;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.LilyPadFeature;
import net.minecraft.world.gen.feature.MelonFeature;
import net.minecraft.world.gen.feature.MushroomFeature;
import net.minecraft.world.gen.feature.NetherFireFeature;
import net.minecraft.world.gen.feature.NetherSpringFeature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.PineTreeFeature;
import net.minecraft.world.gen.feature.PumpkinFeature;
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import net.minecraft.world.gen.feature.SugarcaneFeature;
import net.minecraft.world.gen.feature.TallGrassFeature;
import net.minecraft.world.gen.feature.VineFeature;
import net.minecraft.world.gen.feature.class_2754;

public abstract class class_3844<C extends class_3845> {
	private static final List<Biome.SpawnEntry> field_19154 = Lists.newArrayList();
	public static final class_3902<class_3911> field_19181 = new class_3912();
	public static final class_3902<class_3866> field_19182 = new class_3867();
	public static final class_3902<class_3914> field_19183 = new class_3916();
	public static final class_3902<class_3859> field_19184 = new class_3860();
	public static final class_3902<class_3835> field_19185 = new class_3836();
	public static final class_3902<class_3855> field_19186 = new class_3856();
	public static final class_3902<class_3890> field_19187 = new class_3891();
	public static final class_3902<class_3905> field_19188 = new class_3906();
	public static final class_3902<class_3900> field_19189 = new class_3901();
	public static final class_3902<class_3872> field_19190 = new class_3873();
	public static final class_3902<class_3874> field_19191 = new class_3983();
	public static final class_3902<class_3868> field_19192 = new class_3869();
	public static final class_3902<class_3841> field_19193 = new class_3842();
	public static final class_3902<class_3816> field_19194 = new class_3817();
	public static final FoliageFeature<class_3871> field_19195 = new BigTreeFeature(false);
	public static final FoliageFeature<class_3871> field_19196 = new BirchTreeFeature(false, false);
	public static final FoliageFeature<class_3871> field_19197 = new BirchTreeFeature(false, true);
	public static final FoliageFeature<class_3871> field_19198 = new JungleBushFeature(Blocks.JUNGLE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState());
	public static final FoliageFeature<class_3871> field_19199 = new class_3862(
		false, 4, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState(), true
	);
	public static final FoliageFeature<class_3871> field_19200 = new PineTreeFeature();
	public static final FoliageFeature<class_3871> field_19201 = new DarkOakTreeFeature(false);
	public static final FoliageFeature<class_3871> field_19202 = new AcaciaTreeFeature(false);
	public static final FoliageFeature<class_3871> field_19121 = new SpruceTreeFeature(false);
	public static final FoliageFeature<class_3871> field_19122 = new class_3904();
	public static final FoliageFeature<class_3871> field_19123 = new class_3910(false);
	public static final AbstractGiantTreeFeature<class_3871> field_19124 = new GiantJungleTreeFeature(
		false, 10, 20, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState()
	);
	public static final AbstractGiantTreeFeature<class_3871> field_19125 = new GiantSpruceTreeFeature(false, false);
	public static final AbstractGiantTreeFeature<class_3871> field_19126 = new GiantSpruceTreeFeature(false, true);
	public static final class_3848 field_19127 = new class_3834();
	public static final class_3848 field_19128 = new class_3849();
	public static final class_3848 field_19129 = new class_3876();
	public static final class_3848 field_19130 = new class_3903();
	public static final class_3844<class_3871> field_19131 = new class_3858();
	public static final class_3844<class_3871> field_19132 = new class_3908();
	public static final class_3844<class_3909> field_19133 = new TallGrassFeature();
	public static final class_3844<class_3871> field_19134 = new class_3913();
	public static final class_3844<class_3871> field_19135 = new CactusFeature();
	public static final class_3844<class_3871> field_19136 = new DeadbushFeature();
	public static final class_3844<class_3871> field_19137 = new DesertWellFeature();
	public static final class_3844<class_3871> field_19138 = new class_3013();
	public static final class_3844<class_3871> field_19139 = new NetherFireFeature();
	public static final class_3844<class_3871> field_19140 = new class_3852();
	public static final class_3844<class_3871> field_19141 = new class_3851();
	public static final class_3844<class_3871> field_19142 = new IceSpikeFeature();
	public static final class_3844<class_3871> field_19143 = new class_3865();
	public static final class_3844<class_3871> field_19144 = new MelonFeature();
	public static final class_3844<class_3871> field_19145 = new PumpkinFeature();
	public static final class_3844<class_3871> field_19146 = new SugarcaneFeature();
	public static final class_3844<class_3871> field_19155 = new class_3898();
	public static final class_3844<class_3871> field_19156 = new VineFeature();
	public static final class_3844<class_3871> field_19157 = new LilyPadFeature();
	public static final class_3844<class_3871> field_19158 = new DungeonFeature();
	public static final class_3844<class_3871> field_19159 = new class_3814();
	public static final class_3844<class_3853> field_19160 = new class_3854();
	public static final class_3844<class_3813> field_19161 = new BlockFeature();
	public static final class_3844<class_3819> field_19162 = new MushroomFeature();
	public static final class_3844<class_3838> field_19163 = new class_3839();
	public static final class_3844<class_3840> field_19164 = new DoublePlantFeature();
	public static final class_3844<class_3850> field_19165 = new NetherSpringFeature();
	public static final class_3844<class_3846> field_19166 = new IceDiskFeature();
	public static final class_3844<class_3864> field_19167 = new LakesFeature();
	public static final class_3844<class_3875> field_19168 = new OreFeature();
	public static final class_3844<class_3882> field_19169 = new class_3881();
	public static final class_3844<class_3880> field_19170 = new class_3884();
	public static final class_3844<class_3896> field_19171 = new class_3897();
	public static final class_3844<class_3878> field_19172 = new class_3879();
	public static final class_3844<class_3885> field_19173 = new class_3886();
	public static final class_3844<class_3899> field_19174 = new SpringFeature();
	public static final class_3844<class_3871> field_19175 = new FillerBlockFeature();
	public static final class_3844<class_3871> field_19176 = new class_2754();
	public static final class_3844<class_3871> field_19177 = new class_3820();
	public static final class_3844<class_3843> field_19178 = new EndGatewayFeature();
	public static final class_3844<class_3889> field_19179 = new class_3888();
	public static final class_3844<class_3871> field_19180 = new class_3863();
	public static final class_3844<class_3871> field_19147 = new class_3825();
	public static final class_3844<class_3871> field_19148 = new class_3824();
	public static final class_3844<class_3871> field_19149 = new class_3822();
	public static final class_3844<class_3828> field_19150 = new class_3887();
	public static final class_3844<class_3894> field_19151 = new class_3895();
	public static final Map<String, class_3902<?>> field_19152 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("Village".toLowerCase(Locale.ROOT), field_19181);
		hashMap.put("Mineshaft".toLowerCase(Locale.ROOT), field_19182);
		hashMap.put("Mansion".toLowerCase(Locale.ROOT), field_19183);
		hashMap.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), field_19184);
		hashMap.put("Desert_Pyramid".toLowerCase(Locale.ROOT), field_19185);
		hashMap.put("Igloo".toLowerCase(Locale.ROOT), field_19186);
		hashMap.put("Shipwreck".toLowerCase(Locale.ROOT), field_19187);
		hashMap.put("Swamp_Hut".toLowerCase(Locale.ROOT), field_19188);
		hashMap.put("Stronghold".toLowerCase(Locale.ROOT), field_19189);
		hashMap.put("Monument".toLowerCase(Locale.ROOT), field_19190);
		hashMap.put("Ocean_Ruin".toLowerCase(Locale.ROOT), field_19191);
		hashMap.put("Fortress".toLowerCase(Locale.ROOT), field_19192);
		hashMap.put("EndCity".toLowerCase(Locale.ROOT), field_19193);
		hashMap.put("Buried_Treasure".toLowerCase(Locale.ROOT), field_19194);
	});
	protected final boolean field_19153;

	public class_3844() {
		this(false);
	}

	public class_3844(boolean bl) {
		this.field_19153 = bl;
	}

	public abstract boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, C arg);

	protected void method_17344(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if (this.field_19153) {
			iWorld.setBlockState(blockPos, blockState, 3);
		} else {
			iWorld.setBlockState(blockPos, blockState, 2);
		}
	}

	public List<Biome.SpawnEntry> method_17347() {
		return field_19154;
	}

	public static boolean method_17345(IWorld iWorld, String string, BlockPos blockPos) {
		return ((class_3902)field_19152.get(string.toLowerCase(Locale.ROOT))).method_17435(iWorld, blockPos);
	}
}
