package com.synaway.oneplaces.pojo;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.synaway.oneplaces.exception.GeneralException;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Response {

    private Object data;
    private ErrorMessage error;

    public Response() {

    }

    public Response(GeneralException ex) {
        super();
        error = new ErrorMessage();
        error.setDeveloperMessage(ex.getDeveloperMessage());
        error.setMessage(ex.getMessage());
        error.setCode(ex.getCode());
    }
    
    public Response(Exception ex) {
        super();
        error = new ErrorMessage();
        error.setMessage(ex.getMessage());

    }

    public Response(Object data) {
        super();
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage error) {
        this.error = error;
    }

}
