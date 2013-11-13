package me.SuperPyroManiac.GPR;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

public class GPREListener
  implements Listener
{
  private GPRealEstate plugin;

  public GPREListener(GPRealEstate plugin)
  {
    this.plugin = plugin;
  }

  public void registerEvents()
  {
    PluginManager pm = this.plugin.getServer().getPluginManager();
    pm.registerEvents(this, this.plugin);
  }

  @EventHandler
  public void onSignBreak(BlockBreakEvent event)
  {
  }

  @EventHandler
  public void onSignChange(SignChangeEvent event)
  {
    if ((event.getLine(0).equalsIgnoreCase(this.plugin.signName)) || (event.getLine(0).equalsIgnoreCase(this.plugin.signNameLong))) {
      Player signPlayer = event.getPlayer();
      Location signLocation = event.getBlock().getLocation();

      GriefPrevention gp = GriefPrevention.instance;

      Claim signClaim = gp.dataStore.getClaimAt(signLocation, false, null);

      if (signClaim == null) {
  		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
  		signPlayer.sendMessage(ChatColor.AQUA + "The sign you placed is not inside a claim!");
        event.setCancelled(true);
        return;
      }

      if (!GPRealEstate.perms.has(signPlayer, "GPRealEstate.sell")) {
    		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		signPlayer.sendMessage(ChatColor.AQUA + "You do not have permission to sell claims!");
        event.setCancelled(true);
        return;
      }

      if (event.getLine(1).isEmpty()) {
    		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		signPlayer.sendMessage(ChatColor.AQUA + "You need to enter the price on the second line!");
        event.setCancelled(true);
        return;
      }

      String signCost = event.getLine(1);
      try
      {
        @SuppressWarnings("unused")
		double d = Double.parseDouble(event.getLine(1));
      }
      catch (NumberFormatException e) {
    		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		signPlayer.sendMessage(ChatColor.AQUA + "You need to enter a valid number on the second line.");
        event.setCancelled(true);
        return;
      }

      if (signClaim.parent == null)
      {
        if (signPlayer.getName().equalsIgnoreCase(signClaim.getOwnerName()))
        {
          event.setLine(0, this.plugin.signNameLong);
          event.setLine(1, ChatColor.GREEN + "FOR SALE");
          event.setLine(2, signPlayer.getName());
          event.setLine(3, signCost + " " + GPRealEstate.econ.currencyNamePlural());
        }
        else
        {
          if ((signClaim.isAdminClaim()) && (signPlayer.hasPermission("GPRealEstate.Adminclaim")))
          {
    		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		signPlayer.sendMessage(ChatColor.AQUA + "You cannot sell admin claims! Only can lease admin subdivides.");
            event.setCancelled(true);
            return;
          }

  		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
  		signPlayer.sendMessage(ChatColor.AQUA + "You can only sell claims you own!");
          event.setCancelled(true);
        }

      }
      else if ((signPlayer.getName().equalsIgnoreCase(signClaim.parent.getOwnerName())) || (signClaim.isManager(signPlayer.getName()))) {
        event.setLine(0, this.plugin.signNameLong);
        event.setLine(1, ChatColor.GREEN + "FOR LEASE");
        event.setLine(2, signPlayer.getName());
        event.setLine(3, signCost + " " + GPRealEstate.econ.currencyNamePlural());
      }
      else if ((signClaim.parent.isAdminClaim()) && (signPlayer.hasPermission("GPRealEstate.Adminclaim")))
      {
        event.setLine(0, this.plugin.signNameLong);
        event.setLine(1, ChatColor.GREEN + "FOR LEASE");
        event.setLine(2, "Server");
        event.setLine(3, signCost + " " + GPRealEstate.econ.currencyNamePlural());
      }
      else
      {
    		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		signPlayer.sendMessage(ChatColor.AQUA + "You can only lease subclaims you own!");
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onSignInteract(PlayerInteractEvent event)
  {
    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
    {
      Material type = event.getClickedBlock().getType();
      if ((type == Material.SIGN_POST) || (type == Material.WALL_SIGN))
      {
        Sign sign = (Sign)event.getClickedBlock().getState();

        if ((sign.getLine(0).equalsIgnoreCase(this.plugin.signName)) || (sign.getLine(0).equalsIgnoreCase(this.plugin.signNameLong)))
        {
          Player signPlayer = event.getPlayer();
          if (!GPRealEstate.perms.has(signPlayer, "GPRealEstate.buy")) {
      		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		signPlayer.sendMessage(ChatColor.AQUA + "You do not have permission to buy claims!");
            event.setCancelled(true);
            return;
          }

          Location signLocation = event.getClickedBlock().getLocation();
          GriefPrevention gp = GriefPrevention.instance;
          Claim signClaim = gp.dataStore.getClaimAt(signLocation, false, null);

          if (signClaim == null) {
    		signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		signPlayer.sendMessage(ChatColor.AQUA + "This sign is no longer within a claim!");
            return;
          }

          if (signClaim.parent == null)
          {
            if ((!sign.getLine(2).equalsIgnoreCase(signClaim.getOwnerName())) && (!signClaim.isAdminClaim())) {
      		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		  signPlayer.sendMessage(ChatColor.AQUA + "The listed player no longer has the rights to sell this claim!");
              event.getClickedBlock().setType(Material.AIR);
              return;
            }
            if (signClaim.getOwnerName().equalsIgnoreCase(signPlayer.getName()))
            {
        	  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
          	  signPlayer.sendMessage(ChatColor.AQUA + "You already own this claim!");
          	  return;
            }

          }
          else
          {
            if ((!sign.getLine(2).equalsIgnoreCase(signClaim.parent.getOwnerName())) && (!signClaim.isManager(sign.getLine(2))) && (!signClaim.parent.isAdminClaim()))
            {
      		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		  signPlayer.sendMessage(ChatColor.AQUA + "The listed player no longer has the rights to lease this claim!");
              event.getClickedBlock().setType(Material.AIR);
              return;
            }
            if (signClaim.parent.getOwnerName().equalsIgnoreCase(signPlayer.getName()))
            {
        		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
          		  signPlayer.sendMessage(ChatColor.AQUA + "You already own this claim!");
              return;
            }

          }

          String[] signDelimit = sign.getLine(3).split(" ");
          Double signCost = Double.valueOf(Double.valueOf(signDelimit[0].trim()).doubleValue());
          if (!GPRealEstate.econ.has(signPlayer.getName(), signCost.doubleValue())) {
      		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		  signPlayer.sendMessage(ChatColor.AQUA + "You do not have enough money!");
            return;
          }

          EconomyResponse ecoresp = GPRealEstate.econ.withdrawPlayer(signPlayer.getName(), signCost.doubleValue());
          if (!ecoresp.transactionSuccess()) {
      		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		  signPlayer.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.AQUA + "Could not withdraw money!");
            return;
          }

          if (!sign.getLine(2).equalsIgnoreCase("server"))
          {
            ecoresp = GPRealEstate.econ.depositPlayer(sign.getLine(2), signCost.doubleValue());
            if (!ecoresp.transactionSuccess()) {
      		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
      		  signPlayer.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.AQUA + "Could not transfer money, Refunding Player!");
              GPRealEstate.econ.depositPlayer(signPlayer.getName(), signCost.doubleValue());
              return;
            }

          }

          if (sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "FOR SALE"))
          {
            try {
              gp.dataStore.changeClaimOwner(signClaim, signPlayer.getName());
            }
            catch (Exception e) {
              e.printStackTrace();
              return;
            }

            if (signClaim.getOwnerName().equalsIgnoreCase(signPlayer.getName())) {
        		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
          		  signPlayer.sendMessage(ChatColor.AQUA + "You have successfully purchased this claim!");
            } else {
        		  signPlayer.sendMessage(ChatColor.BLUE + "-------" + ChatColor.GOLD + this.plugin.signNameLong + ChatColor.BLUE + "-------");
          		  signPlayer.sendMessage(ChatColor.RED + "ERROR: " + ChatColor.AQUA + "Cannot purchase claim!");
              return;
            }
            gp.dataStore.saveClaim(signClaim);

            event.getClickedBlock().setType(Material.AIR);
          }

          if (sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "FOR LEASE"))
          {
            signClaim.clearPermissions();

            signClaim.addManager(signPlayer.getName());
            signClaim.setPermission(signPlayer.getName(), ClaimPermission.Build);
            gp.dataStore.saveClaim(signClaim);
            
           event.getClickedBlock().setType(Material.AIR);
          }
        }
      }
    }
  }
}