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
