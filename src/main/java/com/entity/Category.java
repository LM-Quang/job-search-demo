package com.entity;

import javax.persistence.*;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "number_choose")
    private int numberChoose;

//    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
//    private List<Recruitment> recruitments;

    public Category() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<Recruitment> getRecruitments() {
//        return recruitments;
//    }
//
//    public void setRecruitments(List<Recruitment> recruitments) {
//        this.recruitments = recruitments;
//    }

    public int getNumberChoose() {
        return numberChoose;
    }

    public void setNumberChoose(int numberChoose) {
        this.numberChoose = numberChoose;
    }
}
