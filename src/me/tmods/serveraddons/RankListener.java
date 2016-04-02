package me.tmods.serveraddons;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import me.tmods.serveraddons.RankApi.PermissionExecutor;
import me.tmods.serveraddons.RankApi.Rank;
import me.tmods.serverutils.Methods;

public class RankListener implements Listener{
	public FileConfiguration ranks = Ranks.ranks;
	public Plugin p;
	public PermissionExecutor pexec;
	public RankListener(Plugin p) {
		this.p = p;
		this.pexec = new PermissionExecutor(p);
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		try {
		PermissionAttachment a = pexec.attach(e.getPlayer());
		if (ranks.getConfigurationSection("Players").getKeys(false).contains("" + e.getPlayer().getUniqueId())) {
			if (Rank.fromConfig(ranks, "Ranks." + ranks.getString("Players." + e.getPlayer().getUniqueId())) != null) {
				pexec.setPermissions(pexec.getRank(e.getPlayer()).getPermissions(), a);
			} else {
				pexec.setPermissions(pexec.getDefaultRank().getPermissions(), a);
				ranks.set("Players." + e.getPlayer().getUniqueId(), pexec.getDefaultRank().getName());
				try {
					ranks.save(Ranks.rankf);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			pexec.setPermissions(pexec.getDefaultRank().getPermissions(), a);
			ranks.set("Players." + e.getPlayer().getUniqueId(), pexec.getDefaultRank().getName());
			try {
				ranks.save(Ranks.rankf);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		} catch (Exception e1) {
			Methods.log(e1);
		}
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		try {
		e.setCancelled(true);
		Bukkit.broadcastMessage(pexec.getRank(e.getPlayer()).getPrefix() + e.getPlayer().getDisplayName() + pexec.getRank(e.getPlayer()).getSuffix() + e.getMessage());
		} catch (Exception e1) {
			Methods.log(e1);
		}
	}
}
