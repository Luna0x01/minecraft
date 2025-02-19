package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

public interface DataProvider {
	HashFunction SHA1 = Hashing.sha1();

	void run(DataCache cache) throws IOException;

	String getName();

	static void writeToPath(Gson gson, DataCache cache, JsonElement output, Path path) throws IOException {
		String string = gson.toJson(output);
		String string2 = SHA1.hashUnencodedChars(string).toString();
		if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
			Files.createDirectories(path.getParent());
			BufferedWriter bufferedWriter = Files.newBufferedWriter(path);

			try {
				bufferedWriter.write(string);
			} catch (Throwable var10) {
				if (bufferedWriter != null) {
					try {
						bufferedWriter.close();
					} catch (Throwable var9) {
						var10.addSuppressed(var9);
					}
				}

				throw var10;
			}

			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
		}

		cache.updateSha1(path, string2);
	}
}
