package com.eleven.casinobot.core.command;

/**
 * The ICommand interface is the interface that each command class must implement.
 * Process commands with CommandContext as a factor value.
 * Exceptionally, there is only one abstract method, but with the possibility
 * that abstract methods will be added in the future.
 * FunctionalInterface has not been added.
 * @see CommandContext
 * @author iqpizza6349
 */
public interface ICommand {

    /**
     * method where the ability to perform commands should be implemented
     * @param ctx Information about the event that occurred and its associated information
     */
    void onEvent(ICommandContext ctx);
}
