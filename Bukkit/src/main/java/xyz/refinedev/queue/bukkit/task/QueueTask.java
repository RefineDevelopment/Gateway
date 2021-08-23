package xyz.refinedev.queue.bukkit.task;

import xyz.refinedev.queue.emerald.shared.server.ServerStatus;
import xyz.refinedev.queue.bukkit.Locale;
import xyz.refinedev.queue.bukkit.QueuePlugin;
import xyz.refinedev.queue.bukkit.util.JsonChain;
import xyz.refinedev.queue.shared.queue.Queue;
import xyz.refinedev.queue.shared.queue.QueuePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/17/2021
 * Project: Gateway
 */

public class QueueTask extends BukkitRunnable {

    public QueueTask() {
        this.runTaskTimerAsynchronously(QueuePlugin.getInstance(), 0L, QueuePlugin.getInstance().getDelay() * 20L);
    }

    @Override
    public void run() {

        for (Queue queue : QueuePlugin.getInstance().getSharedQueue().getQueueManager().getQueues()) {
            if (queue.getPlayers().isEmpty()) continue;


            for (QueuePlayer queuePlayer : queue.getPlayers()) {
                Player player = Bukkit.getPlayer(queuePlayer.getUuid());

                if (player != null) {
                    for (String s : Locale.QUEUE_REMINDER.getMessageList()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("<pos>", queue.getPosition(queuePlayer) + "").replace("<total>", queue.getPlayers().size() + "")
                                .replace("<bukkit>", queue.getName())));
                    }
                }
            }

            if (queue.isPaused()) continue;
            if (queue.getServer() == null) continue;
            if (queue.getServer().getStatus() == ServerStatus.OFFLINE) continue;
            if (queue.getServer().getOnlinePlayers().size() >= queue.getServer().getMaxPlayers()) continue;

            QueuePlayer queuePlayer = queue.getPlayers().poll();

            if (queuePlayer == null) continue;
            if (queuePlayer.getServer().getUuid().equals(queue.getServer().getUuid())) {
                queue.getPlayers().remove(queuePlayer);
                continue;
            }

            if (queue.getServer().getStatus() == ServerStatus.WHITELISTED && !queue.getServer().getWhitelistedPlayers().contains(queuePlayer.getUuid())) continue;



            JsonChain jc = new JsonChain()
                    .addProperty("uuid", queuePlayer.getUuid().toString())
                    .addProperty("delay", true)
                    .addProperty("server", queue.getBungeeCordName())
                    .addProperty("message", Locale.SEND_PLAYER.getMessage().replace("<server>", queue.getBungeeCordName()));

            QueuePlugin.getInstance().getSharedEmerald().getJedisAPI().getJedisHandler().write("send###" + jc.getAsJsonObject().toString());

            }




    }
}
