package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public final List<String> locations;

	public LayeredTexture(String... strings) {
		this.locations = Lists.newArrayList(strings);
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		this.clearGlId();
		BufferedImage bufferedImage = null;

		try {
			for (String string : this.locations) {
				if (string != null) {
					InputStream inputStream = manager.getResource(new Identifier(string)).getInputStream();
					BufferedImage bufferedImage2 = TextureUtil.create(inputStream);
					if (bufferedImage == null) {
						bufferedImage = new BufferedImage(bufferedImage2.getWidth(), bufferedImage2.getHeight(), 2);
					}

					bufferedImage.getGraphics().drawImage(bufferedImage2, 0, 0, null);
				}
			}
		} catch (IOException var7) {
			LOGGER.error("Couldn't load layered image", var7);
			return;
		}

		TextureUtil.method_5858(this.getGlId(), bufferedImage);
	}
}
