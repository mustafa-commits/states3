package com.ayn.states.realstate.entity.lookup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "lookup",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"type_code", "code"})
        }
)
public class LookUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long Id;

    private int code;

    private String value;

    @Column(name = "type_code")
    private int typeCode;

    @Column(name = "label", length = 255, nullable = false)
    private String label;

    @JsonIgnore
    private Integer sortOrder;

//    @Column(name = "parent_id", nullable = true)
//    private Long parentId;

    @Column(columnDefinition = "TINYINT default 1")
    @JsonIgnore
    private boolean isActive;


//    @OneToMany(mappedBy = "parentId")
//    private List<LookUp> children;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parentId",              // the FK column in this table
            referencedColumnName = "code"    // maps to the parent's `code` field
    )
    @JsonIgnore
    private LookUp parent;

    /**
     * All children whose parent_id == our code.
     */
    @OneToMany(
            mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LookUp> children = new ArrayList<>();

    /***
     * @ManyToOne
     * @JoinColumn(name = "parent_id", insertable = false, updatable = false)
     * private Lookup parent;
     *
     * @OneToMany(mappedBy = "parent")
     * private List<Lookup> children;
     */


    /***    label       code value  typeCode
     *     DONATOR_JOBS	124	مدخلة بيانات	1	مدخلة بيانات
     *     DONATOR_JOBS	125	مدربة 	1	مدربة
     *     DONATOR_JOBS	126	مدرسة	1	مدرسة
     */

}
