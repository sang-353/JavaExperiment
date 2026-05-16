package com.sang.javaexperiment.JavaProject6;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 十字路口交通灯管理系统：负责组装信号灯、路线和控制器。
 */
@Getter
public class TrafficLightSystem implements AutoCloseable {

    private final Lighter northSouthStraightLight = new Lighter("南北直行绿灯");
    private final Lighter northSouthLeftLight = new Lighter("南北左转绿灯");
    private final Lighter eastWestStraightLight = new Lighter("东西直行绿灯");
    private final Lighter eastWestLeftLight = new Lighter("东西左转绿灯");

    private final LightController lightController = new LightController(
            List.of(northSouthStraightLight, northSouthLeftLight, eastWestStraightLight, eastWestLeftLight),
            10
    );

    private final List<Road> roads;

    public TrafficLightSystem() {
        List<Road> roadList = new ArrayList<>();

        // 直行路线（受灯控制）
        roadList.add(new Road("S2N", northSouthStraightLight));
        roadList.add(new Road("N2S", northSouthStraightLight));
        roadList.add(new Road("E2W", eastWestStraightLight));
        roadList.add(new Road("W2E", eastWestStraightLight));

        // 左转路线（受灯控制）
        roadList.add(new Road("S2W", northSouthLeftLight));
        roadList.add(new Road("N2E", northSouthLeftLight));
        roadList.add(new Road("E2S", eastWestLeftLight));
        roadList.add(new Road("W2N", eastWestLeftLight));

        // 右转路线（常绿灯，不受控）
        roadList.add(new Road("S2E", null));
        roadList.add(new Road("N2W", null));
        roadList.add(new Road("E2N", null));
        roadList.add(new Road("W2S", null));

        this.roads = Collections.unmodifiableList(roadList);
    }

    public void start() {
        lightController.start();
        for (Road road : roads) {
            road.start();
        }
    }

    public void stop() {
        for (Road road : roads) {
            road.stop();
        }
        lightController.stop();
    }

    @Override
    public void close() {
        stop();
    }
}

