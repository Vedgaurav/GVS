package com.voice.yatraRegistration.memberReg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.yatraRegistration.memberReg.model.RegisteredMember;

public interface RegisterMemDao extends JpaRepository<RegisteredMember,String> {
    
}
