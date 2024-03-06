package com.bawnorton.impirium;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImPIriumAgent implements LanguageAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger("ImPIriumAgent");

    private static final List<String> protectedPackages = List.of(
            "org/spongepowered",
            "java",
            "javax",
            "sun",
            "com/sun",
            "jdk",
            "org/objectweb/asm",
            "com/bawnorton/impirium"
    );

    static {
        ByteBuddyAgent.install();

        ByteBuddyAgent.getInstrumentation().addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if(protectedPackages.stream().anyMatch(className::startsWith)) {
                    return classfileBuffer;
                }
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected String getCommonSuperClass(String type1, String type2) {
                        return getCommonSuperClassViaParse(type1, type2);
                    }
                };
                ClassNode node = new ClassNode(Opcodes.ASM9);
                reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

                PiReplacerClassVisitor piReplacerClassVisitor = new PiReplacerClassVisitor(writer);
                node.accept(piReplacerClassVisitor);

                if(piReplacerClassVisitor.transformed) {
                    LOGGER.info("[Imπrium] Transformed class " + className);
                    try {
                        Path path = FabricLoader.getInstance().getGameDir().resolve(".transformed-classes").resolve(className + ".class");
                        Files.createDirectories(path.getParent());
                        Files.write(path, writer.toByteArray());
                    } catch (IOException e) {
                        LOGGER.error("Failed to write transformed class", e);
                    }
                    return writer.toByteArray();
                }

                return classfileBuffer;
            }
        });

        try {
            Instrumentation instrumentation = ByteBuddyAgent.getInstrumentation();
            if(instrumentation.isRetransformClassesSupported()) {
                instrumentation.retransformClasses(instrumentation.getAllLoadedClasses());
            } else {
                LOGGER.error("Retransform classes is not supported");
            }
        } catch (UnmodifiableClassException ignored) {}
    }

    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        throw new Error();
    }

    private static String getCommonSuperClassViaParse(String type1, String type2) {
        Set<String> superClasses = new HashSet<>();

        try {
            String currentType = type1;
            while (currentType != null) {
                superClasses.add(currentType);
                ClassNode cn = new ClassNode(Opcodes.ASM9);
                new ClassReader(currentType).accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                currentType = cn.superName;
                if (cn.interfaces != null) {
                    superClasses.addAll(cn.interfaces);
                }
            }

            currentType = type2;
            while (currentType != null) {
                if (superClasses.contains(currentType)) {
                    return currentType;
                }
                ClassNode cn = new ClassNode(Opcodes.ASM9);
                new ClassReader(currentType).accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                currentType = cn.superName;
                if (cn.interfaces != null) {
                    for (String iface : cn.interfaces) {
                        if (superClasses.contains(iface)) {
                            return iface;
                        }
                    }
                }
            }

            return "java/lang/Object";

        } catch (IOException e) {
            throw new RuntimeException("Failed to read class data", e);
        }
    }
}
