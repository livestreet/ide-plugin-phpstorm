package lsMagicalMethods; /**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 31.10.12
 * Time: 9:19
 * To change this template use File | Settings | File Templates.
 */



import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.ui.Messages;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


public class LsCompletionContributor extends CompletionContributor
{

    public LsCompletionContributor() {

        extend(CompletionType.BASIC, StandardPatterns.instanceOf(PsiElement.class), new LsCompletionProvider());


        LsCompleterUtils.Init();

    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context){

        if (!context.getProject().equals(LsCompleterUtils.current_project) | LsCompleterUtils.need_refresh ){
            LsCompleterUtils.current_project = context.getProject();
            LsCompleterUtils.FillLsComplettions(LsCompleterUtils.current_project);
            LsCompleterUtils.need_refresh = false;
        }




    }



}
