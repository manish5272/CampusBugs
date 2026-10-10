package com.campusbugs.campusbugs.model;

import jakarta.persistence.*;



    @Entity //ante table create cheyadam eh class nunchi
    @Table(name = "users") // table name
    public class User{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(nullable = false , unique = true)
        private String rollNumber;

        @Column(nullable =false)
        private String passwordHash;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
    }

