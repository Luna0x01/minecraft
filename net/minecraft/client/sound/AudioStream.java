package net.minecraft.client.sound;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public interface AudioStream extends Closeable {
	AudioFormat getFormat();

	ByteBuffer getBuffer(int size) throws IOException;
}
