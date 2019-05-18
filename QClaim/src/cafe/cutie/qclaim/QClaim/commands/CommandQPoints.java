package cafe.cutie.qclaim.QClaim.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cafe.cutie.qclaim.QClaim.Plugin;
import net.md_5.bungee.api.ChatColor;

public class CommandQPoints implements CommandExecutor {

	private Plugin gamePlugin;
	
	public CommandQPoints(Plugin gamePlugin) {
		this.gamePlugin = gamePlugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if( ! (sender instanceof Player) ) {
			sender.sendMessage("This command must be executed from a client.");
			return true;
		}
		
		sender.sendMessage(ChatColor.BLUE + "You have " + this.gamePlugin.getStorage().getClaimPoints((Player) sender));
		return true;
	}

}
