package me.angrypostman.chatterbot.util;

import org.bukkit.ChatColor;

public class Util {

    public static String color(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String join(String[] input, char joiner){
        StringBuilder builder=new StringBuilder();
        for(int i = 0; i < input.length; i++){
            if(i != 0)builder.append(joiner);
            builder.append(input[i]);
        }
        return builder.toString();
    }

}
