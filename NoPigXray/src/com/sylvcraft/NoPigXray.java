package com.sylvcraft;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.sylvcraft.commands.NPXR;
import com.sylvcraft.events.EntityMount;
import com.sylvcraft.events.PlayerMove;

public class NoPigXray extends JavaPlugin {
  public Permission bypass = new Permission("nopigxray.bypass", PermissionDefault.FALSE);
  public Map<String, String> msgDefaults = new HashMap<String, String>();

  @Override
  public void onEnable() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new EntityMount(this), this);
    pm.registerEvents(new PlayerMove(this), this);
    getCommand("npxr").setExecutor(new NPXR(this));
    saveDefaultConfig();
    initDefaults();
  }

  public Boolean isBlockable(Block b) {
  	if (b.getType() == Material.AIR) return false;
  	List<String> tmp = getConfig().getStringList("exceptions");
  	if (tmp.contains(b.getType().name())) return false;
  	return !b.isEmpty();
  }
  
  public String exceptionHotbar(Inventory inv, String action) {
  	String retVal = "";
  	List<String> tmp = getConfig().getStringList("exceptions");
  	for (int x=0; x<=8; x++) {
  		ItemStack i = inv.getItem(x);
  		if (i == null) continue;
  		
  		retVal += (retVal.equals("")?"":",") + i.getType().name();
  		if (action.toLowerCase().equals("add")) {
  			if (!tmp.contains(i.getType().name())) tmp.add(i.getType().name());
  		} else {
  			if (tmp.contains(i.getType().name())) tmp.remove(i.getType().name());
  		}
  	}
  	getConfig().set("exceptions", tmp);
  	saveConfig();
  	return retVal;
  }

  public Boolean exceptionToggle(Material source) {
  	List<String> tmp = getConfig().getStringList("exceptions");
  	if (tmp.contains(source.name())) {
  		tmp.remove(source.name());
  	} else {
  		tmp.add(source.name());
  	}
  	getConfig().set("exceptions", tmp);
  	saveConfig();
  	return tmp.contains(source.name());
  }
  
  public void showHelp(CommandSender sender, Map<String, String> data) {
  	if (allowed(sender, "toggleitem")) {
  		msg("cmd-toggle-item", sender, data);
  		if (allowed(sender, "toggleitem.hotbar")) msg("cmd-hotbar-item", sender, data); 
  	}
  	if (allowed(sender, "reload")) msg("cmd-reload", sender, data);
  	for (String perm : new String[] {"view","set","list"}) if (allowed(sender, "msg." + perm)) msg("cmd-msg-" + perm, sender, data);
  }
  
  public Boolean allowed(CommandSender sender, String perm) {
  	Boolean retVal = false;
  	if (sender.hasPermission("nopigxray.admin")) return true;
  	for (String node : perm.split(","))	{
  		if (sender.hasPermission("nopigxray." + node)) retVal = true;
  	}
  	return retVal;
  }


  //
  // Yea, my messaging system is awkward.  This is what I made up when first learning Java, and I have yet
  // to take the time to make something more elegant.  Someday...  someday.
  //
  private void initDefaults() {
  	msgDefaults.clear();
    msgDefaults.put("player-only", "This only works in game!");
    msgDefaults.put("access-denied", "&cAccess denied!");
    msgDefaults.put("config-reloaded", "&6Configuration reloaded!");
    msgDefaults.put("no-mount", "&cYou cannot mount a pig unless the block above is empty!");
    msgDefaults.put("no-ride-into", "&cYou cannot ride a pig into a space where the block above is not empty!");
    msgDefaults.put("material-added", "&6%material% &eadded to the exceptions.");
    msgDefaults.put("material-removed", "&6%material% &eremoved from the exceptions.");
    msgDefaults.put("hold-item", "&6Please hold the item to remove/add in your hand.");
    msgDefaults.put("cmd-toggle-item", "&6/npxr &7- &eToggles exception for the item in hand");
    msgDefaults.put("cmd-hotbar-item", "&6/npxr hotbar add &7- &eAdds exceptions for all items in hotbar%br%&6/npxr hotbar remove &7- &eRemoves exceptions for all items in hotbar");
    msgDefaults.put("cmd-reload", "&6/npxr reload &7- &eReloads config from disk");
    msgDefaults.put("cmd-msg-view", "&6/npxr msg [msgcode] &7- &eView the set message for that code"); 
    msgDefaults.put("cmd-msg-set", "&6/npxr msg [msgcode] [msg] &7- &eSet the message for that code");
    msgDefaults.put("cmd-msg-list", "&6/npxr msg list &7- &eList the available message codes");
    msgDefaults.put("msgcodes-header", "&6Available message codes:");
    msgDefaults.put("msgcodes-data", "&e%msgcode%");
    msgDefaults.put("msgcodes-footer", "");
    msgDefaults.put("invalid-msgcode", "&cThat is not a valid message code!");
    msgDefaults.put("msgcode-view", "&e%msgcode% &6is currently &e%msg%");
    msgDefaults.put("msgcode-set", "&e%msgcode% &6has been set to &e%msg%");
  }

  private String getDefault(String code) {
  	if (msgDefaults.containsKey(code)) return msgDefaults.get(code);
  	return code;
	}

  public void msg(String msgCode, CommandSender sender) {
  	String tmp = getConfig().getString("messages." + msgCode, getDefault(msgCode));
  	if (tmp.trim().equals("")) return;
  	for (String m : tmp.split("%br%")) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
  	}
  }

  public void msg(String msgCode, CommandSender sender, Map<String, String> data) {
  	String tmp = getConfig().getString("messages." + msgCode, getDefault(msgCode));
  	if (tmp.trim().equals("")) return;
  	if (msgCode.equalsIgnoreCase("msgcode-view")) tmp = tmp.replace('&', '^');
  	for (Map.Entry<String, String> mapData : data.entrySet()) {
  	  tmp = tmp.replace(mapData.getKey(), mapData.getValue());
  	}
  	if (msgCode.equalsIgnoreCase("msgcode-view")) {
    	for (String m : tmp.split("%br%")) {
  			sender.sendMessage(ChatColor.translateAlternateColorCodes('^', m));
    	}
  	} else {
    	msg(tmp, sender);
  	}
  }
}