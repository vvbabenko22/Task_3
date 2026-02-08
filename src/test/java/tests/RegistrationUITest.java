package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.AuthorizationPage;
import pages.MainPage;
import pages.RegistrationPage;
import utils.BrowserType;
import utils.DriverManager;
import utils.TestConfiguration;
import static config.RestConfig.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AllureJunit5.class)
public class RegistrationUITest {

    private RegistrationPage registrationPage;
    private MainPage mainPage;
    private AuthorizationPage authorizationPage;
    private Faker faker;

    @BeforeEach
    public void setUp() {
        DriverManager.getDriver().get(HOST + "/register");
        registrationPage = new RegistrationPage(DriverManager.getDriver());
        mainPage = new MainPage(DriverManager.getDriver());
        authorizationPage = new AuthorizationPage(DriverManager.getDriver());
        faker = new Faker();
    }

    @AfterEach
    public void tearDown() {
        DriverManager.quitDriver(); // Закрываем браузер
    }

    // Общий метод регистрации и авторизации пользователя
    @Step("Регистрация пользователя и авторизация")
    private void performRegistration() {
        String name = faker.name().firstName(); // Имя пользователя
        String email = faker.internet().emailAddress(); // Email пользователя
        String password = faker.internet().password(6, 12); // Пароль длиной от 6 до 12 символов

        // Регистрация пользователя через UI
        registrationPage.register(name, email, password);

        // Ждем три секунды
        sleep(3000);

        // Авторизация пользователя
        authorizationPage.submitLoginData(email, password);

        // Проверка успешной авторизации
        mainPage.waitForCheckoutButton(); // Ожидаем появление кнопки "Оформить заказ"

        // Удаление пользователя через API
        deleteRegisteredUser(email, password); // Удаляем пользователя после успешного теста
    }

    // Проверка попытки регистрации с коротким паролем (< 6 символов)
    @Step("Проверка недопустимых коротких паролей")
    private void validateShortPassword() {
        String name = faker.name().firstName(); // Имя пользователя
        String email = faker.internet().emailAddress(); // Email пользователя
        String shortPassword = faker.internet().password().substring(0, 5); // Короткий пароль (менее 6 символов)

        // Пытаемся зарегистрироваться с коротким паролем
        registrationPage.register(name, email, shortPassword);

        // Проверяем, что появилось сообщение об ошибке
        String errorMessage = registrationPage.getPasswordErrorMessage();
        assertTrue(errorMessage.contains("Некорректный пароль"));
    }

    // Удаление пользователя через API после успешной регистрации
    @Step("Удаление пользователя через API")
    private void deleteRegisteredUser(String email, String password) {
        // Авторизация пользователя для получения токена
        var loginResponse = given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }")
                .when()
                .post(POST_LOGIN)
                .then()
                .log().all()
                .extract().response();

        // Проверяем статус ответа
        loginResponse.then().assertThat().statusCode(equalTo(200)).and().body("success", equalTo(true));

        // Извлекаем accessToken
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // Удаляем пользователя через API
        given()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then()
                .log().all()
                .assertThat().statusCode(equalTo(202)) // Проверяем успешность удаления
                .and().body("success", equalTo(true));
    }

    // Пауза на указанное количество миллисекунд
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    // Тесты для Chrome
    @Nested
    @DisplayName("Тесты для Chrome")
    class ChromeTests {

        @BeforeAll
        public static void setup() {
            TestConfiguration.setBrowserType(BrowserType.CHROME);
        }

        // Тест регистрации в Chrome
        @Test
        @DisplayName("UI-тест регистрации в Chrome")
        @Description("Регистрация нового пользователя в Chrome")
        public void testRegistrationInChrome() {
            performRegistration(); // Выполняем регистрацию
        }

        // Тест проверки короткой длины пароля в Chrome
        @Test
        @DisplayName("UI-тест проверки минимальной длины пароля в Chrome")
        @Description("Регистрация невозможна с паролем короче 6 символов в Chrome")
        public void testMinimumPasswordLengthValidationChrome() {
            validateShortPassword(); // Проверяем короткий пароль
        }
    }
    // Тесты для Яндекс.Браузера
    @Nested
    @DisplayName("Тесты для Яндекс.Браузера")
    class YandexBrowserTests {

        @BeforeAll
        public static void setup() {
            TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);
        }

        // Тест регистрации в Яндекс.Браузере
        @Test
        @DisplayName("UI-тест регистрации в Яндекс.Браузере")
        @Description("Регистрация нового пользователя в Яндекс.Браузере")
        public void testRegistrationInYandexBrowser() {
            performRegistration(); // Выполняем регистрацию
        }

        // Тест проверки короткой длины пароля в Яндекс.Браузере
        @Test
        @DisplayName("UI-тест проверки минимальной длины пароля в Яндекс.Браузере")
        @Description("Регистрация невозможна с паролем короче 6 символов в Яндекс.Браузере")
        public void testMinimumPasswordLengthValidationYandex() {
            validateShortPassword(); // Проверяем короткий пароль
        }
    }
}