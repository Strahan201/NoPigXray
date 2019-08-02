package com.sylvcraft.events;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.sylvcraft.NoPigXray;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
  NoPigXray plugin;
  
  public PlayerMove(NoPigXray instance) {
    plugin = instance;
  }

	@EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
		if (!hasActuallyMoved(e.getFrom(), e.getTo())) return;
		if (e.getPlayer().getVehicle() == null) return;
		if (e.getPlayer().getVehicle().getType() != EntityType.PIG) return;
		if (!plugin.isBlockable(e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation().add(0, 1, 0)))) return;
		if (e.getPlayer().hasPermission(plugin.bypass)) return;
		
		plugin.msg("no-ride-into", e.getPlayer());
		e.getPlayer().getVehicle().eject();
		e.setCancelled(true);
	}
	
	Boolean hasActuallyMoved(Location from, Location to) {
		if (from.getX() != to.getX()) return true;
		if (from.getY() != to.getY()) return true;
		if (from.getZ() != to.getZ()) return true;
		return false;
  }
}