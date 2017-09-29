package com.jjh.parkinseoul.utils;

import com.squareup.otto.Bus;

/**
 * Created by JJH on 2016-09-20.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}