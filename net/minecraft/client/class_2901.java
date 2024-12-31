package net.minecraft.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resource.Resource;
import org.apache.commons.io.IOUtils;

public class class_2901 {
	public final int field_13651;
	public final int field_13652;

	public class_2901(InputStream inputStream) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		if (dataInputStream.readLong() != -8552249625308161526L) {
			throw new IOException("Bad PNG Signature");
		} else if (dataInputStream.readInt() != 13) {
			throw new IOException("Bad length for IHDR chunk!");
		} else if (dataInputStream.readInt() != 1229472850) {
			throw new IOException("Bad type for IHDR chunk!");
		} else {
			this.field_13651 = dataInputStream.readInt();
			this.field_13652 = dataInputStream.readInt();
			IOUtils.closeQuietly(dataInputStream);
		}
	}

	public static class_2901 method_12485(Resource resource) throws IOException {
		class_2901 var1;
		try {
			var1 = new class_2901(resource.getInputStream());
		} finally {
			IOUtils.closeQuietly(resource);
		}

		return var1;
	}
}
