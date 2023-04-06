package com.marketcruiser.common.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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


    @Transient
    public String getUpdatedTimeOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        return dateFormatter.format(this.updatedTime);
    }

    @Transient
    public void setUpdatedTimeOnForm(String dateString) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        try {
            this.updatedTime = dateFormatter.parse(dateString);
        } catch (ParseException exception) {
            exception.printStackTrace();
        }
    }
}
