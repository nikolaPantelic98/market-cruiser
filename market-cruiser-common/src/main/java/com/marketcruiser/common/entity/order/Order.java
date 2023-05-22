package com.marketcruiser.common.entity.order;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class represents an Order entity in the system. It contains all the necessary information to create
 * and manage an order made by a customer, including information about the customer, shipping address, order details,
 * payment method, order status, and delivery date.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "orders"
)
public class Order {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long orderId;

    @Column(
            name = "first_name",
            nullable = false,
            length = 45
    )
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false,
            length = 45
    )
    private String lastName;
    @Column(
            name = "phone_number",
            nullable = false,
            length = 15
    )
    private String phoneNumber;
    @Column(
            name = "address_line_1",
            nullable = false,
            length = 64
    )
    private String addressLine1;
    @Column(
            name = "address_line_2",
            length = 64
    )
    private String addressLine2;
    @Column(
            name = "city",
            nullable = false,
            length = 45
    )
    private String city;
    @Column(
            name = "state",
            nullable = false,
            length = 45
    )
    private String state;
    @Column(
            name = "post_code",
            nullable = false,
            length = 10
    )
    private String postCode;

    @Column(
            name = "country",
            nullable = false,
            length = 45
    )
    private String country;

    @Column(
            name = "order_time"
    )
    private Date orderTime;

    private float shippingCost;
    private float productCost;
    private float subtotal;
    private float tax;
    private float total;

    private int deliverDays;
    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(
            name = "customer_id"
    )
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();


    public Order(Long orderId, Date orderTime, float productCost, float subtotal, float total) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.total = total;
    }

    /**
     * Copies the address details of the customer to the order instance.
     * Used for setting the order address details to be the same as the customer's address.
     */
    public void copyAddressFromCustomer() {
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setCountry(customer.getCountry().getName());
        setPostCode(customer.getPostCode());
        setState(customer.getState());
    }

    /**
     * Copies the address details of the provided Address object to the order instance.
     * Used for setting the order address details to be the same as the shipping address.
     * @param address the Address object containing the address details to copy
     */
    public void copyShippingAddress(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setCountry(address.getCountry().getName());
        setPostCode(address.getPostCode());
        setState(address.getState());
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", subtotal=" + subtotal +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", customer=" + customer.getFullName() +
                '}';
    }

    /**
     * Returns the destination of the order as a string.
     * Used to display the city, state, and country of the order.
     */
    @Transient
    public String getDestination() {
        String destination =  city + ", ";
        if (state != null && !state.isEmpty()) destination += state + ", ";
        destination += country;

        return destination;
    }

    /**
     * Returns the full name of the customer who placed an order as a string.
     */
    @Transient
    public String getCustomerName() {
        String name = firstName;

        if (lastName != null && !lastName.isEmpty()) {
            name += " " + lastName + ",";
        }

        return name;
    }

    /**
     * Returns the full address of the customer as a string.
     */
    @Transient
    public String getCustomerAddress() {
        String address = "";

        if (!addressLine1.isEmpty()) {
            address += addressLine1;
        }

        if (addressLine2 != null && !addressLine2.isEmpty()) {
            address += ", " + addressLine2;
        }

        if (!city.isEmpty()) {
            address += ", " + city;
        }

        if (state != null && !state.isEmpty()) {
            address += ", " + state;
        }

        address += ", " + country + ",";

        return address;
    }

    /**
     * Returns the postal code and phone number of the customer as a string.
     */
    @Transient
    public String getCustomerPostCodeAndPhoneNumber() {
        String postCodeAndPhoneNumber = "";

        if (!postCode.isEmpty()) {
            postCodeAndPhoneNumber += "Post Code: " + postCode;
        }

        if (!phoneNumber.isEmpty()) {
            postCodeAndPhoneNumber += ", Phone Number: " + phoneNumber;
        }

        return postCodeAndPhoneNumber;
    }

    /**
     * This method returns the delivery date of the order in the "yyyy-MM-dd" format
     */
    @Transient
    public String getDeliverDateOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(this.deliverDate);
    }

    /**
     * This method sets the delivery date of the order based on the given dateString parameter
     * @param dateString a String representing the delivery date in the "yyyy-MM-dd" format
     */
    public void setDeliverDateOnForm(String dateString) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.deliverDate = dateFormatter.parse(dateString);
        } catch (ParseException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method returns the recipient's name by concatenating the first name and last name
     * @return a String representing the recipient's full name
     */
    @Transient
    public String getRecipientName() {
        String name = firstName;

        if (lastName != null && !lastName.isEmpty()) {
            name += " " + lastName;
        }

        return name;
    }

    /**
     * This method returns the recipient's full address
     */
    @Transient
    public String getRecipientAddress() {
        String address = "";

        if (!addressLine1.isEmpty()) {
            address += addressLine1;
        }

        if (addressLine2 != null && !addressLine2.isEmpty()) {
            address += ", " + addressLine2;
        }

        if (!city.isEmpty()) {
            address += ", " + city;
        }

        if (state != null && !state.isEmpty()) {
            address += ", " + state;
        }

        address += ", " + country;

        if (!postCode.isEmpty()) {
            address += ", " + postCode;
        }

        return address;
    }

    /**
     * This method checks if the payment method for the order is Cash on Delivery (COD)
     * @return true if the payment method is COD, false otherwise
     */
    @Transient
    public boolean isCOD() {
        return paymentMethod.equals(PaymentMethod.COD);
    }

    /**
     * This method checks if the order has the status "PROCESSING" in its order tracks
     * @return true if the order has the status "PROCESSING", false otherwise
     */
    @Transient
    public boolean isProcessing() {
        return hasStatus(OrderStatus.PROCESSING);
    }

    /**
     * This method checks if the order has the status "PICKED" in its order tracks
     * @return true if the order has the status "PICKED", false otherwise
     */
    @Transient
    public boolean isPicked() {
        return hasStatus(OrderStatus.PICKED);
    }

    /**
     * This method checks if the order has the status "SHIPPING" in its order tracks
     * @return true if the order has the status "SHIPPING", false otherwise
     */
    @Transient
    public boolean isShipping() {
        return hasStatus(OrderStatus.SHIPPING);
    }

    /**
     * This method checks if the order has the status "DELIVERED" in its order tracks
     * @return true if the order has the status "DELIVERED", false otherwise
     */
    @Transient
    public boolean isDelivered() {
        return hasStatus(OrderStatus.DELIVERED);
    }

    /**
     * This method checks if the order has the status "RETURN_REQUESTED" in its order tracks
     * @return true if the order has the status "RETURN_REQUESTED", false otherwise
     */
    @Transient
    public boolean isReturnRequested() {
        return hasStatus(OrderStatus.RETURN_REQUESTED);
    }

    /**
     * This method checks if the order has the status "RETURNED" in its order tracks
     * @return true if the order has the status "RETURNED", false otherwise
     */
    @Transient
    public boolean isReturned() {
        return hasStatus(OrderStatus.RETURNED);
    }

    /**
     * Checks if the order has a specific status in its order tracks.
     * @param status the status to check for
     * @return boolean indicating whether the order has the specified status
     */
    public boolean hasStatus(OrderStatus status) {
        for (OrderTrack aTrack : orderTracks) {
            if (aTrack.getStatus().equals(status)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method returns a string containing the short name of each product in the order.
     * @return string containing HTML <'li'> tags with each product's short name
     */
    @Transient
    public String getProductNames() {
        String productNames = "";

        for (OrderDetail detail : orderDetails) {
            productNames += "<li>" + detail.getProduct().getShortName() + "</li>";
        }

        productNames += "</ul>";

        return productNames;
    }
}
