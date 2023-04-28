package com.marketcruiser.shoppingcart;

/**
 * Custom exception class for ShoppingCart related exceptions.
 * Extends the built-in Exception class.
 */
public class ShoppingCartException extends Exception{

    public ShoppingCartException(String message) {
        super(message);
    }
}
