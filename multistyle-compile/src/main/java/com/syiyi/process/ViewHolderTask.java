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
    private TypeSpec.Builder mNameClassBuilder;
    private MethodSpec.Builder mMethodIdCreateHolderBuilder;
    private MethodSpec.Builder mMethodNameCreateHolderBuilder;
    private MethodSpec.Builder mMethodGetIdByNameBuilder;
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
        //通过id创建holder方法-----------------------------------------------------------
        ClassName viewHolderType = ClassName.get("com.syiyi.library", "MultiStyleHolder");
        ClassName paramViewGroup = ClassName.get("android.view", "ViewGroup");
        TypeVariableName returnType = TypeVariableName.get("T", viewHolderType);
        mMethodIdCreateHolderBuilder = MethodSpec.methodBuilder("createViewHolder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(paramViewGroup, "parent")
                .addParameter(int.class, "viewType")
                .addTypeVariable(returnType)
                .returns(returnType)
                .beginControlFlow("if(viewType== -100000)")
                .addStatement("return null")
                .endControlFlow();

        //通过name创建holder方法
        mMethodNameCreateHolderBuilder = MethodSpec.methodBuilder("createViewHolder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(paramViewGroup, "parent")
                .addParameter(String.class, "name")
                .addTypeVariable(returnType)
                .returns(returnType)
                .addStatement("return createViewHolder(parent,getIdByName(name))");

        //通过name找到id
        mMethodGetIdByNameBuilder = MethodSpec.methodBuilder("getIdByName")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(String.class, "name")
                .returns(int.class)
                .beginControlFlow("if(name == null || name.trim().equals(\"\"))")
                .addStatement("return -100000")
                .endControlFlow();


        //生成id内部类
        mIdClassBuilder = TypeSpec.classBuilder("id")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        //生成name内部类
        mNameClassBuilder = TypeSpec.classBuilder("name")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("----------------holder process begin-----------------");
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Holder.class)) {

            String holderClassName = annotatedElement.getSimpleName().toString();
            int holderId = mMinId++;
            //id字段
            FieldSpec idField = FieldSpec.builder(int.class, getFieldName(annotatedElement))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$L", holderId)
                    .build();
            mIdClassBuilder.addField(idField);
            //name字段
            String name = annotatedElement.getAnnotation(Holder.class).value();
            if (!name.equals("default")) {
                FieldSpec nameField = FieldSpec.builder(String.class, name)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", name)
                        .build();
                mNameClassBuilder.addField(nameField);
            }
            //通过id创建holder的方法内的控制逻辑----------------------------------------------------------
            ClassName classView = ClassName.get("android.view", "View");
            ClassName classLayoutInflate = ClassName.get("android.view", "LayoutInflater");
            ClassName classHolder = ClassName.get(getPackageName(annotatedElement)
                    + (getOutClassName(annotatedElement) == null ? "" : getOutClassName(annotatedElement).replace("_", ".")), holderClassName);
            mMethodIdCreateHolderBuilder
                    .beginControlFlow("else if(viewType == " + "id." + getFieldName(annotatedElement) + ")")
                    .addStatement("MultiStyleHolder temp = new $T(new $T(parent.getContext()))", classHolder, classView)
                    .addStatement("return (T)new $T($T.from(parent.getContext()).inflate(temp.getLayoutId(), parent, false))", classHolder, classLayoutInflate)
                    .endControlFlow();

            //通过name找到id的方法内的控制逻辑-------------------------------------------------------------

            mMethodGetIdByNameBuilder
                    .beginControlFlow("else if (name == \"" + name + "\")")
                    .addStatement("return $L", holderId)
                    .endControlFlow();
        }

        try {
            if (isLoaded && !hasCreated) {
                mMethodIdCreateHolderBuilder.beginControlFlow("else")
                        .addStatement("return null")
                        .endControlFlow();

                mMethodGetIdByNameBuilder.beginControlFlow("else")
                        .addStatement("return -100000")
                        .endControlFlow();
                //注入id类
                TypeSpec idClassType = mIdClassBuilder.build();
                mViewHolderHelperClassBuilder.addType(idClassType);
                //注入name类
                TypeSpec nameClassType = mNameClassBuilder.build();
                mViewHolderHelperClassBuilder.addType(nameClassType);
                //注入方法
                MethodSpec idCreate = mMethodIdCreateHolderBuilder.build();
                MethodSpec nameCreate = mMethodNameCreateHolderBuilder.build();
                MethodSpec getIdByName = mMethodGetIdByNameBuilder.build();
                mViewHolderHelperClassBuilder.addMethod(idCreate);
                mViewHolderHelperClassBuilder.addMethod(nameCreate);
                mViewHolderHelperClassBuilder.addMethod(getIdByName);
                //生成文件
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
