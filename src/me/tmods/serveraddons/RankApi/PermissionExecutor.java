package me.tmods.serveraddons.RankApi;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import me.tmods.serveraddons.Ranks;
import me.tmods.serverutils.Methods;

public class PermissionExecutor {
	private Plugin plugin;
	private FileConfiguration ranks;
	public PermissionExecutor(Plugin plugin) {
		this.plugin = plugin;
		this.ranks = Ranks.ranks;
	}
	public PermissionAttachment attach(Player p) {
		try {
		return p.addAttachment(plugin);
		} catch (Exception e) {
			Methods.log(e);
		}
		return null;
	}
	public void setPermissions(List<String> permissions,PermissionAttachment a) {
		try {
		if (a.getPermissions().size() > 0) {
			for (String p:a.getPermissions().keySet()) {
				a.setPermission(p, false);
				a.unsetPermission(p);
			}
		}
		if (permissions.size() > 0) {
			for (String perm:permissions) {
				a.setPermission(perm, true);
			}
		}
		} catch (Exception e) {
			Methods.log(e);
		}
	}
	public PermissionAttachment getAttachment(Player p) {
		try {
		if (p.getEffectivePermissions().size() > 0) {
			for (PermissionAttachmentInfo i:p.getEffectivePermissions()) {
				if (i.getAttachment() != null) {
					if (i.getAttachment().getPlugin() == plugin) {
						return i.getAttachment();
					}
				}
			}
		}
		} catch (Exception e) {
			Methods.log(e);
		}
		return null;
	}
	public void reloadRanks() {
		try {
		for (Player p:Bukkit.getOnlinePlayers()) {
			if (getAttachment(p) != null) {
				p.removeAttachment(getAttachment(p));
			}
			PermissionAttachment a = attach(p);
			if (getRank(p) != null) {
				setPermissions(getRank(p).getPermissions(),a);
			}
		}
		} catch (Exception e) {
			Methods.log(e);
		}
	}
	public Rank getDefaultRank() {
		try {
		if (Ranks.ranks.getConfigurationSection("Ranks").getKeys(false).size() > 0) {
			for (String s:Ranks.ranks.getConfigurationSection("Ranks").getKeys(false)) {
				if (Ranks.ranks.getBoolean("Ranks." + s + ".default")) {
					return Rank.fromConfig(Ranks.ranks, "Ranks." + s);
				}
			}
		}
		} catch (Exception e) {
			Methods.log(e);
		}
		return null;
	}
	public Rank getRank(Player p) {
		try {
		if (Rank.fromConfig(ranks, "Ranks." + ranks.getString("Players." + p.getUniqueId())) != null) {
			return Rank.fromConfig(ranks, "Ranks." + ranks.getString("Players." + p.getUniqueId()));
		}
		} catch (Exception e) {
			Methods.log(e);
		}
		return getDefaultRank();
	}
}
