/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;

/**
 *
 * @author tayan
 */
@Path("/sensors")
public class SensorResource{

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor){
        Room room = CampusStore.rooms.get(sensor.getRoomId());

        if (room == null){
            return Response.status(422)
                    .entity("Room does not exist for roomId: " + sensor.getRoomId())
                    .build();
        }
        CampusStore.sensors.put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());

        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId())).build();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Sensor> getSensors(@QueryParam("type") String type){
        if (type == null || type.trim().isEmpty()){
            return CampusStore.sensors.values();
        }

        List<Sensor> filtered = new ArrayList<>();
        for (Sensor sensor : CampusStore.sensors.values()){
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)){
                filtered.add(sensor);
            }
        }
        return filtered;
    }
    @Path("/{id}/readings")
    public SensorReadingResource getReadingResource(@PathParam("id") String id){
        return new SensorReadingResource(id);
}
}