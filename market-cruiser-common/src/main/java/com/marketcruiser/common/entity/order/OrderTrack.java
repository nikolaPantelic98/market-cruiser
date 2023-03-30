package com.marketcruiser.common.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "order_track"
)
public class OrderTrack {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long orderTrackId;

    @Column(
            name = "notes",
            length = 256
    )
    private String notes;

    @Column(
            name = "updated_time"
    )
    private Date updatedTime;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            length = 45,
            nullable = false
    )
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(
            name = "order_id"
    )
    private Order order;
}
