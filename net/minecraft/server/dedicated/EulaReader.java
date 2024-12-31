package net.minecraft.server.dedicated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import net.minecraft.util.SharedConstants;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EulaReader {
	private static final Logger LOGGER = LogManager.getLogger();
	private final File eulaFile;
	private final boolean eulaAgreedTo;

	public EulaReader(File file) {
		this.eulaFile = file;
		this.eulaAgreedTo = SharedConstants.isDevelopment || this.checkEulaAgreement(file);
	}

	private boolean checkEulaAgreement(File eulaFile) {
		FileInputStream fileInputStream = null;
		boolean bl = false;

		try {
			Properties properties = new Properties();
			fileInputStream = new FileInputStream(eulaFile);
			properties.load(fileInputStream);
			bl = Boolean.parseBoolean(properties.getProperty("eula", "false"));
		} catch (Exception var8) {
			LOGGER.warn("Failed to load {}", eulaFile);
			this.createEulaFile();
		} finally {
			IOUtils.closeQuietly(fileInputStream);
		}

		return bl;
	}

	public boolean isEulaAgreedTo() {
		return this.eulaAgreedTo;
	}

	public void createEulaFile() {
		if (!SharedConstants.isDevelopment) {
			FileOutputStream fileOutputStream = null;

			try {
				Properties properties = new Properties();
				fileOutputStream = new FileOutputStream(this.eulaFile);
				properties.setProperty("eula", "false");
				properties.store(
					fileOutputStream,
					"By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)."
				);
			} catch (Exception var6) {
				LOGGER.warn("Failed to save {}", this.eulaFile, var6);
			} finally {
				IOUtils.closeQuietly(fileOutputStream);
			}
		}
	}
}
