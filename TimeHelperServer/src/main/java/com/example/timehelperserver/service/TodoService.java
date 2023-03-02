package com.example.timehelperserver.service;

import com.example.timehelperserver.dao.TodoMapper;
import com.example.timehelperserver.pojo.TodoList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TodoService {

    @Resource
    private TodoMapper todoMapper;

    public String uploadData(TodoList todoList){
        TodoList t = todoMapper.findByUsername(todoList.getUsername());
        if(t != null){
            todoMapper.deleteByUsername(todoList.getUsername());
        }
        todoMapper.save(todoList);
        return "true";
    }

    public TodoList downloadData(String username){
        return todoMapper.findByUsername(username);
    }
}
