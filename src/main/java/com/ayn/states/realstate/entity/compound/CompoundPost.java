package com.ayn.states.realstate.entity.compound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "compound_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompoundPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long postId;

//    @Formula("CONCAT('http://37.239.42.53:1800/realState/V1/api/CompoundAttachment/', image_url)")
//    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "posts_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "IMAGE_URL")
    private List<String> images;

    private String title;

    @Column(length = 2000)
    private String content;

    private LocalDateTime postDate;

    @JsonIgnore
    private boolean isActive=true;

    @JsonIgnore
    private Integer approvedUser;

    @JsonIgnore
    private LocalDateTime approvedAt;

    @JsonIgnore
    private int createUser;

    @JsonIgnore
    private Integer updateUser;

    @JsonIgnore
    private LocalDateTime createAt;

    @JsonIgnore
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    @JsonIgnore
    private Compound compound;

    public CompoundPost(List<String> images, String title, String content, Compound compound,int createUser) {
        this.images = images;
        this.title = title;
        this.content = content;
        this.compound = compound;
        this.createUser=createUser;
    }

    @PrePersist
    public void onCreate() {
        this.postDate = LocalDateTime.now();
    }

    @Transient
    public List<String> getImages() {
        return images.stream()
                .map(fileName -> "http://31.97.185.232:8080/V1/api/CompoundAttachment/" + fileName)
                .toList();
    }
}

