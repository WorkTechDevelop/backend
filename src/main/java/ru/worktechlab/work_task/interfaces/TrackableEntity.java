package ru.worktechlab.work_task.interfaces;

public interface TrackableEntity<T> extends Cloneable {
    void setPreviousState(T previous);

    T getPreviousState();

    T clone();
}
