package cafe.cutie.qclaim.QClaim.commands;

import java.awt.Point;
import java.awt.Rectangle;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cafe.cutie.qclaim.QClaim.Claim;
import cafe.cutie.qclaim.QClaim.Plugin;

public class CommandQClaim implements CommandExecutor {

	private Plugin gamePlugin;
	
	public CommandQClaim(Plugin p) {
		this.gamePlugin = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if( ! ( sender instanceof Player ) ) {
			sender.sendMessage("This command can only be executed on a client.");
			return true;
		}
		
		Location loc = ((Player) sender).getLocation();
		Claim thisClaim = (this.gamePlugin.getStorage().getClaimByPoint(new Point((int) loc.getX(), (int) loc.getZ())));
		
		if( args.length == 0 ) {
			if( ! sender.hasPermission("qclaim.view") ) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to view who owns this claim.");
				return true;
			}
			
			sender.sendMessage(ChatColor.BLUE + (thisClaim == null ? "No one" : ChatColor.UNDERLINE + thisClaim.owner.getName() + ChatColor.RESET + ChatColor.BLUE) + " owns this claim.");
			return true;
		} else if( args.length == 1 && args[0].equals("remove") ) {
			if( ! sender.hasPermission("qclaim.claim.delete") ) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to remove this claim.");
				return true;
			}
			
			if( thisClaim != null ) {
				if( thisClaim.owner.getUniqueId().equals(((Player) sender).getUniqueId()) || ((! thisClaim.owner.getUniqueId().equals(((Player) sender).getUniqueId())) && sender.hasPermission("qclaim.admin.remove") ) ) {
					this.gamePlugin.getStorage().deleteClaim(thisClaim);
					
					Rectangle rect = thisClaim.toRectangle();
					
					this.gamePlugin.getStorage().setClaimPoints(thisClaim.owner, this.gamePlugin.getStorage().getClaimPoints(thisClaim.owner) + (rect.width*rect.height));
					sender.sendMessage(ChatColor.GREEN + (thisClaim.owner.getUniqueId().equals(((Player) sender).getUniqueId()) ? "C": thisClaim.owner.getName() + "'s c") + "laim was deleted. " + (rect.width*rect.height) + " points were restored; the total is now " + (gamePlugin.getStorage().getClaimPoints(thisClaim.owner) + (rect.width*rect.height)) + ".");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "You are not standing in a claim.");
				return true;
			}
		}
		
		return false;
	}

}
