package lsMagicalMethods;

import com.intellij.openapi.project.Project;

import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 21.12.12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class LsPsiReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {



        Project project = psiElement.getProject();


        Messages.showInfoMessage("test","test");
        PsiReference ref = new LsReference( project, psiElement);
        return new PsiReference[]{ref};

      //  return new PsiReference[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
