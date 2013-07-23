package de.craftinc.inventories;


import de.craftinc.inventories.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandHandler
{
    protected static final String reloadCommand = "wireload";
    protected static final String exemptCommand = "wiexempt";

    protected static final String reloadPermission = "worldinventories.reload";
    protected static final String exemptPermission = "worldinventories.exempt";

    public static boolean onCommand(CommandSender sender, Command cmd, String[] args)
    {
        String command = cmd.getName().toLowerCase();

        if (command.equals(reloadCommand)) {
            return handleReloadCommand(sender, args);
        }
        else if (command.equals(exemptCommand)) {
            return handleExemptCommand(sender, args);
        }

        return false;
    }


    protected static boolean handleReloadCommand(CommandSender sender, String[] args)
    {
        Plugin plugin = Plugin.getSharedInstance();

        if (sender.hasPermission(reloadPermission)) {

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Wrong number of arguments given. Usage is /wireload <all/language>");
                return true;
            }

            args[0] = args[0].toLowerCase();

            if (!"all".equals(args[0]) && !"language".equals(args[0])) {
                sender.sendMessage(ChatColor.RED + "Invalid argument. Usage is /wireload <all/language>");
                return true;
            }

            if ("all".equals(args[0])) {
                Logger.logStandard("Reloading all configuration...");
                plugin.reloadConfig();
                plugin.loadConfiguration();
                sender.sendMessage(ChatColor.GREEN + "Reloaded all Plugin configuration successfully");
            }
            else if ("language".equals(args[0])) {
                Logger.logStandard("Reloading language...");
                plugin.reloadConfig();

                if(plugin.loadLanguage())
                    sender.sendMessage(ChatColor.GREEN + "Reloaded Plugin language successfully");
                else
                    sender.sendMessage(ChatColor.GREEN + "Problem occurred whilst reloading Plugin language, used defaults.");
            }
        }

        return true;
    }


    protected static boolean handleExemptCommand(CommandSender sender, String[] args)
    {
        Plugin plugin = Plugin.getSharedInstance();

        if (sender.hasPermission(exemptPermission)) {

            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Wrong number of arguments given. Usage is /wiexempt <add/remove> <player>");
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {

                if (plugin.isPlayerOnExemptList(args[1])) {
                    sender.sendMessage(ChatColor.RED + "That player is already in the exemption list.");
                }
                else {
                    plugin.addPlayerToExemptList(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Added " + args[1] + " to the exemption list successfully.");
                }
            }
            else if (args[0].equalsIgnoreCase("remove")) {

                if (!plugin.isPlayerOnExemptList(args[1])) {
                    sender.sendMessage(ChatColor.RED + "That player isn't in the exemption list.");
                }
                else {
                    plugin.removePlayerFromExemptList(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Removed " + args[1] + " from the exemption list successfully.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Argument invalid. Usage is /wiexempt <add/remove> <player>");
            }

            return true;
        }

        return false;
    }
}
