package com.wf.gts.core.concurrent;

public interface RejectedRunnable extends Runnable {
    void rejected();
}
