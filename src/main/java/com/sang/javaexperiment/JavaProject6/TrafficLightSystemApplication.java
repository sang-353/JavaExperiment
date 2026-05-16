package com.sang.javaexperiment.JavaProject6;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 十字路口交通灯管理系统启动类。
 */
public class TrafficLightSystemApplication {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws InterruptedException {
        long runtimeSeconds = parseRuntimeSeconds(args);

        try (TrafficLightSystem system = new TrafficLightSystem()) {
            Runtime.getRuntime().addShutdownHook(new Thread(system::stop, "Traffic-Light-ShutdownHook"));

            System.out.println("[" + LocalDateTime.now().format(TIME_FORMATTER) + "] 十字路口交通灯系统启动，预计运行 "
                    + runtimeSeconds + " 秒。");
            system.start();
            Thread.sleep(runtimeSeconds * 1000L);
        } finally {
            System.out.println("[" + LocalDateTime.now().format(TIME_FORMATTER) + "] 十字路口交通灯系统已停止。");
        }
    }

    private static long parseRuntimeSeconds(String[] args) {
        if (args == null || args.length == 0) {
            return 60;
        }
        try {
            long parsed = Long.parseLong(args[0]);
            return parsed > 0 ? parsed : 60;
        } catch (NumberFormatException ex) {
            return 60;
        }
    }
}

