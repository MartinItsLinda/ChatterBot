package me.angrypostman.chatterbot.manager;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static Map<UUID, ChatterBotSession> sessions=new HashMap<>();
    private static Map<UUID, ChatterBotMode> modes=new HashMap<>();
    private static ChatterBotFactory factory=new ChatterBotFactory();

    public static Map<UUID, ChatterBotSession> getSessions() {
        return sessions;
    }

    public static Map<UUID, ChatterBotMode> getModes() {
        return modes;
    }

    public static boolean hasSession(UUID uuid){
        return sessions.containsKey(uuid);
    }

    public static ChatterBotSession getSession(UUID uuid){
        return getSession(uuid, false, ChatterBotMode.getDefaultMode());
    }

    public static ChatterBotSession getSession(UUID uuid, boolean createIfNotFound){
        return getSession(uuid, createIfNotFound, ChatterBotMode.getDefaultMode());
    }

    public static ChatterBotSession getSession(UUID uuid, boolean createIfNotFound, ChatterBotMode mode){
        if(hasSession(uuid))return getSession(uuid);

        if(createIfNotFound) {
            ChatterBotSession session = mode.getChatterBot().createSession();
            sessions.put(uuid == null ? null : uuid, session);
            return session;
        }

        return null;
    }

    public enum ChatterBotMode{

        CHOMSKY("b0dafd24ee35a477"),
        LEVI("b8c97d77ae3471d8"),
        FARHA("dbf443e58e345c14"),
        ROZA("c9c4b9bf6e345c25"),
        JERVIS("9efbc6c80e345e65"),
        LISA("b0a6a41a5e345c23"),
        BOYFRIEND("94023160ee3425e0"),
        LAUREN("f6d4afd83e34564d"),
        LAILA("a66718a38e345c15"),
        SANTAS_ROBOT("c39a3375ae34d985");

        private String key;
        private ChatterBot bot;
        private static ChatterBotMode defaultMode=ChatterBotMode.BOYFRIEND;

        ChatterBotMode(String key) {
            try {
                this.key = key;
                this.bot = SessionManager.factory.create(ChatterBotType.PANDORABOTS, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public String getKey(){
            return key;
        }

        public ChatterBot getChatterBot() {
            return bot;
        }

        public static ChatterBotMode getDefaultMode() {
            return defaultMode;
        }

        public static void setDefaultMode(ChatterBotMode defaultMode) {
            ChatterBotMode.defaultMode = defaultMode;
        }

        public static ChatterBotMode getChatterBotMode(String key){
            for(ChatterBotMode mode:values()){
                if(mode.getKey().equals(key)){
                    return mode;
                }
            }
            return null;
        }

    }

}
