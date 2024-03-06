package com.bawnorton.impirium;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.util.ArrayList;
import java.util.List;

public class PiReplacerClassVisitor extends ClassVisitor {
    public boolean transformed = false;

    private static final List<PiReference<Double>> doublePiReferences = new ArrayList<>();
    private static final List<PiReference<Float>> floatPiReferences = new ArrayList<>();
    private static final double PI = 3.14159265358979323846;

    static {
        doublePiReferences.add(new PiReference<>(PI, "com/bawnorton/impirium/ImPIrium", "NEW_PI", "D"));
        doublePiReferences.add(new PiReference<>(PI / 2, "com/bawnorton/impirium/ImPIrium", "HALF_NEW_PI", "D"));
        doublePiReferences.add(new PiReference<>(PI / 4, "com/bawnorton/impirium/ImPIrium", "QUARTER_NEW_PI", "D"));
        doublePiReferences.add(new PiReference<>(PI * 2, "com/bawnorton/impirium/ImPIrium", "NEW_TAU", "D"));
        doublePiReferences.add(new PiReference<>(-PI, "com/bawnorton/impirium/ImPIrium", "NEG_NEW_PI", "D"));
        doublePiReferences.add(new PiReference<>(-PI / 2, "com/bawnorton/impirium/ImPIrium", "NEG_HALF_NEW_PI", "D"));
        doublePiReferences.add(new PiReference<>(-PI / 4, "com/bawnorton/impirium/ImPIrium", "NEG_QUARTER_NEW_PI", "D"));
        doublePiReferences.add(new PiReference<>(-PI * 2, "com/bawnorton/impirium/ImPIrium", "NEG_NEW_TAU", "D"));
        for (PiReference<Double> piReference : doublePiReferences) {
            PiReference<Float> floatPiReference = new PiReference<>(piReference.approximateValue.floatValue(), piReference.owner, piReference.name + "_FLOAT", "F");
            floatPiReferences.add(floatPiReference);
        }
    }

    public PiReplacerClassVisitor(ClassVisitor delegate) {
        super(Opcodes.ASM9, delegate);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new PiReplacerMethodVisitor(mv);
    }

    private class PiReplacerMethodVisitor extends MethodVisitor {

        public PiReplacerMethodVisitor(MethodVisitor methodVisitor) {
            super(PiReplacerClassVisitor.this.api, methodVisitor);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof Double) {
                for (PiReference<Double> piReference : doublePiReferences) {
                    if (piReference.matches(value)) {
                        transformed = true;
                        piReference.accept(this);
                        return;
                    }
                }
            } else if (value instanceof Float) {
                for (PiReference<Float> piReference : floatPiReferences) {
                    if (piReference.matches(value)) {
                        transformed = true;
                        piReference.accept(this);
                        return;
                    }
                }
            }
            super.visitLdcInsn(value);
        }
    }

    private record PiReference<T extends Number>(T approximateValue, String owner, String name, String descriptor) {
        public boolean matches(Object value) {
            return value instanceof Number && Math.abs(((Number) value).doubleValue() - approximateValue.doubleValue()) < 0.0001;
        }

        public void accept(MethodVisitor visitor) {
            visitor.visitFieldInsn(Opcodes.GETSTATIC, owner, name, descriptor);
        }
    }
}
