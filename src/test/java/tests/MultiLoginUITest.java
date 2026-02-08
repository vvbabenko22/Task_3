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

@ExtendWith(AllureJunit5.class)
class MultiLoginUITest {

    private MainPage mainPage;
    private AuthorizationPage authPage;
    private RegistrationPage regPage;
    private String email;
    private String password;
    private String token;

    @BeforeEach
    public void setup() {
        // Переходим на главную страницу
        DriverManager.getDriver().get(HOST);
        mainPage = new MainPage(DriverManager.getDriver());
        authPage = new AuthorizationPage(DriverManager.getDriver());
        regPage = new RegistrationPage(DriverManager.getDriver());
    }

    @Step("Создание временного пользователя через API")
    private void createTemporaryUser() {
        final String uniqueEmail = System.currentTimeMillis() + "@example.com";
        final String userBody = "{ \"email\": \"" + uniqueEmail + "\", \"password\": \"strongpassword\", \"name\": \"autotest\" }";

        // Регистрируемся через API
        var response = given()
                .contentType("application/json")
                .body(userBody)
                .when()
                .post(POST_REGISTER)
                .then()
                .log().all()
                .extract().response();

        // Сохраняем данные
        this.email = response.jsonPath().getString("user.email");
        this.password = "strongpassword";
        this.token = response.jsonPath().getString("accessToken");
    }

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

        // Проверяем успешность авторизации
        loginResponse.then().assertThat().statusCode(equalTo(200))
                .and().body("success", equalTo(true));

