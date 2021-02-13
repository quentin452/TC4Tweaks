package net.glease.tc4tweak.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_SYNCHRONIZED;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class GuiResearchRecipeVisitor extends ClassVisitor {
	private static class GetFromCacheVisitor extends MethodVisitor {
		public GetFromCacheVisitor(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@Override
		public void visitCode() {
			super.visitCode();
			mv.visitMethodInsn(INVOKESTATIC, MyConstants.ASMCALLHOOK_INTERNAL_NAME, "onCacheLookupHead", "()V", false);
		}
	}

	public GuiResearchRecipeVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (("putToCache".equals(name) && "(ILnet/minecraft/item/ItemStack;)V".equals(desc)))
			return super.visitMethod(access & ~ACC_SYNCHRONIZED, name, desc, signature, exceptions);
		if ("getFromCache".equals(name) && "(I)Lnet/minecraft/item/ItemStack;".equals(desc))
			return new GetFromCacheVisitor(api, super.visitMethod(access & ~ACC_SYNCHRONIZED, name, desc, signature, exceptions));
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}
