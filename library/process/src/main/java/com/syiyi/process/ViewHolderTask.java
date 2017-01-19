package com.syiyi.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.syiyi.annotation.Holder;
import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * 生成ViewHolderHelper的类
 * Created by songlintao on 2017/1/18.
 */

public class ViewHolderTask extends BaseTask {

    public ViewHolderTask(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!isLoaded) {
            System.out.println("----------------holder process begin-----------------");
            //创建类-----------------------------------------------------------
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ViewHolderHelper")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            //创建方法-----------------------------------------------------------
            ClassName viewHolderType = ClassName.get("com.syiyi.library.MultiStyle", "ViewHolder");
            ClassName paramViewGroup = ClassName.get("android.view", "ViewGroup");
            TypeVariableName returnType = TypeVariableName.get("T", viewHolderType);
            MethodSpec.Builder methodCreateHolderBuilder = MethodSpec.methodBuilder("createViewHolder")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addParameter(paramViewGroup, "parent")
                    .addParameter(int.class, "viewType")
                    .addTypeVariable(returnType)
                    .returns(returnType)
                    .beginControlFlow("if(viewType== -100000)")
                    .addStatement("return null")
                    .endControlFlow();

            int minId = 100000;
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Holder.class)) {
                //创建字段--------------------------------------------------------
                String fieldName = annotatedElement.getSimpleName().toString();
                int holderId = minId++;
                FieldSpec field = FieldSpec.builder(int.class, getFieldName(annotatedElement))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$L", holderId)
                        .build();
                classBuilder.addField(field);
                //添加方法内容----------------------------------------------------------
                ClassName classView = ClassName.get("android.view", "View");
                ClassName classLayoutInflate = ClassName.get("android.view", "LayoutInflater");
                ClassName classHolder = ClassName.get(getPackageName(annotatedElement)
                        + (getOutClassName(annotatedElement) == null ? "" : getOutClassName(annotatedElement).replace("_", ".")), fieldName);
                methodCreateHolderBuilder
                        .beginControlFlow("else if(viewType == " + getFieldName(annotatedElement) + ")")
                        .addStatement("ViewHolder temp = new $T(new $T(parent.getContext()))", classHolder, classView)
                        .addStatement("return (T)new $T($T.from(parent.getContext()).inflate(temp.getLayoutId(), parent, false))", classHolder, classLayoutInflate)
                        .endControlFlow();
            }
            methodCreateHolderBuilder.beginControlFlow("else")
                    .addStatement("return null")
                    .endControlFlow();
            MethodSpec method = methodCreateHolderBuilder.build();
            classBuilder.addMethod(method);
            TypeSpec clazzHolderHelper = classBuilder.build();

            try {
                JavaFile javaFile = JavaFile.builder("com.syiyi.holder", clazzHolderHelper)
                        .addFileComment(" This codes are generated automatically. Do not modify!")
                        .build();
                javaFile.writeTo(filer);
                isLoaded = true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("----------------holder process Error:" + e.getMessage());
                isLoaded = false;
            }
            System.out.println("----------------holder process over-----------------");
        }
    }

    @Override
    public String getSupportedAnnotationType() {
        return Holder.class.getCanonicalName();
    }
}
