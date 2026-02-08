package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.AuthorizationPage;
import pages.MainPage;
import pages.RecoverPasswordPage;
import pages.RegistrationPage;
import utils.BrowserType;
import utils.DriverManager;
import utils.TestConfiguration;
import static config.RestConfig.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.UUID;

@ExtendWith(AllureJunit5.class)
public class MultiLoginUITest {

    private MainPage mainPage;
    private AuthorizationPage authPage;
    private RegistrationPage regPage;
    private String email;
    private String password;
    private String token;

    // Конструктор для инициализации объектов страниц
    public MultiLoginUITest() {
        mainPage = new MainPage(DriverManager.getDriver());
        authPage = new AuthorizationPage(DriverManager.getDriver());
        regPage = new RegistrationPage(DriverManager.getDriver());
    }

    // Метод создания временного пользователя через API
    @Step("Создание временного пользователя через API")
    private void createTemporaryUser() {
        final String uniqueEmail = UUID.randomUUID() + "@gmail.com";
        final String securePassword = "StrongPass123!";
        final String userBody = "{\"email\":\"" + uniqueEmail + "\", \"password\":\"" + securePassword + "\", \"name\":\"autotest_user\"}";

        var response = given()
                .contentType("application/json")
                .body(userBody)
                .when()
                .post(POST_REGISTER)
                .then()
                .log().all()
                .extract().response();

        response.then().assertThat().statusCode(equalTo(200));
        this.email = uniqueEmail;
        this.password = securePassword;
        this.token = response.jsonPath().getString("accessToken");
    }

    // Метод удаления зарегистрированного пользователя через API
    @Step("Удаление пользователя через API")
    private void deleteRegisteredUser() {
        Response loginResponse = given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }")
                .when()
                .post(POST_LOGIN)
                .then()
                .log().all()
                .extract().response();

