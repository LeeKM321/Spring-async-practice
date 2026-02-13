package com.codeit.async.config;

import org.springframework.core.task.TaskDecorator;

import java.util.List;

public class CompositeTaskDecorator implements TaskDecorator {

    private final List<TaskDecorator> decorators;

    public CompositeTaskDecorator(List<TaskDecorator> decorators) {
        this.decorators = decorators;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        Runnable wrappedRunnable = runnable;

        for (TaskDecorator decorator : decorators) {
            wrappedRunnable = decorator.decorate(wrappedRunnable);
        }

        return wrappedRunnable;
    }


}











