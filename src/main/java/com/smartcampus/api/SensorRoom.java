/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;

/**
 *
 * @author tayan
 */
@Path("/rooms")
public class SensorRoom{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Room> getRooms(){
        return CampusStore.rooms.values();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room){
        CampusStore.rooms.put(room.getId(), room);
        return Response.created(URI.create("/api/v1/rooms/" + room.getId())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoom(@PathParam("id") String id){
        return CampusStore.rooms.get(id);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id){
        Room room = CampusStore.rooms.get(id);

        if (room == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!room.getSensorIds().isEmpty()){
            throw new RoomNotEmptyException("Room has active sensors");
        }
        CampusStore.rooms.remove(id);
        return Response.noContent().build();
    }
}