package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class NbtIo {
	public static NbtCompound readCompressed(InputStream stream) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(stream)));

		NbtCompound var2;
		try {
			var2 = read(dataInputStream, PositionTracker.DEFAULT);
		} finally {
			dataInputStream.close();
		}

		return var2;
	}

	public static void writeCompressed(NbtCompound nbt, OutputStream stream) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));

		try {
			write(nbt, dataOutputStream);
		} finally {
			dataOutputStream.close();
		}
	}

	public static void safeWrite(NbtCompound nbt, File file) throws IOException {
		File file2 = new File(file.getAbsolutePath() + "_tmp");
		if (file2.exists()) {
			file2.delete();
		}

		write(nbt, file2);
		if (file.exists()) {
			file.delete();
		}

		if (file.exists()) {
			throw new IOException("Failed to delete " + file);
		} else {
			file2.renameTo(file);
		}
	}

	public static void write(NbtCompound nbt, File file) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

		try {
			write(nbt, dataOutputStream);
		} finally {
			dataOutputStream.close();
		}
	}

	@Nullable
	public static NbtCompound read(File file) throws IOException {
		if (!file.exists()) {
			return null;
		} else {
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

			NbtCompound var2;
			try {
				var2 = read(dataInputStream, PositionTracker.DEFAULT);
			} finally {
				dataInputStream.close();
			}

			return var2;
		}
	}

	public static NbtCompound read(DataInputStream stream) throws IOException {
		return read(stream, PositionTracker.DEFAULT);
	}

	public static NbtCompound read(DataInput input, PositionTracker tracker) throws IOException {
		NbtElement nbtElement = read(input, 0, tracker);
		if (nbtElement instanceof NbtCompound) {
			return (NbtCompound)nbtElement;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void write(NbtCompound nbt, DataOutput output) throws IOException {
		write((NbtElement)nbt, output);
	}

	private static void write(NbtElement nbt, DataOutput output) throws IOException {
		output.writeByte(nbt.getType());
		if (nbt.getType() != 0) {
			output.writeUTF("");
			nbt.write(output);
		}
	}

	private static NbtElement read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		byte b = input.readByte();
		if (b == 0) {
			return new NbtEnd();
		} else {
			input.readUTF();
			NbtElement nbtElement = NbtElement.createFromType(b);

			try {
				nbtElement.read(input, depth, tracker);
				return nbtElement;
			} catch (IOException var8) {
				CrashReport crashReport = CrashReport.create(var8, "Loading NBT data");
				CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
				crashReportSection.add("Tag type", b);
				throw new CrashException(crashReport);
			}
		}
	}
}
