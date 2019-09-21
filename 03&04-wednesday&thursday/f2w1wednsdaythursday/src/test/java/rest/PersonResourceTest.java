package rest;

import entities.Person;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    //Read this line from a settings-file  since used several places
    private static final String TEST_DB = "jdbc:mysql://localhost:3307/startcode_test";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    Person p = new Person("Mr.", "Bean", "85274189");
    Person p2 = new Person("Mrs.","Bean", "96385287");

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

        //NOT Required if you use the version of EMF_Creator.createEntityManagerFactory used above        
        //System.setProperty("IS_TEST", TEST_DB);
        //We are using the database on the virtual Vagrant image, so username password are the same for all dev-databases
        
        httpServer = startServer();
        
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
   
        RestAssured.defaultParser = Parser.JSON;
    }
    
    @AfterAll
    public static void closeTestServer(){
        //System.in.read();
         httpServer.shutdownNow();
    }
    
    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p);
            em.getTransaction().commit();
            
            em.getTransaction().begin();
            em.persist(p2); 
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }
   
    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
        .contentType("application/json").when()
        .get("/person/").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("msg", equalTo("Hello World"));   
    }
    
    @Test
    public void testCount() throws Exception {
        given()
        .contentType("application/json").when()
        .get("/person/count").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("count", equalTo(2));   
    }
    
    @Test
    public void testGetAll() throws Exception {
        given()
        .contentType("application/json").when()
        .get("/person/all").then().log().body()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("[0].firstname", equalTo("Mr."))
        .body("[1].phone", equalTo("96385287"))
        .body("size()", equalTo(2));
    }
    
    @Test
    public void testGetById() throws Exception {
        //Person 1
        given()
        .contentType("application/json").when()
        .get("/person/" + p.getId()).then().log().body()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("id", equalTo(p.getId().intValue()))    
        .body("firstname", equalTo(p.getFirstname()))            
        .body("lastname", equalTo(p.getLastname()))      
        .body("phone", equalTo(p.getPhone()));      
        
        //Person 2
        given()
        .contentType("application/json").when()
        .get("/person/" + p2.getId()).then().log().body()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("id", equalTo(p2.getId().intValue()))       
        .body("firstname", equalTo(p2.getFirstname()))             
        .body("lastname", equalTo(p2.getLastname()))      
        .body("phone", equalTo(p2.getPhone()));          
    }
    
    @Test
    public void testGetByIdFail() throws Exception {
        given()
        .contentType("application/json").when()
        .get("/person/2321").then().log().body()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))    
        .body("message", equalTo("No person with provided id found"));
    }
    
    @Test
    public void testEdit() throws Exception {
        p2.setFirstname("Teddy");
        p2.setPhone("112");
        given()
        .contentType(ContentType.JSON).when()
        .body(p2)
        .put("/person/").then().log().body()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("id", equalTo(p2.getId().intValue()))      
        .body("firstname", equalTo(p2.getFirstname()))           
        .body("lastname", equalTo(p2.getLastname()))      
        .body("phone", equalTo(p2.getPhone())); 
    }
    
    @Test
    public void testEditFail() throws Exception {
        String json = "{\"id\": \"2345\"}";
        given()
        .contentType(ContentType.JSON).when()
        .body(json)
        .put("/person/").then().log().body()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))
        .body("message", equalTo("No person with provided id found"));
    }
    
    @Test
    public void testDelete() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .delete("/person/"+p.getId()).then().log().body()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("status", equalTo("deleted"));
    }
    //
    @Test
    public void testDeleteFail() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .delete("/person/56890").then().log().body()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))    
        .body("message", equalTo("Could not delete, provided id does not exist"));
    }
    
}
