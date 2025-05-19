package com.ayn.states.realstate.service.verification;

import com.ayn.states.realstate.entity.verification.UserVerification;
import com.ayn.states.realstate.repository.userVerification.UserVerificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;

@Service
public class VerificationService {
    @Autowired
    UserVerificationRepo userVerificationRepo;

    @Transactional
    public boolean verify(long id, long otp) {
        UserVerification userVerification = userVerificationRepo.findFirstByUserid(id);
        if (userVerification != null) {
            if (userVerification.getSecret() == otp) {
                if (userVerification.getUserId() == id) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(userVerification.getCreateDate());
                    calendar.add(Calendar.MINUTE, 10);
                    return userVerification.getCreateDate().before(calendar.getTime());
                }
            }
        }
        return false;
    }

}
