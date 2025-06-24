package com.ayn.states.realstate.service.sections;


import com.ayn.states.realstate.controller.RealStatesController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    @Autowired
    private JdbcClient jdbcClient;


    public List<RealStatesController.AppSections> getAppSections() {
        return null;
    }
}
