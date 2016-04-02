package me.tmods.serveraddons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.tmods.serveraddons.RankApi.PermissionExecutor;
import me.tmods.serveraddons.RankApi.Rank;
import me.tmods.serverutils.Methods;

public class Ranks extends JavaPlugin implements Listener{

	public static File rankf = new File("plugins/TModsServerUtils","ranks.yml");
	public File langf = new File("plugins/TModsServerUtils","lang.yml");
	public File cfgf = new File("plugins/TModsServerUtils","config.yml");
	public static FileConfiguration ranks = YamlConfiguration.loadConfiguration(rankf);
	public FileConfiguration lang = YamlConfiguration.loadConfiguration(langf);
	public FileConfiguration cfg = YamlConfiguration.loadConfiguration(cfgf);
	public List<Rank> serverRanks = new ArrayList<Rank>();
	public PermissionExecutor pexec;

	@Override
	public void onEnable() {
		try {
		this.pexec = new PermissionExecutor(this);
		if (Bukkit.getOnlinePlayers().size() > 0) {
			pexec.reloadRanks();
		}		
		Bukkit.getPluginManager().registerEvents(new RankListener(this), this);
		if (ranks.getConfigurationSection("Players") == null) {
			ranks.set("Players.tempPlayer", "temp");
			try {
				ranks.save(rankf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ranks.set("Players.tempPlayer", null);
			try {
				ranks.save(rankf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ranks.getConfigurationSection("Ranks") == null) {
			List<String> perm = new ArrayList<String>();
			Rank r = new Rank("Guest", perm, "[Guest] ", ": ", true);
			r.toConfig(ranks, "Ranks.Guest");
			try {
				ranks.save(rankf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ranks.getConfigurationSection("Ranks").getKeys(false).size() > 0) {
			for (String s:ranks.getConfigurationSection("Ranks").getKeys(false)) {
				serverRanks.add(Rank.fromConfig(ranks, s));
			}
		}
		} catch (Exception e) {
			Methods.log(e);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
		if (cmd.getName().equalsIgnoreCase("rank")) {
			if (!sender.hasPermission("ServerAddons.manageRanks")) {
				sender.sendMessage(Methods.getLang("permdeny"));
				return true;
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("addPerm")) {
					if (args.length != 3) {
						sender.sendMessage("/rank addPerm <Rank> <Permission>");
						return true;
					}
					Rank r = Rank.fromConfig(ranks, "Ranks." + args[1]);
					if (r != null) {
						r = r.addPermission(args[2]);
						r.toConfig(ranks, "Ranks." + args[1]);
						try {
							ranks.save(rankf);
						} catch (IOException e) {
							e.printStackTrace();
						}
						sender.sendMessage(Methods.getLang("rankpermadded"));
						pexec.reloadRanks();
						return true;
					} else {
						sender.sendMessage(Methods.getLang("nsrank"));
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("removePerm")) {
					if (args.length != 3) {
						sender.sendMessage("/rank removePerm <Rank> <Permission>");
						return true;
					}
					Rank r = Rank.fromConfig(ranks, "Ranks." + args[1]);
					if (r != null) {
						r = r.removePermission(args[2]);
						r.toConfig(ranks, "Ranks." + args[1]);
						try {
							ranks.save(rankf);
						} catch (IOException e) {
							e.printStackTrace();
						}
						sender.sendMessage(Methods.getLang("rankpermremoved"));
						pexec.reloadRanks();
						return true;
					} else {
						sender.sendMessage(Methods.getLang("nsrank"));
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("info")) {
					if (args.length != 2) {
						sender.sendMessage("/rank info <Player>");
						return true;
					}
					if (Bukkit.getPlayer(args[1]) == null) {
						sender.sendMessage(Methods.getLang("notonline"));
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					sender.sendMessage("------------------------------------");
					sender.sendMessage("Info for " + args[1] + ":");
					sender.sendMessage("");
					sender.sendMessage("UUID: " + p.getUniqueId());
					sender.sendMessage("Rank: " + pexec.getRank(p).getName());
					sender.sendMessage("Permission amount: " + pexec.getRank(p).getPermissions().size());
					sender.sendMessage("");
					sender.sendMessage("to get all permissions type");
					sender.sendMessage("/rank permissions " + args[1]);
					sender.sendMessage("------------------------------------");
					return true;
				}
				if (args[0].equalsIgnoreCase("permissions")) {
					if (args.length != 2) {
						sender.sendMessage("/rank permissions <Player>");
						return true;
					}
					Player p = Bukkit.getPlayer(args[1]);
					if (p == null) {
						sender.sendMessage(Methods.getLang("notonline"));
						return true;
					}
					sender.sendMessage("Permissions of " + args[1] + " in Rank " + pexec.getRank(p).getName());
					if (pexec.getRank(p).getPermissions().size() > 0) {
						for (String s:pexec.getRank(p).getPermissions()) {
							sender.sendMessage(s);
						}
					}
					sender.sendMessage("-------------");
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (args.length != 3) {
						sender.sendMessage("/rank set <Player> <Rank>");
						return true;
					}
					Rank r = Rank.fromConfig(ranks, "Ranks." + args[2]);
					if (r == null) {
						sender.sendMessage(Methods.getLang("nsrank"));
						return true;
					}
					if (Bukkit.getPlayer(args[1]) != null) {
						ranks.set("Players." + Bukkit.getPlayer(args[1]).getUniqueId(), r.getName());
						try {
							ranks.save(rankf);
						} catch (IOException e) {
							e.printStackTrace();
						}
						pexec.reloadRanks();
						sender.sendMessage(Methods.getLang("rankset"));
						return true;
					} else {
						sender.sendMessage(Methods.getLang("notonline"));
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("create")) {
					if (args.length != 5) {
						sender.sendMessage("/rank create <Name> <Prefix> <Suffix> <Default>");
						return true;
					}
					if (pexec.getDefaultRank() != null && Boolean.valueOf(args[4])) {
						ranks.set("Ranks." + pexec.getDefaultRank().getName() + ".default", false);
						sender.sendMessage(Methods.getLang("defrep"));
					}
					String name = args[1].replace("&", "§");
					String prefix = args[2].replace("&", "§") + " ";
					String suffix = args[3].replace("&", "§") + " ";
					Rank r = new Rank(name, new ArrayList<String>(), prefix, suffix, Boolean.valueOf(args[4]));
					r.toConfig(ranks, "Ranks." + r.getName());
					try {
						ranks.save(rankf);
					} catch (IOException e) {
						e.printStackTrace();
					}
					sender.sendMessage("rank created!");
					sender.sendMessage("Rank format:");
					sender.sendMessage(r.getPrefix() + "Player" + r.getSuffix() + "ExampleMessage!");
					return true;
				}
				if (args[0].equalsIgnoreCase("remove")) {
					if (args.length != 2) {
						sender.sendMessage("/rank remove <Rank>");
						return true;
					}
					if (ranks.getBoolean("Ranks." + args[1] + ".default")) {
						sender.sendMessage(Methods.getLang("defrankwarn"));
						return true;
					}
					if (Rank.fromConfig(ranks, "Ranks." + args[1]) == null) {
						sender.sendMessage(Methods.getLang("nsrank"));
						return true;
					}
					ranks.set("Ranks." + args[1], null);
					try {
						ranks.save(rankf);
					} catch (IOException e) {
						e.printStackTrace();
					}
					sender.sendMessage("Rank deleted!");
					return true;
				}
				if (args[0].equalsIgnoreCase("list")) {
					for (String s:ranks.getConfigurationSection("Ranks").getKeys(false)) {
						Rank r = Rank.fromConfig(ranks, "Ranks." + s);
						sender.sendMessage(r.getName() + "     " + r.getPrefix() + "Player" + r.getSuffix() + "ExampleMessage!");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("setPrefix")) {
					if (args.length != 3) {
						sender.sendMessage("/rank setPrefix <Rank> <Prefix>");
						return true;
					}
					String prefix = args[2].replace("&", "§") + " ";
					if (Rank.fromConfig(ranks, "Ranks." + args[1]) != null) {
						ranks.set("Ranks." + args[1] + ".prefix", prefix);
						try {
							ranks.save(rankf);
						} catch (IOException e) {
							e.printStackTrace();
						}
						sender.sendMessage("prefix set.");
					} else {
						sender.sendMessage(Methods.getLang("nsrank"));
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("setSuffix")) {
					if (args.length != 3) {
						sender.sendMessage("/rank setSuffix <Rank> <Suffix>");
						return true;
					}
					String suffix = args[2].replace("&", "§") + " ";
					if (Rank.fromConfig(ranks, "Ranks." + args[1]) != null) {
						ranks.set("Ranks." + args[1] + ".suffix", suffix);
						try {
							ranks.save(rankf);
						} catch (IOException e) {
							e.printStackTrace();
						}
						sender.sendMessage("suffix set.");
					} else {
						sender.sendMessage(Methods.getLang("nsrank"));
					}
					return true;
				}
			}
		}
		} catch (Exception e) {
			Methods.log(e);
		}
		return false;
	}
	
}
