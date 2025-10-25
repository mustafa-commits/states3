package com.ayn.states.realstate.repository.advertisement;

import com.ayn.states.realstate.entity.advertisement.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

//    List<Advertisement> findByIsActiveTrue();
//
//    @Query(value = """
//        SELECT * FROM mydb.advertisements a
//        WHERE a.is_active = true
//          AND DATE_ADD(a.created_at, INTERVAL a.period DAY) <= NOW()
//    """, nativeQuery = true)
//    List<Advertisement> findExpiredAds();
//
//    @Query(value = """
//    SELECT * FROM mydb.advertisements a
//    WHERE a.active = true
//      AND DATE_ADD(a.created_at, INTERVAL a.period_days DAY) > NOW()
//      AND DATE_SUB(DATE_ADD(a.created_at, INTERVAL a.period DAY), INTERVAL 2 DAY) <= NOW()
//""", nativeQuery = true)
//    List<Advertisement> findAdsExpiringSoon();

    List<Advertisement> findByIsActiveTrue();

    @Query(value = """
        SELECT a.title, a.advertiser_phone
        FROM mydb.advertisements a
        WHERE a.is_active = true
          AND DATE_ADD(a.created_at, INTERVAL a.period -1 DAY) <= NOW()
    """, nativeQuery = true)
    List<Object[]> findExpiredAdsData();

    @Query(value = """
        SELECT a.title, a.advertiser_phone
        FROM mydb.advertisements a
        WHERE a.is_active = true
          AND DATE_ADD(a.created_at, INTERVAL a.period DAY) > NOW()
          AND DATE_SUB(DATE_ADD(a.created_at, INTERVAL a.period DAY), INTERVAL 2 DAY) <= NOW()
    """, nativeQuery = true)
    List<Object[]> findAdsExpiringSoonData();

}
