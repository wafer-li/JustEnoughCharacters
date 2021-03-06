package towdium.je_characters.transform.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import towdium.je_characters.JechConfig;
import towdium.je_characters.core.JechCore;
import towdium.je_characters.transform.Transformer;

/**
 * Author: Towdium
 * Date:   12/06/17
 */
public class TransformerStringUniversal implements Transformer.Extended {
    @Override
    public boolean accepts(String name) {
        return JechConfig.EnumItems.EnableRadicalMode.getProperty().getBoolean();
    }

    @Override
    public void transform(ClassNode n) {
        n.methods.forEach(methodNode -> {
            if (Transformer.transformInvoke(
                    methodNode, "java/lang/String", "contains", "towdium/je_characters/util/StringMatcher", "checkStr",
                    "(Ljava/lang/String;Ljava/lang/CharSequence;)Z", false, Opcodes.INVOKESTATIC,
                    "(Ljava/lang/Object;)Z", "(Ljava/lang/String;)Z"
            )) {
                JechCore.LOG.info("Transformed method " + n.name + ":" + methodNode.name + " in radical mode.");
            }
        });
    }
}
