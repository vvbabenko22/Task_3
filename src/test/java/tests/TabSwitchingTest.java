import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.AuthorizationPage;
import pages.MainPage;
import pages.ProfilePage;
import pages.RegistrationPage;
import utils.BrowserType;
import utils.DriverManager;
import utils.TestConfiguration;
import static config.RestConfig.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.github.javafaker.Faker;
import java.time.Duration;

@ExtendWith(AllureJunit5.class)
public class TabSwitchingTest {

    private MainPage mainPage;
    private AuthorizationPage authPage;
    private RegistrationPage regPage;
    private String email;
    private String password;
    private String token;
    Faker faker = new Faker();

    // Метод создания временного пользователя через API
    @Step("Создание временного пользователя через API")
    private void createTemporaryUser() {
        // Генерируем уникальный email и надежный пароль с помощью Faker
        final String uniqueEmail = faker.internet().emailAddress();
        final String securePassword = faker.internet().password(6, 12); // длина пароля от 6 до 12 символов
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

    // Общий метод для удаления пользователя
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

    // Общий метод очистки после теста
    @AfterEach
    public void tearDown() {
        deleteRegisteredUser(); // Удаляем пользователя через API
        DriverManager.quitDriver(); // Закрываем браузер
    }

    // Тест для Chrome
    @Nested
    @DisplayName("Тесты для Chrome")
    class ChromeTests {

        public ChromeTests() {
            TestConfiguration.setBrowserType(BrowserType.CHROME);
        }

        @Test
        @DisplayName("UI-тест авторизации и выбора вкладок в конструкторе для Chrome")
        @Description("Авторизация через кнопку 'Личный кабинет' и последующий переход в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки'")
        public void testLoginAndSelectTabsInChrome() {
            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Переходим на страницу авторизации
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickPersonalAccountButton();

            // Авторизация пользователя
            authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.submitLoginData(email, password);

            // Дождёмся полной загрузки страницы после авторизации
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));

            // Повторный клик на вкладку "Личный кабинет"
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Переход в конструктор
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.ConstructorButton));
            mainPage.clickConstructor();

            // Нажатие на вкладку "Соусы" и проверка активности вкладки
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.SaucesTab));
            mainPage.clickSaucesTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Соусы' не активна!");

            // Нажатие на вкладку "Начинки" и проверка активности вкладки
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.FillingsTab));
            mainPage.clickFillingsTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Начинки' не активна!");

            // Нажатие на вкладку "Булки" и проверка активности вкладки
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.BunsTab));
            mainPage.clickBunsTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна!");

            // Переход обратно в Личный кабинет
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Переход по клику на логотип и проверка активности вкладки "Булки"
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.LogotypeButton));
            mainPage.clickLogotypeTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна после клика на логотип!");

            // Переход обратно в Личный кабинет
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Выход из Личного кабинета
            ProfilePage profilePage = new ProfilePage(DriverManager.getDriver());
            profilePage.logOut();

            // Проверка отображения кнопки "Войти" после выхода
            wait.until(ExpectedConditions.visibilityOfElementLocated(AuthorizationPage.LoginButton));
        }
    }

    // Тест для Яндекс.Браузера
    @Nested
    @DisplayName("Тесты для Яндекс.Браузера")
    class YandexTests {

        public YandexTests() {
            TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);
        }

        @Test
        @DisplayName("UI-тест авторизации и выбора вкладок в конструкторе для Яндекс.Браузера")
        @Description("Авторизация через кнопку 'Личный кабинет' и последующий переход в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки'")
        public void testLoginAndSelectTabsInYandex() {
            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser();

            // Открываем страницу авторизации
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickPersonalAccountButton();

            // Авторизация пользователя
            authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.submitLoginData(email, password);

            // Дождёмся полной загрузки страницы после авторизации
            wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CheckoutButton));

            // Повторный клик на вкладку "Личный кабинет"
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Переход в конструктор
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.ConstructorButton));
            mainPage.clickConstructor();

            // Нажатие на вкладку "Соусы" и проверка активности вкладки
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.SaucesTab));
            mainPage.clickSaucesTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Соусы' не активна!");

            // Нажатие на вкладку "Начинки" и проверка активности вкладки
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.FillingsTab));
            mainPage.clickFillingsTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Начинки' не активна!");

            // Нажатие на вкладку "Булки" и проверка активности вкладки
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.BunsTab));
            mainPage.clickBunsTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна!");

            // Переход обратно в Личный кабинет
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Переход по клику на логотип и проверка активности вкладки "Булки"
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.LogotypeButton));
            mainPage.clickLogotypeTab();
            wait.until((webDriver) -> mainPage.isTabActive());
            assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна после клика на логотип!");

            // Переход обратно в Личный кабинет
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            mainPage.clickPersonalAccountButton();

            // Выход из Личного кабинета
            ProfilePage profilePage = new ProfilePage(DriverManager.getDriver());
            profilePage.logOut();

            // Проверка отображения кнопки "Войти" после выхода
            wait.until(ExpectedConditions.visibilityOfElementLocated(AuthorizationPage.LoginButton));
        }
    }
}