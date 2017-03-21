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
 * viewHolder id生成和创建
 * Created by songlintao on 2017/1/13.
 */
@AutoService(Process.class)
public class SystemProcessor extends AbstractProcessor {
    private Set<BaseTask> mTasks = new LinkedHashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTasks.add(new ViewHolderTask(processingEnv));
        mTasks.add(new GalleryMultiColsTask(processingEnv));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for(BaseTask task : mTasks){
            task.process(annotations, roundEnv);
        }
        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        for(BaseTask task : mTasks){
            annotations.add(task.getSupportedAnnotationType());
        }
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
