

public class GiveDyeListener extends PluginListener
{
	private final String DYE = "/dye";
	private final String D = "/d";
	private final String WOOL = "/wool";
	private final String W = "/w";
	private final String LOG = "/log";
	private final String L = "/l";
	private final String GIVE_DYE = "/givedye";
	private final String GIVE_DYE_TO = "/givedyeto";
	private final String SHEEP = "/sheep";
	private final String LEAF_TOOL = "/leaftool";
	private final String LOG_TOOL = "/logtool";
	
	private final int DYE_ID = 351;
	private final int WOOL_ID = 35;
	private final int LOG_ID = 17;
	private final int LEAF_ID = 18;
	
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
		if(split[0].equalsIgnoreCase(SHEEP) && player.canUseCommand(SHEEP))
		{
			if(split.length < 2)
			{
				player.sendMessage(Colors.Rose + "Correct usage is: /sheep [color_id] <amount>");
				return false;
			}
			
			spawnSheep(player, split, 15, 1);
			
			return true;
		}
		else if(player.canUseCommand(GIVE_DYE) || player.canUseCommand(GIVE_DYE_TO))
		{
			if( (!properties[0].containsKey("allow-dye") || properties[0].getBoolean("allow-dye", true) ) &&
					(split[0].equalsIgnoreCase(DYE) || split[0].equalsIgnoreCase(D)) )
			{
				if(split.length < 2)
				{
					if(player.canUseCommand(GIVE_DYE_TO))
						player.sendMessage(Colors.Rose + "Correct usage is: /dye [color_id] <amount> <player>");
					else
						player.sendMessage(Colors.Rose + "Correct usage is: /dye [color_id] <amount>");
					return false;
				}
				
				giveColor(player, DYE_ID, split, 15, 0);
			}
			else if( (!properties[1].containsKey("allow-wool") || properties[1].getBoolean("allow-wool", true) ) &&
					(split[0].equalsIgnoreCase(WOOL) || split[0].equalsIgnoreCase(W)) )
			{
				if(split.length < 2)
				{
					if(player.canUseCommand(GIVE_DYE_TO))
						player.sendMessage(Colors.Rose + "Correct usage is: /wool [color_id] <amount> <player>");
					else
						player.sendMessage(Colors.Rose + "Correct usage is: /wool [color_id] <amount>");
					return false;
				}
				
				giveColor(player, WOOL_ID, split, 15, 1);
			}
			else if( (!properties[2].containsKey("allow-log") || properties[2].getBoolean("allow-log", true) ) &&
					(split[0].equalsIgnoreCase(LOG) || split[0].equalsIgnoreCase(L)) )
			{
				if(split.length < 2)
				{
					if(player.canUseCommand(GIVE_DYE_TO))
						player.sendMessage(Colors.Rose + "Correct usage is: /log [color_id] <amount> <player>");
					else
						player.sendMessage(Colors.Rose + "Correct usage is: /log [color_id] <amount>");
					return false;
				}
				
				giveColor(player, LOG_ID, split, 2, 2);
			}
			else if(split[0].equalsIgnoreCase(GIVE_DYE))
			{
				player.sendMessage(Colors.Rose + dyePlugin.NAME + " ver" + dyePlugin.VERSION+". [] = required <> = optional. Commands are:");
				player.sendMessage(Colors.Rose + "/dye [color_id] <amount>");
				player.sendMessage(Colors.Rose + "/wool [color_id] <amount>");
				player.sendMessage(Colors.Rose + "/log [color_id] <amount>");
			}
			else if(player.canUseCommand(GIVE_DYE_TO) && split[0].equalsIgnoreCase(GIVE_DYE_TO))
			{
				player.sendMessage(Colors.Rose + dyePlugin.NAME + " ver" + dyePlugin.VERSION+". [] = required <> = optional. Commands are:");
				player.sendMessage(Colors.Rose + "/dye [color_id] <amount> <player>");
				player.sendMessage(Colors.Rose + "/wool [color_id] <amount> <player>");
				player.sendMessage(Colors.Rose + "/log [color_id] <amount> <player>");
			}
			else
			{
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean onBlockDestroy(Player player, Block block) 
	{
		if(!clickAllowed(player, block))
			return false;
		
		if(setLeaf(player, block, 2))
			return false;
		
		cycleBlock(player, block);
		
		return false;
	}
	
	public void onBlockRightClicked(Player player, Block blockClicked, Item item)
	{
		if(!clickAllowed(player, blockClicked))
			return;
		
		setLeaf(player, blockClicked, 1);
	}
	
	/*
	 * @return Returns true if leaf block was changed.
	 */
	private boolean setLeaf(Player player, Block block, int leafType)
	{
		if(block.getType() != LEAF_ID || block.getData() == leafType)
			return false;
		
		int tool = -1;
		try
		{
			if(properties[2].containsKey("leaf-tool"))
				tool = properties[2].getInt("leaf-tool", -1);
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		
		int itemId = player.getItemInHand();
		
		if(tool < 0 || itemId != tool)
			return false;
		
		etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), leafType);
		return true;
	}
	
	private void cycleBlock(Player player, Block block)
	{
		if(block.getStatus() != 0)
			return;
		
		int itemId = player.getItemInHand();
		int tool = -1;
		
		try
		{
			if(block.getType() == LEAF_ID)
			{
				if(properties[2].containsKey("leaf-cycle-tool"))
					tool = properties[2].getInt("leaf-cycle-tool", -1);
			}
			else if(block.getType() == LOG_ID)
			{
				if(properties[2].containsKey("log-cycle-tool"))
					tool = properties[2].getInt("log-cycle-tool", -1);
			}
		}
		catch(NumberFormatException e)
		{
			return;
		}
		
		if(tool < 0 || itemId != tool)
			return;
		
		int blockType = block.getData() + 1;
		
		if(blockType > 2)
			blockType = 0;
		
		etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), blockType);
	}
	
	private boolean clickAllowed(Player player, Block block)
	{
		if(block.getType() != LEAF_ID && block.getType() != LOG_ID)
			return false;
		
		if( ( !player.canUseCommand(GIVE_DYE) && !player.canUseCommand(GIVE_DYE_TO) ) &&
			( (block.getType() == LEAF_ID && !player.canUseCommand(LEAF_TOOL) ) ||
			  (block.getType() == LOG_ID && !player.canUseCommand(LOG_TOOL) )
			)
		  )
		{
			return false;
		}
		
		return true;
	}
	
	//Mostly a copy of hMod Player and related code
	private void giveColor(Player player, int id, String[] args, int max, int prop)
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
			
			if(color < 0 || color > max)
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
			
			if(!properties[prop].containsKey("log-cycle-tool") || properties[prop].getBoolean("log-giving", true))
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
		if(amount == -1)
		{
			setEntity(player.getEntity(), itemId, 255, color);
		}
		else
		{
			int temp = amount;
			do
			{
				if(temp - 64 >= 64)
				{
					setEntity(player.getEntity(), itemId, 64, color);
				}
				else
				{
					setEntity(player.getEntity(), itemId, temp, color);
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
	
	private void spawnSheep(Player player, String[] args, int max, int prop)
	{
		int color = 0;
		int amount = 1;
		String rider = "";
		
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
		
		if(color < 0 || color > max)
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
		
		int maxSpawn = 256;
		if(properties[prop].containsKey("spawnmob-max")) //added check to get around hMod's want to save missing settings
			maxSpawn = properties[prop].getInt("spawnmob-max", maxSpawn);
		
		if (amount > maxSpawn)
			amount = maxSpawn;
		
		if (amount <= 0)
			return;
		
		
		if(args.length > 3)
		{
			if(Mob.isValid(args[3]))
			{
				rider = args[3];
			}
			else
			{
				player.sendMessage(Colors.Rose + "Invalid rider. Name has to start with a capital like so: Pig");
				return;
			}
		}
		
		
		for(int i = 0; i < amount; i++)
		{
			Mob mob = new Mob("Sheep", player.getLocation() );
			
			if(rider.length() > 0)
				mob.spawn(new Mob(rider));
			else
				mob.spawn();
			
			setSheep(mob.getEntity(), color);
		}
	}
	
	private void setEntity(fy entity, int itemId, int amount, int color)
	{
		entity.a(new jl(itemId, amount, color));
	}
	
	private void setSheep(mj entity, int color)
	{
		dv sheep = (dv)entity;
		sheep.a(color);
	}
	
	
	private void setEntity(OEntityPlayerMP entity, int itemId, int amount, int color)
	{
		entity.a(new OItemStack(itemId, amount, color));
	}
	
	private void setSheep(OEntityLiving entity, int color)
	{
		OEntitySheep sheep = (OEntitySheep)entity;
		sheep.a(color);
	}
	
}
