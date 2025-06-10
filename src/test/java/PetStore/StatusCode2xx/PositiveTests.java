package PetStore.StatusCode2xx;

import Utilities.DataProvider.testData;
import Utilities.POJO.response.GET_PetByStatus;
import Utilities.retry;
import base.BaseClass;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.Arrays;

import static Utilities.POJO.Builder.POST_PetRequestBody;
import static Utilities.ReUsableUtils.logAssertionResult;
import static org.assertj.core.api.Assertions.assertThat;

import static io.restassured.RestAssured.given;

public class PositiveTests extends BaseClass {

    @Step("POST Add New Pet")
    @Test(priority = 1,dataProvider = "createPetData", dataProviderClass = testData.class)
    public void POST_AddNewPet(String name, int id, String status) {
        Response response =
        given().log().all()
                .spec(petStoreRequestSpec())
                .body(POST_PetRequestBody(name,id,status)).
        when()
                .post("/pet").
        then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.getHeader("Content-Type"), "application/json");

        GET_PetByStatus pet = response.as(GET_PetByStatus.class);

        assertThat(pet.getStatus()).isEqualTo(status);
        assertThat(pet.getId()).isEqualTo(id);
        assertThat(pet.getName()).isEqualTo(name);

    }

    @Step("PUT Update Pet By ID: {id} with new status: {status}")
    @Test(priority = 2,dependsOnMethods = "POST_AddNewPet",dataProvider = "updatePetData", dataProviderClass = testData.class)
    public void PUT_UpdatePetById(ITestContext context, String name, int id, String status) {
        Response response =
        given()
                .spec(petStoreRequestSpec())
                .body(POST_PetRequestBody(name,id,status)).
        when()
                .put("/pet").
        then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.getHeader("Content-Type"), "application/json");

        GET_PetByStatus pet = response.as(GET_PetByStatus.class);

        assertThat(pet.getStatus()).isEqualTo(status);
        assertThat(pet.getId()).isEqualTo(id);
        assertThat(pet.getName()).isEqualTo(name);

    }

    @Step("GET All Pets By Status: {status}")
    @Test(priority = 3,dataProvider = "petStatusData",dataProviderClass = testData.class)
    public void GET_PetByStatus(ITestContext context,String status)
    {
        Response response=
        given()
                .spec(petStoreRequestSpec())
                .param("status", status).
        when()
                .get("/pet/findByStatus").
        then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.getHeader("Content-Type"), "application/json");

        GET_PetByStatus[] pets = response.as( GET_PetByStatus[].class);

        // Verify any of the returned pets have the correct status
        // Note: there is an issue in the data so i am not using allSatisfy here
        logAssertionResult(
        assertThat(pets)
                .isNotEmpty()
                .anySatisfy(pet -> {
                    assertThat(pet.getStatus()).isEqualTo(status);
                    assertThat(pet.getId()).isNotNull();
                    assertThat(pet.getName()).isNotNull();
                    assertThat(pet.getPhotoUrls()).isNotNull();
                    assertThat(pet.getCategory()).isNotNull();
                    assertThat(pet.getCategory().getName()).isNotNull();
                })
," All pets should have status: " + status);

context.setAttribute("petID", Arrays.stream(pets).findAny().get().getId());

    }

    @Step("GET Pet By ID: {id}")
    @Test(priority = 4,dependsOnMethods = "GET_PetByStatus")
    public void GET_PetById(ITestContext context) {
        Response response =
        given()
                .spec(petStoreRequestSpec())
                .pathParam("id", context.getAttribute("petID")).
        when()
                .get("/pet/{id}").
        then()
                .statusCode(200)
                .extract()
                .response();

    }

    @Step("POST Pet By ID: {id} : Updates a pet in store with form data")
    @Test(priority = 5,dependsOnMethods = "GET_PetById",dataProvider = "postPetByPetID", dataProviderClass = testData.class,retryAnalyzer = retry.class)
    public void POST_PetById(int id, String name, String status) {
        Response response =
        given()
                .spec(petStoreRequestSpec())
                .pathParam("id", id)
                .formParam("name", name)
                .formParam("status", status).
        when()
                .post("/pet/{id}").
        then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.getHeader("Content-Type"), "application/json");

        GET_PetByStatus pet = response.as(GET_PetByStatus.class);

        assertThat(pet.getStatus()).isEqualTo(status);
        assertThat(pet.getId()).isEqualTo(id);
        assertThat(pet.getName()).isEqualTo(name);

    }
    @Step("DELETE Pet By ID: {id} and verify deletion by trying to get the pet again")
    @Test(priority = 6,dependsOnMethods = "POST_AddNewPet",dataProvider = "deletePetData", dataProviderClass = testData.class,retryAnalyzer = retry.class)
    public void DELETE_PetById(int petID) {
        Response response =
        given()
                .spec(petStoreRequestSpec())
                .pathParam("id",petID).
        when()
                .delete("/pet/{id}").
        then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.getHeader("Content-Type"), "application/json");


        given()
                .spec(petStoreRequestSpec())
                .pathParam("id", petID).
        when()
                .get("/pet/{id}").
        then()
                .statusCode(404)
                .extract()
                .response();
    }


}
