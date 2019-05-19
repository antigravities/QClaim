package cafe.cutie.qclaim.QClaim.commands;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import cafe.cutie.qclaim.QClaim.Claim;
import cafe.cutie.qclaim.QClaim.Plugin;

public class CommandQMark implements CommandExecutor {

	private HashMap<Player, ArrayList<Location>> playerLocs = new HashMap<Player, ArrayList<Location>>();
	private Plugin gamePlugin;
	
	public CommandQMark(Plugin p) {
		this.gamePlugin = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if( ! ( sender instanceof Player ) ) {
			sender.sendMessage("This command can only be executed on a client.");
			return true;
		}
		
		if( ! sender.hasPermission("qclaim.claim.point") ) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to mark points to claim.");
			return true;
		}
		
		Player ply = (Player) sender;
		
		if( args.length > 0 && args[0].equals("claim") ) {
			if( ! sender.hasPermission("qclaim.claim.create") ) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to claim areas.");
				return true;
			}
			
			if( this.playerLocs.get(ply).size() < 2 ) {
				sender.sendMessage(ChatColor.RED + "You must mark both corners of your proposed claim first.");
				return true;
			}
			
			ArrayList<Location> locs = this.playerLocs.get(ply);
			
			if( gamePlugin.getConfig().getBoolean("spawnprotect.enabled") && ! ply.hasPermission("qclaim.admin.claimspawn") ) {
				long dist = gamePlugin.getConfig().getLong("spawnprotect.distance");
				Location spawn = ply.getWorld().getSpawnLocation();
				
				if( spawn.distance(locs.get(0)) < dist || spawn.distance(locs.get(1)) < dist ) {
					sender.sendMessage(ChatColor.RED + "You are claiming too close to spawn (" + ((int) Math.floor(Math.min(spawn.distance(locs.get(0)), spawn.distance(locs.get(1))))) + " blocks)! Please move at least " + dist + " blocks away from the spawnpoint and try again.");
					return true;
				}
			}
			
			int x1 = locs.get(0).getBlockX();
			int x2 = locs.get(1).getBlockX();
			
			int y1 = locs.get(0).getBlockZ();
			int y2 = locs.get(1).getBlockZ();
			
			Rectangle proposed = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2-x1), Math.abs(y2-y1));
			
			if( proposed.width == 0 || proposed.height == 0 ) {
				sender.sendMessage(ChatColor.RED + "Claims must have a width and height greater than zero.");
				return true;
			}
			
			if( this.gamePlugin.getStorage().doesClaimConflict(proposed) ) {
				sender.sendMessage(ChatColor.RED + "That claim conflicts with another claim.");
				return true;
			}
			
			if( ! ply.hasPermission("qclaim.free") ) {
				long points = this.gamePlugin.getStorage().getClaimPoints(ply);
				
				if( this.gamePlugin.getDefaultPoints()-points < proposed.width*proposed.height ) {
					sender.sendMessage(ChatColor.RED + "You do not have enough points to claim this (have: " + (this.gamePlugin.getDefaultPoints()-points) + ", need: " + (proposed.width*proposed.height));
					return true;
				}
				
				this.gamePlugin.getStorage().setClaimPoints(ply, points+proposed.width*proposed.height);
				sender.sendMessage(ChatColor.BLUE + "This claim cost " + (proposed.width*proposed.height) + " points. You now have " + (this.gamePlugin.getDefaultPoints()-(points+(proposed.width*proposed.height))) + " points.");
			}
			
			this.gamePlugin.getStorage().createClaim(new Claim(x1, y1, x2, y2, ply));
			sender.sendMessage(ChatColor.GREEN + "Your claim has been created");
			return true;
		}
		
		Location loc = ply.getLocation();
		
		if( ! this.playerLocs.containsKey(ply) || this.playerLocs.get(ply).size() > 1 ) {
			sender.sendMessage(ChatColor.RED + "Locations reset.");
			this.playerLocs.put(ply, new ArrayList<Location>());
		}
		
		this.playerLocs.get(ply).add(loc);
		
		sender.sendMessage(ChatColor.GREEN + "Location marked at (" + loc.getBlockX() + ", " + loc.getBlockZ() + ").");
		sender.sendMessage(ChatColor.AQUA + "Once you have marked both corners of your proposed claim, finalize it with /qmark claim.");
		
		return true;
	}

}
