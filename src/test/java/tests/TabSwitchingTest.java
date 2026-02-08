package tests;

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
import java.time.Duration;
import java.util.UUID;

@ExtendWith(AllureJunit5.class)
public class TabSwitchingTest {
    private String email;
    private String password;

    // Общие вспомогательные методы вынесены наружу
    // Метод для создания пользователя
    @Step("Создание временного пользователя через API")
    private static void createTemporaryUser(String[] dataHolder) {
        final String uniqueEmail = UUID.randomUUID() + "@gmail.com";
        final String securePassword = "Str0ngPa$$w0rd";
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
        dataHolder[0] = uniqueEmail;
        dataHolder[1] = securePassword;
        dataHolder[2] = response.jsonPath().getString("accessToken");
    }

    // Метод для удаления пользователя
    @Step("Удаление пользователя через API")
    private static void deleteRegisteredUser(String email, String password) {
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
                .assertThat().statusCode(equalTo(202)); // Проверяем успешность удаления
    }

    // Главный тест для Chrome
    @DisplayName("Тесты для Chrome")
    static class ChromeTests {

        @BeforeAll
        public static void beforeAll() {
            TestConfiguration.setBrowserType(BrowserType.CHROME);
        }

        @Test
        @DisplayName("UI-тест авторизации и выбора вкладок в конструкторе для Chrome")
        @Description("Авторизация через кнопку 'Личный кабинет' и последующий переход в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки'")
        public void testLoginAndSelectTabsInChrome() throws Exception {
            // Массив для хранения данных пользователя
            String[] userData = new String[3];

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser(userData);

            // Запоминаем созданные данные
            String email = userData[0];
            String password = userData[1];

            // Переход на главную страницу и открытие страницы авторизации
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickPersonalAccountButton();

            // Авторизация пользователя
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.submitLoginData(email, password);

            // Дожидаемся полной загрузки страницы после авторизации
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

    // Главный тест для Яндекс.Браузера
    @DisplayName("Тесты для Яндекс.Браузера")
    static class YandexTests {

        @BeforeAll
        public static void beforeAll() {
            TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);
        }

        @Test
        @DisplayName("UI-тест авторизации и выбора вкладок в конструкторе для Яндекс.Браузера")
        @Description("Авторизация через кнопку 'Личный кабинет' и последующий переход в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки'")
        public void testLoginAndSelectTabsInYandex() throws Exception {
            // Массив для хранения данных пользователя
            String[] userData = new String[3];

            // Переходим на главную страницу
            DriverManager.getDriver().get(HOST);

            // Создаем пользователя
            createTemporaryUser(userData);

            // Запоминаем созданные данные
            String email = userData[0];
            String password = userData[1];

            // Переход на главную страницу и открытие страницы авторизации
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PersonalAccountButton));
            MainPage mainPage = new MainPage(DriverManager.getDriver());
            mainPage.clickPersonalAccountButton();

            // Авторизация пользователя
            AuthorizationPage authPage = new AuthorizationPage(DriverManager.getDriver());
            authPage.submitLoginData(email, password);

            // Дожидаемся полной загрузки страницы после авторизации
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

    // После завершения теста очищаем состояние системы
    @AfterEach
    public void cleanUp() {
        if (this.email != null && this.password != null) {
            deleteRegisteredUser(this.email, this.password);
        }
        DriverManager.quitDriver(); // Закрываем браузер
    }
}