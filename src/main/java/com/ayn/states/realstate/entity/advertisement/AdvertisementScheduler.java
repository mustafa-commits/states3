package com.ayn.states.realstate.entity.advertisement;

import com.ayn.states.realstate.repository.advertisement.AdvertisementRepository;
import com.ayn.states.realstate.service.msg.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementScheduler {

    @Autowired
    private AdvertisementRepository repo;

    @Autowired
    private WhatsAppService whatsAppService;

    @Scheduled(cron = "0 0 12 * * ?")
    public void checkExpiredAds() {
        repo.findExpiredAds().forEach(ad -> {
                System.out.println("Ad expired: " + ad.getTitle());
                whatsAppService.sendMessage(String.valueOf(ad.getAdvertiserPhone()),"ads get expired");
            });

        repo.findAdsExpiringSoon().forEach(ad -> {
            System.out.println("Ad expired soon: " + ad.getTitle());
            whatsAppService.sendMessage(String.valueOf(ad.getAdvertiserPhone()),"ad will expire soon");
        });


    }
}
