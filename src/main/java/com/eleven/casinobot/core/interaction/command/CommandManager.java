package com.eleven.casinobot.core.interaction.command;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.annotations.EventHandler;
import com.eleven.casinobot.core.context.ComponentContextSingleton;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A CommandManager class is a manager class that calls command classes and calls
 * specific methods when an event occurs. The EventListener annotation dynamically
 * stores and invokes the command classes specified in the commands array.
 * @see Command
 * @see EventListener
 * @see ComponentContextSingleton
 * @see ICommand
 * @author iqpizza6349
 */
public final class CommandManager {

    private static Map<Class<?>, ?> Commands;
    private static final Map<Class<?>, Set<ICommand>> listenerCommands;

    static {
        listenerCommands = new HashMap<>();
        try {
            Commands = ComponentContextSingleton.getInstance().getAllCommands();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * The instruction classes are dynamically stored and the same instruction (method)
     * is called among the currently existing instructions.
     * @param caller To dynamically save and recall commands to
     * @param value The value corresponding to the command. ex) /ping -> value is 'ping'
     * @param ctx Occurred Event
     * @see CommandContext
     */
    public static void invoke(Object caller, final String value, ICommandContext ctx) {
        Class<?> callerClass = caller.getClass();
        load(callerClass);
        invokeCommand(callerClass, value, ctx);
    }

    private static void load(Class<?> caller) {
        // already add all commands
        if (listenerCommands.get(caller) != null) {
            return;
        }

        if (caller.getSuperclass() != ListenerAdapter.class
                || Arrays.stream(caller.getInterfaces()).anyMatch(clazz -> clazz == EventListener.class)) {
            throw new RuntimeException("Caller must extends ListenerAdapter.class" +
                    " or implements EventListener.class");
        }

        if (!caller.isAnnotationPresent(EventHandler.class)) {
            throw new RuntimeException("Caller must have EventHandler annotation");
        }

        Class<? extends ICommand>[] commands = caller.getAnnotation(EventHandler.class)
                .commands();
        for (Class<? extends ICommand> command : commands) {
            listenerCommands.putIfAbsent(caller, new LinkedHashSet<>());
            for (Map.Entry<Class<?>, ?> classEntry : Commands.entrySet()) {
                if (classEntry.getKey() != command) {
                    continue;
                }

                listenerCommands.get(caller).add((ICommand) classEntry.getValue());
            }
        }
    }

    private static void invokeCommand(Class<?> caller, String value,
                                      ICommandContext ctx) {
        Set<ICommand> commands = listenerCommands.getOrDefault(caller, new HashSet<>());
        for (ICommand command : commands) {
            Command cmd = command.getClass().getAnnotation(Command.class);
            if (cmd.value().equals(value)) {
                command.onEvent(ctx);
                break;
            }
        }
    }

    /**
     * Returns a collection of value data in Command Annotation in command classes.
     * These aggregates are not duplicated. If redundancy occurs to avoid duplication,
     * an exception occurs.
     * @see Command
     * @return Returns a collection of value data in Command Annotation in command classes.
     */
    public static Set<Command> commandValues() {
        return Commands.keySet().stream().map(aClass ->
                        aClass.getAnnotation(Command.class))
                .collect(Collectors.toSet());
    }
}
