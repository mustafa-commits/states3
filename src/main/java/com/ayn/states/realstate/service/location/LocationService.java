package com.ayn.states.realstate.service.location;

import com.ayn.states.realstate.dto.location.Area;
import com.ayn.states.realstate.dto.location.Province;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * خدمة إدارة المواقع الجغرافية - المحافظات والمناطق
 * Service for managing geographical locations - Provinces and Areas
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final JdbcClient jdbcClient;

    // SQL Queries as constants for better maintainability
    private static final String SELECT_ALL_PROVINCES =
            "SELECT id, name FROM provinces ORDER BY name";

    private static final String SELECT_AREAS_BY_PROVINCE =
            "SELECT id, name FROM areas WHERE province_id = :provinceId ORDER BY name";

    private static final String SELECT_PROVINCE_BY_ID =
            "SELECT id, name FROM provinces WHERE id = :id";

    private static final String SELECT_AREA_BY_ID =
            "SELECT id, province_id, name FROM areas WHERE id = :id";

    // Row Mappers - استخدام Records
    private final RowMapper<Province> provinceMapper = (rs, rowNum) -> {
        try {
            return new Province(
                    rs.getInt("id"),
                    rs.getString("name")
            );
        } catch (Exception e) {
            log.error("Error mapping province row: {}", e.getMessage());
            throw new DataAccessException("Failed to map province data") {};
        }
    };

    private final RowMapper<Area> areaMapper = (rs, rowNum) -> {
        try {
            return new Area(
                    rs.getInt("id"),
                    rs.getString("name")
            );
        } catch (Exception e) {
            log.error("Error mapping area row: {}", e.getMessage());
            throw new DataAccessException("Failed to map area data") {};
        }
    };

    /**
     * الحصول على جميع المحافظات
     * Get all provinces with caching
     *
     * @return قائمة بجميع المحافظات مرتبة أبجدياً
     */
    @Cacheable(value = "provinces", unless = "#result.isEmpty()")
    public List<Province> getAllProvinces() {
        try {
            log.debug("Fetching all provinces");
            List<Province> provinces = jdbcClient.sql(SELECT_ALL_PROVINCES)
                    .query(provinceMapper)
                    .list();

            log.info("Successfully retrieved {} provinces", provinces.size());
            return provinces;

        } catch (DataAccessException e) {
            log.error("Failed to fetch provinces: {}", e.getMessage());
            throw new LocationServiceException("Unable to retrieve provinces", e);
        }
    }

    /**
     * الحصول على المناطق حسب معرف المحافظة
     * Get areas by province ID with validation
     *
     * @param provinceId معرف المحافظة
     * @return قائمة المناطق التابعة للمحافظة
     * @throws IllegalArgumentException إذا كان معرف المحافظة غير صالح
     * @throws LocationServiceException إذا حدث خطأ في قاعدة البيانات
     */
    @Cacheable(value = "areasByProvince", key = "#provinceId", unless = "#result.isEmpty()")
    public List<Area> getAreasByProvinceId(int provinceId) {
        if (provinceId <= 0) {
            throw new IllegalArgumentException("Province ID must be positive");
        }

        try {
            log.debug("Fetching areas for province ID: {}", provinceId);

            // التحقق من وجود المحافظة أولاً
            if (!provinceExists(provinceId)) {
                log.warn("Province with ID {} does not exist", provinceId);
                throw new LocationServiceException("Province not found with ID: " + provinceId);
            }

            List<Area> areas = jdbcClient.sql(SELECT_AREAS_BY_PROVINCE)
                    .param("provinceId", provinceId)
                    .query(areaMapper)
                    .list();

            log.info("Successfully retrieved {} areas for province ID: {}", areas.size(), provinceId);
            return areas;

        } catch (DataAccessException e) {
            log.error("Failed to fetch areas for province ID {}: {}", provinceId, e.getMessage());
            throw new LocationServiceException("Unable to retrieve areas for province: " + provinceId, e);
        }
    }

    /**
     * الحصول على محافظة بمعرفها
     * Get province by ID
     *
     * @param provinceId معرف المحافظة
     * @return المحافظة إن وجدت
     */
    @Cacheable(value = "province", key = "#provinceId")
    public Optional<Province> getProvinceById(int provinceId) {
        if (provinceId <= 0) {
            return Optional.empty();
        }

        try {
            log.debug("Fetching province with ID: {}", provinceId);

            List<Province> provinces = jdbcClient.sql(SELECT_PROVINCE_BY_ID)
                    .param("id", provinceId)
                    .query(provinceMapper)
                    .list();

            return provinces.isEmpty() ? Optional.empty() : Optional.of(provinces.get(0));

        } catch (DataAccessException e) {
            log.error("Failed to fetch province with ID {}: {}", provinceId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * الحصول على منطقة بمعرفها
     * Get area by ID
     *
     * @param areaId معرف المنطقة
     * @return المنطقة إن وجدت
     */
    @Cacheable(value = "area", key = "#areaId")
    public Optional<Area> getAreaById(int areaId) {
        if (areaId <= 0) {
            return Optional.empty();
        }

        try {
            log.debug("Fetching area with ID: {}", areaId);

            List<Area> areas = jdbcClient.sql(SELECT_AREA_BY_ID)
                    .param("id", areaId)
                    .query(areaMapper)
                    .list();

            return areas.isEmpty() ? Optional.empty() : Optional.of(areas.get(0));

        } catch (DataAccessException e) {
            log.error("Failed to fetch area with ID {}: {}", areaId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * التحقق من وجود المحافظة
     * Check if province exists
     *
     * @param provinceId معرف المحافظة
     * @return true إذا كانت المحافظة موجودة
     */
    public boolean provinceExists(int provinceId) {
        if (provinceId <= 0) {
            return false;
        }

        try {
            Integer count = jdbcClient.sql("SELECT COUNT(*) FROM provinces WHERE id = :id")
                    .param("id", provinceId)
                    .query(Integer.class)
                    .single();

            return count != null && count > 0;

        } catch (DataAccessException e) {
            log.error("Failed to check province existence for ID {}: {}", provinceId, e.getMessage());
            return false;
        }
    }

    /**
     * عدد المناطق في محافظة معينة
     * Count areas in a specific province
     *
     * @param provinceId معرف المحافظة
     * @return عدد المناطق
     */
    @Cacheable(value = "areaCount", key = "#provinceId")
    public int countAreasByProvinceId(int provinceId) {
        if (provinceId <= 0) {
            return 0;
        }

        try {
            Integer count = jdbcClient.sql("SELECT COUNT(*) FROM areas WHERE province_id = :provinceId")
                    .param("provinceId", provinceId)
                    .query(Integer.class)
                    .single();

            return count != null ? count : 0;

        } catch (DataAccessException e) {
            log.error("Failed to count areas for province ID {}: {}", provinceId, e.getMessage());
            return 0;
        }
    }

    /**
     * البحث في المحافظات بالاسم
     * Search provinces by name
     *
     * @param searchTerm مصطلح البحث
     * @return قائمة المحافظات المطابقة
     */
    public List<Province> searchProvincesByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProvinces();
        }

        try {
            String searchPattern = "%" + searchTerm.trim() + "%";

            return jdbcClient.sql("SELECT id, name FROM provinces WHERE name LIKE :searchTerm ORDER BY name")
                    .param("searchTerm", searchPattern)
                    .query(provinceMapper)
                    .list();

        } catch (DataAccessException e) {
            log.error("Failed to search provinces with term '{}': {}", searchTerm, e.getMessage());
            throw new LocationServiceException("Unable to search provinces", e);
        }
    }

    /**
     * البحث في المناطق بالاسم ومعرف المحافظة
     * Search areas by name and province ID
     *
     * @param provinceId معرف المحافظة
     * @param searchTerm مصطلح البحث
     * @return قائمة المناطق المطابقة
     */
    public List<Area> searchAreasByName(int provinceId, String searchTerm) {
        if (provinceId <= 0) {
            throw new IllegalArgumentException("Province ID must be positive");
        }

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAreasByProvinceId(provinceId);
        }

        try {
            String searchPattern = "%" + searchTerm.trim() + "%";

            return jdbcClient.sql("""
                    SELECT id, province_id, name FROM areas 
                    WHERE province_id = :provinceId AND name LIKE :searchTerm 
                    ORDER BY name
                    """)
                    .param("provinceId", provinceId)
                    .param("searchTerm", searchPattern)
                    .query(areaMapper)
                    .list();

        } catch (DataAccessException e) {
            log.error("Failed to search areas for province {} with term '{}': {}",
                    provinceId, searchTerm, e.getMessage());
            throw new LocationServiceException("Unable to search areas", e);
        }
    }
}

/**
 * استثناء مخصص لخدمة المواقع
 * Custom exception for LocationService
 */
class LocationServiceException extends RuntimeException {
    public LocationServiceException(String message) {
        super(message);
    }

    public LocationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}