package com.sylvcraft.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sylvcraft.NoPigXray;

public class NPXR implements CommandExecutor {
  NoPigXray plugin;
  
  public NPXR(NoPigXray instance) {
    plugin = instance;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      if (!(sender instanceof Player)) {
      	plugin.msg("player-only", sender);
        return true;
      }
      Map<String, String> data = new HashMap<String, String>();

      Player p = (Player)sender;
      data.put("%player%", p.getName());

      if (args.length == 0) {

    		if (!plugin.allowed(sender, "toggleitem")) {
    			plugin.msg("access-denied", sender, data);
    			return true;
    		}
      	ItemStack i = p.getInventory().getItemInMainHand();
        if (i.getType() == Material.AIR) {
        	plugin.msg("hold-item", sender, data);
        	return true;
        }
        data.put("%material%", i.getType().name());
        
        if (plugin.exceptionToggle(i.getType())) {
        	plugin.msg("material-added", sender, data);
        } else {
        	plugin.msg("material-removed", sender, data);
        }
        
      } else {
      	
      	switch (args[0].toLowerCase()) {
      	//
      	// Display help
      	//
      	case "help":
      	case "?":
      		plugin.showHelp(sender, data);
      		break;

      	//
      	// Reload configuration from disk
      	//
      	case "reload":
      		if (!plugin.allowed(sender, "reload")) {
      			plugin.msg("access-denied", sender, data);
      			return true;
      		}
      		plugin.reloadConfig();
      		plugin.msg("config-reloaded", sender, data);
      		break;
      		
      	//
      	// Add/remove all items on hotbar from exception list
      	//
      	case "hotbar":
      		if (!plugin.allowed(sender, "toggleitem.hotbar")) {
      			plugin.msg("access-denied", sender, data);
      			return true;
      		}
      		if (args.length == 1 || (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove"))) {
      			plugin.showHelp(sender, data);
      			return true;
      		}

      		data.put("%material%", plugin.exceptionHotbar(p.getInventory(), args[1].toLowerCase()));
        	plugin.msg("material-" + (args[1].equalsIgnoreCase("add")?"added":"removed"), sender, data);
      		break;
      	
      	//
      	// Message config handler
      	//
      	case "msg":
      		if (!plugin.allowed(sender, "msg.view,msg.set,msg.list")) {
      			plugin.msg("access-denied", sender, data);
      			return true;
      		}

      		if (args.length == 1) {
      			plugin.showHelp(sender, data);
      			return true;
      		}

      		if (args[1].equalsIgnoreCase("list")) {
        		if (!plugin.allowed(sender, "msg.list")) {
        			plugin.msg("access-denied", sender, data);
        			return true;
        		}

        		plugin.msg("msgcodes-header", sender, data);
      			for (Map.Entry<String, String> msg : plugin.msgDefaults.entrySet()) {
      				data.put("%msgcode%", msg.getKey());
      				data.put("%msg%", msg.getValue());
      				plugin.msg("msgcodes-data", sender, data);
      			}
      			plugin.msg("msgcodes-footer", sender, data);
      			return true;
      		}
      		
      		data.put("%msgcode%", args[1].toLowerCase());

      		if (!plugin.msgDefaults.containsKey(args[1].toLowerCase())) {
      			plugin.msg("invalid-msgcode", sender, data);
      			return true;
      		}
      		
      		if (args.length == 2) {
        		if (!plugin.allowed(sender, "msg.view")) {
        			plugin.msg("access-denied", sender, data);
        			return true;
        		}
      			data.put("%msg%", plugin.getConfig().getString("messages." + args[1].toLowerCase(), plugin.msgDefaults.get(args[1].toLowerCase())));
      			plugin.msg("msgcode-view", sender, data);
      			return true;
      		}

      		if (!plugin.allowed(sender, "msg.set")) {
      			plugin.msg("access-denied", sender, data);
      			return true;
      		}
      		String msg = StringUtils.join(args, " ", 2, args.length); 
      		plugin.getConfig().set("messages." + args[1].toLowerCase(), msg);
      		plugin.saveConfig();
      		data.put("%msg%", msg);
      		plugin.msg("msgcode-set", sender, data);
      		break;
      		
      	default:
      		break;
      	}
      }
      
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
}
