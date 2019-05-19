package cafe.cutie.qclaim.QClaim;

import java.awt.Point;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;

public class BlockInteractListener implements Listener {
	private Plugin gamePlugin;
	private HashMap<String, Long> lastSent = new HashMap<String, Long>();
	
	public BlockInteractListener(Plugin gamePlugin) {
		this.gamePlugin = gamePlugin;
		gamePlugin.getServer().getPluginManager().registerEvents(this, gamePlugin);
	}
	
	private Point locationToPoint(Location loc) {
		return new Point((int) loc.getX(), (int) loc.getZ());
	}
	
	private boolean hasAccessToBlock(Player p, Location loc, Event q) {
		if( p == null ) return true;
		if( p.hasPermission("qclaim.admin.ignore") ) return true;
		
		Claim c = this.gamePlugin.getStorage().getClaimByPoint(new Point((int) loc.getX(), (int) loc.getZ()));
		
		if( c != null && ! c.owner.getUniqueId().equals(p.getUniqueId()) && ! this.gamePlugin.getStorage().isClaimBuddy((OfflinePlayer) c.owner, (OfflinePlayer) p)  ) {
			this.sendDenyMessage(p, ChatColor.DARK_RED + "You do not have permission to access, place, or modify this on " + (p.hasPermission("qclaim.view") ? c.owner.getName() + "'s" : "this user's") + " claim.");
			
			if( q != null ) {
				if( q instanceof Cancellable ) ((Cancellable) q).setCancelled(true);
				else if( q instanceof BlockCanBuildEvent ) ((BlockCanBuildEvent) q).setBuildable(false);
				else if( q instanceof BlockPlaceEvent ) ((BlockPlaceEvent) q).setBuild(false);
			}
			
			return false;
		}
		
		return true;
	}
	
	private void sendDenyMessage(Player p, String message) {
		if( ! this.lastSent.containsKey(p.getUniqueId().toString()) ) {
			this.lastSent.put(p.getUniqueId().toString(), (long) 0);
		}
		
		if( System.currentTimeMillis()-this.lastSent.get(p.getUniqueId().toString()) > 1000 ) {
			p.sendMessage(message);	
			this.lastSent.put(p.getUniqueId().toString(), System.currentTimeMillis());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBlock().getLocation(), e);
	}
	
	@EventHandler
	public void onBlockCanBuild(BlockCanBuildEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBlock().getLocation(), e);
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBlock().getLocation(), e);
	}
	
	@EventHandler
	public void onBlockFertilize(BlockFertilizeEvent e) {
		for( BlockState blk : e.getBlocks() ) {
			if( ! this.hasAccessToBlock(e.getPlayer(), blk.getLocation(), e) ) break;
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBlock().getLocation(), e);
	}
	
	@EventHandler
	public void onBlockMultiPlace(BlockMultiPlaceEvent e) {
		for( BlockState blk : e.getReplacedBlockStates() ) {
			if( ! this.hasAccessToBlock(e.getPlayer(), blk.getLocation(), e) ) break;
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBlockReplacedState().getLocation(), e);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBlock().getLocation(), e);
	}
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBed().getLocation(), e);
	}
	
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
		this.hasAccessToBlock(e.getPlayer(), e.getBlockClicked().getLocation(), e);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if( e.getRightClicked() == null ) return;
		
		this.hasAccessToBlock(e.getPlayer(), e.getRightClicked().getLocation(), e);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if( e.getClickedBlock() == null ) return;
		
		this.hasAccessToBlock(e.getPlayer(), e.getClickedBlock().getLocation(), e);
	}
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		if( ! ( e.getEntered() instanceof Player ) ) return;
		this.hasAccessToBlock((Player) e.getEntered(), e.getVehicle().getLocation(), e);
	}
	
	@EventHandler
	public void onFlow(BlockFromToEvent e) {
		// Ignore anything that isn't flowing into a claim
		if( this.gamePlugin.getStorage().getClaimByPoint(this.locationToPoint(e.getToBlock().getLocation())) == null ) return;
		
		// Block fluids from outside the claim from flowing into the claim
		if( this.gamePlugin.getStorage().getClaimByPoint(this.locationToPoint(e.getBlock().getLocation())) == null ) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		Inventory w = e.getInventory();
		
		if( w.getHolder() instanceof Chest || w.getHolder() instanceof DoubleChest ) {
			this.hasAccessToBlock((Player) e.getPlayer(), ((Container) w.getHolder()).getLocation(), e);
		}
	}
}
