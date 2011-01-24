
import java.util.logging.Logger;


public class GiveDye extends Plugin
{
	public static final Logger log = Logger.getLogger("Minecraft");
	
	public final String NAME = "GiveDye";
	public final String VERSION = "0.3";
	
	private GiveDyeListener listener;
	private PropertiesFile[] properties;
	
	public void enable()
	{
		if(!loadConfig())
		{
			log.info(NAME + " failed to load Properties File! Continuing will likely produce errors.");
			log.info("Disabling this plug-in (" + NAME + ") is recommended.");
		}
		
		listener = new GiveDyeListener(this, properties);
		
		//log.info(NAME + " " + VERSION + " enabled.");
	}
	
	public void disable()
	{
		log.info(NAME + " " + VERSION + " disabled.");
	}
	
	public void initialize()
	{
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		
		log.info(NAME + " " + VERSION + " initialized");
	}
	
	private boolean loadConfig()
	{
		properties = FileUtil.loadSettings();
		
		return !(properties[0] == null || properties[1] == null || properties[2] == null);
	}
}
