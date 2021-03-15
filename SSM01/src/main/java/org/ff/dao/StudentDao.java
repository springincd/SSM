package org.ff.dao;

import org.ff.pojo.Student;

public interface StudentDao {
    public Student findStudentById(int id) throws Exception;
}
