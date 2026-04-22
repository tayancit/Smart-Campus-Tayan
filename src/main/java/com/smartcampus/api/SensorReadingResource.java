/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author tayan
 */
public class SensorReadingResource{

    private String sensorId;

    public SensorReadingResource(String sensorId){
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings(){
        return CampusStore.readings.getOrDefault(sensorId, new ArrayList<>());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading){

        Sensor sensor = CampusStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Sensor is under maintenance")
                    .build();
        }
        CampusStore.readings
                .computeIfAbsent(sensorId, k -> new ArrayList<>())
                .add(reading);
        sensor.setCurrentValue(reading.getValue());
        return Response.status(Response.Status.CREATED).build();
    }
}