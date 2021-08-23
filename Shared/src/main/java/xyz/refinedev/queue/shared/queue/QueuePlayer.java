package xyz.refinedev.queue.shared.queue;

import xyz.refinedev.queue.emerald.shared.server.EmeraldServer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter @Setter
public class QueuePlayer {

    private final UUID uuid;
    private EmeraldServer server;
    private QueueRank rank;

    private Queue queue;

    /**
     * Create an instance of QueuePlayer
     *
     * @param uuid player's uuid
     */

    public QueuePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * returns a player's priority
     *
     * @return {@link Integer}
     */

    public int getPriority() {
        return rank == null ? 0 : rank.getPriority();
    }
}
