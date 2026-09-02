package me.bottdev.kern.commons;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntFunction;

public class AtomicArray<T> {

    private final AtomicReference<T[]> arrayRef;
    private final IntFunction<T[]> arrayFactory;

    public AtomicArray(IntFunction<T[]> arrayFactory) {
        this.arrayFactory = arrayFactory;
        this.arrayRef = new AtomicReference<>(arrayFactory.apply(0));
    }

    public T[] get() {
        return arrayRef.get();
    }

    public boolean isEmpty() {
        return arrayRef.get().length == 0;
    }

    public int length() {
        return arrayRef.get().length;
    }

    /// Atomic addition of element (CAS loop)
    public void add(T value) {
        for (;;) {
            T[] current = arrayRef.get();
            int n = current.length;
            T[] next = arrayFactory.apply(n + 1);
            
            System.arraycopy(current, 0, next, 0, n);
            next[n] = value;

            if (arrayRef.compareAndSet(current, next)) {
                return;
            }
        }
    }

    /// Atomic deletion of element (CAS loop)
    public void remove(T value) {
        for (;;) {
            T[] current = arrayRef.get();
            int n = current.length;
            if (n == 0) return;

            int index = -1;
            for (int i = 0; i < n; i++) {
                if (current[i] == value) {
                    index = i;
                    break;
                }
            }

            if (index < 0) return;

            T[] next;
            if (n == 1) {
                next = arrayFactory.apply(0);
            } else {
                next = arrayFactory.apply(n - 1);
                System.arraycopy(current, 0, next, 0, index);
                System.arraycopy(current, index + 1, next, index, n - index - 1);
            }

            if (arrayRef.compareAndSet(current, next)) {
                return;
            }
        }
    }

    /// Atomic array clear
    public void clear() {
        arrayRef.set(arrayFactory.apply(0));
    }

}