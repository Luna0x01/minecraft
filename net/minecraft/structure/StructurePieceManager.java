package net.minecraft.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3817;
import net.minecraft.class_3836;
import net.minecraft.class_3842;
import net.minecraft.class_3856;
import net.minecraft.class_3860;
import net.minecraft.class_3867;
import net.minecraft.class_3869;
import net.minecraft.class_3873;
import net.minecraft.class_3891;
import net.minecraft.class_3901;
import net.minecraft.class_3902;
import net.minecraft.class_3906;
import net.minecraft.class_3912;
import net.minecraft.class_3916;
import net.minecraft.class_3974;
import net.minecraft.class_3976;
import net.minecraft.class_3978;
import net.minecraft.class_3983;
import net.minecraft.class_3986;
import net.minecraft.class_3988;
import net.minecraft.class_3992;
import net.minecraft.class_3993;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.IWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePieceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, Class<? extends class_3992>> configIdMap = Maps.newHashMap();
	private static final Map<Class<? extends class_3992>, String> configClassMap = Maps.newHashMap();
	private static final Map<String, Class<? extends StructurePiece>> pieceIdMap = Maps.newHashMap();
	private static final Map<Class<? extends StructurePiece>, String> pieceClassMap = Maps.newHashMap();

	private static void register(Class<? extends class_3992> structureConfigClass, String name) {
		configIdMap.put(name, structureConfigClass);
		configClassMap.put(structureConfigClass, name);
	}

	public static void registerPiece(Class<? extends StructurePiece> structurePieceClass, String name) {
		pieceIdMap.put(name, structurePieceClass);
		pieceClassMap.put(structurePieceClass, name);
	}

	public static String method_5519(class_3992 arg) {
		return (String)configClassMap.get(arg.getClass());
	}

	public static String getId(StructurePiece piece) {
		return (String)pieceClassMap.get(piece.getClass());
	}

	@Nullable
	public static class_3992 method_17641(NbtCompound nbtCompound, IWorld iWorld) {
		class_3992 lv = null;
		String string = nbtCompound.getString("id");
		if ("INVALID".equals(string)) {
			return class_3902.field_19260;
		} else {
			try {
				Class<? extends class_3992> class_ = (Class<? extends class_3992>)configIdMap.get(string);
				if (class_ != null) {
					lv = (class_3992)class_.newInstance();
				}
			} catch (Exception var5) {
				LOGGER.warn("Failed Start with id {}", string);
				var5.printStackTrace();
			}

			if (lv != null) {
				lv.method_17662(iWorld, nbtCompound);
			} else {
				LOGGER.warn("Skipping Structure with id {}", string);
			}

			return lv;
		}
	}

	public static StructurePiece method_5522(NbtCompound nbtCompound, IWorld iWorld) {
		StructurePiece structurePiece = null;

		try {
			Class<? extends StructurePiece> class_ = (Class<? extends StructurePiece>)pieceIdMap.get(nbtCompound.getString("id"));
			if (class_ != null) {
				structurePiece = (StructurePiece)class_.newInstance();
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed Piece with id {}", nbtCompound.getString("id"));
			var4.printStackTrace();
		}

		if (structurePiece != null) {
			structurePiece.method_5527(iWorld, nbtCompound);
		} else {
			LOGGER.warn("Skipping Piece with id {}", nbtCompound.getString("id"));
		}

		return structurePiece;
	}

	static {
		register(class_3867.class_1258.class, "Mineshaft");
		register(class_3912.class_38.class, "Village");
		register(class_3869.class_1260.class, "Fortress");
		register(class_3901.class_10.class, "Stronghold");
		register(class_3860.class_3861.class, "Jungle_Pyramid");
		register(class_3983.class_3984.class, "Ocean_Ruin");
		register(class_3836.class_3837.class, "Desert_Pyramid");
		register(class_3856.class_3857.class, "Igloo");
		register(class_3906.class_3907.class, "Swamp_Hut");
		register(class_3873.class_2260.class, "Monument");
		register(class_3842.class_2758.class, "EndCity");
		register(class_3916.class_3071.class, "Mansion");
		register(class_3817.class_3818.class, "Buried_Treasure");
		register(class_3891.class_3892.class, "Shipwreck");
		MineshaftPieces.registerPieces();
		VillagePieces.registerPieces();
		NetherFortressPieces.registerPieces();
		StrongholdPieces.registerPieces();
		class_3978.method_17607();
		class_3986.method_17628();
		class_3976.method_17601();
		class_3993.method_17671();
		class_3974.method_17600();
		OceanMonumentPieces.registerPieces();
		class_2759.registerPieces();
		class_3072.registerPieces();
		TemplePieces.registerPieces();
		class_3988.method_17637();
	}
}
