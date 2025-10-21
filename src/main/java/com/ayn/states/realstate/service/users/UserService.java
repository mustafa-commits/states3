package com.ayn.states.realstate.service.users;


import com.ayn.states.realstate.controller.CredentialController;
import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.entity.verification.DTO.VerificationRequest;
import com.ayn.states.realstate.entity.verification.DTO.VerificationResponse;
import com.ayn.states.realstate.entity.verification.UserVerification;
import com.ayn.states.realstate.enums.CodeSend;
import com.ayn.states.realstate.enums.LoginStatus;
import com.ayn.states.realstate.enums.SignUpStatus;
import com.ayn.states.realstate.enums.UserStatus;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.models.user.UserCheck;
import com.ayn.states.realstate.models.user.UserCheckNumber;
import com.ayn.states.realstate.repository.user.UsersRepo;
import com.ayn.states.realstate.repository.userVerification.UserVerificationRepo;
import com.ayn.states.realstate.service.msg.WhatsAppService;
import com.ayn.states.realstate.service.token.TokenService;
import com.ayn.states.realstate.service.verification.VerificationService;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class UserService {

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    WhatsAppService whatsAppService;

    @Autowired
    UserVerificationRepo userVerificationRepo;

    @Autowired
    VerificationService verificationService;

    @Autowired
    TokenService tokenService;


//    @Bean
//    public String otp(){
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        System.out.println(generateRandomPassword(1,CodeSend.WHATS_APP,"7737678540"));
//        return "";
//    }


    public UserCheckNumber login(String userNum, String countryCode, CodeSend codeSend) {
        userNum = convertArabicNumbersToEnglish(userNum);

        int len = userNum.length();
        if (len >= 3 && userNum.startsWith("964")) {
            userNum = userNum.substring(3);
            len -= 3;
        }
        if (len >= 2 && userNum.startsWith("07")) {
            userNum = '7' + userNum.substring(2);
        }

        Optional<Integer> userIdOpt = usersRepo.existsByPhoneNumber(userNum);
        if (!userIdOpt.isPresent()) {
            return new UserCheckNumber(new UserCheck(LoginStatus.NOT_REGISTER, null), 0L);
        }

        int userId = userIdOpt.get();
        String fullPhone = countryCode + userNum;


        if (codeSend == CodeSend.WHATS_APP) {
            int code = generateRandomPassword(userId, codeSend, fullPhone);
//            whatsAppService.sendMessage(
//                    fullPhone,
//                    "*" + code + "* هو كود التحقق الخاص بك. للحفاظ على معلوماتك، لا تشارك هذا الكود مع أي شخص.\n"
//            );
            return new UserCheckNumber(new UserCheck(LoginStatus.REGISTER, fullPhone), userId);
        } else if (codeSend == CodeSend.TELEGRAM) {
            String loginKey = countryCode.charAt(0) == '+'
                    ? countryCode.substring(1) + userNum + "+"
                    : countryCode + userNum + "+";
            return new UserCheckNumber(new UserCheck(LoginStatus.REGISTER, loginKey), userId);
        }

        throw new UnauthorizedException("phone not found");
    }



    private String formatNumber(String num, String region) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber NumberProto = phoneUtil.parse(num, region);
            boolean isValid = phoneUtil.isValidNumber(NumberProto);
            return phoneUtil.format(NumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            throw new UnauthorizedException("phone wrong");
        }
    }

    public static String convertArabicNumbersToEnglish(String arabicNumber) {
        // Mapping between Arabic and English digits
        char[] arabicDigits = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};
        char[] englishDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // Replace each Arabic digit in the input string with its corresponding English digit
        StringBuilder englishNumber = new StringBuilder(arabicNumber);
        for (int i = 0; i < arabicDigits.length; i++) {
            englishNumber = new StringBuilder(englishNumber.toString().replace(arabicDigits[i], englishDigits[i]));
        }

        return englishNumber.toString();
    }



    public int generateRandomPassword(int id, CodeSend codeSend, String phone) {
        int code = 0;
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//
//        for (int i = 0; i < 6; i++) {
//            code = code * 10 + random.nextInt(10);
//        }
        code = ThreadLocalRandom.current().nextInt(100000, 1_000_000);
        userVerificationRepo.save(new UserVerification(id, code, codeSend, phone));
        return code;
    }



    public VerificationResponse verification(VerificationRequest verificationRequest) {

        if (verificationService.verify(verificationRequest.id(), verificationRequest.otp())) {

            return new VerificationResponse(tokenService.generateToken(null, verificationRequest.id()), usersRepo.findNameById(verificationRequest.id()));
        } else throw new UnauthorizedException("info not correct");

    }

    public SignUpStatus signUp(CredentialController.SignUpRequest signUpRequest) {

        if (usersRepo.existsByPhoneNumber(signUpRequest.phoneNumber()).isPresent()) {
            return SignUpStatus.EXISTS;
        }else {
            usersRepo.save(new Users(signUpRequest.firstName(),
                    signUpRequest.lastName(), signUpRequest.phoneNumber(),signUpRequest.country(), signUpRequest.governorate(), LocalDateTime.now(), UserStatus.ACTIVE));
            return SignUpStatus.CREATED;
        }

    }
}
