package cafe.cutie.qclaim.QClaim.storage;

import java.awt.Point;
import java.awt.Rectangle;

import org.bukkit.OfflinePlayer;

import cafe.cutie.qclaim.QClaim.Claim;

public interface IStorage {
	public boolean pointIsClaimed(Point p);
	public Claim getClaimByPoint(Point p);
	public Claim[] getAllClaims();
	public Claim getClaimById(String id);
	public void createClaim(Claim claim);
	public boolean doesClaimConflict(Rectangle rect);
	public OfflinePlayer[] getClaimBuddies(OfflinePlayer player);
	public boolean isClaimBuddy(OfflinePlayer owner, OfflinePlayer requester);
	public boolean addClaimBuddy(OfflinePlayer owner, OfflinePlayer target);
	public boolean removeClaimBuddy(OfflinePlayer owner, OfflinePlayer target);
	public long getClaimPoints(OfflinePlayer player);
	public void setClaimPoints(OfflinePlayer player, long points);
	public void deleteClaim(Claim claim);
	public void shutdown();
	public void save();
}
