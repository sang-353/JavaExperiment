package com.sang.javaexperiment.JavaProject6;

import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 路线对象：负责模拟车辆进入路口与通过路口（使用高并发无锁队列 + 同步日志锁）。
 */
@Getter
public class Road implements AutoCloseable {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String name;
    private final Lighter lighter;
    private final ConcurrentLinkedQueue<String> vehicles = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong vehicleSequence = new AtomicLong(0);
    private final ScheduledExecutorService passingExecutor;
    private final Object logLock = new Object();

    private Thread producerThread;

    public Road(String name, Lighter lighter) {
        this.name = name;
        this.lighter = lighter;
        this.passingExecutor = Executors.newSingleThreadScheduledExecutor(createThreadFactory());
    }

    public int getWaitingVehicleCount() {
        return vehicles.size();
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        startVehicleProducer();
        startVehiclePassingTask();
    }

    private void startVehicleProducer() {
        producerThread = new Thread(() -> {
            while (running.get()) {
                try {
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 11));
                    if (!running.get()) {
                        break;
                    }
                    addVehicle();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Road-" + name + "-producer");
        producerThread.setDaemon(true);
        producerThread.start();
    }

    private void startVehiclePassingTask() {
        passingExecutor.scheduleAtFixedRate(() -> {
            if (!running.get()) {
                return;
            }
            tryPassVehicle();
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void addVehicle() {
        String vehicleName = name + '_' + vehicleSequence.incrementAndGet();
        // 使用锁把入队和打印日志保护起来，变成一个原子操作
        synchronized (logLock) {
            vehicles.offer(vehicleName);
            int queueSize = vehicles.size();
            log("车辆进入路口：" + vehicleName + "，当前等待数=" + queueSize);
        }
    }

    public void tryPassVehicle() {
        if (!canPass()) {
            return;
        }
        String vehicleName;
        int remaining;

        // 放车时同样使用这把锁
        synchronized (logLock) {
            vehicleName = vehicles.poll();
            if (vehicleName == null) {
                return;
            }
            remaining = vehicles.size();
            log(vehicleName + " 正在通过路口，剩余等待数=" + remaining);
        }
    }

    private boolean canPass() {
        return lighter == null || lighter.isGreen();
    }

    private void log(String message) {
        System.out.println("[" + LocalTime.now().format(TIME_FORMATTER) + "] "
                + "[" + name + "] " + message);
    }

    private ThreadFactory createThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable, "Road-" + name + "-passer");
            thread.setDaemon(true);
            return thread;
        };
    }

    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        if (producerThread != null) {
            producerThread.interrupt();
        }
        passingExecutor.shutdownNow();
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    public String toString() {
        return "Road{" +
                "name='" + name + '\'' +
                ", lighter=" + lighter +
                ", waitingVehicles=" + getWaitingVehicleCount() +
                '}';
    }
}
