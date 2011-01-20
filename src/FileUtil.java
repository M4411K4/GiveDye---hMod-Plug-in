
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileUtil
{
	private static final String DYE_SETTINGS_PATH = "dyes.properties";
	private static final String WOOL_SETTINGS_PATH = "wool.properties";
	
	private static String getDyeDefault()
	{
		String output = ""+
		
		"#To allow players to use this plug-in you must add the /givedye command to groups in the groups.txt server file.\r\n"+
		"#If you want to allow a group to give other players dyes or wool, add the /givedyeto command to the group. \r\n"+
		"#In-game commands will be:\r\n"+
		"#/dye [color id or name] <amount> <player>\r\n"+
		"#/wool [color id or name] <amount> <player>\r\n"+
		"###################\r\n"+
		"\r\n"+
		"#Allow this plug-in to give out dyes? This applies to all players. If missing, this will be set to true. [default = true]\r\n"+
		"allow-dye=true\r\n"+
		"\r\n"+
		"#Display server log messages when players get or give dyes? If missing, this will be set to true. [default = true]\r\n"+
		"log-giving=true\r\n"+
		"\r\n"+
		"###################\r\n"+
		"\r\n"+
		"#Dye Values:\r\n"+
		"\r\n"+
		"#Add the dye names you want followed by the color id number (0 to 15) to go with the name\r\n"+
		"format: [dye_name]=[color_id]\r\n"+
		"#(same process as items.txt server file, but with '=' instead of ':')\r\n"+
		"#You can remove any default dye names too. Removing a dye name will not remove the ability to get them.\r\n"+
		"#Players with permissions will still be able to get dyes through typing in the color id number instead of the names.\r\n"+
		"\r\n"+
		"#NO SPACES IN THE NAMES!\r\n"+
		"\r\n"+
		
		"inksack=0\r\n"+
		"rosered=1\r\n"+
		"cactusgreen=2\r\n"+
		"cocobeans=3\r\n"+
		"lapislazuli=4\r\n"+
		"blue=4\r\n"+
		"purple=5\r\n"+
		"cyan=6\r\n"+
		"lightgray=7\r\n"+
		"gray=8\r\n"+
		"pink=9\r\n"+
		"lime=10\r\n"+
		"dandelionyellow=11\r\n"+
		"lightblue=12\r\n"+
		"magenta=13\r\n"+
		"orange=14\r\n"+
		"bonemeal=15\r\n"+
		
		"";
		
		return output;
	}
	
	private static String getWoolDefault()
	{
		String output = ""+
		
		"#To allow players to use this plug-in you must add the /givedye command to groups in the groups.txt server file.\r\n"+
		"#If you want to allow a group to give other players dyes or wool, add the /givedyeto command to the group. \r\n"+
		"#In-game commands will be:\r\n"+
		"#/dye [color id or name] <amount> <player>\r\n"+
		"#/wool [color id or name] <amount> <player>\r\n"+
		"###################\r\n"+
		"\r\n"+
		"#Allow this plug-in to give out wool? This applies to all players. If missing, this will be set to true. [default = true]\r\n"+
		"allow-wool=true\r\n"+
		"\r\n"+
		"#Display server log messages when players get or give wools? If missing, this will be set to true. [default = true]\r\n"+
		"log-giving=true\r\n"+
		"\r\n"+
		"###################\r\n"+
		"\r\n"+
		"#Wool Values:\r\n"+
		"\r\n"+
		"#Add the wool color names you want followed by the color id number (0 to 15) to go with the name\r\n"+
		"format: [wool_name]=[color_id]\r\n"+
		"#(same process as items.txt server file, but with '=' instead of ':')\r\n"+
		"#You can remove any default wool names too. Removing a wool name will not remove the ability to get them.\r\n"+
		"#Players with permissions will still be able to get colored wool through typing in the color id number instead of the names.\r\n"+
		"\r\n"+
		"#NO SPACES IN THE NAMES!\r\n"+
		"\r\n"+
		
		"wool=0\r\n"+
		"white=0\r\n"+
		"orange=1\r\n"+
		"magenta=2\r\n"+
		"lightblue=3\r\n"+
		"yellow=4\r\n"+
		"lightgreen=5\r\n"+
		"pink=6\r\n"+
		"gray=7\r\n"+
		"lightgray=8\r\n"+
		"cyan=9\r\n"+
		"purple=10\r\n"+
		"blue=11\r\n"+
		"brown=12\r\n"+
		"darkgreen=13\r\n"+
		"red=14\r\n"+
		"black=15\r\n"+
		
		"";
		
		return output;
	}
	
	public static PropertiesFile[] loadSettings()
	{
		PropertiesFile[] props = new PropertiesFile[2];
		
		props[0] = loadSetting(DYE_SETTINGS_PATH, getDyeDefault());
		props[1] = loadSetting(WOOL_SETTINGS_PATH, getWoolDefault());
		
		return props;
	}
	
	private static PropertiesFile loadSetting(String path, String output)
	{
		File file = new File(path);
		
		if(!file.exists())
		{
			if(!saveSettings(path, output))
			{
				return null;
			}
		}
		
		PropertiesFile properties = new PropertiesFile(path);
		
		try
		{
			properties.load();
		}
		catch(IOException e)
		{
			return null;
		}
		
		return properties;
	}
	
	private static boolean saveSettings(String path, String output)
	{
		BufferedWriter buffWriter = null;
		
		try
		{
			buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF8"));
			
			buffWriter.write(output);
		}
		catch (UnsupportedEncodingException e)
		{
			System.out.println("GiveDye saveSettings Unsupported Encoding Error: "+e);
			return false;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("GiveDye saveSettings: "+path+" could not be found. "+e);
			return false;
		}
		catch (IOException e)
		{
			System.out.println("GiveDye saveSettings Error: "+e);
			return false;
		}
		finally
		{
			if(buffWriter != null)
			{
				try
				{
					buffWriter.flush();
					buffWriter.close();
				}
				catch(IOException e)
				{
					System.out.println("GiveDye could not close BufferedWriter.");
				}
			}
		}
		
		return true;
	}
}
