package com.myplugin.survival;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class GameListener implements Listener {

    private final SimpleSurvivalGame plugin;

    public GameListener(SimpleSurvivalGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // 플레이어가 사망했을 때 호출됨
        Player deadPlayer = event.getEntity();

        // 사망자에게 관전 모드 부여
        deadPlayer.setGameMode(GameMode.SPECTATOR);
        Bukkit.broadcastMessage(ChatColor.GRAY + deadPlayer.getName() + "님이 생존 게임에서 탈락했습니다.");

        // 1. 생존 플레이어 수 확인
        long remainingPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getGameMode() != GameMode.SPECTATOR) // 관전자가 아닌 플레이어만 계산
                .count();

        // 2. 우승자 확인
        if (remainingPlayers <= 1) {
            // 게임 종료 로직 실행

            Player winner = null;
            if (remainingPlayers == 1) {
                // 남은 한 명의 플레이어가 우승자
                winner = Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getGameMode() != GameMode.SPECTATOR)
                        .findFirst().orElse(null);
            }

            // 스케줄러 정지
            ((StartCommand) plugin.getCommand("startsurvival").getExecutor()).stopScheduler();

            if (winner != null) {
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "=======================================");
                Bukkit.broadcastMessage(ChatColor.GREEN + "🎉 우승! " + ChatColor.YELLOW + winner.getName() + ChatColor.GREEN + "님이 생존 게임에서 승리했습니다!");
                Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "=======================================");
                Bukkit.broadcastMessage(" ");
            } else {
                // 모두 탈락하여 우승자가 없는 경우 (예: 마지막 두 명이 동시에 사망)
                Bukkit.broadcastMessage(ChatColor.RED + "게임이 종료되었습니다. 아쉽게도 생존자는 없습니다.");
            }

            // 모든 플레이어를 다시 크리에이티브 모드나 스폰으로 돌려보내는 추가 로직은 여기에 구현할 수 있습니다.
        }
    }
}