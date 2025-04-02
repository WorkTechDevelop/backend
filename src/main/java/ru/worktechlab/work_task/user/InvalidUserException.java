package ru.worktechlab.work_task.user;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String message) {
        super(message);
    }
}
