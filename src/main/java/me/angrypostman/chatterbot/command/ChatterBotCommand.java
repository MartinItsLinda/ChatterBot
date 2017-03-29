package me.angrypostman.chatterbot.command;

import com.google.code.chatterbotapi.ChatterBotSession;

import me.angrypostman.chatterbot.ChatterBot;
import me.angrypostman.chatterbot.Config;
import me.angrypostman.chatterbot.manager.SessionManager;
import me.angrypostman.chatterbot.util.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.angrypostman.chatterbot.util.Util.color;

public class ChatterBotCommand implements CommandExecutor, TabCompleter {

    private ChatterBot instance = ChatterBot.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("chatterbot"))return false;

        if (!sender.hasPermission("chatterbot.use")) {
            sender.sendMessage(color(Config.PREFIX+Config.NO_PERMISSION));
            return true;
        }

        boolean isPlayer = sender instanceof Player;
        Player player = isPlayer ? (Player)sender : null;

        //Uhh... we don't know what they want so ask them
        if (args.length < 1) {
            sender.sendMessage(color(Config.PREFIX+Config.CORRECT_USAGE
                    .replace("$command", "chatbot").replace("$usage", "<setid|reload|question...> [id]")));
            return false;
        }

        String action = args[0].toLowerCase();

        /* Set the ID for your current bot */
        if (action.equals("setid")) {

            if (!sender.hasPermission("chatterbot.changeid")) {
                sender.sendMessage(color(Config.PREFIX+Config.NO_PERMISSION));
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(color(Config.PREFIX+Config.CORRECT_USAGE
                        .replace("$command", "chatbot").replace("$usage", "setid <id>")));
                return false;
            }

            String id = args[1];

            SessionManager.ChatterBotMode mode = SessionManager.ChatterBotMode.getChatterBotMode(id);
            if (mode == null) {
                sender.sendMessage(color(Config.PREFIX+Config.INVALID_BOT_ID
                        .replace("$id", id)));
                return false;
            }

            SessionManager.ChatterBotMode.setDefaultMode(mode);

            //If the user already has an existing session, terminate it and create a new one with the new bot id
            if (SessionManager.hasSession(isPlayer ? player.getUniqueId() : null))
                SessionManager.getSessions().remove(isPlayer ? player.getUniqueId() : null);

            //Create a new ChatterBotSession for the player/server
            ChatterBotSession session = SessionManager.getSession(isPlayer ? player.getUniqueId() : null, true);
            SessionManager.getSessions().put(isPlayer ? player.getUniqueId() : null, session);

            sender.sendMessage(color(Config.PREFIX+Config.CHANGED_BOT_ID
                    .replace("$id", id).replace("$name", mode.name().toLowerCase().replace("_", " "))));
            return true;
        }

        /* Reload config.yml and bots.yml */

        else if (action.equals("reload")){

            if(!sender.hasPermission("chatterbot.reload")){
                sender.sendMessage(color(Config.PREFIX+Config.NO_PERMISSION));
                return true;
            }

            instance.reloadConfig();
            sender.sendMessage(color(Config.PREFIX+Config.CONFIGURATION_RELOADED));
            return true;
        }

        //Wont return null as we create a session if one doesn't already exist
        ChatterBotSession session = SessionManager.getSession(isPlayer ? player.getUniqueId() : null, true);

        String query = Util.join(args, ' ');
        try {
            //strip html (this is a web api so it can contain html in the text returned)
            String reply = session.think(query).replaceAll("<.*?>", "");
            sender.sendMessage(color(Config.PREFIX+Config.BOT_REPLY_SUCCESS_FORMAT
                    .replace("$reply", reply)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("chatterbot"))return null;
        if (!sender.hasPermission("chatterbot.use")) return null;

        if (args.length < 1) {
            sender.sendMessage(color(Config.PREFIX+Config.CORRECT_USAGE
                    .replace("$command", "chatbot").replace("$usage", "<setid|reload|question...> [id]")));
            return Arrays.asList("setid", "reload");
        }

        String action = args[0].toLowerCase();
        if (action.equals("setid")) {

            if (!sender.hasPermission("chatterbot.changeid")) {
                sender.sendMessage(color(Config.PREFIX+Config.NO_PERMISSION));
                return null;
            }

            List<String> keys = new ArrayList<>();
            for(SessionManager.ChatterBotMode mode: SessionManager.ChatterBotMode.values()){
                keys.add(mode.getKey());
            }
            return keys;
        }
        return null;
    }
}
