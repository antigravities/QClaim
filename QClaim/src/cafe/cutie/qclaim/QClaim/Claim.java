package cafe.cutie.qclaim.QClaim;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class Claim implements ConfigurationSerializable { // Claaim
	public String id;
	
	public OfflinePlayer owner;
	
	public int x1; // Top left
	public int y1;
	
	public int x2; // Bottom right
	public int y2;
	
	private Rectangle cachedRectangle;
	
	public Claim(Map<String, Object> serialized) {
		this.id = (String) serialized.get("id");
		this.owner = Bukkit.getOfflinePlayer(UUID.fromString((String) serialized.get("owner")));
		
		this.x1 = (int) serialized.get("x1");
		this.y1 = (int) serialized.get("y1");
		
		this.x2 = (int) serialized.get("x2");
		this.y2 = (int) serialized.get("y2");
	}
	
	public Claim(int x1, int y1, int x2, int y2, Player owner) {
		this.id = UUID.randomUUID().toString();
		
		this.owner = (OfflinePlayer) owner;
		
		this.x1 = x1;
		this.y1 = y1;
		
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public Rectangle toRectangle() {
		if( cachedRectangle != null ) return cachedRectangle;
		else {
			Rectangle rect = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
			return rect;
		}
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> res = new HashMap<String, Object>();
		
		res.put("id", this.id);
		
		res.put("owner", this.owner.getUniqueId().toString());
		
		res.put("x1", this.x1);
		res.put("y1", this.y1);
		
		res.put("x2", this.x2);
		res.put("y2", this.y2);
		
		return res;
	}
}
