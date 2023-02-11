package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "settings"
)
public class Settings {

    @Id
    @Column(
            name = "settings_key",
            nullable = false,
            length = 128
    )
    private String key;
    @Column(
            name = "value",
            nullable = false,
            length = 1024
    )
    private String value;
    @Enumerated(EnumType.STRING)
    @Column(
            length = 45,
            nullable = false
    )
    private SettingsCategory category;
}
