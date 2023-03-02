package com.example.timehelperserver.pojo;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Todos")
public class TodoList {
    @Id
    private String id;
    private String time;
    private String username;
    private List<Todo> todos;

    @Override
    public String toString() {
        return JSONObject.toJSONString(todos);
    }
}
