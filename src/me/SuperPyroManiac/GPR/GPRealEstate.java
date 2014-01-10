package me.SuperPyroManiac.GPR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        this.log.info("Vault is using " + econ.getName() + " as the economy plugin.");
      } else {
        this.log.warning("No compatible economy plugin detected [Vault].");
        this.log.warning("Disabling plugin.");
        getPluginLoader().disablePlugin(this);
        return;
      }
      if (setupPermissions()) {
        this.log.info("Vault is using " + perms.getName() + " for the permissions.");
      } else {
        this.log.warning("No compatible permissions plugin detected [Vault].");
        this.log.warning("Disabling plugin.");
        getPluginLoader().disablePlugin(this);
        return;
      }
    }

    saveDefaultConfig();
    this.signName = ("[" + getConfig().getString("SignShort") + "]");
    this.signNameLong = ("[" + getConfig().getString("SignLong") + "]");
    this.log.info("RealEstate Signs have been set to use " + this.signName + " or " + this.signNameLong);
    saveConfig();
  }
  
  
  public static void logtoFile(String message)
  {
      try
      {
          File saveTo = new File("plugins/GPRealEstate/GPRealEstate.log");
          if (!saveTo.exists())
          {
              saveTo.createNewFile();
          }
          FileWriter fw = new FileWriter(saveTo, true);
          PrintWriter pw = new PrintWriter(fw);
          pw.println(message);
          pw.flush();
          pw.close();

      } catch (IOException e)
      {
          e.printStackTrace();
      }

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
