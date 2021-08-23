package xyz.refinedev.queue.shared.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter @Setter
public class QueueRank {

    private String name, permission;
    private int priority;
    private boolean isDefault;

    public QueueRank(String name) {
        this.name = name;
    }
}
