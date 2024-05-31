package com.voice.dbRegistration.restController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import com.voice.dbRegistration.model.GetIDFnameGender;
import com.voice.dbRegistration.service.DatabaseService;
import com.voice.dbRegistration.utils.security.CustomSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.DevoteeInfo;

@RestController
@RequestMapping("/v1/hlzGlobalReg")
@CrossOrigin(origins = "*")
public class DevoteeInfoRestController {

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Autowired
    DatabaseService databaseService;

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/saveInput")
    public DevoteeInfo insertDevoteeInfo(@RequestBody DevoteeInfo input) {

        // dob shouldc be in "2020-12-31" format
        if (input.getDateOfBirth().length()!=0)
            input.setAge(Helper.calculateAge(input.getDateOfBirth()));
        // DevoteeInfo encrypted = encryptData(input);
        return databaseService.saveInputAndSendMessage(input);
        // return devoteeInfoDao.save(input);
    }

    private DevoteeInfo encryptData(DevoteeInfo input) {
        input.setPrimaryPhone(CustomSecurity.encrypt(input.getPrimaryPhone()));
        return input;
    }

    @GetMapping("/doesUserExist")
    public ResponseEntity<DevoteeInfo> doesExist(Authentication authentication) {
        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);
        return user.map(userAuth -> ResponseEntity.ok(devoteeInfoDao.findByEmailAndConnectedTo(userAuth.getUserEmail(),"guru"))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

    }

    @GetMapping("/fetchAllDepById")
    public ResponseEntity<List<DevoteeInfo>> fetchAllDepByConnectedId(Authentication authentication) {
        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);
        return user.map(userAuth -> ResponseEntity.ok(devoteeInfoDao.findAllByConnectedTo(userAuth.getUserId()))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // localhost:8080/v1/hlzGlobalReg/fetchAllDev
    // localhost:8080/v1/hlzGlobalReg/saveInput

    @PostMapping("/fetchAllDevWithDecryption")
    public List<DevoteeInfo> fetchAllDevWithDec() {
        List<DevoteeInfo> allDev = devoteeInfoDao.findAll();
        try {
            allDev = (List<DevoteeInfo>) allDev.stream().map(oneDev -> {
                oneDev.setPrimaryPhone(CustomSecurity.decrypt(oneDev.getPrimaryPhone()));
                return oneDev;
            }).collect(Collectors.toList());
            return allDev;
        } catch (Exception e) {
            System.out.println("Failed to decrpyt the data");
            return new ArrayList();

        }
    }

    @PostMapping("/fetchAllDev")
    public List<DevoteeInfo> fetchAllDev() {
        List<DevoteeInfo> allDev = devoteeInfoDao.findAll();
        return allDev;
    }

    @PostMapping("/fetchAllDevWithLimitedData")
    public List<GetIDFnameGender> fetchAllDevWithLimitedData() {
        List<GetIDFnameGender> allDev = devoteeInfoDao.findAllDev();
        return allDev;
    }

    @PostMapping("/fetchSpecefic/{userId}")
    public DevoteeInfo fetchSpecefic(@PathVariable("userId") String userId) {
        return devoteeInfoDao.findOneById(userId);
    }

    @DeleteMapping("/deleteInfo")
    private void deleteDevoteeInfo(@RequestBody DevoteeInfo devoteeInfo) {
         devoteeInfoDao.delete(devoteeInfo);
    }

}