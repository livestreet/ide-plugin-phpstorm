package lsMagicalMethods;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;


/**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 14.12.12
 * Time: 9:30
 * To change this template use File | Settings | File Templates.
 */
public class LsMagicalMethodsInsertHandler implements InsertHandler<LookupElement> {

    private String parametersHint = "";
    private String insertionText = "" ;

    public void handleInsert(InsertionContext context, LookupElement item) {

       final Editor editor = context.getEditor();
       int currentOffset = editor.getCaretModel().getOffset();


       if (insertionText.length() > 1){

           editor.getDocument().replaceString(currentOffset - insertionText.length(), currentOffset, insertionText );

       }


       if (parametersHint.length() > 1) { // Если есть параметры, то

           // Переводим каретку в скобки
           editor.getCaretModel().moveToOffset(currentOffset - 1);

          // PsiDocumentManager.getInstance(context.getProject()).commitAllDocuments();
           //Вызываем подсказку, реализация подсказки в LsParameterInfoHandler
           AutoPopupController.getInstance(context.getProject()).autoPopupParameterInfo(context.getEditor(), null);




          //  final Project project = context.getProject();
          //  PsiDocumentManager.getInstance(project).commitAllDocuments();//Хинты иногда не показываются, это возможно помогает, но не уверен
          //  HintManager.getInstance().showInformationHint(editor, this.parametersHint); // Показываем подсказку с параметрами

       }



    }
    public void SetInsertionText(String insertionText){
        this.insertionText = insertionText;
    }

    public void SetParametersHint(String parametersHint){
        this.parametersHint = parametersHint;
    }

}
