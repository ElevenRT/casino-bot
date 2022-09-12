package com.eleven.casinobot.config.scanner;

import javassist.bytecode.ClassFile;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.Map;

public class ReflectionScanner implements Scanner {
    private final Scanner scanner = Scanners.SubTypes.filterResultsBy(s -> true);

    @Override
    public String index() {
        return scanner.index();
    }

    @Override
    public List<Map.Entry<String, String>> scan(ClassFile classFile) {
        return scanner.scan(classFile);
    }
}
