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

/**
 * Represents an order track entity which contains information about the order's status, notes, and updated time.
 * This entity is mapped to the "order_track" table in the database.
 */
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


    /**
     * Returns the updated time in the format "yyyy-MM-dd'T'hh:mm:ss" for display purposes.
     */
    @Transient
    public String getUpdatedTimeOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        return dateFormatter.format(this.updatedTime);
    }

    /**
     * Sets the updated time based on the provided date string in the format "yyyy-MM-dd'T'hh:mm:ss".
     * @param dateString the date string to use for setting the updated time
     */
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
