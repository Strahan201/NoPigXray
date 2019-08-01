package com.sylvcraft.events;

import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.sylvcraft.NoPigXray;
import org.spigotmc.event.entity.EntityMountEvent;


public class EntityMount implements Listener {
  NoPigXray plugin;
  
  public EntityMount(NoPigXray instance) {
    plugin = instance;
  }

	@EventHandler
  public void onEntityMount(EntityMountEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		if (!(e.getMount() instanceof Pig)) return;
		if (e.getMount().getWorld().getBlockAt(e.getMount().getLocation().add(0, 1, 0)).isEmpty()) return;
		if (e.getEntity().hasPermission(plugin.bypass)) return;
	
		plugin.msg("no-mount", (Player)e.getEntity());
		e.setCancelled(true);
  }
}