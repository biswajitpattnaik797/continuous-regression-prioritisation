package tests;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ReqresRegressionTests {

    @BeforeClass
    public void setup() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test(priority = 1, groups = {"smoke", "critical", "regression"})
    public void getPostsList() {
        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test(priority = 2, groups = {"smoke", "critical", "regression"})
    public void getSinglePost() {
        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", notNullValue())
                .body("title", notNullValue());
    }

    @Test(priority = 3, groups = {"critical", "regression"})
    public void getCommentsForPost() {
        given()
                .when()
                .get("/posts/1/comments")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].postId", equalTo(1));
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
                .post("/posts")
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
                .body("{ \"id\": 1, \"title\": \"Updated title\", \"body\": \"Updated regression test\", \"userId\": 1 }")
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
                .statusCode(200);
    }

    @Test(priority = 8, groups = {"regression"})
    public void getUsersList() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].email", containsString("@"));
    }
}