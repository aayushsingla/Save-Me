package com.codefundo.saveme;

public abstract class KillableRunnable implements Runnable {
    private boolean isKilled = false;

    public abstract void doWork();


    @Override
    public void run() {


        if (!isKilled) {
            doWork();
        }
    }

    final public void restart() {
        isKilled = false;
    }

    final public void kill() {
        isKilled = true;
    }

}
