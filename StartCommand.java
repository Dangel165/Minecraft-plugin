package com.myplugin.survival;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.Random;

public class StartCommand implements CommandExecutor {

    private final SimpleSurvivalGame plugin;
    private BukkitTask eventScheduler;
    private final int RADIUS = 500; // 랜덤 텔레포트 반경
    private final Random random = new Random();

    public StartCommand(SimpleSurvivalGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "이 명령어는 OP 권한을 가진 플레이어만 사용할 수 있습니다.");
            return true;
        }

        World world = ((Player) sender).getWorld();
        int playersCount = 0;

        // 1. 플레이어 랜덤 텔레포트
        for (Player player : Bukkit.getOnlinePlayers()) {
            // 무작위 좌표 생성 (중앙 0,0에서 반경 RADIUS 이내)
            int x = random.nextInt(2 * RADIUS) - RADIUS;
            int z = random.nextInt(2 * RADIUS) - RADIUS;
            // 월드 최고 높이(Y)를 찾기 (낙사 방지)
            int y = world.getHighestBlockYAt(x, z);

            Location randomLoc = new Location(world, x + 0.5, y + 1, z + 0.5);
            player.teleport(randomLoc);
            player.sendMessage(ChatColor.GREEN + "랜덤 생존 게임이 시작됩니다! 무작위 위치로 텔레포트되었습니다.");
            playersCount++;
        }

        if (playersCount == 0) {
            sender.sendMessage(ChatColor.YELLOW + "접속 중인 플레이어가 없습니다.");
            return true;
        }

        // 2. 30초마다 반복되는 이벤트 스케줄러 시작
        // 20틱(Tick) = 1초, 30초 = 600틱
        eventScheduler = Bukkit.getScheduler().runTaskTimer(plugin, this::runRandomEvent, 600L, 600L);
        sender.sendMessage(ChatColor.AQUA + "게임을 시작했으며, 30초마다 무작위 이벤트가 발생합니다!");

        return true;
    }

    // 3. 무작위 이벤트 실행 로직 (스케줄러에 의해 호출됨)
    private void runRandomEvent() {
        int eventType = random.nextInt(3); // 0, 1, 2 중 하나 선택

        String message = "";

        for (Player player : Bukkit.getOnlinePlayers()) {
            switch (eventType) {
                case 0:
                    // 번개 효과 (피해 없음)
                    player.getWorld().strikeLightningEffect(player.getLocation());
                    message = ChatColor.YELLOW + "경고! 번개가 주변에 강타합니다!";
                    break;
                case 1:
                    // 독 효과 (잠시 동안)
                    player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.POISON, 100, 0)); // 5초간 독 1
                    message = ChatColor.DARK_GREEN + "주변 환경이 당신에게 독을 주입합니다!";
                    break;
                case 2:
                    // 무작위 음식 지급
                    player.getInventory().addItem(org.bukkit.Material.COOKED_BEEF.asQuantity(5));
                    message = ChatColor.GOLD + "하늘에서 고기가 떨어졌습니다! 생존에 이용하세요.";
                    break;
            }
        }

        // 전체 서버에 이벤트 메시지 전송
        Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "[생존 이벤트] " + message);
    }

    // 외부에서 스케줄러를 취소할 수 있도록 메서드 제공 (게임 종료 시 사용)
    public void stopScheduler() {
        if (eventScheduler != null && !eventScheduler.isCancelled()) {
            eventScheduler.cancel();
        }
    }
}