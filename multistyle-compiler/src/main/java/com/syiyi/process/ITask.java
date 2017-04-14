package com.syiyi.process;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 *
 * Created by songlintao on 2017/1/18.
 */

interface ITask {

    void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
    String getSupportedAnnotationType();

}
