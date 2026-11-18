package com.exg.mygame;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/// Starts calls at the same moment, as near as threads allow. A count and a
/// flag, polled: the class library a compiled server links against has the
/// atomics and no latch.
final class Race {
    private Race() {
    }

    static void together(Runnable[] calls) throws Exception {
        final AtomicInteger ready = new AtomicInteger();
        final AtomicBoolean go = new AtomicBoolean();
        Thread[] threads = new Thread[calls.length];
        for (int iter = 0; iter < calls.length; iter++) {
            final Runnable call = calls[iter];
            threads[iter] = new Thread(new Runnable() {
                @Override
                public void run() {
                    ready.incrementAndGet();
                    long giveUp = System.currentTimeMillis() + 30000L;
                    while (!go.get()) {
                        if (System.currentTimeMillis() > giveUp) {
                            return;
                        }
                        Thread.yield();
                    }
                    call.run();
                }
            }, "race-" + iter);
            threads[iter].start();
        }
        while (ready.get() < calls.length) {
            Thread.yield();
        }
        go.set(true);
        for (Thread t : threads) {
            t.join(60000L);
        }
    }
}
