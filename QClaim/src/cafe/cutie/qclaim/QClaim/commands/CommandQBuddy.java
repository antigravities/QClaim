package cafe.cutie.qclaim.QClaim.commands;

import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cafe.cutie.qclaim.QClaim.Plugin;
import net.md_5.bungee.api.ChatColor;

public class CommandQBuddy implements CommandExecutor {
	private Plugin gamePlugin;
	
	public CommandQBuddy(Plugin plugin) {
		this.gamePlugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if( ! ( sender instanceof Player ) ) {
			sender.sendMessage("This command can only be executed on a client.");
			return true;
		}
		
		if( ! sender.hasPermission("qclaim.buddy") ) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to add buddies.");
			return true;
		}
		
		if( args.length < 1 ) {
			ArrayList<String> players = new ArrayList<String>();
			
			for( OfflinePlayer ply : this.gamePlugin.getStorage().getClaimBuddies((Player) sender) ) {
				players.add(ply.getName());
			}
			
			if( players.size() < 1 ) {
				sender.sendMessage(ChatColor.RED + "No one is on your buddy list. To add a buddy, type /qbuddy [name].");
				return true;
			}
			
			sender.sendMessage(ChatColor.BLUE + String.join(", ", players) + (players.size() < 2 ? " is" : " are" ) + " on your buddy list.");
			return true;
		} else if( args[0].equals("remove") && args.length > 1 ) {
			if( this.gamePlugin.getServer().getPlayer(args[1]) == null ) {
				sender.sendMessage(ChatColor.RED + "Could not find that player. Are they online?");
				return true;
			}
			
			if( ! this.gamePlugin.getStorage().removeClaimBuddy((OfflinePlayer) sender, gamePlugin.getServer().getPlayer(args[1])) ) {
				sender.sendMessage(ChatColor.RED + "Could not remove that player from your buddy list. Were they even on it at all?");
				return true;
			}
			
			sender.sendMessage(ChatColor.GREEN + "Removed.");
			return true;
		} else {
			if( this.gamePlugin.getServer().getPlayer(args[0]) == null ) {
				sender.sendMessage(ChatColor.RED + "Could not find that player. Are they online?");
				return true;
			}
			
			if( ! this.gamePlugin.getStorage().addClaimBuddy((OfflinePlayer) sender, gamePlugin.getServer().getPlayer(args[0])) ) {
				sender.sendMessage(ChatColor.RED + "Could not add that player to your buddy list. Are they already on it?");
				return true;
			}
			
			sender.sendMessage(ChatColor.GREEN + "Added.");
			return true;
		}
	}
}
