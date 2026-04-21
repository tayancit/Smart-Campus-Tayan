/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author tayan
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException>{

    @Override
    public Response toResponse(LinkedResourceNotFoundException e){
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorMsg(422, "Invalid Reference", e.getMessage()))
                .build();
    }
}