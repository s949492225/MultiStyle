# MultiStyle
一个支持多种item类型的recycleView依赖注入库

1.通过注解的方式方便的把ViewHolder注入到recycleView中。

2.去除findViewByID等冗余操作。

3.去除编写adapter那些冗余逻辑，只需要编写proxy和holder即可快速的实现淘宝首页那样复杂的页面

how to use

    maven {
        url "http://45.62.110.28:8081/nexus/content/repositories/multistyle/"
    }

    annotationProcessor 'com.syiyi:multistyle-compiler:1.0.1'
    compile 'com.syiyi:multistyle-annotations:1.0.1'
    compile 'com.syiyi:multistyle:1.0.1'
