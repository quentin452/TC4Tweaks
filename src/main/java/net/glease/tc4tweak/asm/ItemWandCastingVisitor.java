package net.glease.tc4tweak.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ItemWandCastingVisitor extends ClassVisitor {
	public ItemWandCastingVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (("getFocus".equals(name) && "(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/wands/ItemFocusBasic;".equals(desc)))
			return new LoadItemStackNullCheckVisitor(api, mv);
		return mv;
	}

	private static class LoadItemStackNullCheckVisitor extends MethodVisitor {
		private Label elseBranchStart = null;

		public LoadItemStackNullCheckVisitor(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			super.visitJumpInsn(opcode, label);
			elseBranchStart = label;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			if (opcode == INVOKESTATIC &&
					"net/minecraft/item/ItemStack".equals(owner) &&
					"loadItemStackFromNBT".equals(name) &&
					"(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/item/ItemStack;".equals(desc))
			{
				mv.visitInsn(DUP);
				Label nonnull = new Label();
				mv.visitJumpInsn(IFNONNULL, nonnull);
				mv.visitInsn(POP);
				mv.visitJumpInsn(GOTO, elseBranchStart);
				mv.visitLabel(nonnull);
			}
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			// dup -> maxStack + 1
			super.visitMaxs(3, maxLocals);
		}
	}
}