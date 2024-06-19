package com.entity;

import javax.persistence.*;

@Entity
@Table(name = "save_job")
public class SaveJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public SaveJob() {}

    public SaveJob(Recruitment recruitment, User user) {
        this.recruitment = recruitment;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Recruitment getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
