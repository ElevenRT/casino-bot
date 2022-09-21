package com.eleven.casinobot.core.command;

import com.eleven.casinobot.core.annotations.EventHandler;
import com.eleven.casinobot.core.context.ComponentContextSingleton;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;
import java.util.stream.Collectors;

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

    public static void invoke(Object caller, final String value, CommandContext ctx) {
        Class<?> callerClass = caller.getClass();
        load(callerClass);
        invokeCommand(callerClass, value, ctx);
    }

    private static void load(Class<?> caller) {
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
                                      CommandContext ctx) {
        Set<ICommand> commands = listenerCommands.getOrDefault(caller, new HashSet<>());
        for (ICommand command : commands) {
            Command cmd = command.getClass().getAnnotation(Command.class);
            if (cmd.value().equals(value)) {
                command.onEvent(ctx);
                break;
            }
        }
    }

    public static Set<Command> commandValues() {
        return Commands.keySet().stream().map(aClass ->
                        aClass.getAnnotation(Command.class))
                .collect(Collectors.toSet());
    }
}
