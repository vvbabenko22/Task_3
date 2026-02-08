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

// Внешний тестовый класс
@ExtendWith(AllureJunit5.class)
public class RegistrationUITest {

    private static RegistrationPage registrationPage;
    private static MainPage mainPage;
    private static AuthorizationPage authorizationPage;
    private static Faker faker;

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

    // Метод для общей процедуры регистрации и авторизации
    @Step("Регистрация пользователя и авторизация")
    private static void performRegistration() {
        String name = faker.name().firstName(); // Имя пользователя
        String email = faker.internet().emailAddress(); // Email пользователя
        String password = faker.internet().password(6, 12); // Длина пароля от 6 до 12 символов

        // Регистрация пользователя через UI
        registrationPage.register(name, email, password);

        // Ждем три секунды
        sleep(3000);

        // Авторизация пользователя
        authorizationPage.submitLoginData(email, password);

        // Проверка успешной авторизации
        mainPage.waitForCheckoutButton(); // Ожидаем появления кнопки оформления заказа

        // Удаление пользователя через API
        deleteRegisteredUser(email, password); // Удаляем пользователя после успешного теста
    }

    // Метод для проверки короткого пароля (< 6 символов)
    @Step("Проверка недопустимости коротких паролей")
    private static void validateShortPassword() {
        String name = faker.name().firstName(); // Имя пользователя
        String email = faker.internet().emailAddress(); // Email пользователя
        String shortPassword = faker.internet().password().substring(0, 5); // Короткий пароль менее 6 символов

        // Попытка зарегистрировать пользователя с коротким паролем
        registrationPage.register(name, email, shortPassword);

        // Проверяем наличие сообщения об ошибке
        String errorMessage = registrationPage.getPasswordErrorMessage();
        assertTrue(errorMessage.contains("Некорректный пароль")); // Убедимся, что ошибка отображается
    }

    // Метод для удаления пользователя через API
    @Step("Удаление пользователя через API")
    private static void deleteRegisteredUser(String email, String password) {
        // Получаем доступный токен через POST-запрос авторизации
        var loginResponse = given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }")
                .when()
                .post(POST_LOGIN)
                .then()
                .log().all()
                .extract().response();

        // Проверяем успех авторизации
        loginResponse.then().assertThat().statusCode(equalTo(200))
                .and().body("success", equalTo(true));

        // Извлекаем accessToken
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        // Отправляем DELETE-запрос для удаления пользователя
        given()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then()
                .log().all()
                .assertThat().statusCode(equalTo(202)) // Удостоверяемся, что удаление прошло успешно
                .and().body("success", equalTo(true));
    }

    // Простая пауза на заданное количество миллисекунд
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Основной класс тестов для браузера Chrome
    @DisplayName("Тесты для Chrome")
    public static class ChromeTests {

        @BeforeAll
        public static void setup() {
            TestConfiguration.setBrowserType(BrowserType.CHROME);
        }

        // Тест регистрации в браузере Chrome
        @Test
        @DisplayName("UI-тест регистрации в Chrome")
        @Description("Регистрация нового пользователя в Chrome")
        public void testRegistrationInChrome() {
            performRegistration(); // Выполнение регистрации
        }

        // Тест проверки минимально допустимой длины пароля в Chrome
        @Test
        @DisplayName("UI-тест проверки минимальной длины пароля в Chrome")
        @Description("Регистрация невозможна с паролем короче 6 символов в Chrome")
        public void testMinimumPasswordLengthValidationChrome() {
            validateShortPassword(); // Проверка короткого пароля
        }
    }

    // Класс тестов для Яндекс.Браузера
    @DisplayName("Тесты для Яндекс.Браузера")
    public static class YandexBrowserTests {

        @BeforeAll
        public static void setup() {
            TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);
        }

        // Тест регистрации в Яндекс.Браузере
        @Test
        @DisplayName("UI-тест регистрации в Яндекс.Браузере")
        @Description("Регистрация нового пользователя в Яндекс.Браузере")
        public void testRegistrationInYandexBrowser() {
            performRegistration(); // Выполнение регистрации
        }

        // Тест проверки минимальной длины пароля в Яндекс.Браузере
        @Test
        @DisplayName("UI-тест проверки минимальной длины пароля в Яндекс.Браузере")
        @Description("Регистрация невозможна с паролем короче 6 символов в Яндекс.Браузере")
        public void testMinimumPasswordLengthValidationYandex() {
            validateShortPassword(); // Проверка короткого пароля
        }
    }
}