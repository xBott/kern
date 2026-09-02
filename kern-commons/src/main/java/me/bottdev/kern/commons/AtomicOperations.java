package me.bottdev.kern.commons;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicOperations {

    public static void addCap(AtomicLong value, long n) {
        for (;;) {

            long current = value.get();
            long next = current + n;

            if (next < 0L) {
                next = Long.MAX_VALUE;
            }

            if (value.compareAndSet(current, next)) {
                return;
            }

        }
    }


}
