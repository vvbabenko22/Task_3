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

    private static String email;
    private static String password;

    // Общий метод для создания пользователя через API
    @Step("Создание временного пользователя через API")
    private static void createTemporaryUser(String[] dataHolder) {
        final String uniqueEmail = UUID.randomUUID() + "@gmail.com";       // Уникальный email
        final String securePassword = "StrongPass123!";                   // Надежный пароль
        final String userBody = "{\"email\":\"" + uniqueEmail + "\", \"password\":\"" + securePassword + "\", \"name\":\"autotest_user\"}";

        var response = given()
                .contentType("application/json")                          // Устанавливаем контент-тип JSON
                .body(userBody)                                           // Передаем тело запроса
                .when()
                .post(POST_REGISTER)                                      // Отправляем POST-запрос на регистрацию
                .then()
                .log().all()                                              // Логируем весь отклик сервера
                .extract().response();                                    // Извлекаем ответ

        response.then().assertThat().statusCode(equalTo(200));            // Проверяем HTTP-код успеха

        dataHolder[0] = uniqueEmail;                                      // Сохраняем созданный email
        dataHolder[1] = securePassword;                                   // Сохраняем созданный пароль
        dataHolder[2] = response.jsonPath().getString("accessToken");     // Сохраняем токен доступа
    }

    // Общий метод для удаления пользователя через API
    @Step("Удаление пользователя через API")
    private static void deleteRegisteredUser(String email, String password) {
        Response loginResponse = given()
                .contentType("application/json")                      // Устанавливаем контент-тип JSON
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }") // Тело запроса с учетными данными
                .when()
                .post(POST_LOGIN)                                     // Отправляем POST-запрос на авторизацию
                .then()
                .log().all()                                          // Логируем весь отклик сервера
                .extract().response();                                // Извлекаем ответ

        loginResponse.then().assertThat().statusCode(equalTo(200));    // Проверяем HTTP-код успеха

        String accessToken = loginResponse.jsonPath().getString("accessToken"); // Получаем токен доступа

        given()
                .header("Authorization", accessToken)                 // Добавляем заголовок авторизации
                .when()
                .delete(DELETE_USER)                                  // Отправляем DELETE-запрос на удаление пользователя
                .then()
                .log().all()                                          // Логируем весь отклик сервера
                .assertThat().statusCode(equalTo(202));               // Проверяем HTTP-код успеха
    }

    // Метод выполняется после каждого теста
    @AfterEach
    public void tearDown() {
        if (email != null && password != null) {
            deleteRegisteredUser(email, password);                    // Удаляем пользователя через API
        }
        DriverManager.quitDriver();                                   // Закрываем браузер
    }

    // Группа тестов для Chrome
    @DisplayName("Тесты для Chrome")
    static class ChromeTests {

        @BeforeAll
        public static void beforeAll() {
            TestConfiguration.setBrowserType(BrowserType.CHROME);      // Выбираем Chrome
        }

        // Тест №1: Логин через кнопку "Войти в аккаунт" в Chrome
        @Test
        @DisplayName("UI-тест авторизации через кнопку 'Войти в аккаунт' в Chrome")
        @Description("Авторизация через кнопку 'Войти в аккаунт' на главной странице в Chrome")
        public void testLoginThroughEnterAccountButtonChrome() {
            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Массив для сохранения данных пользователя
            String[] userData = new String[3];
            createTemporaryUser(userData);                            // Создаем пользователя
            email = userData[0];                                      // Получаем email
            password = userData[1];                                   // Получаем пароль

            // Открываем страницу авторизации через нажатие на кнопку входа
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());           // Экземпляр страницы MainPage
            mainPage.clickEnterAccountButton();                              // Клик на кнопку входа

            // Авторизация пользователя
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver()); // Экземпляр страницы авторизации
            authPage.submitLoginData(email, password);                         // Осуществляем ввод логина и пароля

            // Проверка успешной авторизации
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!"); // Проверяем наличие кнопки оформления заказа
        }

        // Тест №2: Логин через кнопку "Личный кабинет" в Chrome
        @Test
        @DisplayName("UI-тест авторизации через кнопку 'Личный кабинет' в Chrome")
        @Description("Авторизация через кнопку 'Личный кабинет' на главной странице в Chrome")
        public void testLoginThroughPersonalAccountButtonChrome() {
            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            String[] userData = new String[3];
            createTemporaryUser(userData);
            email = userData[0];
            password = userData[1];

            // Переходим на главную страницу и открываем страницу авторизации через личный кабинет
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickPersonalAccountButton();                        // Кликаем на кнопку личного кабинета

            // Авторизация пользователя
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.submitLoginData(email, password);                     // Осуществляем авторизацию

            // Проверка успешной авторизации
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №3: Авторизация через кнопку перехода с формы регистрации
        @Test
        @DisplayName("UI-тест авторизации через кнопку в форме регистрации")
        @Description("Авторизация через кнопку 'Войти в аккаунт' с последующим переходом на форму регистрации и возвратом в авторизацию в Chrome")
        public void testLoginThroughRegisterFormRedirectChrome() {
            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            String[] userData = new String[3];
            createTemporaryUser(userData);
            email = userData[0];
            password = userData[1];

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickEnterAccountButton();                           // Кликаем на кнопку входа

            // Переходим на страницу регистрации
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.RegisterLink));
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.clickRegisterLink();                                 // Кликаем на ссылку регистрации

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RegistrationPage.LoginLinkBack));
            RegistrationPage regPage = new RegistrationPage(DriverManager.getDriver());
            regPage.clickAlreadyRegisteredLink();                         // Кликаем на ссылку возврата в авторизацию

            // Авторизация пользователя
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);                     // Осуществляем авторизацию

            // Проверка успешной авторизации
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
            String[] userData = new String[3];
            createTemporaryUser(userData);
            email = userData[0];
            password = userData[1];

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickEnterAccountButton();                           // Кликаем на кнопку входа

            // Нажимаем на ссылку 'Восстановить пароль'
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.ForgotPasswordLink));
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.forgotPassword();                                   // Кликаем на ссылку восстановления пароля

            // Ожидание загрузки страницы восстановления пароля
            RecoverPasswordPage recoverPasswordPage = new RecoverPasswordPage(DriverManager.getDriver());
            recoverPasswordPage.waitForPageLoad();                       // Ждем загрузку страницы восстановления пароля

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RecoverPasswordPage.RememberPassword));
            authPage.rememberPassword();                                 // Кликаем на ссылку возвращения назад

            // Авторизация пользователя
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);                     // Осуществляем авторизацию

            // Проверка успешной авторизации
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }
    }

    // Группа тестов для Yandex Browser
    @DisplayName("Тесты для Yandex Browser")
    static class YandexBrowserTests {

        @BeforeAll
        public static void beforeAll() {
            TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER); // Выбираем Яндекс Браузер
        }

        // Тест №1: Логин через кнопку "Войти в аккаунт" в Yandex Browser
        @Test
        @DisplayName("UI-тест авторизации через кнопку 'Войти в аккаунт' в Yandex Browser")
        @Description("Авторизация через кнопку 'Войти в аккаунт' на главной странице в Yandex Browser")
        public void testLoginThroughEnterAccountButtonYandex() {
            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            String[] userData = new String[3];
            createTemporaryUser(userData);
            email = userData[0];
            password = userData[1];

            // Переход на главную страницу и открываем страницу авторизации
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickEnterAccountButton();                          // Кликаем на кнопку входа

            // Авторизация пользователя
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.submitLoginData(email, password);                   // Осуществляем авторизацию

            // Проверка успешной авторизации
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
            String[] userData = new String[3];
            createTemporaryUser(userData);
            email = userData[0];
            password = userData[1];

            // Переходим на главную страницу и открываем страницу авторизации через личный кабинет
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickPersonalAccountButton();                       // Кликаем на кнопку личного кабинета

            // Авторизация пользователя
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.submitLoginData(email, password);                   // Осуществляем авторизацию

            // Проверка успешной авторизации
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }

        // Тест №3: Авторизация через кнопку в форме регистрации
        @Test
        @DisplayName("UI-тест авторизации через кнопку в форме регистрации")
        @Description("Авторизация через кнопку 'Войти в аккаунт' с последующим переходом на форму регистрации и возвратом в авторизацию в Yandex Browser")
        public void testLoginThroughRegisterFormRedirectYandex() {
            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            String[] userData = new String[3];
            createTemporaryUser(userData);
            email = userData[0];
            password = userData[1];

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickEnterAccountButton();                           // Кликаем на кнопку входа

            // Переходим на страницу регистрации
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.RegisterLink));
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.clickRegisterLink();                                 // Кликаем на ссылку регистрации

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RegistrationPage.LoginLinkBack));
            RegistrationPage regPage = new RegistrationPage(DriverManager.getDriver());
            regPage.clickAlreadyRegisteredLink();                         // Кликаем на ссылку возврата в авторизацию

            // Авторизация пользователя
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);                     // Осуществляем авторизацию

            // Проверка успешной авторизации
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
            String[] userData = new String[3];
            createTemporaryUser(userData);
            email = userData[0];
            password = userData[1];

            // Переходим на главную страницу и нажимаем на кнопку 'Войти в аккаунт'
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.EnterAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickEnterAccountButton();                           // Кликаем на кнопку входа

            // Нажимаем на ссылку 'Восстановить пароль'
            wait.until(ExpectedConditions.presenceOfElementLocated(AuthorizationPage.ForgotPasswordLink));
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.forgotPassword();                                   // Кликаем на ссылку восстановления пароля

            // Ожидание загрузки страницы восстановления пароля
            RecoverPasswordPage recoverPasswordPage = new RecoverPasswordPage(DriverManager.getDriver());
            recoverPasswordPage.waitForPageLoad();                       // Ждем загрузку страницы восстановления пароля

            // Возвращаемся на форму авторизации
            wait.until(ExpectedConditions.presenceOfElementLocated(RecoverPasswordPage.RememberPassword));
            authPage.rememberPassword();                                 // Кликаем на ссылку возвращения назад

            // Авторизация пользователя
            wait.until(ExpectedConditions.elementToBeClickable(AuthorizationPage.LoginButton));
            authPage.submitLoginData(email, password);                     // Осуществляем авторизацию

            // Проверка успешной авторизации
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));
            assertNotNull(mainPage.getCheckoutButton(), "Кнопка 'Оформить заказ' не найдена!");
        }
    }
}