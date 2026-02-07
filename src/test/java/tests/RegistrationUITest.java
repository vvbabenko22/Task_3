package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import com.github.javafaker.Faker;
import io.restassured.response.Response;
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

    // Регистрация в Chrome
    @Test
    @DisplayName("UI-тест регистрации в Chrome")
    @Description("Регистрация нового пользователя в Chrome")
    public void testRegistrationInChrome() {
        TestConfiguration.setBrowserType(BrowserType.CHROME);
        performRegistration();
    }

    // Регистрация в Яндекс.Браузере
    @Test
    @DisplayName("UI-тест регистрации в Яндекс.Браузере")
    @Description("Регистрация нового пользователя в Яндекс.Браузере")
    public void testRegistrationInYandexBrowser() {
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);
        performRegistration();
    }

    // Проверка минимальной длины пароля в Chrome
    @Test
    @DisplayName("UI-тест проверки минимальной длины пароля в Chrome")
    @Description("Регистрация невозможна с паролем короче 6 символов в Chrome")
    public void testMinimumPasswordLengthValidationChrome() {
        TestConfiguration.setBrowserType(BrowserType.CHROME);
        validateShortPassword();
    }

    // Проверка минимальной длины пароля в Яндекс.Браузере
    @Test
    @DisplayName("UI-тест проверки минимальной длины пароля в Яндекс.Браузере")
    @Description("Регистрация невозможна с паролем короче 6 символов в Яндекс.Браузере")
    public void testMinimumPasswordLengthValidationYandex() {
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);
        validateShortPassword();
    }

    @Step("Регистрация пользователя и авторизация")
    private void performRegistration() {
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 12);

        // Шаг 1: Регистрация пользователя
        registrationPage.register(name, email, password);

        // Шаг 2: Ожидание после регистрации
        sleep(3000); // Дожидаемся возможной задержки

        // Шаг 3: Авторизация пользователя
        authorizationPage.submitLoginData(email, password);

        // Шаг 4: Проверка успешной авторизации
        mainPage.waitForCheckoutButton();

        // Шаг 5: Удаление пользователя через API
        deleteRegisteredUser(email, password);
    }

    @Step("Проверка недопустимых коротких паролей")
    private void validateShortPassword() {
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String shortPassword = faker.internet().password().substring(0, 5); // Пароль короче 6 символов

        // Регистрация пользователя с коротким паролем
        registrationPage.register(name, email, shortPassword);

        // Проверка наличия сообщения об ошибке
        String errorMessage = registrationPage.getPasswordErrorMessage();
        assertTrue(errorMessage.contains("Некорректный пароль"));
    }

    @Step("Удаление пользователя через API")
    private void deleteRegisteredUser(String email, String password) {
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    @AfterEach
    public void tearDown() {
        DriverManager.quitDriver(); // Закрываем браузер
    }
}