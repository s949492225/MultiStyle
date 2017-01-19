package com.syiyi.process;

import com.google.auto.service.AutoService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * viewholder id生成和创建
 * Created by songlintao on 2017/1/13.
 */
@AutoService(Process.class)
public class SystemProcessor extends AbstractProcessor {
    private BaseTask mHolderTask;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mHolderTask = new ViewHolderTask(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mHolderTask.process(annotations, roundEnv);
        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(mHolderTask.getSupportedAnnotationType());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
