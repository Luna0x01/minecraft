package net.minecraft.server.dedicated;

public interface DedicatedServer {
	int getIntOrDefault(String name, int value);

	String getOrDefault(String name, String value);

	void setProperty(String property, Object value);

	void saveAbstractPropertiesHandler();

	String getPropertiesFilePath();

	String getHostname();

	int getPort();

	String getMotd();

	String getVersion();

	int getCurrentPlayerCount();

	int getMaxPlayerCount();

	String[] getPlayerNames();

	String getLevelName();

	String getPlugins();

	String executeRconCommand(String name);

	boolean isDebuggingEnabled();

	void logInfo(String string);

	void logWarn(String string);

	void logError(String string);

	void log(String string);
}
