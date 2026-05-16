package com.sang.javaexperiment.JavaProject6;

import lombok.Getter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xie ying
 * AI coding by GitHub Copilot GPT-5.2
 * 交通信号灯，使用AtomicBoolean表示红绿状态。
 */
@Getter
public class Lighter {

    private final String name;
    private final AtomicBoolean active;

    public Lighter(String name) {
        this.name = name;
        this.active = new AtomicBoolean(false);
    }

    public boolean isGreen() {
        return active.get();
    }

    public void turnGreen() {
        active.set(true);
    }

    public void turnRed() {
        active.set(false);
    }

    @Override
    public String toString() {
        return "Lighter{" +
                "name='" + name + '\'' +
                ", active=" + (active.get() ? "GREEN" : "RED") +
                '}';
    }
}

