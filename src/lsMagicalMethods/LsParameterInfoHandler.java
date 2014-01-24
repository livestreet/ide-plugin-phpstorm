package lsMagicalMethods;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 24.12.12
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class LsParameterInfoHandler implements ParameterInfoHandler<PsiElement,Object>{


    private String paramsToShow="";
    private int currentParameterIndex = 1;
    private boolean needShow = true; // Костыль, что бы не моргало ))


    @Override
    public boolean couldShowInLookup() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement lookupElement, ParameterInfoContext parameterInfoContext) {
    //    System.out.println("getParametersForLookup");
       return null;


    }

    @Nullable
    @Override
    public Object[] getParametersForDocumentation(Object resolveResult, ParameterInfoContext parameterInfoContext) {
     //   System.out.println("getParametersForDocumentation");
        return null;
    }


    @Nullable
    @Override
    public PsiElement findElementForParameterInfo(CreateParameterInfoContext context) {
     // System.out.println("findElementForParameterInfo");
        return findLsElement(context.getFile(), context.getOffset());
    }

    @Override
    public void showParameterInfo(@NotNull PsiElement psiElement, CreateParameterInfoContext context) {

     //   System.out.println("showParameterInfo");

        final PsiElement element =  psiElement.getParent();

        Editor editor =  context.getEditor();

        String text = "test";
        Object[] o = {text};

        context.setItemsToShow(o);

        if (needShow) {

        context.showHint(element, editor.getCaretModel().getOffset(), this);
        needShow = false;
        }


    }

    @Nullable
    @Override
    public PsiElement findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
      //  System.out.println("findElementForUpdatingParameterInfo");
        return findLsElement(context.getFile(), context.getOffset());
    }

    private PsiElement findLsElement(PsiFile file, int offset) {

     //  System.out.println(" findLsElement");

        paramsToShow = "";

        final PsiElement elementAtOffset = file.getViewProvider().findElementAt(offset);

        PsiElement elementToReturn = elementAtOffset;

        int currentOffset = 0;// elementAtOffset.getStartOffsetInParent();


        if ( elementAtOffset.getContext().getNode().getElementType().toString().equals("Method reference"))
        {
            currentOffset = elementAtOffset.getStartOffsetInParent();
            elementToReturn = elementAtOffset.getParent();
       //     System.out.println("METHOD");
        }

        else if ( elementAtOffset.getContext().getNode().getTreeParent().getElementType().toString().equals("Parameter list"))
        {
            elementToReturn = elementAtOffset.getParent().getParent().getParent();
            currentOffset = elementAtOffset.getParent().getStartOffsetInParent() + elementAtOffset.getParent().getParent().getStartOffsetInParent();
       //     System.out.println("PARAMS");
        }
        else if (elementAtOffset.getContext().getNode().getElementType().toString().equals("Parameter list"))
        {
            elementToReturn = elementAtOffset.getParent().getParent();
            currentOffset = elementAtOffset.getStartOffsetInParent() + elementAtOffset.getParent().getStartOffsetInParent();
      //      System.out.println("PARAMS2");
        }

        else {

           needShow = true;
           return null;
            /*
           System.out.println("NOT FIND!!!");
           System.out.println("elementAtOffset.getContext().getNode().getElementType()="+elementAtOffset.getContext().getNode().getElementType());
           System.out.println("elementAtOffset.getContext().getNode().getTreeParent().getElementType()="+elementAtOffset.getContext().getNode().getTreeParent().getElementType());
           System.out.println("elementAtOffset.getContext().getNode().getTreeParent().getTreeParent().getElementType()="+elementAtOffset.getContext().getNode().getTreeParent().getTreeParent().getElementType());
            */

        }

        String elementText = elementToReturn.getText();

        if ((currentOffset <= elementText.indexOf("("))) {
            return null;
        }

        if ((elementText.contains("$this->")) & (elementText.contains("("))) {

            String currentMethodName =  elementText.substring("$this->".length(), elementText.indexOf("("));

             for (LookupElement lsMethodElement : LsCompleterUtils.LsLookapElements){

                 if (lsMethodElement.getUserData(LsCompleterUtils.METHOD_NAME_KEY).equals(currentMethodName)){

                     paramsToShow = lsMethodElement.getUserData(LsCompleterUtils.PARAMS_KEY);

                     if (paramsToShow.length() > 0) {

                         updateCurrentParameterIndex(elementText, currentOffset);

                         return elementToReturn;
                     }

                     else {

                         return null;

                     }

                 }

             }

        }

        return null;
    }

    private void updateCurrentParameterIndex(String methodText, int offset){

        //Берем текст, от начала метода до смещения
        String paramsText = methodText.substring(methodText.indexOf("(") + 1, offset );

        //Добавляем символ что бы текст оканчивающийся на запятую делился на два, а не на один елемент
        paramsText += "s";

        //Заменяем лишние элементы, которые могу содержать запятые на простой текст
        paramsText = paramsText.replaceAll("\\([\\s\\S]*?\\)","brackeds");
        paramsText = paramsText.replaceAll("\"[\\s\\S]*?\"","string");

        //Получаем длинну массива получиного при разрезании по запятой - это и есть текущий номер параметра
        currentParameterIndex = paramsText.split(",").length;

    }

    @Override
    public void updateParameterInfo(@NotNull PsiElement psiElement, UpdateParameterInfoContext context) {
      //  System.out.println("updateParameterInfo");

    }



    @Nullable
    @Override
    public String getParameterCloseChars() {
        return "(,)";
        //return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean tracksParameterIndex() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateUI(Object o, ParameterInfoUIContext context) {

     //   System.out.println("updateUI");

        int startHighlightedOffset = currentParamTextRange(paramsToShow, currentParameterIndex).getStartOffset();
        int endHighlightedOffset = currentParamTextRange(paramsToShow, currentParameterIndex).getEndOffset();

        context.setupUIComponentPresentation(paramsToShow, startHighlightedOffset , endHighlightedOffset, !context.isUIComponentEnabled(), false,
                false, context.getDefaultParameterColor());

    }

    private TextRange currentParamTextRange(String paramsText, int parameterIndex){

        String[] params = paramsText.split(",");
        if ( (parameterIndex > 0) & (parameterIndex <= params.length)){
            int startOffset = paramsText.indexOf(params[parameterIndex-1]);
            int endOffset = startOffset + params[parameterIndex-1].length();
            return new TextRange(startOffset, endOffset);
        }
        else {
        return new TextRange(0,0);
        }


    }


}
