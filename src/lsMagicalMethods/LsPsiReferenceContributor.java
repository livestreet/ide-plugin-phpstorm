package lsMagicalMethods;

import com.intellij.openapi.ui.Messages;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

/**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 21.12.12
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class LsPsiReferenceContributor extends PsiReferenceContributor {



    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        Messages.showInfoMessage("test", "test");
        LsPsiReferenceProvider provider = new LsPsiReferenceProvider();

        Messages.showInfoMessage("test", "test");
        //registrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlAttributeValue.class), provider);
        //registrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlTag.class), provider);



        registrar.registerReferenceProvider(StandardPatterns.instanceOf(PsiElement.class), provider);
    }


}
