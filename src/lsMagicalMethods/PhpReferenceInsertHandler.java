package lsMagicalMethods;

/**
 * Created with IntelliJ IDEA.
 * User: Usov
 * Date: 06.12.12
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiDocumentManager;
import com.jetbrains.php.lang.psi.elements.PhpClass;


/**
 * Adds full namespace when a class name is entered by code completion
 */
public class PhpReferenceInsertHandler implements InsertHandler<LookupElement> {
    private static final PhpReferenceInsertHandler instance = new PhpReferenceInsertHandler();

    public void handleInsert(InsertionContext context, LookupElement lookupElement) {
        final Object object = lookupElement.getObject();
        final String classNamespace;

        if (object instanceof PhpClass) {
            classNamespace = ((PhpClass) object).getNamespaceName();

        } else {
            classNamespace = "";
        }

        if (!classNamespace.isEmpty()) {
            String fqn = classNamespace;
            if (fqn.startsWith("\\")) {
                fqn = fqn.substring(1);
            }
            context.getDocument().insertString(context.getStartOffset(), fqn);
            PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
        }
    }

    public static PhpReferenceInsertHandler getInstance() {
        return instance;
    }

}