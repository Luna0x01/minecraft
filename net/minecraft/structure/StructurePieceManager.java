package net.minecraft.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.GeneratorConfig;
import net.minecraft.world.gen.MineshaftGeneratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePieceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, Class<? extends GeneratorConfig>> configIdMap = Maps.newHashMap();
	private static final Map<Class<? extends GeneratorConfig>, String> configClassMap = Maps.newHashMap();
	private static final Map<String, Class<? extends StructurePiece>> pieceIdMap = Maps.newHashMap();
	private static final Map<Class<? extends StructurePiece>, String> pieceClassMap = Maps.newHashMap();

	private static void register(Class<? extends GeneratorConfig> structureConfigClass, String name) {
		configIdMap.put(name, structureConfigClass);
		configClassMap.put(structureConfigClass, name);
	}

	static void registerPiece(Class<? extends StructurePiece> structurePieceClass, String name) {
		pieceIdMap.put(name, structurePieceClass);
		pieceClassMap.put(structurePieceClass, name);
	}

	public static String getId(GeneratorConfig config) {
		return (String)configClassMap.get(config.getClass());
	}

	public static String getId(StructurePiece piece) {
		return (String)pieceClassMap.get(piece.getClass());
	}

	@Nullable
	public static GeneratorConfig getGeneratorConfigFromNbt(NbtCompound nbt, World world) {
		GeneratorConfig generatorConfig = null;

		try {
			Class<? extends GeneratorConfig> class_ = (Class<? extends GeneratorConfig>)configIdMap.get(nbt.getString("id"));
			if (class_ != null) {
				generatorConfig = (GeneratorConfig)class_.newInstance();
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed Start with id {}", new Object[]{nbt.getString("id")});
			var4.printStackTrace();
		}

		if (generatorConfig != null) {
			generatorConfig.fromNbt(world, nbt);
		} else {
			LOGGER.warn("Skipping Structure with id {}", new Object[]{nbt.getString("id")});
		}

		return generatorConfig;
	}

	public static StructurePiece getStructurePieceFromNbt(NbtCompound nbt, World world) {
		StructurePiece structurePiece = null;

		try {
			Class<? extends StructurePiece> class_ = (Class<? extends StructurePiece>)pieceIdMap.get(nbt.getString("id"));
			if (class_ != null) {
				structurePiece = (StructurePiece)class_.newInstance();
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed Piece with id {}", new Object[]{nbt.getString("id")});
			var4.printStackTrace();
		}

		if (structurePiece != null) {
			structurePiece.fromNbt(world, nbt);
		} else {
			LOGGER.warn("Skipping Piece with id {}", new Object[]{nbt.getString("id")});
		}

		return structurePiece;
	}

	static {
		register(MineshaftGeneratorConfig.class, "Mineshaft");
		register(VillageStructure.VillageGeneratorConfig.class, "Village");
		register(NetherFortressStructure.FortressGeneratorConfig.class, "Fortress");
		register(StrongholdStructure.StrongholdGeneratorConfig.class, "Stronghold");
		register(TempleStructure.TempleGeneratorConfig.class, "Temple");
		register(OceanMonumentStructure.OceanMonumentGeneratorConfig.class, "Monument");
		register(EndCityStructure.EndCityGeneratorConfig.class, "EndCity");
		MineshaftPieces.registerPieces();
		VillagePieces.registerPieces();
		NetherFortressPieces.registerPieces();
		StrongholdPieces.registerPieces();
		TemplePieces.registerPieces();
		OceanMonumentPieces.registerPieces();
		class_2759.registerPieces();
	}
}
