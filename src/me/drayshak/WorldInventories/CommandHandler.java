package me.drayshak.WorldInventories;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandHandler
{
    private final WorldInventories plugin;


    public CommandHandler(WorldInventories plugin)
    {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        String command = cmd.getName();

        if (command.equalsIgnoreCase("wireload")) {
            return this.handleReloadCommand(sender, cmd, commandLabel, args);
        }
        else if (command.equalsIgnoreCase("wiexempt")) {
            return this.handleExemptCommand(sender, cmd, commandLabel, args);
        }

        return false;
    }


    protected boolean handleReloadCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (sender.hasPermission("worldinventories.reload")) {

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
                WorldInventories.logStandard("Reloading all configuration...");
                plugin.reloadConfig();
                plugin.loadConfiguration();
                sender.sendMessage(ChatColor.GREEN + "Reloaded all WorldInventories configuration successfully");
            }
            else if ("language".equals(args[0])) {
                WorldInventories.logStandard("Reloading language...");
                plugin.reloadConfig();

                if(plugin.loadLanguage())
                    sender.sendMessage(ChatColor.GREEN + "Reloaded WorldInventories language successfully");
                else
                    sender.sendMessage(ChatColor.GREEN + "Problem occurred whilst reloading WorldInventories language, used defaults.");
            }
        }

        return true;
    }


    protected boolean handleExemptCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (sender.hasPermission("worldinventories.exempt")) {

            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Wrong number of arguments given. Usage is /wiexempt <add/remove> <player>");
                return true;
            }

            args[1] = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("add")) {

                if (WorldInventories.exempts.contains(args[1])) {
                    sender.sendMessage(ChatColor.RED + "That player is already in the exemption list.");
                }
                else {
                    WorldInventories.exempts.add(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Added " + args[1] + " to the exemption list successfully.");
                    plugin.getConfig().set("exempt", WorldInventories.exempts);
                    plugin.saveConfig();
                }
            }
            else if(args[0].equalsIgnoreCase("remove")) {

                if (!WorldInventories.exempts.contains(args[1].toLowerCase())) {
                    sender.sendMessage(ChatColor.RED + "That player isn't in the exemption list.");
                }
                else {
                    WorldInventories.exempts.remove(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Removed " + args[1] + " from the exemption list successfully.");
                    plugin.getConfig().set("exempt", WorldInventories.exempts);
                    plugin.saveConfig();
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
