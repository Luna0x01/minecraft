package net.minecraft.client.network;

import java.util.Hashtable;
import java.util.Optional;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FunctionalInterface
public interface RedirectResolver {
	Logger LOGGER = LogManager.getLogger();
	RedirectResolver INVALID = address -> Optional.empty();

	Optional<ServerAddress> lookupRedirect(ServerAddress address);

	static RedirectResolver createSrv() {
		DirContext dirContext;
		try {
			String string = "com.sun.jndi.dns.DnsContextFactory";
			Class.forName("com.sun.jndi.dns.DnsContextFactory");
			Hashtable<String, String> hashtable = new Hashtable();
			hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			hashtable.put("java.naming.provider.url", "dns:");
			hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
			dirContext = new InitialDirContext(hashtable);
		} catch (Throwable var3) {
			LOGGER.error("Failed to initialize SRV redirect resolved, some servers might not work", var3);
			return INVALID;
		}

		return address -> {
			if (address.getPort() == 25565) {
				try {
					Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + address.getAddress(), new String[]{"SRV"});
					Attribute attribute = attributes.get("srv");
					if (attribute != null) {
						String[] strings = attribute.get().toString().split(" ", 4);
						return Optional.of(new ServerAddress(strings[3], ServerAddress.portOrDefault(strings[2])));
					}
				} catch (Throwable var5) {
				}
			}

			return Optional.empty();
		};
	}
}
