package ru.netologu.selenid;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class AuthTest {
    @BeforeEach
    public void setUp() {
        Selenide.open("http://localhost:9999");
    }

    @Test
    public void successAuth() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.registrationUser("en", "active");

        String login = userInfo.getLogin();
        String password = userInfo.getPassword();

        $("[data-test-id='login'] input").setValue(login);
        $("[data-test-id='password'] input").setValue(password);
        $("[data-test-id='action-login']").click();

        $(Selectors.withText("Личный кабинет")).shouldBe(Condition.visible, Duration.ofSeconds(2));
    }

    @Test
    public void authWithBlockedStatus() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.registrationUser("en", "blocked");

        String login = userInfo.getLogin();
        String password = userInfo.getPassword();

        $("[data-test-id='login'] input").setValue(login);
        $("[data-test-id='password'] input").setValue(password);
        $("[data-test-id='action-login']").click();

        $(Selectors.byText("Ошибка")).shouldBe(Condition.visible, Duration.ofSeconds(2));
        SelenideElement errMsg = $("[data-test-id='error-notification'] .notification__content");
        errMsg.shouldBe(Condition.visible);
        errMsg.shouldHave(Condition.text("Ошибка! Пользователь заблокирован"));
    }

    @Test
    public void invalidLogin() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.registrationUser("en", "active");

        String login = DataGenerator.generateLogin("en");
        String password = userInfo.getPassword();

        $("[data-test-id='login'] input").setValue(login);
        $("[data-test-id='password'] input").setValue(password);
        $("[data-test-id='action-login']").click();

        $(Selectors.byText("Ошибка")).shouldBe(Condition.visible, Duration.ofSeconds(2));
        SelenideElement errMsg = $("[data-test-id='error-notification'] .notification__content");
        errMsg.shouldBe(Condition.visible);
        errMsg.shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    public void invalidPassword() {
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.registrationUser("en", "active");

        String login = userInfo.getLogin();
        String password = DataGenerator.generatePassword("en");

        $("[data-test-id='login'] input").setValue(login);
        $("[data-test-id='password'] input").setValue(password);
        $("[data-test-id='action-login']").click();

        $(Selectors.byText("Ошибка")).shouldBe(Condition.visible, Duration.ofSeconds(2));
        SelenideElement errMsg = $("[data-test-id='error-notification'] .notification__content");
        errMsg.shouldBe(Condition.visible);
        errMsg.shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));
    }
}
