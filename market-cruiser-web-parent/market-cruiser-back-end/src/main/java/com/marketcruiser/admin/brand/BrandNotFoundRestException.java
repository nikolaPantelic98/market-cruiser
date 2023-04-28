package com.marketcruiser.admin.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested brand is not found in the system.
 * This exception is used to return a 404 HTTP status code with the reason "Brand not found".
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Brand not found")
public class BrandNotFoundRestException extends Exception{


}
