package com.example.demo.domain.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

45345@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;

    private String userName;

    private String role;

    private Date createTime;

    private Date updateTime;

//    public User() {
//    }
//
//    public User(Integer id, String userName, String role, Date createTime, Date updateTime) {
//        this.id = id;
//        this.userName = userName;
//        this.role = role;
//        this.createTime = createTime;
//        this.updateTime = updateTime;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//
//    public Date getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }
//
//    public Date getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(Date updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    @Override
//    public String toString() {
//        final SimpleDateFormat sdf =
//                new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
//
//        // Set as Asia/Shanghai time zone
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//
//        return "User{" +
//                "id=" + id +
//                ", userName='" + userName + '\'' +
//                ", role='" + role + '\'' +
//                ", createTime=" + sdf.format(createTime) +
//                ", updateTime=" + sdf.format(updateTime) +
//                '}';
//    }
}
