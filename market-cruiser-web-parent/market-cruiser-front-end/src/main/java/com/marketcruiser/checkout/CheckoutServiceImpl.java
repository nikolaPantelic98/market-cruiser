package com.marketcruiser.checkout;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.common.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    private static final int DIM_DIVISOR = 139;

    // prepares the checkout information by calculating the various costs
    @Override
    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();

        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());

        return checkoutInfo;
    }

    // calculates the total cost for shipping all items in the cart using the specified shipping rate
    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0F;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();

            item.setShippingCost(shippingCost);

            shippingCostTotal += shippingCost;
        }

        return shippingCostTotal;
    }

    // calculates the total cost of all products in the cart
    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0F;

        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        return total;
    }

    // calculates the total cost of all items in the cart, taking into account the quantity of each item and its unit cost
    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0F;

        for (CartItem item : cartItems) {
            cost += item.getQuantity() * item.getProduct().getCost();
        }

        return cost;
    }
}
