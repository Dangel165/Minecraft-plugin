package com.myplugin.survival;

import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSurvivalGame extends JavaPlugin {

    // 플러그인 인스턴스를 저장하여 다른 클래스에서 쉽게 접근 가능하도록 합니다.
    private static SimpleSurvivalGame instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Simple Survival Game 플러그인이 활성화되었습니다!");

        // 1. 커맨드 등록: /startsurvival 명령어를 StartCommand 클래스로 연결
        getCommand("startsurvival").setExecutor(new StartCommand(this));

        // 2. 이벤트 리스너 등록: GameListener 클래스를 통해 이벤트 처리
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Simple Survival Game 플러그인이 비활성화되었습니다.");
    }

    // 인스턴스를 가져오는 정적 메서드
    public static SimpleSurvivalGame getInstance() {
        return instance;
    }
}