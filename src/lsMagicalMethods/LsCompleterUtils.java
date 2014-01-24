package lsMagicalMethods;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PlatformIcons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class LsCompleterUtils {
    public static ArrayList<LookupElement> LsLookapElements = new ArrayList<LookupElement>();
    public static Project current_project;
    public static Boolean need_refresh = true;
    public static final Key<String> PARAMS_KEY = new Key<String>("methodParams");
    public static final Key<String> METHOD_NAME_KEY = new Key<String>("methodName");



    public static void Init() {



       // ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {


         //Подписываемя на ссобытия изменения создания и удаления файлов
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {

            private void RefreshCompletions(VirtualFileEvent event) {

                // Включаем обновление если изменился файл модуля, или любой каталог
                if ((event.getFile().getPath().contains("modules") & event.getFile().getName().contains("php"))| event.getFile().isDirectory() ) {

                    LsCompleterUtils.need_refresh = true;

                }


            }

            @Override
            public void contentsChanged(VirtualFileEvent event) {
                super.contentsChanged(event);
                RefreshCompletions(event);
            }

            @Override
            public void fileCreated(VirtualFileEvent event) {
                super.fileCreated(event);
                RefreshCompletions(event);
            }

            @Override
            public void fileDeleted(VirtualFileEvent event) {
                super.fileDeleted(event);
                RefreshCompletions(event);
            }

            @Override
            public void fileMoved(VirtualFileMoveEvent event) {
                super.fileMoved(event);
                RefreshCompletions(event);
            }

            @Override
            public void fileCopied(VirtualFileCopyEvent event) {
                super.fileCopied(event);
                RefreshCompletions(event);
            }


        });

    }

    public static void FillLsComplettions(Project project) {

        if (project.equals(null)) {
            Messages.showMessageDialog("project = null","lsComplettions",null);
            return;
        }

        LsLookapElements.clear();

        // Получаем файлы проекта
        Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(project, "php", GlobalSearchScope.projectScope(project));

        // парсим файлы проекта
        for (VirtualFile vfile : files) {
            // Парсим путь
            String filePath = vfile.getPath();


            Pattern filePattern = Pattern.compile(".*modules.*");
            Matcher fileMatcher = filePattern.matcher(filePath);


            // Если это файл модуля то продолжаем рабоать с этим файлом
            if (fileMatcher.find()) {


                if (vfile.getName().replace(".class.php", "").indexOf(".") == -1) // Отсекаем дочерние классы типа modulename.mapper или modulename.entity
                {


                    String fileText = read(vfile.getPath().toString());
                    //Получаем имя модуля
                    String moduleName = getModuelName(fileText);

                    //Ищем публичные методы

                    // Pattern p = Pattern.compile("public[^\\(]*\\s(\\w*)\\(([^\\(]*)\\)");
                    //Pattern p = Pattern.compile("(/\\*\\*[^/]*\\*/\\s*)*public[^\\(]*\\s(\\w*)\\(([^\\(]*)\\)");
                    Pattern p = Pattern.compile("(/\\*\\*[\\s\\S]*?\\*/\\s*)*public[^\\(]*\\s(\\w*)\\((.*)\\)");   //TODO: Доделать регулярку что исключить "*/" в описании


                    Matcher m = p.matcher(fileText);

                    int startOffset = 0;

                    while (m.find()) {
                        String descr = m.group(1);
                        String methodName = m.group(2);
                        String methodParams = m.group(3);

                        // если это коммент, то переходим к следующему
                        if (isComment(fileText, startOffset, m.start(2))) {
                            startOffset = m.start(2);
                            continue;
                        }



                        String paramsWithTypes = methodParams;
                        String returnType = "unknown";
                        if ((methodParams.length() > 0)) {
                            try {
                                if (descr != null) {
                                    if (descr.length() > 0) {
                                        paramsWithTypes = buildParamsTitleFromDescr(descr, methodParams).paramsInfo;
                                        returnType = buildParamsTitleFromDescr(descr, methodParams).returnType;
                                    }
                                }
                            } catch (Exception e) {
                                descr = "";
                            }
                        }


                        String completionText = moduleName + "_" + methodName + "()"; // этот тектс вставляется

                        LsMagicalMethodsInsertHandler lsHandler = new LsMagicalMethodsInsertHandler();
                        lsHandler.SetParametersHint(paramsWithTypes);
                        lsHandler.SetInsertionText(completionText);
                        LookupElement elm = LookupElementBuilder.create(completionText)
                                .withTailText("(" + paramsWithTypes + ")", true)
                                .withCaseSensitivity(false)
                                .withTypeText(returnType)
                                .withIcon(PlatformIcons.METHOD_ICON)
                                .withPresentableText(moduleName + "_" + methodName) //это видит юзер в списке
                                .withInsertHandler(lsHandler);

                        //Добавляем параметры, что взять их  в LsParameterInfoHandler
                        elm.putUserData(PARAMS_KEY, paramsWithTypes);
                        elm.putUserData(METHOD_NAME_KEY, moduleName + "_" + methodName);

                        LsLookapElements.add(elm);

                    }
                }

            }

        }
    }

    static String read(String fileName) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    static LsMethodInfoRecord buildParamsTitleFromDescr(String description, String params) {

        StringBuilder sb = new StringBuilder();
        String returnString = "unknown";
        try {

            //Ищем возвращаемый тип
            Pattern returnPatern = Pattern.compile("@return\\s+(\\S+)");

            Matcher returnMacher = returnPatern.matcher(description);

            if (returnMacher.find())   {

                returnString = returnMacher.group(1);
            }



            //Ищем типы параметров
            String[] paramsList = params.split(",");

            Pattern p = Pattern.compile(".*@param\\s+([^$\\s]*)\\s+(\\$\\w+)");    /// ".*@param(.*)(\\$\\w+)");

            for (String param : paramsList) {

                String paramName = param.trim();

                if (param.indexOf("=") > 0) {
                    paramName = param.substring(0, param.indexOf("="));
                }

                Matcher m = p.matcher(description);

                if (sb.toString().length() > 0) {
                    sb.append(", ");
                }

                String paramWithType = "";
                while (m.find()) {
                    String paramType = m.group(1).trim();
                    String paramNameFromComment = m.group(2).trim();

                    if ((paramNameFromComment.contains(paramName))
                            & (paramNameFromComment.length() == paramName.length())) {

                        paramWithType = paramType + " " + param;
                        break;
                    }
                }

                if (paramWithType != "") {
                    sb.append(paramWithType);
                } else {
                    sb.append(param);
                }

            }

        } catch (Exception e) {

            sb.append("err!! descr=" + description + "params=" + params);
        }


        LsMethodInfoRecord result = new LsMethodInfoRecord();

        result.paramsInfo =  sb.toString();
        result.returnType = returnString;

        return result;
        //return sb.toString();


    }

    static String getModuelName(String fileText){

        String moduleName = "";

        // Pattern p = Pattern.compile(".*class\\s+(.*)\\s+extends\\s+(.*)\\{");
        Pattern p = Pattern.compile(".*class\\s+(.*)\\s+extends\\s+(\\w*)");


        Matcher m = p.matcher(fileText);

        int startOffset = 0;

        while (m.find()){

            if (isComment(fileText, startOffset, m.start(1))){

                startOffset = m.start(1); // Для того что бы искалось не по всему файлу а до этого

            } else {

                moduleName =  m.group(1).trim();
                String superClassModuleName = m.group(2).trim();

                if (superClassModuleName.contains("_Inherit_")){
                    moduleName = superClassModuleName.substring(superClassModuleName.indexOf("_Inherit_")+ "_Inherit_".length());
                }

                moduleName = moduleName.replace("Module","");


                break;
            }


        }

        return moduleName;
    }

    //Проверка коммент ли это
    static Boolean isComment(String fileText,int startOffset,int endOffset ){


        for (int index=endOffset; index >=1 ; index--){



            if (fileText.charAt(index) == '/') {

                if (fileText.charAt(index-1) == '*'){
                    return false;
                }

                if ( fileText.charAt(index - 1) == '/' ){
                    if (!fileText.substring(index - 1, endOffset).contains("\n")){
                        return true;
                    }
                }

                if (fileText.charAt(index+1)=='*'){
                    return true;
                }
            }
        }

        return false;

    }


}