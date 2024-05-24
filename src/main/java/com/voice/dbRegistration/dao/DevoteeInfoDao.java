package com.voice.dbRegistration.dao;

import java.util.List;

import com.voice.dbRegistration.model.GetIDFnameGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voice.dbRegistration.model.DevoteeInfo;

public interface DevoteeInfoDao extends JpaRepository<DevoteeInfo, String> {
    public List<DevoteeInfo> findAllByEmail(String email);
    public DevoteeInfo findOneById(String id);

    public void deleteAllByEmail(String email);

    boolean existsByEmail(String email);

    List<DevoteeInfo> findAllByConnectedTo(String Id);

    @Query(value = "SELECT new com.voice.dbRegistration.model.GetIDFnameGender(d.id,d.fname,d.gender,d.age) FROM DevoteeInfo d")
    public List<GetIDFnameGender> findAllDev();
}