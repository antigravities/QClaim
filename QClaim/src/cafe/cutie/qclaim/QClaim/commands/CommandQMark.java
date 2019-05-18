package cafe.cutie.qclaim.QClaim.commands;

import java.awt.Point;
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

	private HashMap<Player, ArrayList<Point>> playerLocs = new HashMap<Player, ArrayList<Point>>();
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
			
			ArrayList<Point> locs = this.playerLocs.get(ply);
			
			int x1 = locs.get(0).x;
			int x2 = locs.get(1).x;
			
			int y1 = locs.get(0).y;
			int y2 = locs.get(1).y;
			
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
				
				if( points < proposed.width*proposed.height ) {
					sender.sendMessage(ChatColor.RED + "You do not have enough points to claim this (have: " + points + ", need: " + (proposed.width*proposed.height));
					return true;
				}
				
				this.gamePlugin.getStorage().setClaimPoints(ply, points-proposed.width*proposed.height);
				sender.sendMessage(ChatColor.BLUE + "This claim cost " + (proposed.width*proposed.height) + " points. You now have " + (points-proposed.width*proposed.height) + " points.");
			}
			
			this.gamePlugin.getStorage().createClaim(new Claim(locs.get(0).x, locs.get(0).y, locs.get(1).x, locs.get(1).y, ply));
			sender.sendMessage(ChatColor.GREEN + "Your claim has been created");
			return true;
		}
		
		Location loc = ply.getLocation();
		
		if( ! this.playerLocs.containsKey(ply) || this.playerLocs.get(ply).size() > 1 ) {
			sender.sendMessage(ChatColor.RED + "Locations reset.");
			this.playerLocs.put(ply, new ArrayList<Point>());
		}
		
		this.playerLocs.get(ply).add(0, new Point(loc.getBlockX(), loc.getBlockZ()));
		
		sender.sendMessage(ChatColor.GREEN + "Location marked at (" + loc.getBlockX() + ", " + loc.getBlockZ() + ").");
		sender.sendMessage(ChatColor.AQUA + "Once you have marked both corners of your proposed claim, finalize it with /qmark claim.");
		
		return true;
	}

}
