package com.example.consolidate.participant;

import jakarta.persistence.*;

@Entity
class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    private String fullName;
    private String contactMail;

    public Long getPid() { return pid; }
    public void setPid(Long pid) { this.pid = pid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getContactMail() { return contactMail; }
    public void setContactMail(String contactMail) { this.contactMail = contactMail; }
}