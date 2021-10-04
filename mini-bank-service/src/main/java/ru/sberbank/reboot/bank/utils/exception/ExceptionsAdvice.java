package ru.sberbank.reboot.bank.utils.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionsAdvice {

    @ResponseBody
    @ExceptionHandler({
      ClientNotFoundException.class,
      AccountNotFoundException.class,
      AccountNotFoundForClientException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError notFoundRuntimeError(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, e);
    }

    @ResponseBody
    @ExceptionHandler({
      AccountNotApplicableForTransferingException.class,
      AccountNotEnoughFunds.class,
      AccountDeleteHasBalanceException.class,
      AccountDeleteAlreadyDeletedException.class,
      AccountDeletedException.class
    })
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    ApiError notAcceptableRuntimeError(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_ACCEPTABLE, e);
    }

    @ResponseBody
    @ExceptionHandler({
      InvalidFormatException.class
    })
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    ApiError mismatchedInputException(MismatchedInputException e) {
        return new ApiError(HttpStatus.NOT_ACCEPTABLE, e);
    }

    @ResponseBody
    @ExceptionHandler({
      PropertyValueException.class
    })
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    ApiError propertyValueException(PropertyValueException e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
}
