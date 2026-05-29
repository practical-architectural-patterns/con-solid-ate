package com.example.consolidate.participant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;


    private String fullName;
    @NotBlank
    @Email
    private String contactMail;

    public Long getPid() { return pid; }
    public void setPid(Long pid) { this.pid = pid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getContactMail() { return contactMail; }
    public void setContactMail(String contactMail) { this.contactMail = contactMail; }
}