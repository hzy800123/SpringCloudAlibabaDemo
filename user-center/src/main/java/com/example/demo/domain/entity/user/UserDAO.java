package com.example.demo.domain.entity.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDAO {

    @Select("select * from user where id = #{userId}")
    @Options(useCache=false, flushCache=Options.FlushCachePolicy.TRUE)
//    @Options(useCache=true, flushCache=Options.FlushCachePolicy.FALSE)
    public User getUserRecord(Integer userId);
}

