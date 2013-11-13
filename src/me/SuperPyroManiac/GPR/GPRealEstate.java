package me.SuperPyroManiac.GPR;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GPRealEstate extends JavaPlugin
{
  Logger log;
  public static boolean vaultPresent = false;
  public static Economy econ = null;
  public static Permission perms = null;
  public String signName;
  public String signNameLong;

  public void onEnable()
  {
    this.log = getLogger();
    new GPREListener(this).registerEvents();

    if (checkVault())
    {
      this.log.info("Vault detected and enabled.");
      if (setupEconomy()) {
        this.log.info("Vault has detected and connected to " + econ.getName());
      } else {
        this.log.warning("No compatible economy plugin detected [Vault].");
        this.log.warning("Disabling plugin.");
        getPluginLoader().disablePlugin(this);
        return;
      }
      if (setupPermissions()) {
        this.log.info("Vault has detected and connected to " + perms.getName());
      } else {
        this.log.warning("No compatible permissions plugin detected [Vault].");
        this.log.warning("Disabling plugin.");
        getPluginLoader().disablePlugin(this);
        return;
      }
    }
    this.log.info("V" + getDescription().getVersion() + " Enabled!");

    saveDefaultConfig();
    this.signName = ("[" + getConfig().getString("SignShort") + "]");
    this.signNameLong = ("[" + getConfig().getString("SignLong") + "]");
    this.log.info("RealEstate Signs have been set to use " + this.signName + " or " + this.signNameLong);
    saveConfig();
  }

  public void onDisable()
  {
    this.log.info("V" + getDescription().getVersion() + " Disabled!");
  }

  private boolean checkVault()
  {
    vaultPresent = getServer().getPluginManager().getPlugin("Vault") != null;
    return vaultPresent;
  }

  private boolean setupEconomy()
  {
    @SuppressWarnings("rawtypes")
	RegisteredServiceProvider rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = (Economy)rsp.getProvider();
    return econ != null;
  }

  private boolean setupPermissions() {
    @SuppressWarnings("rawtypes")
	RegisteredServiceProvider rsp = getServer().getServicesManager().getRegistration(Permission.class);
    perms = (Permission)rsp.getProvider();
    return perms != null;
  }
}
