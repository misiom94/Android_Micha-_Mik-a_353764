package com.example.user.zbieraczdanych;


public class Person {

    private Integer id;
    private String name;
    private String surname;
    private String birthday;
    private String photoPath;

    public Person(){};
    public Person(Integer _id,String _name,String _surname,String _age, String _path)
    {
        this.id=_id;
        this.name=_name;
        this.surname=_surname;
        this.birthday=_age;
        this.photoPath=_path;
    }

    public String getPhotoPath(){return photoPath;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String age) {
        this.birthday = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
