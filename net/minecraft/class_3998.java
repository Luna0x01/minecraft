package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.Structure;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3998 implements ResourceReloadListener {
	private static final Logger field_19428 = LogManager.getLogger();
	private final Map<Identifier, Structure> field_19429 = Maps.newHashMap();
	private final DataFixer field_19430;
	private final MinecraftServer field_19431;
	private final Path field_19432;

	public class_3998(MinecraftServer minecraftServer, File file, DataFixer dataFixer) {
		this.field_19431 = minecraftServer;
		this.field_19430 = dataFixer;
		this.field_19432 = file.toPath().resolve("generated").normalize();
		minecraftServer.method_20326().registerListener(this);
	}

	public Structure method_17682(Identifier identifier) {
		Structure structure = this.method_17684(identifier);
		if (structure == null) {
			structure = new Structure();
			this.field_19429.put(identifier, structure);
		}

		return structure;
	}

	@Nullable
	public Structure method_17684(Identifier identifier) {
		return (Structure)this.field_19429.computeIfAbsent(identifier, identifierx -> {
			Structure structure = this.method_17689(identifierx);
			return structure != null ? structure : this.method_17688(identifierx);
		});
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.field_19429.clear();
	}

	@Nullable
	private Structure method_17688(Identifier identifier) {
		Identifier identifier2 = new Identifier(identifier.getNamespace(), "structures/" + identifier.getPath() + ".nbt");

		try {
			Resource resource = this.field_19431.method_20326().getResource(identifier2);
			Throwable var4 = null;

			Structure var5;
			try {
				var5 = this.method_17681(resource.getInputStream());
			} catch (Throwable var16) {
				var4 = var16;
				throw var16;
			} finally {
				if (resource != null) {
					if (var4 != null) {
						try {
							resource.close();
						} catch (Throwable var15) {
							var4.addSuppressed(var15);
						}
					} else {
						resource.close();
					}
				}
			}

			return var5;
		} catch (FileNotFoundException var18) {
			return null;
		} catch (Throwable var19) {
			field_19428.error("Couldn't load structure {}: {}", identifier, var19.toString());
			return null;
		}
	}

	@Nullable
	private Structure method_17689(Identifier identifier) {
		if (!this.field_19432.toFile().isDirectory()) {
			return null;
		} else {
			Path path = this.method_17685(identifier, ".nbt");

			try {
				InputStream inputStream = new FileInputStream(path.toFile());
				Throwable var4 = null;

				Structure var5;
				try {
					var5 = this.method_17681(inputStream);
				} catch (Throwable var16) {
					var4 = var16;
					throw var16;
				} finally {
					if (inputStream != null) {
						if (var4 != null) {
							try {
								inputStream.close();
							} catch (Throwable var15) {
								var4.addSuppressed(var15);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var5;
			} catch (FileNotFoundException var18) {
				return null;
			} catch (IOException var19) {
				field_19428.error("Couldn't load structure from {}", path, var19);
				return null;
			}
		}
	}

	private Structure method_17681(InputStream inputStream) throws IOException {
		NbtCompound nbtCompound = NbtIo.readCompressed(inputStream);
		if (!nbtCompound.contains("DataVersion", 99)) {
			nbtCompound.putInt("DataVersion", 500);
		}

		Structure structure = new Structure();
		structure.method_11897(NbtHelper.method_20141(this.field_19430, DataFixTypes.STRUCTURE, nbtCompound, nbtCompound.getInt("DataVersion")));
		return structure;
	}

	public boolean method_17686(Identifier identifier) {
		Structure structure = (Structure)this.field_19429.get(identifier);
		if (structure == null) {
			return false;
		} else {
			Path path = this.method_17685(identifier, ".nbt");
			Path path2 = path.getParent();
			if (path2 == null) {
				return false;
			} else {
				try {
					Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
				} catch (IOException var19) {
					field_19428.error("Failed to create parent directory: {}", path2);
					return false;
				}

				NbtCompound nbtCompound = structure.method_11891(new NbtCompound());

				try {
					OutputStream outputStream = new FileOutputStream(path.toFile());
					Throwable var7 = null;

					try {
						NbtIo.writeCompressed(nbtCompound, outputStream);
					} catch (Throwable var18) {
						var7 = var18;
						throw var18;
					} finally {
						if (outputStream != null) {
							if (var7 != null) {
								try {
									outputStream.close();
								} catch (Throwable var17) {
									var7.addSuppressed(var17);
								}
							} else {
								outputStream.close();
							}
						}
					}

					return true;
				} catch (Throwable var21) {
					return false;
				}
			}
		}
	}

	private Path method_17683(Identifier identifier, String string) {
		try {
			Path path = this.field_19432.resolve(identifier.getNamespace());
			Path path2 = path.resolve("structures");
			return Util.method_20225(path2, identifier.getPath(), string);
		} catch (InvalidPathException var5) {
			throw new class_4374("Invalid resource path: " + identifier, var5);
		}
	}

	private Path method_17685(Identifier identifier, String string) {
		if (identifier.getPath().contains("//")) {
			throw new class_4374("Invalid resource path: " + identifier);
		} else {
			Path path = this.method_17683(identifier, string);
			if (path.startsWith(this.field_19432) && Util.method_20224(path) && Util.method_20229(path)) {
				return path;
			} else {
				throw new class_4374("Invalid resource path: " + path);
			}
		}
	}

	public void method_17687(Identifier identifier) {
		this.field_19429.remove(identifier);
	}
}
