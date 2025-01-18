package ru.netologu.selenid;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;
import org.apache.http.HttpStatus;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private DataGenerator() {
    }

    public static String generateLogin(String locale) {
        Faker faker = new Faker(new Locale(locale));

        return faker.name().username();
    }

    public static String generatePassword(String locale) {
        Faker faker = new Faker(new Locale(locale));

        return faker.internet().password();
    }

    @Value
    public static class UserInfo {
        String login;
        String password;
        String status;
    }

    public static class Registration {
        private Registration() {
        }

        private static RequestSpecification requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        public static UserInfo registrationUser(String locale, boolean isActive) {
            String status;
            if (isActive) {
                status = "active";
            } else if (!isActive) {
                status = "blocked";
            } else {
                status = null;
            }

            UserInfo userInfo = new UserInfo(generateLogin(locale), generatePassword(locale), status);
            Gson gson = new Gson();
            String requestBody = gson.toJson(userInfo);

            given()
                    .spec(requestSpecification)
                    .body(requestBody)
                    .when()
                    .post("/api/system/users")
                    .then()
                    .statusCode(HttpStatus.SC_OK);

            return userInfo;
        }
    }
}
