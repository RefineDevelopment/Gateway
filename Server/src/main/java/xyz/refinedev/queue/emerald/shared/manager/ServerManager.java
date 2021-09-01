package xyz.refinedev.queue.emerald.shared.manager;

import com.google.gson.*;
import xyz.refinedev.queue.emerald.shared.SharedEmerald;
import xyz.refinedev.queue.emerald.shared.server.EmeraldServer;
import xyz.refinedev.queue.emerald.shared.server.ServerProperties;
import xyz.refinedev.queue.emerald.shared.server.ServerStatus;
import xyz.refinedev.queue.emerald.shared.util.TPSUtility;
import lombok.Getter;
import xyz.refinedev.queue.emerald.shared.server.EmeraldGroup;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/10/2021
 * Project: Emerald
 */

@Getter
public class ServerManager {

    private final SharedEmerald emerald;

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public ServerManager(SharedEmerald emerald) {
        this.emerald = emerald;
    }

    /**
     * Saves a server to redis cache
     *
     * @param properties data to save
     */

    public void saveServer(ServerProperties properties) {
        CompletableFuture.runAsync(() -> {
            emerald.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                jedis.hset("servers", properties.getUuid().toString(), gson.toJson(properties));
            });
        });
    }

    /**
     * saves a server to redis cache
     *
     * @param server data to save
     */

    public void saveServer(EmeraldServer server) {
        CompletableFuture.runAsync(() -> {
            emerald.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                jedis.hset("servers", server.getUuid().toString(), gson.toJson(server));
            });
        });
    }

    /**
     * gets all server in a list
     *
     * @return {@link CompletableFuture<List>}
     */

    public CompletableFuture<List<EmeraldServer>> getAsList() {
        return CompletableFuture.supplyAsync(() -> {
            final List<EmeraldServer> servers = new ArrayList<>();

            emerald.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                for (final String value : jedis.hgetAll("servers").values()) {
                    if (value == null) continue;

                    EmeraldServer server = gson.fromJson(value, EmeraldServer.class);
                    if (server == null) continue;

                    servers.add(server);
                }
            });

            return servers;
        });
    }

    /**
     * gets a server by the uuid
     *
     * @param uuid uuid of the server
     * @return {@link CompletableFuture<EmeraldServer>}
     */

    public CompletableFuture<EmeraldServer> getByUUID(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final AtomicReference<EmeraldServer> emeraldServer = new AtomicReference<>();

            emerald.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                if (jedis.hexists("servers", uuid.toString())) {
                    emeraldServer.set(gson.fromJson(jedis.hget("servers", uuid.toString()), EmeraldServer.class));
                }
            });

            return emeraldServer.get();
        });
    }

    public CompletableFuture<EmeraldServer> getByPlayer(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
           AtomicReference<EmeraldServer> emeraldServer = new AtomicReference<>();

           getAsList().thenAccept(emeraldServers -> {
               for (EmeraldServer server : emeraldServers) {
                   if (server.getOnlinePlayers().contains(player)) {
                       emeraldServer.set(server);
                       break;
                   }
               }
           });

           return emeraldServer.get();
        });
    }

    /**
     * gets a server matching a name
     *
     * @param name name of the server
     * @return {@link CompletableFuture<EmeraldServer>}
     */

    public CompletableFuture<EmeraldServer> getByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            final AtomicReference<EmeraldServer> emeraldServer = new AtomicReference<>();

            emerald.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                for (final String value : jedis.hgetAll("servers").values()) {
                    if (value == null) continue;

                    final JsonObject object = new JsonParser().parse(value).getAsJsonObject();
                    if (object == null) continue;

                    if (object.get("name").getAsString().equalsIgnoreCase(name)) {
                        emeraldServer.set(gson.fromJson(value, EmeraldServer.class));
                        break;
                    }
                }
            });

            return emeraldServer.get();
        });
    }

    /**
     * gets a server matching the ip and port
     *
     * @param ip ip of the server
     * @param port port of the server
     * @return {@link CompletableFuture<EmeraldServer>}
     */

    public CompletableFuture<EmeraldServer> getByConnection(String ip, int port) {
        return CompletableFuture.supplyAsync(() -> {
            final AtomicReference<EmeraldServer> emeraldServer = new AtomicReference<>();

            emerald.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                for (final String value : jedis.hgetAll("servers").values()) {
                    if (value == null) {
                        continue;
                    }

                    final EmeraldServer server = gson.fromJson(value, EmeraldServer.class);
                    if (server == null) {
                        continue;
                    }

                    if (server.getIp().equalsIgnoreCase(ip) && server.getPort() == port) {
                        emeraldServer.set(server);
                        break;
                    }
                }
            });

            return emeraldServer.get();
        });
    }



}