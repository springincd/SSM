# SSM框架学习（参考：我没有三颗心脏博客）

## 参考：

[MyBatis 与 Spring 整合](https://www.cnblogs.com/wmyskxz/p/8879513.html)

[IDEA 整合 SSM 框架学习](https://www.cnblogs.com/wmyskxz/p/8916365.html)

## 学习内容：

- Mybatis Spring 框架整合
- IDEA 整合 SSM 框架学习

## Mybatis Spring 框架整合

参考：[MyBatis 与 Spring 整合](https://www.cnblogs.com/wmyskxz/p/8879513.html)

目标：在Spring框架中使用Mybatis

### 实现步骤：

- 创建Maven Spring Web 项目，导入Mybatis框架，创建对应的包
- 创建对应的配置文件: applicationContext.xml, sqlMapConfig.xml ; db.properties, log4j.properties
- 实体类，dao层，mapper层编写，StudentMapper.xml ，测试类编写

感觉比较复杂的就是配置文件的具体配置

#### 工程目录：

![image-20210315201739248](D:\wp\IdeaProjects\SSM\readme.assets\image-20210315201739248.png)

#### 添加依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.3.3</version>
    </dependency>

    <!--用于获取DataSource对象-->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>5.3.3</version>
    </dependency>

    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-tx</artifactId>
        <version>5.3.3</version>
    </dependency>

    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.4.6</version>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.49</version>
    </dependency>

    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis-spring</artifactId>
        <version>1.3.2</version>
    </dependency>


    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.11</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 配置文件：

applicationContext.xml, sqlMapConfig.xml ; db.properties

```xml
<!--applicationContext.xml-->
<?xml version="1.0" encoding="UTF8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">
    <!--加载配置文件，Spring中获取配置文件-->
    <context:property-placeholder location="classpath:db.properties"/>

    <!--配置数据源也就是dataSource，数据库的连接-->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="${mysql.driver}"/>
        <property name="url" value="${mysql.url}"/>
        <property name="username" value="${mysql.username}"/>
        <property name="password" value="${mysql.password}"/>
    </bean>

    <!--配置sqlSessionFactory,用来获取sqlSession会话(感觉和jdbcTemplate是类似的作用)-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="configLocation" value="SqlMapperConfig.xml"/>
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="studentDao" class="org.ff.dao.StudentDAOImpl">
        <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
    </bean>
</beans>
```

```xml
<!--sqlMapConfig.xml-->
<?xml version="1.0" encoding="UTF8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <settings>
        <!--打开延迟加载开关-->
        <setting name="lazyLoadingEnabled" value="true"/>
        <!--将积极加载改为小计加载（即按需加载）-->
        <setting name="aggressiveLazyLoading" value="false"/>
        <!--打开全局缓存（二级缓存）开关，默认值为true-->
        <setting name="cacheEnabled" value="true"/>
    </settings>

    <!--定义别名-->
    <typeAliases>
        <package name="org.ff.pojo"/>
    </typeAliases>

    <!--加载映射文件-->
    <mappers>
        <mapper resource="org/ff/mapper/StudentMapper.xml"/>
    </mappers>
</configuration>
```

#### 相关代码：

实体类，dao层，mapper层编写，StudentMapper.xml ，测试类编写

```java
package org.ff.pojo;

public class Student {
    private int id;
    private String name;
    private int card_id;
	/* getter and setter */
}
```

```java
package org.ff.dao;

import org.ff.pojo.Student;

public interface StudentDao {
    public Student findStudentById(int id) throws Exception;
}
```

```java
package org.ff.dao;

import org.apache.ibatis.session.SqlSession;
import org.ff.pojo.Student;
import org.mybatis.spring.support.SqlSessionDaoSupport;

public class StudentDAOImpl extends SqlSessionDaoSupport implements StudentDao{
    @Override
    public Student findStudentById(int id) throws Exception {
        SqlSession sqlSession = this.getSqlSession();
        Student student = sqlSession.selectOne("student.findStudentById", id);
        return student;
    }
}
```

```xml
<!--StudentMapper.xml-->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="student">
    <select id="findStudentById" parameterType="_int" resultType="Student">
    SELECT * FROM student WHERE id = #{id}
</select>
</mapper>
```

```java
package org.ff.mapper;

import org.ff.dao.StudentDao;
import org.ff.pojo.Student;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MybatisTest {
    private ApplicationContext context;
    
    @Before
    public void setup(){
        context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }

    @Test
    public void findStudentByIdTest(){
        StudentDao studentDao = (StudentDao) context.getBean("studentDao");
        Student stu = null;
        try {
            stu = studentDao.findStudentById(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(stu);
    }
}
```

- @Before注解表示在调用@Test方法之前会自动调用添加注解的方法

### 总结：

- mapper.xml中的 namespace 表示的本条sql语句所属的命名空间，设置之后通过sqlSession或者Mapper映射类调用sql的时候都可以用 【namespace. sql语句id】 的形式指明调用的是那一条sql。

- spring mybatis 整合一句话总结：spring将mybatis中获取SqlSessionFactory, SqlSession对象的进行di（依赖注入），不需要手动实例化这两个对象。

- spring mybatis 调用流程

  ![image-20210315164217089](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210315164217089.png)