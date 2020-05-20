package com.nuc.server.bean;

public class User {
    private Integer id;
    private String userName;
    private String password;
    private String num;
    private Integer sid;
    private String role;

    public User(){}

    public User(Integer id, String userName, String password, String num, Integer sid, String role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.num = num;
        this.sid = sid;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", num='" + num + '\'' +
                ", sid=" + sid +
                ", role='" + role + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
