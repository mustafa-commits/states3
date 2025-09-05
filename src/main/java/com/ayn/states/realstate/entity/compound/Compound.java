package com.ayn.states.realstate.entity.compound;

import com.ayn.states.realstate.entity.dashboard.DashboardUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "compounds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    // صورة الغلاف الخارجية
    @Formula("CONCAT('http://72.60.81.126:8080/V1/api/CompoundAttachment/', cover_image_url)")
    private String coverImageUrl;


    // اسم المجمع
    @Column(nullable = false)
    private String name;

    // عدد المتابعين
    @Formula("(SELECT COUNT(cf.id) FROM compound_followers cf WHERE cf.compound_id = id)")
    private int followersCount;

    @Transient
    private boolean followedByUser;

    // صورة مصغرة
    @Formula("CONCAT('http://72.60.81.126:8080/V1/api/CompoundAttachment/', thumbnail_url)")
    private String thumbnailUrl;

    // العنوان
    private String address;

    // روابط مواقع التواصل
    @Embedded
    private SocialLinks socialLinks;

    // عدد المشاهدين
    @Formula("(SELECT COUNT(DISTINCT COALESCE(ua.app_user_id, ua.unregistered_id)) FROM user_actions ua WHERE ua.compound_id = id AND ua.action_type = 'VIEW')")
    private int viewsCount;

    // عدد العقارات
    private int propertiesCount;

    // وصف المجمع
    @Column(length = 2000)
    private String description;

    // مواصفات المجمع
    @ElementCollection
    @CollectionTable(name = "compound_features", joinColumns = @JoinColumn(name = "compound_id"))
    @Column(name = "feature")
    private List<String> features;

    // منشورات المجمع
    @OneToMany(mappedBy = "compound", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "is_active = true AND approved_at IS NOT NULL")
    @OrderBy("approvedAt DESC")
    private Set<CompoundPost> posts= new HashSet<>();

    // خرائط الوحدات السكنية
    @OneToMany(mappedBy = "compound", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitMap> unitMaps;

    // صورة ثلاثية الأبعاد
    @Formula("CONCAT('http://72.60.81.126:8080/V1/api/CompoundAttachment/', model3d_url)")
    private String model3dUrl;

    // موقع المجمع على الخارطة
    private Double latitude;
    private Double longitude;

    // رقم الاتصال
    private String contactNumber;

    private int governorate;


    private int createUser;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnore
//    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id") // This column will be in the 'compounds' table
//    private DashboardUser createdBy; // Changed from 'int createUser' to 'User createdBy'

    @JsonIgnore
    private Integer updateUser;

    @JsonIgnore
    private LocalDateTime createAt;

    @JsonIgnore
    private LocalDateTime updateAt;

    @JsonIgnore
    private boolean isActive=true;

    @JsonIgnore
    private Integer approvedUser;

    @JsonIgnore
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "compound", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CompoundFollower> followers = new ArrayList<>();


    @PrePersist
    void onCreate(){
        createAt=LocalDateTime.now();
    }


}