        // Получаем accessToken
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // Отправляем запрос на удаление пользователя
        given()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then()
                .log().all()
                .assertThat().statusCode(equalTo(202))
                .and().body("success", equalTo(true));
    }

    // ---- НАЧАЛО ТЕСТОВ ----

    // Тест №1: Логин через кнопку "Войти в аккаунт" в Chrome
    @Test
    @DisplayName("UI-тест авторизации через кнопку 'Войти в аккаунт' в Chrome")
    @Description("Авторизация через кнопку 'Войти в аккаунт' на главной странице в Chrome")
    public void testLoginThroughEnterAccountButtonChrome() {
        TestConfiguration.setBrowserType(BrowserType.CHROME);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и открытие страницы авторизации
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
        mainPage.clickEnterAccountButton();

        // Шаг 3: Авторизация пользователя через UI
        authPage.submitLoginData(email, password);

        // Шаг 4: Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    // Тест №1: Логин через кнопку "Войти в аккаунт" в Yandex Browser
    @Test
    @DisplayName("UI-тест авторизации через кнопку 'Войти в аккаунт' в Yandex Browser")
    @Description("Авторизация через кнопку 'Войти в аккаунт' на главной странице в Yandex Browser")
    public void testLoginThroughEnterAccountButtonYandex() {
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и открытие страницы авторизации
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
        mainPage.clickEnterAccountButton();

        // Шаг 3: Авторизация пользователя через UI
        authPage.submitLoginData(email, password);

        // Шаг 4: Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    // Тест №2: Логин через кнопку "Личный кабинет" в Chrome
    @Test
    @DisplayName("UI-тест авторизации через кнопку 'Личный кабинет' в Chrome")
    @Description("Авторизация через кнопку 'Личный кабинет' на главной странице в Chrome")
    public void testLoginThroughPersonalAccountButtonChrome() {
        TestConfiguration.setBrowserType(BrowserType.CHROME);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и открытие страницы авторизации через личный кабинет
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
        mainPage.clickPersonalAccountButton();

        // Шаг 3: Авторизация пользователя через UI
        authPage.submitLoginData(email, password);

        // Шаг 4: Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    // Тест №2: Логин через кнопку "Личный кабинет" в Yandex Browser
    @Test
    @DisplayName("UI-тест авторизации через кнопку 'Личный кабинет' в Yandex Browser")
    @Description("Авторизация через кнопку 'Личный кабинет' на главной странице в Yandex Browser")
    public void testLoginThroughPersonalAccountButtonYandex() {
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и открытие страницы авторизации через личный кабинет
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
        mainPage.clickPersonalAccountButton();

        // Шаг 3: Авторизация пользователя через UI
        authPage.submitLoginData(email, password);

        // Шаг 4: Проверка успешной авторизации (наличие кнопки 'Оформить заказ')
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    // Тест №3: Логин через форму регистрации в Chrome
    @Test
    @DisplayName("UI-тест авторизации через переход на регистрацию и возврат на авторизацию в Chrome")
    @Description("Авторизация через кнопку 'Войти в аккаунт' с последующим переходом на регистрацию и возвратом авторизацию в Chrome")
    public void testLoginThroughRegisterFormRedirectChrome() {
        TestConfiguration.setBrowserType(BrowserType.CHROME);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и нажатие на кнопку 'Войти в аккаунт'
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
        mainPage.clickEnterAccountButton();

        // Шаг 3: Переход на страницу регистрации
        wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.RegisterLink));
        authPage.clickRegisterLink();

        // Шаг 4: Возвращаемся на форму авторизации
        wait.until(ExpectedConditions.presenceOfElementLocated(RegistrationPage.LoginLinkBack));
        regPage.clickAlreadyRegisteredLink();

        // Шаг 5: Авторизация пользователя через UI
        wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
        authPage.submitLoginData(email, password);

        // Шаг 6: Проверка успешной авторизации (появление кнопки "Оформить заказ")
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    // Тест №3: Логин через форму регистрации в Yandex Browser
    @Test
    @DisplayName("UI-тест авторизации через переход на регистрацию и возврат на авторизацию в Yandex Browser")
    @Description("Авторизация через кнопку 'Войти в аккаунт' с последующим переходом на регистрацию и возвратом авторизацию в Yandex Browser")
    public void testLoginThroughRegisterFormRedirectYandex() {
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и нажатие на кнопку 'Войти в аккаунт'
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
        mainPage.clickEnterAccountButton();

        // Шаг 3: Переход на страницу регистрации
        wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.RegisterLink));
        authPage.clickRegisterLink();

        // Шаг 4: Возвращаемся на форму авторизации
        wait.until(ExpectedConditions.presenceOfElementLocated(RegistrationPage.LoginLinkBack));
        regPage.clickAlreadyRegisteredLink();

        // Шаг 5: Авторизация пользователя через UI
        wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
        authPage.submitLoginData(email, password);

        // Шаг 6: Проверка успешной авторизации (появление кнопки "Оформить заказ")
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    // Тест №4: Вход через кнопку в форме восстановления пароля в Chrome
    @Test
    @DisplayName("UI-тест через кнопку в форме восстановления пароля и последующей авторизации в Chrome")
    @Description("Нажатие на кнопку в форме восстановления пароля и последующая авторизация в Chrome")
    public void testForgotPasswordFlowChrome() {
        TestConfiguration.setBrowserType(BrowserType.CHROME);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и нажатие на кнопку 'Войти в аккаунт'
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
        mainPage.clickEnterAccountButton();

        // Шаг 3: Нажатие на ссылку 'Восстановить пароль'
        wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.ForgotPasswordLink));
        authPage.forgotPassword();

        // Шаг 4: Ожидание загрузки страницы восстановления пароля
        RecoverPasswordPage recoverPasswordPage = new RecoverPasswordPage(DriverManager.getDriver());
        recoverPasswordPage.waitForPageLoad();

        // Шаг 5: Возвращаемся на форму авторизации
        wait.until(ExpectedConditions.presenceOfElementLocated(RecoverPasswordPage.RememberPassword));
        authPage.rememberPassword();

        // Шаг 6: Авторизация пользователя через UI
        wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
        authPage.submitLoginData(email, password);

        // Шаг 7: Проверка успешной авторизации (появление кнопки "Оформить заказ")
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    // Тест №4: Восстановление пароля и последующая авторизация в Yandex Browser
    @Test
    @DisplayName("UI-тест через кнопку в форме восстановления пароля и последующей авторизации в Yandex Browser")
    @Description("Нажатие на кнопку в форме восстановления пароля и последующая авторизация в Yandex Browser")
    public void testForgotPasswordFlowYandex() {
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и нажатие на кнопку 'Войти в аккаунт'
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
        mainPage.clickEnterAccountButton();

        // Шаг 3: Нажатие на ссылку 'Восстановить пароль'
        wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.ForgotPasswordLink));
        authPage.forgotPassword();

        // Шаг 4: Ожидание загрузки страницы восстановления пароля
        RecoverPasswordPage recoverPasswordPage = new RecoverPasswordPage(DriverManager.getDriver());
        recoverPasswordPage.waitForPageLoad();

        // Шаг 5: Возвращаемся на форму авторизации
        wait.until(ExpectedConditions.presenceOfElementLocated(RecoverPasswordPage.RememberPassword));
        authPage.rememberPassword();

        // Шаг 6: Авторизация пользователя через UI
        wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
        authPage.submitLoginData(email, password);

        // Шаг 7: Проверка успешной авторизации (появление кнопки "Оформить заказ")
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
        assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
    }

    @AfterEach
    public void tearDown() {
        deleteRegisteredUser(); // Удаляем пользователя через API
        DriverManager.quitDriver(); // Закрываем браузер
    }

}