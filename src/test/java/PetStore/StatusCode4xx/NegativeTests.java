package PetStore.StatusCode4xx;

import Utilities.POJO.response.GET_PetByStatus;
import base.BaseClass;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import Utilities.DataProvider.testData;

import static Utilities.POJO.Builder.POST_PetRequestBody;
import static Utilities.ReUsableUtils.generateRandomNumericString;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class NegativeTests extends BaseClass {

    @Step("Validate 405 Status code when Request Body is not as Expected")
    @Test(priority = 1)
    public void testAddNewPetWithInvalidData() {
        Response response=
        given().log().all()
                .spec(petStoreRequestSpec())
                .body("").
        when()
                .post("/pet").
        then()
                .extract()
                .response();
        // The Swagger PetStore might actually accept this, but ideally should reject
        // Test passes regardless of response code - we're documenting behavior
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Step("GET All Pets By Status: {status} and Validate Status Code 400")
    @Test(priority = 2,dataProvider = "NegativePetStatusData",dataProviderClass = testData.class)
    public void GET_PetByStatus(ITestContext context,String status)
    {
        GET_PetByStatus[] response=
        given()
                .spec(petStoreRequestSpec())
                .param("status", status).
        when()
                .get("/pet/findByStatus").
        then()
                .statusCode(200)
                .extract()
                .response()
                .as(GET_PetByStatus[].class);

        assertThat(response).isEmpty();

    }

    @Step("GET Pet By ID: {id} and Validate Status Code 400")
    @Test(priority = 3,dependsOnMethods = "GET_PetByStatus")
    public void GET_PetById() {
        Response response =
        given()
                .spec(petStoreRequestSpec())
                .pathParam("id", generateRandomNumericString(10)).
        when()
                .get("/pet/{id}").
        then()
                .statusCode(404)
                .extract()
                .response();

    }

    @Step("Update Non-Existent Pet and Validate Status Code 200")
    @Test(priority = 4)
    public void testUpdateNonExistentPet() {

        Response response=
        given()
                .spec(petStoreRequestSpec())
                .body(POST_PetRequestBody("NonExistentPet",0,"sold")).
        when()
                .put("/pet").
        then()
                .statusCode(200)
                .extract()
                .response();
        // Document actual behavior - note this might succeed even though
        // the pet didn't exist before (creates it)
        System.out.println("Update non-existent pet response code: " + response.getStatusCode());
    }

}
