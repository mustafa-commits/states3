package com.ayn.states.realstate.entity.advertisement;

import com.ayn.states.realstate.repository.advertisement.AdvertisementRepository;
import com.ayn.states.realstate.service.msg.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementScheduler {

    @Autowired
    private AdvertisementRepository repo;

    @Autowired
    private WhatsAppService whatsAppService;

    @Autowired
    private JdbcClient jdbcClient;

    @Scheduled(cron = "0 0 12 * * ?")
    public void checkExpiredAds() {
//        repo.findExpiredAds().forEach(ad -> {
//                System.out.println("Ad expired: " + ad.getTitle());
//                whatsAppService.sendMessage(String.valueOf(ad.getAdvertiserPhone()),"ads get expired");
//            });
//
//        repo.findAdsExpiringSoon().forEach(ad -> {
//            System.out.println("Ad expired soon: " + ad.getTitle());
//            whatsAppService.sendMessage(String.valueOf(ad.getAdvertiserPhone()),"ad will expire soon");
//        });

        jdbcClient.sql("""
            SELECT a.title, a.advertiser_phone as phone
            FROM mydb.advertisements a
            WHERE a.is_active = true
              AND DATE_ADD(a.created_at, INTERVAL a.period  DAY) <= NOW()""")
                .query(AdvertisementData.class).list().forEach(e->{
                    whatsAppService.sendMessage(String.valueOf(e.phone()),"your ad "+e.title()+" get expired");
                });

        jdbcClient.sql("""
            SELECT a.title, a.advertiser_phone as phone
            FROM mydb.advertisements a
                WHERE a.active = true
                 AND DATE_ADD(a.created_at, INTERVAL a.period_days DAY) > NOW()
                 AND DATE_SUB(DATE_ADD(a.created_at, INTERVAL a.period DAY), INTERVAL 2 DAY) <= NOW()""")
                .query(AdvertisementData.class).list().forEach(e->{
                    whatsAppService.sendMessage(String.valueOf(e.phone()),"your ad "+e.title()+" will expire soon");
                });


    }




    public record AdvertisementData(String title, Long phone){}
}
