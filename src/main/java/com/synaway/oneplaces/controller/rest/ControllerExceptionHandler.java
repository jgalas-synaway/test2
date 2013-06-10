package com.synaway.oneplaces.controller.rest;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.synaway.oneplaces.exception.FileOperationException;
import com.synaway.oneplaces.exception.GeneralException;
import com.synaway.oneplaces.exception.ObjectByIdNotFoundException;
import com.synaway.oneplaces.pojo.Response;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = Logger.getLogger(ControllerExceptionHandler.class);
    
    @ExceptionHandler({ NoResultException.class, ObjectByIdNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected Response handleException(GeneralException ex) {
        logGeneralException(ex);
        return new Response(ex);
    }

    @ExceptionHandler({ FileOperationException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected Response handleFileOperationException(FileOperationException ex) {
        logGeneralException(ex);
        return new Response(ex);
    }
    
    @ExceptionHandler({ GeneralException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected Response handleGeneralException(GeneralException ex) {
        logGeneralException(ex);
        return new Response(ex);
    }
    
    @ExceptionHandler({ Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected Response handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return new Response(new GeneralException(ex.getMessage(), GeneralException.GENERAL_EXCEPTION));
    }
    
    private void logGeneralException(GeneralException ex) {
        logger.error("Message: " + ex.getMessage() + ", more information: " 
    + ex.getDeveloperMessage() + ", error code: " + ex.getCode());
    }

}
