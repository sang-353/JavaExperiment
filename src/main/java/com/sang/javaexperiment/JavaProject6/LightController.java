package com.sang.javaexperiment.JavaProject6;

import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 信号灯控制器：按固定顺序切换绿灯。
 */
@Getter
public class LightController implements AutoCloseable {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<Lighter> lights;
    private final long phaseDurationSeconds;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger currentPhaseIndex = new AtomicInteger(-1);

    public LightController(List<Lighter> lights, long phaseDurationSeconds) {
        if (lights == null || lights.isEmpty()) {
            throw new IllegalArgumentException("lights must not be empty");
        }
        if (phaseDurationSeconds <= 0) {
            throw new IllegalArgumentException("phaseDurationSeconds must be positive");
        }
        this.lights = Collections.unmodifiableList(new ArrayList<>(lights));
        this.phaseDurationSeconds = phaseDurationSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "Traffic-Light-Controller");
            thread.setDaemon(true);
            return thread;
        });
    }


    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        applyPhase(0);
        scheduler.scheduleAtFixedRate(this::advancePhase, phaseDurationSeconds, phaseDurationSeconds, TimeUnit.SECONDS);
    }

    private void advancePhase() {
        if (!running.get()) {
            return;
        }
        int nextIndex = (currentPhaseIndex.get() + 1) % lights.size();
        applyPhase(nextIndex);
    }

    private void applyPhase(int phaseIndex) {
        currentPhaseIndex.set(phaseIndex);
        for (int i = 0; i < lights.size(); i++) {
            if (i == phaseIndex) {
                lights.get(i).turnGreen();
            } else {
                lights.get(i).turnRed();
            }
        }
        log("当前放行方向：" + lights.get(phaseIndex).getName());
    }

    private void log(String message) {
        System.out.println("[" + LocalTime.now().format(TIME_FORMATTER) + "] [信号灯控制器] " + message);
    }

    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        for (Lighter lighter : lights) {
            lighter.turnRed();
        }
        scheduler.shutdownNow();
    }

    @Override
    public void close() {
        stop();
    }
}

