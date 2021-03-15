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
