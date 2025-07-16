package com.ayn.states.realstate.service.states;


import com.ayn.states.realstate.controller.RealStatesController;
import com.ayn.states.realstate.entity.lookup.LookUp;
import com.ayn.states.realstate.repository.lookup.LookUpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class StateFeaturesService {


    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private LookUpRepo lookUpRepo;


    public List<RealStatesController.LookUpData> getStateFeatures() {

        return jdbcClient.sql("""
                SELECT l.code, l.value  FROM lookup l where l.type_code=3""").query(RealStatesController.LookUpData.class).list();
    }

    public List<RealStatesController.LookUpData> getAllGovernate() {
        return jdbcClient.sql("""
                SELECT f.code, f.name_ar value from fnd_governorates f""").query(RealStatesController.LookUpData.class).list();
    }


    public List<LookUp> getPropertyType() {
        return lookUpRepo.findByTypeCodeCustom();
    }

    public List<RealStatesController.LookUpData> getOwnershipType() {
        return jdbcClient.sql("""
                SELECT l.code, l.value  FROM lookup l where l.type_code=2""").query(RealStatesController.LookUpData.class).list();
    }
}
