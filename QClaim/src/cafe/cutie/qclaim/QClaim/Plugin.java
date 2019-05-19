package cafe.cutie.qclaim.QClaim;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import cafe.cutie.qclaim.QClaim.commands.CommandQBuddy;
import cafe.cutie.qclaim.QClaim.commands.CommandQClaim;
import cafe.cutie.qclaim.QClaim.commands.CommandQMark;
import cafe.cutie.qclaim.QClaim.commands.CommandQPoints;
import cafe.cutie.qclaim.QClaim.storage.FlatStorage;
import cafe.cutie.qclaim.QClaim.storage.IStorage;

public class Plugin extends JavaPlugin {
	protected FileConfiguration config;
	protected IStorage storage;
	
	private long defaultPoints; 
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new BlockInteractListener(this), this);
		
		this.config = this.getConfig();
		
		this.config.addDefault("storage", "flat");
		
		this.config.addDefault("points.enabled", true);
		this.config.addDefault("points.default", (long) 22500);
		
		defaultPoints = this.config.getLong("points.default");
		
		this.config.addDefault("spawnprotect.enabled", true);
		this.config.addDefault("spawnprotect.distance", (long) 100);
		
		this.getCommand("qmark").setExecutor(new CommandQMark(this));
		this.getCommand("qclaim").setExecutor(new CommandQClaim(this));
		this.getCommand("qbuddy").setExecutor(new CommandQBuddy(this));
		this.getCommand("qpoints").setExecutor(new CommandQPoints(this));
		
		ConfigurationSerialization.registerClass(Claim.class);
		
		if( this.config.getString("storage").equals("flat") ) {
			this.storage = new FlatStorage(this);
		} else {
			this.getLogger().severe("No valid storage system specified. Please specify one and try again!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		this.getLogger().info("Enabled!");
	}
	
	public IStorage getStorage() {
		return storage;
	}
	
	public long getDefaultPoints() {
		return defaultPoints;
	}
}
