package xyz.derkades.serverselectorx;

import static org.bukkit.ChatColor.AQUA;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import xyz.derkades.serverselectorx.placeholders.Placeholder;
import xyz.derkades.serverselectorx.placeholders.Server;

public class ServerSelectorXCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		if (!sender.hasPermission("ssx.admin")) {
			sender.sendMessage(ChatColor.RED + "You need the permission 'ssx.reload' to execute this command.");
			return true;
		}

		if (args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))){
			Main.getConfigurationManager().reload();
			sender.sendMessage(AQUA + "The configuration file has been reloaded.");

			Main.server.stop();
			Main.server.start();
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
			if (Server.getServers().isEmpty()){
				sender.sendMessage("No data has been received from servers.");
				return true;
			}

			for (final Server server : Server.getServers()) {
				final long ms = server.getTimeSinceLastMessage();
				final String lastInfo = ms < 999999 ? server.getTimeSinceLastMessage() + "ms" : "∞ ms";
				if (server.isOnline()) {
					final List<String> placeholderKeys = server.getPlaceholders().stream()
							.map(Placeholder::getKey).collect(Collectors.toList());
					sender.sendMessage(server.getName() + ": " + ChatColor.GREEN + "ONLINE (" + lastInfo + ") " + ChatColor.WHITE +
							":" + ChatColor.GRAY + String.join(", ", placeholderKeys));
				} else {
					sender.sendMessage(server.getName() + ": " + ChatColor.RED + "OFFLINE (" + lastInfo + ")");
				}
			}
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("items")) {
			Main.getConfigurationManager().getItems().forEach((name, config) -> {
				sender.sendMessage(name);
			});
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("menus")) {
			Main.getConfigurationManager().getMenus().forEach((name, config) -> {
				sender.sendMessage(name);
			});
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("lagdebug")) {
			Main.LAG_DEBUG = true;
			sender.sendMessage("Lag related debug console messages are now enabled until the next server restart/reload.");
		}

		return false;
	}

}
