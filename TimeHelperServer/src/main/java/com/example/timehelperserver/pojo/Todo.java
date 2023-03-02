package com.example.timehelperserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    private String id;
    private String title;
    private String content;
    private String createdTime;
    private String startTime;
    private String endTime;
    private int done;
    private int star;

}
