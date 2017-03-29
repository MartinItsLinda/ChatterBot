package me.angrypostman.chatterbot.listener;

import com.google.code.chatterbotapi.ChatterBotSession;

import me.angrypostman.chatterbot.ChatterBot;
import me.angrypostman.chatterbot.Config;
import me.angrypostman.chatterbot.manager.SessionManager;
import me.angrypostman.chatterbot.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.angrypostman.chatterbot.util.Util.color;

public class AsyncChatListener implements Listener {

    private ChatterBot instance = ChatterBot.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncChat(final AsyncPlayerChatEvent event){

        if(!Config.IS_GLOBAL_BOT)return;

        final Player player=event.getPlayer();
        String message=event.getMessage();
        String[] oldArgs=message.split(" ");
        String[] args=new String[oldArgs.length-1];

        if(!message.startsWith(Config.COMMAND_PREFIX+"chat"))return;

        if (!player.hasPermission("chatterbot.use")) {
            player.sendMessage(color(Config.PREFIX+Config.NO_PERMISSION));
            event.setCancelled(true);
            return;
        }

        if(args.length < 1){
            player.sendMessage(color(Config.PREFIX+Config.CORRECT_USAGE.replace("$command", "chatbot")
                    .replace("$usage", "<setid|reload|question...> [id]")));
            event.setCancelled(true);
            return;
        }

        System.arraycopy(oldArgs, 1, args, 0, args.length);

        if (args.length < 1) {
            player.sendMessage(color(Config.PREFIX+Config.CORRECT_USAGE
                    .replace("$command", "chatbot").replace("$usage", "<setid|reload|question...> [id]")));
            event.setCancelled(true);
            return;
        }

        String action = args[0].toLowerCase();
        if (action.equals("setid")) {

            event.setCancelled(true);

            if (!player.hasPermission("chatterbot.changeid")) {
                player.sendMessage(color(Config.PREFIX+Config.NO_PERMISSION));
                return;
            }

            if (args.length < 2) {
                player.sendMessage(color(Config.PREFIX+Config.CORRECT_USAGE
                        .replace("$command", "chatbot").replace("$usage", "setid <id>")));
                return;
            }

            String id = args[1];

            SessionManager.ChatterBotMode mode = SessionManager.ChatterBotMode.getChatterBotMode(id);
            if (mode == null) {
                player.sendMessage(color(Config.PREFIX+Config.INVALID_BOT_ID
                        .replace("$id", id)));
                return;
            }

            SessionManager.ChatterBotMode.setDefaultMode(mode);

            //If the user already has an existing session, terminate it and create a new one with the new bot id
            if (SessionManager.hasSession(null))
                SessionManager.getSessions().remove(null);

            ChatterBotSession session = SessionManager.getSession(null, true);
            SessionManager.getSessions().put(null, session);

            player.sendMessage(color(Config.PREFIX+Config.CHANGED_BOT_ID
                    .replace("$id", id).replace("$name", mode.name().toLowerCase().replace("_", " "))));
            return;
        }else if (action.equals("reload")){

            event.setCancelled(true);

            if(!player.hasPermission("chatterbot.reload")){
                player.sendMessage(color(Config.PREFIX+Config.NO_PERMISSION));
                return;
            }

            instance.reloadConfig();
            player.sendMessage(color(Config.PREFIX+Config.CONFIGURATION_RELOADED));
            return;
        }

        //Wont return null as we create a session if one doesn't already exist
        final ChatterBotSession session = SessionManager.getSession(null, true);
        final String[] copyOfArgs=args;

        Bukkit.getScheduler().runTaskLater(ChatterBot.getInstance(), new Runnable() {
            @Override
            public void run() {
                String query = ChatColor.stripColor(Util.join(copyOfArgs, ' '));
                try {
                    //strip html format (this is a web api which can return html)
                    String reply = session.think(query).replaceAll("\\<.*?>", "");
                    Bukkit.broadcastMessage(color(Config.PREFIX+Config.BOT_REPLY_SUCCESS_FORMAT
                            .replace("$reply", reply)));
                }
                catch (Exception e) {
                    player.sendMessage(Config.PREFIX+Config.BOT_REPLY_FAILURE_FORMAT);
                    event.setCancelled(true);
                    e.printStackTrace();
                }
            }
        }, 5L);

    }

}