        loginResponse.then().assertThat().statusCode(equalTo(200));
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        given()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then()
                .log().all()
                .assertThat().statusCode(equalTo(202));
    }

    // После каждого теста удаляется пользователь и закрывается браузер
    @AfterEach
    public void tearDown() {
        deleteRegisteredUser(); // Удаляем пользователя через API
        DriverManager.quitDriver(); // Закрываем браузер
    }

    // Группа тестов для Chrome
    @Nested
    @DisplayName("Тесты для Chrome")
    class ChromeTests {

        @BeforeAll
        public static void beforeAll() {
            TestConfiguration.setBrowserType(BrowserType.CHROME); // Установка Chrome
        }

        // Тест №1: Логин через кнопку "Войти в аккаунт" в Chrome
        @Test
        @DisplayName("UI-тест авторизации через кнопку 'Войти в аккаунт' в Chrome")
        @Description("Авторизация через кнопку 'Войти в аккаунт' на главной странице в Chrome")
        public void testLoginThroughEnterAccountButtonChrome() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Открываем страницу авторизации
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            mainPage.clickEnterAccountButton();

            // Авторизация пользователя через UI
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №2: Логин через кнопку "Личный кабинет" в Chrome
        @Test
        @DisplayName("UI-тест авторизации через кнопку 'Личный кабинет' в Chrome")
        @Description("Авторизация через кнопку 'Личный кабинет' на главной странице в Chrome")
        public void testLoginThroughPersonalAccountButtonChrome() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переходим на главную страницу и открываем страницу авторизации через личный кабинет
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Авторизация пользователя через UI
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №3: Вход через кнопку в форме регистрации
        @Test
        @DisplayName("UI-тест авторизации через кнопку в форме регистрации")
        @Description("Авторизация через кнопку 'Войти в аккаунт' с последующим переходом на форму регистрации и возвратом в авторизацию в Chrome")
        public void testLoginThroughRegisterFormRedirectChrome() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            mainPage.clickEnterAccountButton();

            // Переходим на страницу регистрации
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.RegisterLink));
            authPage.clickRegisterLink();

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RegistrationPage.LoginLinkBack));
            regPage.clickAlreadyRegisteredLink();

            // Авторизация пользователя через UI
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (появление кнопки "Оформить заказ")
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №4: Авторизация через восстановление пароля
        @Test
        @DisplayName("UI-тест авторизации через восстановление пароля в Chrome")
        @Description("Авторизация через кнопку 'Забыли пароль?' и последующая авторизация в Chrome")
        public void testForgotPasswordFlowChrome() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            mainPage.clickEnterAccountButton();

            // Нажимаем на ссылку 'Восстановить пароль'
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.ForgotPasswordLink));
            authPage.forgotPassword();

            // Ожидание загрузки страницы восстановления пароля
            RecoverPasswordPage recoverPasswordPage = new RecoverPasswordPage(DriverManager.getDriver());
            recoverPasswordPage.waitForPageLoad();

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RecoverPasswordPage.RememberPassword));
            authPage.rememberPassword();

            // Авторизация пользователя через UI
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (появление кнопки "Оформить заказ")
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }
    }

    // Группа тестов для Yandex Browser
    @Nested
    @DisplayName("Тесты для Yandex Browser")
    class YandexBrowserTests {

        @BeforeAll
        public static void beforeAll() {
            TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER); // Установка Yandex Browser
        }

        // Тест №1: Логин через кнопку "Войти в аккаунт" в Yandex Browser
        @Test
        @DisplayName("UI-тест авторизации через кнопку 'Войти в аккаунт' в Yandex Browser")
        @Description("Авторизация через кнопку 'Войти в аккаунт' на главной странице в Yandex Browser")
        public void testLoginThroughEnterAccountButtonYandex() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переход на главную страницу и открываем страницу авторизации
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            mainPage.clickEnterAccountButton();

            // Авторизация пользователя через UI
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №2: Логин через кнопку "Личный кабинет" в Yandex Browser
        @Test
        @DisplayName("UI-тест авторизации через кнопку 'Личный кабинет' в Yandex Browser")
        @Description("Авторизация через кнопку 'Личный кабинет' на главной странице в Yandex Browser")
        public void testLoginThroughPersonalAccountButtonYandex() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переходим на главную страницу и открываем страницу авторизации через личный кабинет
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Авторизация пользователя через UI
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №3: Авторизация через регистрацию и возвращение обратно
        @Test
        @DisplayName("UI-тест авторизации через кнопку в форме регистрации")
        @Description("Авторизация через кнопку 'Войти в аккаунт' с последующим переходом на форму регистрации и возвратом в авторизацию в Yandex Browser")
        public void testLoginThroughRegisterFormRedirectYandex() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            mainPage.clickEnterAccountButton();

            // Переходим на страницу регистрации
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.RegisterLink));
            authPage.clickRegisterLink();

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RegistrationPage.LoginLinkBack));
            regPage.clickAlreadyRegisteredLink();

            // Авторизация пользователя через UI
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (появление кнопки "Оформить заказ")
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №4: Авторизация через восстановление пароля
        @Test
        @DisplayName("UI-тест авторизации через восстановление пароля в Yandex Browser")
        @Description("Авторизация через кнопку 'Забыли пароль?' и последующая авторизация в Yandex Browser")
        public void testForgotPasswordFlowYandex() {

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            mainPage.clickEnterAccountButton();

            // Нажимаем на ссылку 'Восстановить пароль'
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.ForgotPasswordLink));
            authPage.forgotPassword();

            // Ожидание загрузки страницы восстановления пароля
            RecoverPasswordPage recoverPasswordPage = new RecoverPasswordPage(DriverManager.getDriver());
            recoverPasswordPage.waitForPageLoad();

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RecoverPasswordPage.RememberPassword));
            authPage.rememberPassword();

            // Авторизация пользователя через UI
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);

            // Проверка успешной авторизации (появление кнопки "Оформить заказ")
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }
    }
}