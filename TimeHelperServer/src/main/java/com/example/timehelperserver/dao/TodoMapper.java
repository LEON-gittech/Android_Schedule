package com.example.timehelperserver.dao;

import com.example.timehelperserver.pojo.TodoList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoMapper extends MongoRepository<TodoList, String> {

    TodoList findByUsername(String username);
    void deleteByUsername(String username);

}
