package net.minecraft.client.sound;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;

public interface AudioStream extends Closeable {
	AudioFormat getFormat();

	ByteBuffer getBuffer() throws IOException;

	@Nullable
	ByteBuffer method_19720(int i) throws IOException;
}
