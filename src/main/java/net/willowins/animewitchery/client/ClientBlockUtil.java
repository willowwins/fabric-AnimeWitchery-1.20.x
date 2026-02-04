package net.willowins.animewitchery.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.willowins.animewitchery.block.entity.ServerButtonBlockEntity;
import net.willowins.animewitchery.block.entity.WorldButtonBlockEntity;
import net.willowins.animewitchery.config.MenuWorldConfig;
import net.willowins.animewitchery.networking.WorldButtonSyncPacket;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientBlockUtil {

    private static String cachedTargetWorld = null;

    public static String getCachedTargetWorld() {
        return cachedTargetWorld;
    }

    public static void clearCachedTargetWorld() {
        System.out.println("[WorldButton] Step 5: Clearing cache");
        cachedTargetWorld = null;
    }

    public static void openWorldSelectScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            System.out.println("[WorldButton] Opening world select screen...");
            System.out.println("[WorldButton] Current screen: " + client.currentScreen);
            client.execute(() -> {
                client.setScreen(new SelectWorldScreen(new TitleScreen()));
                System.out.println("[WorldButton] World select screen opened");
            });
        }
    }

    public static void cycleWorldClientSide(BlockPos pos) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.world == null)
                return;

            String menuWorldName = MenuWorldConfig.getMenuWorldName();
            System.out.println("[WorldButton] Menu world to exclude: '" + menuWorldName + "'");

            net.minecraft.world.level.storage.LevelStorage levelStorage = client.getLevelStorage();
            File savesDir = levelStorage.getSavesDirectory().toFile();

            List<String> worldNames = new ArrayList<>();
            File[] worldFolders = savesDir.listFiles();
            if (worldFolders != null) {
                for (File folder : worldFolders) {
                    String worldName = folder.getName();
                    if (folder.isDirectory() && new File(folder, "level.dat").exists()) {
                        if (menuWorldName != null && (worldName.equals(menuWorldName)
                                || worldName.contains(menuWorldName) || menuWorldName.contains(worldName))) {
                            System.out.println("[WorldButton] Excluding menu world: '" + worldName + "'");
                            continue;
                        }
                        worldNames.add(worldName);
                    }
                }
            }

            if (worldNames.isEmpty()) {
                System.out.println("[WorldButton] No non-menu worlds available");
                return;
            }

            Collections.sort(worldNames);

            String currentWorld = "";
            if (client.world.getBlockEntity(pos) instanceof WorldButtonBlockEntity be) {
                currentWorld = be.getWorldName();
            }

            int currentIndex = worldNames.indexOf(currentWorld);
            int nextIndex = (currentIndex + 1) % worldNames.size();
            String nextWorld = worldNames.get(nextIndex);

            WorldButtonSyncPacket.sendSetWorld(pos, nextWorld);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadWorldImmediately(String worldName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null)
            return;

        System.out.println("[WorldButton] Immediate load requested: " + worldName);

        client.execute(() -> {
            System.out.println("[WorldButton] Step 1: Opening game menu to stop input");
            client.setScreen(new GameMenuScreen(true));
            new Thread(() -> {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.execute(() -> {
                    System.out.println("[WorldButton] Step 2: Loading world from game menu");
                    loadWorldSafely(worldName);
                });
            }).start();
        });
    }

    private static void loadWorldSafely(String worldName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null)
            return;

        System.out.println("[WorldButton] Loading world: " + worldName);

        if (client.getServer() != null) {
            String currentWorld = client.getServer().getSaveProperties().getLevelName();
            String menuWorld = MenuWorldConfig.getMenuWorldName();

            System.out.println("[WorldButton] Current: '" + currentWorld + "', Menu: '" + menuWorld + "'");

            if (menuWorld == null || currentWorld.equals(menuWorld) || currentWorld.contains(menuWorld)
                    || menuWorld.contains(currentWorld)) {
                System.out.println("[WorldButton] Allowed to load world");

                if (client.player != null && client.getServer() != null) {
                    ServerWorld world = client.getServer().getOverworld();
                    BlockPos spawnPos = world.getSpawnPos();
                    float spawnAngle = world.getSpawnAngle();
                    client.player.setPosition((double) spawnPos.getX(), (double) spawnPos.getY(),
                            (double) spawnPos.getZ());
                    client.player.setYaw(spawnAngle);
                    client.player.setPitch(0.0f);
                    System.out.println("[WorldButton] Teleported to spawn");
                }

                System.out.println("[WorldButton] Disconnecting and loading world...");
                client.world.disconnect();
                client.disconnect();

                client.execute(() -> {
                    client.setScreen(new net.willowins.animewitchery.client.IntermediaryScreen());
                    System.out.println("[WorldButton] Intermediary screen shown");

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }

                    client.execute(() -> {
                        try {
                            System.out.println("[WorldButton] Creating world loader...");
                            client.createIntegratedServerLoader().start(client.currentScreen, worldName);
                            System.out.println("[WorldButton] World load started");
                        } catch (Exception e) {
                            System.out.println("[WorldButton] Error loading world: " + e.getMessage());
                            e.printStackTrace();
                            client.setScreen(new TitleScreen());
                        }
                    });
                });
            } else {
                System.out.println("[WorldButton] Not in menu world - only showing name");
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§cCan only load worlds from menu world!"), false);
                }
            }
        }
    }

    public static void openMultiplayerScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.execute(() -> {
                client.setScreen(new MultiplayerScreen(client.currentScreen));
            });
        }
    }

    public static void cycleServerClientSide(BlockPos pos) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null)
                return;

            ServerList serverList = new ServerList(client);
            serverList.loadFile();

            if (serverList.size() == 0)
                return;

            String currentIP = "";
            if (client.world != null && client.world.getBlockEntity(pos) instanceof ServerButtonBlockEntity be) {
                currentIP = be.getServerIP();
            }

            int currentIndex = -1;
            for (int i = 0; i < serverList.size(); i++) {
                if (serverList.get(i).address.equals(currentIP)) {
                    currentIndex = i;
                    break;
                }
            }

            int nextIndex = (currentIndex + 1) % serverList.size();
            ServerInfo serverInfo = serverList.get(nextIndex);

            WorldButtonSyncPacket.sendSetServer(pos, serverInfo.name, serverInfo.address);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connectToServerImmediately(String serverName, String serverIP) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null)
            return;

        System.out.println("[ServerButton] Immediate connection requested: " + serverIP);

        client.execute(() -> {
            System.out.println("[ServerButton] Step 1: Opening game menu to stop input");
            client.setScreen(new GameMenuScreen(true));

            new Thread(() -> {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                client.execute(() -> {
                    System.out.println("[ServerButton] Step 2: Disconnecting from current world");
                    if (client.world != null) {
                        client.world.disconnect();
                    }
                    client.disconnect();

                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        client.execute(() -> {
                            System.out.println("[ServerButton] Step 3: Connecting to server: " + serverIP);
                            ServerInfo serverInfo = new ServerInfo(
                                    serverName != null && !serverName.isEmpty() ? serverName : serverIP,
                                    serverIP,
                                    false);

                            ConnectScreen.connect(new TitleScreen(), client, ServerAddress.parse(serverIP), serverInfo,
                                    false);
                            System.out.println("[ServerButton] Connection initiated");
                        });
                    }).start();
                });
            }).start();
        });
    }
}
