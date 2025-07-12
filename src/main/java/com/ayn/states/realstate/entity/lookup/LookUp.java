package com.ayn.states.realstate.entity.lookup;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long Id;

    private int code;

    private String value;

    @Column(name = "type_code")
    private int typeCode;

    @Column(name = "label", length = 255, nullable = false)
    private String label;

    private Integer sortOrder;

    @Column(name = "parent_id", nullable = true)
    private Long parentId;

    @Column(columnDefinition = "TINYINT default 1")
    private boolean isActive;

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
