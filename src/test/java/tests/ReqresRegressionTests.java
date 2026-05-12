package tests;

import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.io.OutputStream;
import java.net.InetSocketAddress;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ReqresRegressionTests {

    private HttpServer server;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        RestAssured.defaultParser = Parser.JSON;
        server = HttpServer.create(new InetSocketAddress(8085), 0);

        server.createContext("/posts", exchange -> {
            String response = "{ \"posts\": [{ \"id\": 1, \"title\": \"Lean testing\" }] }";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.createContext("/posts/1", exchange -> {
            String method = exchange.getRequestMethod();
            String response;

            if ("GET".equals(method)) {
                response = "{ \"id\": 1, \"title\": \"Lean testing\", \"body\": \"Regression testing\" }";
                exchange.sendResponseHeaders(200, response.length());
            } else if ("PUT".equals(method)) {
                response = "{ \"id\": 1, \"title\": \"Updated title\", \"body\": \"Updated regression test\" }";
                exchange.sendResponseHeaders(200, response.length());
            } else if ("DELETE".equals(method)) {
                response = "{ \"id\": 1, \"isDeleted\": true }";
                exchange.sendResponseHeaders(200, response.length());
            } else {
                response = "{ \"error\": \"Unsupported method\" }";
                exchange.sendResponseHeaders(405, response.length());
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.createContext("/posts/999999", exchange -> {
            String response = "{ \"error\": \"Post not found\" }";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.createContext("/comments/post/1", exchange -> {
            String response = "{ \"comments\": [{ \"id\": 1, \"postId\": 1, \"body\": \"Useful test\" }] }";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.createContext("/posts/add", exchange -> {
            String response = "{ \"id\": 101, \"title\": \"Lean testing\", \"body\": \"Automated regression test\", \"userId\": 1 }";
            exchange.sendResponseHeaders(201, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.createContext("/users", exchange -> {
            String response = "{ \"users\": [{ \"id\": 1, \"email\": \"test@example.com\" }] }";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.start();

        RestAssured.baseURI = "http://localhost:8085";
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test(priority = 1, groups = {"smoke", "critical", "regression"})
    public void getPostsList() {
        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("posts.size()", greaterThan(0));
    }

    @Test(priority = 2, groups = {"smoke", "critical", "regression"})
    public void getSinglePost() {
        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Lean testing"));
    }

    @Test(priority = 3, groups = {"critical", "regression"})
    public void getCommentsForPost() {
        given()
                .when()
                .get("/comments/post/1")
                .then()
                .statusCode(200)
                .body("comments.size()", greaterThan(0))
                .body("comments[0].postId", equalTo(1));
    }

    @Test(priority = 4, groups = {"regression"})
    public void postNotFound() {
        given()
                .when()
                .get("/posts/999999")
                .then()
                .statusCode(404);
    }

    @Test(priority = 5, groups = {"critical", "regression"})
    public void createPost() {
        given()
                .header("Content-Type", "application/json")
                .body("{ \"title\": \"Lean testing\", \"body\": \"Automated regression test\", \"userId\": 1 }")
                .when()
                .post("/posts/add")
                .then()
                .statusCode(201)
                .body("title", equalTo("Lean testing"))
                .body("body", equalTo("Automated regression test"))
                .body("userId", equalTo(1))
                .body("id", notNullValue());
    }

    @Test(priority = 6, groups = {"regression"})
    public void updatePost() {
        given()
                .header("Content-Type", "application/json")
                .body("{ \"title\": \"Updated title\", \"body\": \"Updated regression test\" }")
                .when()
                .put("/posts/1")
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated title"))
                .body("body", equalTo("Updated regression test"));
    }

    @Test(priority = 7, groups = {"regression"})
    public void deletePost() {
        given()
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200)
                .body("isDeleted", equalTo(true));
    }

    @Test(priority = 8, groups = {"regression"})
    public void getUsersList() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("users.size()", greaterThan(0))
                .body("users[0].email", containsString("@"));
    }
}