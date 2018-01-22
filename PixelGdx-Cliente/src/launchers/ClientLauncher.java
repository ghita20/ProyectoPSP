package launchers;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pop.gheorghe.pixelgdx.PixelGdx;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class ClientLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = PixelGdx.WIDTH;
		config.height = PixelGdx.HEIGHT;

		new LwjglApplication(new PixelGdx(1), config);
	}
}
