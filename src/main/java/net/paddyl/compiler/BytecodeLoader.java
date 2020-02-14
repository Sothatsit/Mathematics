package net.paddyl.compiler;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Methods to load the bytecode of objects.
 */
public class BytecodeLoader {

    public static void readClassBytecode(Class<?> clazz, ClassVisitor visitor) throws IOException {
        try (InputStream is = clazz.getResourceAsStream(clazz.getSimpleName() + ".class")) {
            ClassReader reader = new ClassReader(is);
            reader.accept(visitor, 0);
        }
    }
}
