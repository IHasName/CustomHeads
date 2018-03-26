package de.mrstein.customheads.stuff;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.SubCategory;
import de.mrstein.customheads.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class CHTabCompleter implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            return null;
        }
        Player player = (Player) sender;
        List<String> commandList = new ArrayList<>();

        if (args.length == 1)
            Utils.getPermissions(player).forEach(permission -> commandList.addAll(Arrays.stream(Utils.getPermissions().get(permission)).filter(subCommand -> subCommand.startsWith(args[0].toLowerCase()) || args[0].equals("")).collect(Collectors.toList())));
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                if (CustomHeads.heads.get().isConfigurationSection("heads." + player.getUniqueId()) && Utils.hasPermission(player, "heads.use.more")) {
                    commandList.addAll(CustomHeads.heads.get().getConfigurationSection("heads." + player.getUniqueId()).getKeys(false));
                }
            }
            if (args[0].equalsIgnoreCase("history")) {
                for (Player a : Bukkit.getOnlinePlayers()) {
                    if (sender.hasPermission("heads.view.history." + a.getName())) {
                        if (a.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                            commandList.add(a.getName());
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("categories")) {
                if (sender.hasPermission("heads.admin")) {
                    commandList.addAll(Arrays.asList("remove", "delete", "import"));
                }
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("categories")) {
                if (args[1].equalsIgnoreCase("remove")) {
                    CustomHeads.getCategoryImporter().getCategoryList().forEach(category -> commandList.add(category.getId()));
                }
                if (args[1].equalsIgnoreCase("delete")) {
                    for (Category category : CustomHeads.getCategoryImporter().getCategoryList()) {
                        commandList.add(category.getId());
                        if (category.hasSubCategories()) {
                            for (SubCategory subCategory : category.getSubCategories()) {
                                commandList.add(subCategory.getId());
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(commandList);
        return commandList;
    }

}
