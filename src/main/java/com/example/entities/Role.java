package com.example.entities;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public enum Role implements Serializable, GrantedAuthority {
    USER, ADMIN;


    @Override
    public String getAuthority() {
        return this.name();
    }
}
