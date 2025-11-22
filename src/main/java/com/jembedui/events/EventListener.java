package com.jembedui.events;

/**
 * Functional interface for event listeners.
 */
@FunctionalInterface
public interface EventListener<T extends UIEvent> {
    void onEvent(T event);
}
