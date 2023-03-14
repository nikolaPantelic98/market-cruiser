package com.marketcruiser.common.entity.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

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


    public Settings(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(key, settings.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Settings{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
