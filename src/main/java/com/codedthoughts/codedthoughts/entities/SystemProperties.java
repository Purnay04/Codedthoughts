package com.codedthoughts.codedthoughts.entities;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "ctsyst_prop")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemProperties extends BaseEntity {
    @Id
    @TableGenerator(
            name = "CT_SEQ",
            table = "ctseq",
            pkColumnName = "seq_name",
            valueColumnName = "seq_value",
            pkColumnValue = "ct_prop_pk",
            allocationSize = 4
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CT_SEQ")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "value")
    private String value;

    @Column(name = "isBoolean")
    private boolean isBoolean;
}
