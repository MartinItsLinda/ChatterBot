package me.angrypostman.chatterbot;

public class Config {

    private static ChatterBot instance=ChatterBot.getInstance();

    public static String COMMAND_PREFIX=instance.getConfig().getString("command_prefix").charAt(0)=='/' ? "-" : instance.getConfig().getString("command_prefix");
    public static String PREFIX=instance.getConfig().getString("messages.prefix").trim()+" ";
    public static String NO_PERMISSION=instance.getConfig().getString("messages.error.no_permission");
    public static String CORRECT_USAGE=instance.getConfig().getString("messages.error.correct_usage");
    public static String CHANGED_BOT_ID=instance.getConfig().getString("messages.action.set_id.success");
    public static String INVALID_BOT_ID=instance.getConfig().getString("messages.action.set_id.failure");
    public static String BOT_REPLY_SUCCESS_FORMAT=instance.getConfig().getString("messages.action.bot_reply.success");
    public static String BOT_REPLY_FAILURE_FORMAT=instance.getConfig().getString("messages.action.bot_reply.failure");
    public static String CONFIGURATION_RELOADED=instance.getConfig().getString("messages.action.reload.success");
    public static boolean IS_GLOBAL_BOT=instance.getConfig().getBoolean("is_global_bot");

}
