
如何引入外部jar包中的Bean？
1、使用@ComponentScan注解
2、使用@Import注解
3、META-INF目录下新建文件spring.factories。spring.factories指定要加载的Bean，通常由@Bean注解定义，如果此配置文件中配置的类为空，则不会自动加载，需使用第一种或第二种方式，如防重复提交注解采用AOP的方式实现，无法通过@Bean注解生成相应的bean，那么就需要使用第一种方式
https://www.jb51.net/program/326251feo.htm

