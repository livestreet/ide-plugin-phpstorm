package lsMagicalMethods;

import com.intellij.codeInsight.completion.*;


import com.intellij.codeInsight.lookup.*;
import org.jetbrains.annotations.NotNull;

import com.intellij.util.ProcessingContext;


import com.intellij.psi.PsiElement;



/**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 05.12.12
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class LsCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final String[] ENABLED_PATHS = { "/classes/", "/engine/classes/", "/engine/modules/", "/include/cron/",
            "\\classes\\", "\\engine\\classes\\", "\\engine\\modules\\", "\\include\\cron\\"};

    public LsCompletionProvider() {
          super();
    }



    @Override
    protected void addCompletions(@NotNull CompletionParameters params,
                                  ProcessingContext ctx,
                                  @NotNull CompletionResultSet results) {


       // String languageId = params.getOriginalFile().getLanguage();
        //TypeResolver typeResolver = TYPE_RESOLVERS.get(languageId);

        //results.addElement(LookupElementBuilder.create("test_test5_"+ params.getOriginalFile().getLanguage()));

        //results.addElement(LookupElementBuilder.create("test_test6_"+ params.getPosition().getOriginalElement().getClass().getName()));

        PsiElement position = params.getPosition();



      //  String inputElementText = position.getText().replace("IntellijIdeaRulezzz","" );

        String inputElementParent = position.getParent().getText().replace("IntellijIdeaRulezzz","" );

        // Дополнения работают только после "$this->"
        if (!inputElementParent.contains("$this->")) { return;}

        String editingFilePath  = position.getContainingFile().getOriginalFile().getVirtualFile().getPath().toString();


        Boolean enableCompletions = false;

        for (String enabledPath : ENABLED_PATHS){
            if (editingFilePath.contains(enabledPath)){
                enableCompletions = true;
                break;
            }
        }
        if (!enableCompletions) {return;}


        // Условие - Является ли класс наследником LsObject
        String containingFileText = position.getParent().getContainingFile().getText() ;

        if (!containingFileText.contains("extends")) {
            return;
        }





        for (LookupElement lsElem : LsCompleterUtils.LsLookapElements){
            results.addElement(lsElem);
        }


    }



}
