package com.example.timehelperserver.dao;

import com.example.timehelperserver.pojo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends MongoRepository<User,String> {

    User findByUsername(String username);

}
