package rest;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Person;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/f2w1person",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE =  PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPerson(String json){
        Person p = GSON.fromJson(json, Person.class);
        FACADE.addPerson(p.getFirstname(), p.getLastname(), p.getPhone());
        return Response.ok(json).build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response editPerson(String json){
        try{
            Person p = GSON.fromJson(json, Person.class);
            FACADE.editPerson(p);
            return Response.ok(json).build();
        } catch(PersonNotFoundException e) {
            return Response.status(404).entity("{\"code\":"+404+", \"message\":\""+e.getMessage()+"\"}").build();
        }
    }
    
    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response deletePerson(@PathParam("id") int id){
        try{
            FACADE.deletePerson(id);
            return Response.ok("{\"status\": \"deleted\"}").build();
        }catch(PersonNotFoundException e){
            return Response.status(404).entity("{\"code\":"+404+", \"message\":\""+e.getMessage()+"\"}").build();
        }
    }
    
    @Path("all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAll() {
        return GSON.toJson(FACADE.getAllPersons());
    }
    
    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response countPerson() {
        long count = FACADE.countPerson();
        return Response.ok("{\"count\":"+count+"}").build();
    }
    
    @Path("dto/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDTOall() {
        return Response.ok(GSON.toJson(new PersonsDTO(FACADE.getAllPersons()))).build();
    }
    
    @Path("dto/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDTObyId(@PathParam("id") int id){
        try{
            return Response.ok(GSON.toJson(new PersonDTO(FACADE.getPerson(id)))).build();
        }catch(PersonNotFoundException e) {
            return Response.status(404).entity("{\"code\":"+404+", \"message\":\""+e.getMessage()+"\"}").build();
        }
    }
    
    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") int id){
        try{
           return Response.ok(GSON.toJson(FACADE.getPerson(id))).build();
        }catch(PersonNotFoundException e) {
            return Response.status(404).entity("{\"code\":"+404+", \"message\":\""+e.getMessage()+"\"}").build();
        }
    }
    


 
}
