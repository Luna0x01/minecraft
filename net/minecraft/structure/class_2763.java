package net.minecraft.structure;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

public class class_2763 {
	private final Map<String, Structure> field_13022 = Maps.newHashMap();
	private final String field_13023;

	public class_2763(String string) {
		this.field_13023 = string;
	}

	public Structure method_11861(@Nullable MinecraftServer server, Identifier identifier) {
		Structure structure = this.method_13384(server, identifier);
		if (structure == null) {
			structure = new Structure();
			this.field_13022.put(identifier.getPath(), structure);
		}

		return structure;
	}

	@Nullable
	public Structure method_13384(@Nullable MinecraftServer server, Identifier identifier) {
		String string = identifier.getPath();
		if (this.field_13022.containsKey(string)) {
			return (Structure)this.field_13022.get(string);
		} else {
			if (server != null) {
				this.method_11862(server, identifier);
			} else {
				this.method_11860(identifier);
			}

			return this.field_13022.containsKey(string) ? (Structure)this.field_13022.get(string) : null;
		}
	}

	public boolean method_11862(MinecraftServer minecraftServer, Identifier identifier) {
		String string = identifier.getPath();
		File file = new File(this.field_13023, string + ".nbt");
		if (!file.exists()) {
			return this.method_11860(identifier);
		} else {
			InputStream inputStream = null;

			boolean var7;
			try {
				inputStream = new FileInputStream(file);
				this.method_11859(string, inputStream);
				return true;
			} catch (Throwable var11) {
				var7 = false;
			} finally {
				IOUtils.closeQuietly(inputStream);
			}

			return var7;
		}
	}

	private boolean method_11860(Identifier identifier) {
		String string = identifier.getNamespace();
		String string2 = identifier.getPath();
		InputStream inputStream = null;

		boolean var6;
		try {
			inputStream = MinecraftServer.class.getResourceAsStream("/assets/" + string + "/structures/" + string2 + ".nbt");
			this.method_11859(string2, inputStream);
			return true;
		} catch (Throwable var10) {
			var6 = false;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return var6;
	}

	private void method_11859(String string, InputStream inputStream) throws IOException {
		NbtCompound nbtCompound = NbtIo.readCompressed(inputStream);
		Structure structure = new Structure();
		structure.method_11897(nbtCompound);
		this.field_13022.put(string, structure);
	}

	public boolean method_11863(@Nullable MinecraftServer minecraftServer, Identifier identifier) {
		String string = identifier.getPath();
		if (minecraftServer != null && this.field_13022.containsKey(string)) {
			File file = new File(this.field_13023);
			if (!file.exists()) {
				if (!file.mkdirs()) {
					return false;
				}
			} else if (!file.isDirectory()) {
				return false;
			}

			File file2 = new File(file, string + ".nbt");
			Structure structure = (Structure)this.field_13022.get(string);
			OutputStream outputStream = null;

			boolean var9;
			try {
				NbtCompound nbtCompound = structure.method_11891(new NbtCompound());
				outputStream = new FileOutputStream(file2);
				NbtIo.writeCompressed(nbtCompound, outputStream);
				return true;
			} catch (Throwable var13) {
				var9 = false;
			} finally {
				IOUtils.closeQuietly(outputStream);
			}

			return var9;
		} else {
			return false;
		}
	}

	public void method_13383(Identifier identifier) {
		this.field_13022.remove(identifier.getPath());
	}
}
