

public class GiveDyeListener extends PluginListener
{
	private final String DYE = "/dye";
	private final String D = "/d";
	private final String WOOL = "/wool";
	private final String W = "/w";
	private final String GIVE_DYE = "/givedye";
	private final String GIVE_DYE_TO = "/givedyeto";
	
	private final int DYE_ID = 351;
	private final int WOOL_ID = 35;
	
	private GiveDye dyePlugin;
	private PropertiesFile[] properties;
	
	public GiveDyeListener(GiveDye dyePlugin, PropertiesFile[] properties)
	{
		super();
		
		this.dyePlugin = dyePlugin;
		this.properties = properties;
	}
	
	public boolean onCommand(Player player, String[] split)
	{
		if(player.canUseCommand(GIVE_DYE) || player.canUseCommand(GIVE_DYE_TO))
		{
			if(properties[0].getBoolean("allow-dye", true) && (split[0].equalsIgnoreCase(DYE) || split[0].equalsIgnoreCase(D)) )
			{
				if(split.length < 2)
				{
					if(player.canUseCommand(GIVE_DYE_TO))
						player.sendMessage(Colors.Rose + "Correct usage is: /dye [color_id] <amount> <player>");
					else
						player.sendMessage(Colors.Rose + "Correct usage is: /dye [color_id] <amount>");
					return false;
				}
				
				giveColor(player, DYE_ID, split, 0);
			}
			else if(properties[1].getBoolean("allow-wool", true) && (split[0].equalsIgnoreCase(WOOL) || split[0].equalsIgnoreCase(W)) )
			{
				if(split.length < 2)
				{
					if(player.canUseCommand(GIVE_DYE_TO))
						player.sendMessage(Colors.Rose + "Correct usage is: /wool [color_id] <amount> <player>");
					else
						player.sendMessage(Colors.Rose + "Correct usage is: /wool [color_id] <amount>");
					return false;
				}
				
				giveColor(player, WOOL_ID, split, 1);
			}
			else if(split[0].equalsIgnoreCase(GIVE_DYE))
			{
				player.sendMessage(Colors.Rose + dyePlugin.NAME + " ver" + dyePlugin.VERSION+". [] = required <> = optional. Commands are:");
				player.sendMessage(Colors.Rose + "/dye [color_id] <amount>");
				player.sendMessage(Colors.Rose + "/wool [color_id] <amount>");
			}
			else if(player.canUseCommand(GIVE_DYE_TO) && split[0].equalsIgnoreCase(GIVE_DYE_TO))
			{
				player.sendMessage(Colors.Rose + dyePlugin.NAME + " ver" + dyePlugin.VERSION+". [] = required <> = optional. Commands are:");
				player.sendMessage(Colors.Rose + "/dye [color_id] <amount> <player>");
				player.sendMessage(Colors.Rose + "/wool [color_id] <amount> <player>");
			}
			else
			{
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	//Mostly a copy of hMod Player and related code
	private void giveColor(Player player, int id, String[] args, int prop)
	{
		Player toGive = player;
		if (args.length == 4 && player.canUseCommand(GIVE_DYE_TO))
		{
			toGive = etc.getServer().matchPlayer(args[3]);
		}
		
		if(toGive != null)
		{
			int color = 0;
			int amount = 1;
			
			try
			{
				color = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException e)
			{
				if(properties[prop].containsKey(args[1]))
				{
					color = properties[prop].getInt(args[1], -1);
				}
				else
				{
					color = -1;
				}
			}
			
			if(color < 0 || color > 15)
			{
				player.sendMessage(Colors.Rose + "Improper color id.");
				return;
			}
			
			if(args.length > 2)
			{
				try
				{
					amount = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException e)
				{
					player.sendMessage(Colors.Rose + "Improper amount.");
					return;
				}
			}
				
			if (amount <= 0 && !player.isAdmin())
				amount = 1;
			
			if (amount > 64 && !player.canIgnoreRestrictions())
				amount = 64;
			
			if (amount > 1024)
				amount = 1024; //16 stacks worth. Max for hMod, so max for this too
			
			//[NOTE]: leaving out allowed item check since this is a plug-in that can be disabled if the items are not wanted
			
			if(properties[prop].getBoolean("log-giving", true))
				GiveDye.log.info("Giving " + toGive.getName() + " some " + id + " with color " + color);
			
			Inventory inventory = toGive.getInventory();
			
			if(amount == -1)
			{
				int emptySlot = inventory.getEmptySlot();
				if(emptySlot == -1)
				{
					giveItemDrop(toGive, id, -1, color);
				}
				else
				{
					inventory.setSlot(id, 255, color, emptySlot);
					inventory.update();
				}
				return;
			}
			
			int temp = amount;
			
			do
			{
				int amountToAdd = temp >= 64 ? 64 : temp;
				
				if (hasItem(inventory, id, color, 1, 63))
				{
					Item i = getItemFromId(inventory, id, 63, color);
					if (i != null)
					{
						if (amountToAdd == 64)
						{
							int a = amountToAdd - i.getAmount();
							i.setAmount(64);
							temp -= a;
						}
						else if (amountToAdd + i.getAmount() > 64)
						{
							int a = amountToAdd + i.getAmount() - 64;
							i.setAmount(64);
							temp = a;
						}
						else if (amountToAdd + i.getAmount() <= 64)
						{
							i.setAmount(amountToAdd + i.getAmount());
							temp = 0;
						}
						
						addItem(inventory, i);
						continue;
					}
				}

				int emptySlot = inventory.getEmptySlot();
				if (emptySlot == -1)
				{
					break;
				}
				
				inventory.setSlot(id, amountToAdd, color, emptySlot);
				temp -= 64;
			} while (temp > 0);
			
			inventory.update();
			
			if (temp > 0)
			{
				giveItemDrop(toGive, id, temp, color);
			}
			
			if (toGive.getName().equalsIgnoreCase(player.getName()))
			{
				player.sendMessage(Colors.Rose + "There you go c:");
			}
			else
			{
				player.sendMessage(Colors.Rose + "Gift given! :D");
				toGive.sendMessage(Colors.Rose + "Enjoy your gift! :D");
			}
		}
		else
		{
			player.sendMessage(Colors.Rose + "Can't find user " + args[3]);
		}
	}
	
	private void giveItemDrop(Player player, int itemId, int amount, int color)
	{
		fy entity = player.getEntity();
		if(amount == -1)
		{
			entity.a(new jl(itemId, 255, color));
		}
		else
		{
			int temp = amount;
			do
			{
				if(temp - 64 >= 64)
				{
					entity.a(new jl(itemId, 64, color));
				}
				else
				{
					entity.a(new jl(itemId, temp, color));
				}
				temp -= 64;
			} while (temp > 0);
		}
	}
	
	private void addItem(Inventory inventory, Item item)
	{
		if (item == null)
			return;
	
		int slot = item.getSlot();
		int size = inventory.getContentsSize();
	
		if (slot < size && slot >= 0)
		{
			if (item.getAmount() <= 0)
			{
				inventory.setSlot(null, slot);
			}
			else if (Item.isValidItem(item.getItemId()))
			{
				inventory.setSlot(item.getItemId(), item.getAmount(), item.getDamage(), slot);
			}
		}
		else if (slot == -1)
		{
			int newSlot = inventory.getEmptySlot();
			if (newSlot != -1)
			{
				inventory.setSlot(item.getItemId(), item.getAmount(), item.getDamage(), newSlot);
				item.setSlot(newSlot);
			}
		}
	}
	
	private boolean hasItem(Inventory inventory, int itemId, int color, int minimum, int maximum)
	{
		Item[] items = inventory.getContents();
		
		for (Item item : items)
		{
			if ((item != null) && (item.getItemId() == itemId) && (item.getAmount() >= minimum) &&
				(item.getAmount() <= maximum) && item.getDamage() == color)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private Item getItemFromId(Inventory inventory, int id, int maxAmount, int color)
	{
		Item[] items = inventory.getContents();
		
		for (Item item : items)
		{
			if ((item != null) && (item.getItemId() == id) && (item.getAmount() <= maxAmount) && item.getDamage() == color)
			{
				return item;
			}
		}
		
		return null;
	}
}
