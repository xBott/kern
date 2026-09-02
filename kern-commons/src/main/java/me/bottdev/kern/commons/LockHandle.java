package me.bottdev.kern.commons;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public final class LockHandle implements AutoCloseable {

    private final List<ReentrantLock> locks;

    @Override
    public void close() {
        for (int i = locks.size() - 1; i >= 0; i--) {
            locks.get(i).unlock();
        }
    }

}