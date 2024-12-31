package net.minecraft.client.texture;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColorMaskTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Identifier identifier;
	private final List<String> names;
	private final List<DyeColor> colors;

	public ColorMaskTexture(Identifier identifier, List<String> list, List<DyeColor> list2) {
		this.identifier = identifier;
		this.names = list;
		this.colors = list2;
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		this.clearGlId();

		BufferedImage bufferedImage2;
		try {
			BufferedImage bufferedImage = TextureUtil.create(manager.getResource(this.identifier).getInputStream());
			int i = bufferedImage.getType();
			if (i == 0) {
				i = 6;
			}

			bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), i);
			Graphics graphics = bufferedImage2.getGraphics();
			graphics.drawImage(bufferedImage, 0, 0, null);

			for (int j = 0; j < 17 && j < this.names.size() && j < this.colors.size(); j++) {
				String string = (String)this.names.get(j);
				MaterialColor materialColor = ((DyeColor)this.colors.get(j)).getMaterialColor();
				if (string != null) {
					InputStream inputStream = manager.getResource(new Identifier(string)).getInputStream();
					BufferedImage bufferedImage3 = TextureUtil.create(inputStream);
					if (bufferedImage3.getWidth() == bufferedImage2.getWidth() && bufferedImage3.getHeight() == bufferedImage2.getHeight() && bufferedImage3.getType() == 6) {
						for (int k = 0; k < bufferedImage3.getHeight(); k++) {
							for (int l = 0; l < bufferedImage3.getWidth(); l++) {
								int m = bufferedImage3.getRGB(l, k);
								if ((m & 0xFF000000) != 0) {
									int n = (m & 0xFF0000) << 8 & 0xFF000000;
									int o = bufferedImage.getRGB(l, k);
									int p = MathHelper.multiplyColors(o, materialColor.color) & 16777215;
									bufferedImage3.setRGB(l, k, n | p);
								}
							}
						}

						bufferedImage2.getGraphics().drawImage(bufferedImage3, 0, 0, null);
					}
				}
			}
		} catch (IOException var17) {
			LOGGER.error("Couldn't load layered image", var17);
			return;
		}

		TextureUtil.method_5858(this.getGlId(), bufferedImage2);
	}
}
