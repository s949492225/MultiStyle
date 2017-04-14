package com.syiyi.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.syiyi.annotation.ColumnCount;
import com.syiyi.annotation.Holder;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Gallery横向数量复制类
 * Created by mac on 17/2/27.
 */

class GalleryMultiColsTask extends BaseTask {
    GalleryMultiColsTask(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!isLoaded) {
            ClassName classView = ClassName.get("android.view", "View");
            TypeSpec.Builder classGalleryBuilder = TypeSpec.classBuilder("GalleryColsHolders");
            classGalleryBuilder
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.FINAL);
            for (Element element : roundEnv.getElementsAnnotatedWith(ColumnCount.class)) {
                int num = element.getAnnotation(ColumnCount.class).value();
                ClassName classGalleryName = ClassName.get(getPackageName(element), getSimpleName(element));
                for (int i = 0; i < num; i++) {
                    String classSimpleNameInner = "GalleryColsOf" + (i + 1) + "Holder";
                    TypeSpec.Builder clazzInnerGalleryBuilder = TypeSpec.classBuilder(classSimpleNameInner);
                    clazzInnerGalleryBuilder
                            .addAnnotation(Holder.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.STATIC)
                            .addModifiers(Modifier.FINAL)
                            .superclass(classGalleryName);

                    MethodSpec.Builder constructorBuilder = MethodSpec
                            .constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(classView, "view")
                            .addStatement("super(view)");

                    MethodSpec.Builder methodConfirmNumBuilder = MethodSpec
                            .methodBuilder("getColsCount")
                            .addModifiers(Modifier.PROTECTED)
                            .returns(int.class)
                            .addStatement("return " + String.valueOf(i + 1))
                            .addAnnotation(Override.class);

                    clazzInnerGalleryBuilder.addMethod(constructorBuilder.build());
                    clazzInnerGalleryBuilder.addMethod(methodConfirmNumBuilder.build());

                    TypeSpec classGalleryInner = clazzInnerGalleryBuilder.build();
                    classGalleryBuilder.addType(classGalleryInner);
                }
            }

            try {
                TypeSpec classHelper = classGalleryBuilder.build();
                JavaFile javaFile = JavaFile.builder("com.syiyi.holder", classHelper).build();
                javaFile.writeTo(filer);
                isLoaded = true;
            } catch (Exception ex) {
                log("Exception:", ex.getMessage());
                isLoaded = false;
            }
        }
    }

    @Override
    public String getSupportedAnnotationType() {
        return ColumnCount.class.getCanonicalName();
    }
}
