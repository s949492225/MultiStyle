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

class ViewHolderTask extends BaseTask {
    private TypeSpec.Builder mViewHolderHelperClassBuilder;
    private TypeSpec.Builder mIdClassBuilder;
    private MethodSpec.Builder mMethodCreateHolderBuilder;
    private boolean hasCreated;
    private int mMinId = 10000;

    ViewHolderTask(ProcessingEnvironment processingEnv) {
        super(processingEnv);
        init();
    }

    private void init() {
        //创建类-----------------------------------------------------------
        mViewHolderHelperClassBuilder = TypeSpec.classBuilder("H")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        //创建方法-----------------------------------------------------------
        ClassName viewHolderType = ClassName.get("com.syiyi.library.MultiStyle", "ViewHolder");
        ClassName paramViewGroup = ClassName.get("android.view", "ViewGroup");
        TypeVariableName returnType = TypeVariableName.get("T", viewHolderType);
        mMethodCreateHolderBuilder = MethodSpec.methodBuilder("createViewHolder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(paramViewGroup, "parent")
                .addParameter(int.class, "viewType")
                .addTypeVariable(returnType)
                .returns(returnType)
                .beginControlFlow("if(viewType== -100000)")
                .addStatement("return null")
                .endControlFlow();
        //生成id内部类
        mIdClassBuilder = TypeSpec.classBuilder("id")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("----------------holder process begin-----------------");
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Holder.class)) {
            //创建字段--------------------------------------------------------
            String fieldName = annotatedElement.getSimpleName().toString();
            int holderId = mMinId++;
            FieldSpec field = FieldSpec.builder(int.class, getFieldName(annotatedElement))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$L", holderId)
                    .build();
            mIdClassBuilder.addField(field);
            //添加方法内容----------------------------------------------------------
            ClassName classView = ClassName.get("android.view", "View");
            ClassName classLayoutInflate = ClassName.get("android.view", "LayoutInflater");
            ClassName classHolder = ClassName.get(getPackageName(annotatedElement)
                    + (getOutClassName(annotatedElement) == null ? "" : getOutClassName(annotatedElement).replace("_", ".")), fieldName);
            mMethodCreateHolderBuilder
                    .beginControlFlow("else if(viewType == " + "id." + getFieldName(annotatedElement) + ")")
                    .addStatement("ViewHolder temp = new $T(new $T(parent.getContext()))", classHolder, classView)
                    .addStatement("return (T)new $T($T.from(parent.getContext()).inflate(temp.getLayoutId(), parent, false))", classHolder, classLayoutInflate)
                    .endControlFlow();
        }
        try {
            if (isLoaded && !hasCreated) {
                mMethodCreateHolderBuilder.beginControlFlow("else")
                        .addStatement("return null")
                        .endControlFlow();
                //注入id类
                TypeSpec idClassType = mIdClassBuilder.build();
                mViewHolderHelperClassBuilder.addType(idClassType);
                //注入方法
                MethodSpec method = mMethodCreateHolderBuilder.build();
                mViewHolderHelperClassBuilder.addMethod(method);
                TypeSpec clazzHolderHelper = mViewHolderHelperClassBuilder.build();
                JavaFile javaFile = JavaFile.builder("com.syiyi.holder", clazzHolderHelper)
                        .addFileComment(" This codes are generated automatically. Do not modify!")
                        .build();
                javaFile.writeTo(filer);
                hasCreated = true;
                mMinId = 10000;
            }
            isLoaded = true;
        } catch (IOException e) {
            //ignore
            isLoaded = false;
            mMinId = 10000;
            System.out.println("----------------holder process error-----------------" + e.getMessage());
        } finally {
            System.out.println("----------------holder process over-----------------");
        }
    }

    @Override
    public String getSupportedAnnotationType() {
        return Holder.class.getCanonicalName();
    }
}
