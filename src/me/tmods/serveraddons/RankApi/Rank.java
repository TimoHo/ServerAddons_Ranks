package me.tmods.serveraddons.RankApi;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.tmods.serverutils.Methods;

public class Rank {
	private List<String> permissions;
	private String name;
	private String prefix;
	private String suffix;
	private Boolean defaultRank = false;
	public Rank(String name,List<String> permissions,String prefix,String suffix,Boolean defaultRank) {
		this.name = name;
		this.permissions = permissions;
		this.prefix = prefix;
		this.suffix = suffix;
		this.defaultRank = defaultRank;
	}
	public Rank(String name,List<String> permissions,String prefix,String suffix) {
		this.name = name;
		this.permissions = permissions;
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	public List<String> getPermissions() {
		return this.permissions;
	}
	public String getName() {
		return this.name;
	}
	public String getPrefix() {
		return this.prefix;
	}
	public String getSuffix() {
		return this.suffix;
	}
	public static Rank fromConfig(FileConfiguration cfg,String path) {
		try {
		if (cfg.getConfigurationSection(path) != null) {
			return new Rank(cfg.getString(path + ".name"),cfg.getStringList(path + ".permissions"),cfg.getString(path + ".prefix"),cfg.getString(path + ".suffix"),cfg.getBoolean(path + ".default"));
		}
		} catch (Exception e) {
			Methods.log(e);
		}
		return null;
	}
	public void toConfig(FileConfiguration cfg,String path) {
		try {
		cfg.set(path + ".name", this.name);
		cfg.set(path + ".permissions", this.permissions);
		cfg.set(path + ".prefix", this.prefix);
		cfg.set(path + ".suffix", this.suffix);
		cfg.set(path + ".default", this.defaultRank);
		} catch (Exception e) {
			Methods.log(e);
		}
	}
	public Rank addPermission(String permission) {
		if (!this.permissions.contains(permission)) {
			this.permissions.add(permission);
		}
		return this;
	}
	public Rank removePermission(String permission) {
		if (this.permissions.contains(permission)) {
			this.permissions.remove(permission);
		}
		return this;
	}
}
