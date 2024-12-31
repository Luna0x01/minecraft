package net.minecraft.server.dedicated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbstractPropertiesHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Properties properties = new Properties();
	private final File propertyFile;

	public AbstractPropertiesHandler(File file) {
		this.propertyFile = file;
		if (file.exists()) {
			FileInputStream fileInputStream = null;

			try {
				fileInputStream = new FileInputStream(file);
				this.properties.load(fileInputStream);
			} catch (Exception var12) {
				LOGGER.warn("Failed to load {}", file, var12);
				this.generate();
			} finally {
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException var11) {
					}
				}
			}
		} else {
			LOGGER.warn("{} does not exist", file);
			this.generate();
		}
	}

	public void generate() {
		LOGGER.info("Generating new properties file");
		this.save();
	}

	public void save() {
		FileOutputStream fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(this.propertyFile);
			this.properties.store(fileOutputStream, "Minecraft server properties");
		} catch (Exception var11) {
			LOGGER.warn("Failed to save {}", this.propertyFile, var11);
			this.generate();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException var10) {
				}
			}
		}
	}

	public File getPropertiesFile() {
		return this.propertyFile;
	}

	public String getOrDefault(String key, String value) {
		if (!this.properties.containsKey(key)) {
			this.properties.setProperty(key, value);
			this.save();
			this.save();
		}

		return this.properties.getProperty(key, value);
	}

	public int getIntOrDefault(String key, int i) {
		try {
			return Integer.parseInt(this.getOrDefault(key, "" + i));
		} catch (Exception var4) {
			this.properties.setProperty(key, "" + i);
			this.save();
			return i;
		}
	}

	public long getLongOrDefault(String key, long value) {
		try {
			return Long.parseLong(this.getOrDefault(key, "" + value));
		} catch (Exception var5) {
			this.properties.setProperty(key, "" + value);
			this.save();
			return value;
		}
	}

	public boolean getBooleanOrDefault(String key, boolean value) {
		try {
			return Boolean.parseBoolean(this.getOrDefault(key, "" + value));
		} catch (Exception var4) {
			this.properties.setProperty(key, "" + value);
			this.save();
			return value;
		}
	}

	public void set(String key, Object value) {
		this.properties.setProperty(key, "" + value);
	}

	public boolean method_12760(String string) {
		return this.properties.containsKey(string);
	}

	public void method_12761(String string) {
		this.properties.remove(string);
	}
}
