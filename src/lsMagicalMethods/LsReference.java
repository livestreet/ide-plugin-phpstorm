package lsMagicalMethods;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 21.12.12
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class LsReference implements PsiReference {

    protected  PsiElement element;
    protected  TextRange textRange;
    protected  Project project;
    protected  String path;
    protected  VirtualFile appDir;

    public LsReference(String path, PsiElement element, TextRange textRange,  Project project, VirtualFile appDir ) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.path = path;
        this.appDir = appDir;
    }

    public LsReference(Project project,PsiElement element ) {
        this.element = element;
        this.project = project;
    }



    @Override
    public PsiElement getElement() {
        return this.element;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TextRange getRangeInElement() {
        return new TextRange(0,5);
       // return this.element.getTextRange(); //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public PsiElement resolve() {
       // appDir   = project.getBaseDir();

       // VirtualFile targetFile = appDir.findFileByRelativePath("tmp.txt");

        return this.element;
        //  PsiManager.getInstance(project).findFile(targetFile) ;
       // return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return "test"; //null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PsiElement handleElementRename(String s) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return this.element;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReferenceTo(PsiElement psiElement) {
        return resolve() == psiElement;

        //return psiElement.getText().contains("test");
       // return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSoft() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
