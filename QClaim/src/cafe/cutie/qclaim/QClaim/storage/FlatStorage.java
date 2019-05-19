package cafe.cutie.qclaim.QClaim.storage;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import cafe.cutie.qclaim.QClaim.Claim;
import cafe.cutie.qclaim.QClaim.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FlatStorage implements IStorage {
	private YamlConfiguration config;
	private HashMap<String, Claim> cache = new HashMap<String, Claim>();
	private HashMap<String, ArrayList<OfflinePlayer>> buddyCache = new HashMap<String, ArrayList<OfflinePlayer>>();
	private HashMap<String, Long> pointsCache = new HashMap<String, Long>();
	
	private Plugin plugin;
	
	public FlatStorage(Plugin p) {
		this.plugin = p;
		
		this.config = YamlConfiguration.loadConfiguration(new File(p.getDataFolder(), "claims.yml"));
		
		for( String key : config.getKeys(false) ) {
			if( key.equals("buddies") || key.equals("points") ) continue;
			this.getClaimById(key);
		}
		
		ConfigurationSection buds = this.config.getConfigurationSection("buddies");
		
		if( buds != null ) {		
			for( String item : buds.getKeys(false) ) {
				ArrayList<OfflinePlayer> res = new ArrayList<OfflinePlayer>();
				
				for( Object a : buds.getList(item) ) {
					res.add(this.plugin.getServer().getOfflinePlayer(UUID.fromString((String) a)));
				}
				
				this.buddyCache.put(item, res);
			}
		}
		
		ConfigurationSection points = this.config.getConfigurationSection("points");
		
		if( points != null ) {
			for( String item : points.getKeys(false) ) {
				this.pointsCache.put(item, points.getLong(item));
			}
		}
	}
	
	@Override
	public boolean pointIsClaimed(Point p) {
		return this.getClaimByPoint(p) != null;
	}

	@Override
	public Claim getClaimByPoint(Point p) {
		for( Claim claim : this.cache.values() ) {
			if( claim.toRectangle().contains(p) ) return claim;
		}
		
		return null;
	}

	@Override
	public Claim[] getAllClaims() {
		return this.cache.values().toArray(new Claim[this.cache.values().size()]);
	}

	@Override
	public Claim getClaimById(String id) {
		if( this.cache.containsKey(id) ) return cache.get(id);
		if( ! this.config.contains(id) ) return null;
		
		Claim claim = (Claim) config.get(id);
		
		this.cache.put(id, claim);
		return claim;
	}

	@Override
	public void createClaim(Claim claim) {
		this.cache.put(claim.id, claim);
		this.config.set(claim.id, claim);
		this.save();
	}
	
	@Override
	public void shutdown() {
		for( String key : cache.keySet() ) {
			this.config.set(key, cache.get(key));
		}
	}

	@Override
	public boolean doesClaimConflict(Rectangle rect) {
		for( Claim claim : this.getAllClaims() ) {
			if( claim.toRectangle().intersects(rect) || claim.toRectangle().contains(rect) ) return true;
		}
		
		return false;
	}

	@Override
	public void save() {
		try {
			this.config.save(new File(plugin.getDataFolder(), "claims.yml"));
		} catch (IOException e) {
			this.plugin.getLogger().warning("Error saving config: " + e.getMessage());
		}
	}

	@Override
	public OfflinePlayer[] getClaimBuddies(OfflinePlayer player) {
		if( ! this.buddyCache.containsKey(player.getUniqueId().toString()) ) return new OfflinePlayer[0];
		return this.buddyCache.get(player.getUniqueId().toString()).toArray(new OfflinePlayer[0]);
	}

	@Override
	public boolean isClaimBuddy(OfflinePlayer owner, OfflinePlayer requester) {
		if( ! this.buddyCache.containsKey(owner.getUniqueId().toString()) ) return false;
		if( owner.getUniqueId().equals(requester.getUniqueId()) ) return true;
		
		for(OfflinePlayer p : this.buddyCache.get(owner.getUniqueId().toString()) ) {
			if( p.getUniqueId().equals(requester.getUniqueId()) ) return true;
		}
		
		return false;
	}

	@Override
	public boolean addClaimBuddy(OfflinePlayer owner, OfflinePlayer target) {
		if( this.isClaimBuddy(owner, target) ) return false;
		
		String ouuid = owner.getUniqueId().toString();
		
		if( ! this.buddyCache.containsKey(ouuid) ) {
			this.buddyCache.put(ouuid, new ArrayList<OfflinePlayer>());
		}
		
		this.buddyCache.get(ouuid).add(target);
		this.config.set("buddies." + ouuid, this.buddyCache.get(ouuid));
		this.save();
		
		return true;
	}

	@Override
	public boolean removeClaimBuddy(OfflinePlayer owner, OfflinePlayer target) {
		String ouuid = owner.getUniqueId().toString();
		
		if( ! this.buddyCache.containsKey(ouuid) || ! this.isClaimBuddy(owner, target) ) return false;
		
		ArrayList<OfflinePlayer> plys = this.buddyCache.get(ouuid);
		
		for( int i=0; i<plys.size(); i++ ) {
			if( plys.get(i).getUniqueId().equals(target.getUniqueId()) ) {
				plys.remove(i);
				
				this.config.set("buddies." + ouuid, plys);
				this.save();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public long getClaimPoints(OfflinePlayer player) {
		return this.pointsCache.getOrDefault(player.getUniqueId().toString(), (long) 0);
	}

	@Override
	public void setClaimPoints(OfflinePlayer player, long points) {
		this.pointsCache.put(player.getUniqueId().toString(), points);
		this.config.set("points." + player.getUniqueId().toString(), points);
		this.save();
	}

	@Override
	public void deleteClaim(Claim claim) {
		this.cache.remove(claim.id);
		this.config.set(claim.id, null);
		this.save();
	}

}
