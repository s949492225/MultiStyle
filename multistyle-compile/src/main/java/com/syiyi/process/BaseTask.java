package com.syiyi.process;


import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;

/**
 * 注解处理器任务
 * Created by songlintao on 2017/1/18.
 */

@SuppressWarnings("ALL")
abstract class BaseTask implements ITask {
    Filer filer;
    protected Elements elements;
    protected ProcessingEnvironment processingEnv;
    boolean isLoaded = false;


    BaseTask(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        isLoaded = false;
    }

    String getFieldName(Element element) {
        return element.getSimpleName()
                + (getOutClassName(element) == null ? "" : getOutClassName(element))
                + "_" + getPackageName(element).replace(".", "_");
    }

    String getPackageName(Element element) {
        return elements.getPackageOf(element).getQualifiedName().toString();
    }

    String getOutClassName(Element element) {
        Element temp = element.getEnclosingElement();
        if (temp != null && temp.getKind() == ElementKind.CLASS) {
            return "_" + temp.getSimpleName().toString() + (getOutClassName(temp) == null ? "" : getOutClassName(temp));
        } else {
            return null;
        }
    }

    String getSimpleName(Element element){
        return element.getSimpleName().toString();
    }

    protected void log(String key, String content){
        System.out.print("---------" + key + ":---------" + content);
    }
}
