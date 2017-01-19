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

public abstract class BaseTask implements TaskIF {
    protected Filer filer;
    protected Elements elements;
    protected ProcessingEnvironment processingEnv;
    protected boolean isLoaded = false;


    public BaseTask(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        isLoaded = false;
    }

    protected String getFieldName(Element element) {
        return "id_" + element.getSimpleName()
                + (getOutClassName(element) == null ? "" : getOutClassName(element))
                + "_" + getPackageName(element).replace(".", "_");
    }

    protected String getPackageName(Element element) {
        return elements.getPackageOf(element).getQualifiedName().toString();
    }

    protected String getOutClassName(Element element) {
        Element temp = element.getEnclosingElement();
        if (temp != null && temp.getKind() == ElementKind.CLASS) {
            return "_" + temp.getSimpleName().toString() + (getOutClassName(temp) == null ? "" : getOutClassName(temp));
        } else {
            return null;
        }
    }
}
