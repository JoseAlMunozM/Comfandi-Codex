package com.comfandi.korlon.entities;


import com.comfandi.korlon.enums.Profiles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "countable_data")
public class CountableDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "regional")
    private String regional;

    @Column(name = "profile")
    private String profile;

    @Column(name = "group")
    private int group;

    @Column(name = "internal_group")
    private int internalGroup;

    @Column(name = "account")
    private String majorAccount;

    @Column(name = "countable_key")
    private int countableKey;

    @Column(name = "beneficiary")
    private String beneficiaryCenter;

    @Column(name = "is_cebe")
    private boolean cebe;

    @Column(name = "is_account")
    private boolean account;

}
