package com.example.timehelperserver.controller;

import com.example.timehelperserver.pojo.TodoList;
import com.example.timehelperserver.pojo.User;
import com.example.timehelperserver.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    TodoService todoService;

    @RequestMapping("/upload")
    public String upload(@RequestBody TodoList todoList) {
        return todoService.uploadData(todoList);
    }

    @RequestMapping("/download")
    public String download(String username) {
        TodoList todoList = todoService.downloadData(username);
        if(todoList == null){
            return "false";
        }
        return todoList.toString();
    }
}
