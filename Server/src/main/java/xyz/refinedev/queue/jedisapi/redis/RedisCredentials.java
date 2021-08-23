package xyz.refinedev.queue.jedisapi.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This Project is property of RefineDevelopment © 2021
 *
 * @author Zowpy
 * Created: 8/10/2021
 * Project: JedisAPI
 */

@Getter @Setter @AllArgsConstructor
public class RedisCredentials {

    private String host, password, channel;
    private int port;
    private boolean auth;
}
