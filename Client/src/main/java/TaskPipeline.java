package ru.ifmo.ct.khalansky.coursework.client;
import java.util.function.Supplier;
import java.util.function.Consumer;

class TaskPipeline<R> {

    Supplier<? extends R> task;
    Consumer<? super R> actor;
    long cooldown;

    public TaskPipeline(
        Supplier<? extends R> task,
        Consumer<? super R> actor,
        long cooldown)
    {
        this.task = task;
        this.actor = actor;
        this.cooldown = cooldown;
    }

    public void run(int n) {
        long prev = 0;
        for (int i = 0; i < n; ++i) {
            if (Thread.interrupted()) {
                return;
            }
            R val = task.get();
            long diff = cooldown - (System.nanoTime() - prev);
            if (diff > 0) {
                try {
                    Thread.sleep(diff / 1000000L, (int)(diff % 1000000L));
                } catch (InterruptedException e) {
                    return;
                }
            }
            actor.accept(val);
            prev = System.nanoTime();
        }
    }

}
