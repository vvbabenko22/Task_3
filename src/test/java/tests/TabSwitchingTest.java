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

@ExtendWith(AllureJunit5.class)
class TabSwitchingTest {

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

        // Удаляем пользователя
        given()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then()
                .log().all()
                .assertThat().statusCode(equalTo(202))
                .and().body("success", equalTo(true));
    }

    // Тест для Хром
    @Test
    @DisplayName("UI-тест авторизации и перехода в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки', возвратом в Личный кабинет, переходом по клику на логотип и выходом из системы в Яндекс.Браузере")
    @Description("Авторизация через кнопку 'Личный кабинет' и последующий переход в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки', возвратом в Личный кабинет, переходом по клику на логотип и выходом из системы в Яндекс.Браузере")
    public void testLoginAndSelectTabsChrome() {
        TestConfiguration.setBrowserType(BrowserType.CHROME); // Настройка Яндекс.Браузера

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и открытие страницы авторизации через личный кабинет
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 3: Авторизация пользователя через UI
        authPage.submitLoginData(email, password);

        // Шаг 4: Дождёмся полной загрузки страницы после авторизации
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CHECKOUT_BUTTON));

        // Шаг 5: Повторный клик на вкладку "Личный кабинет"
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 6: Переход в конструктор
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.ConstructorButton));
        mainPage.clickConstructor();

        // Шаг 7: Нажимаем на вкладку "Соусы" и проверяем, что она активна
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.SAUCES_TAB));
        mainPage.clickSaucesTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Соусы' не активна!");

        // Шаг 8: Нажимаем на вкладку "Начинки" и проверяем, что она активна
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.FILLINGS_TAB));
        mainPage.clickFillingsTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Начинки' не активна!");

        // Шаг 9: Нажимаем на вкладку "Булки" и проверяем, что она активна
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.BUNS_TAB));
        mainPage.clickBunsTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна!");

        // Шаг 10: Переход обратно в Личный кабинет
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 11: Переход по клику на логотип и проверка активности вкладки "Булки"
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.LogotypeButton));
        mainPage.clickLogotypeTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна после клика на логотип!");

        // Шаг 12: Переход обратно в Личный кабинет
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 13: Выход из Личного кабинета
        ProfilePage profilePage = new ProfilePage(DriverManager.getDriver());
        profilePage.logOut();

        // Шаг 14: Проверка отображения кнопки "Войти" после выхода
        wait.until(ExpectedConditions.visibilityOfElementLocated(AuthorizationPage.LOGIN_BUTTON));
    }

    // Тест для Яндекс.Браузера
    @Test
    @DisplayName("UI-тест авторизации и перехода в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки', возвратом в Личный кабинет, переходом по клику на логотип и выходом из системы в Яндекс.Браузере")
    @Description("Авторизация через кнопку 'Личный кабинет' и последующий переход в конструктор с выбором вкладок 'Соусы', 'Начинки', 'Булки', возвратом в Личный кабинет, переходом по клику на логотип и выходом из системы в Яндекс.Браузере")
    public void testLoginAndSelectTabsYandex() {
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER); // Настройка Яндекс.Браузера

        // Шаг 1: Создаем пользователя через API
        createTemporaryUser();

        // Шаг 2: Переход на главную страницу и открытие страницы авторизации через личный кабинет
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 3: Авторизация пользователя через UI
        authPage.submitLoginData(email, password);

        // Шаг 4: Дождёмся полной загрузки страницы после авторизации
        wait.until(ExpectedConditions.visibilityOfElementLocated(MainPage.CHECKOUT_BUTTON));

        // Шаг 5: Повторный клик на вкладку "Личный кабинет"
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 6: Переход в конструктор
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.ConstructorButton));
        mainPage.clickConstructor();

        // Шаг 7: Нажимаем на вкладку "Соусы" и проверяем, что она активна
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.SAUCES_TAB));
        mainPage.clickSaucesTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Соусы' не активна!");

        // Шаг 8: Нажимаем на вкладку "Начинки" и проверяем, что она активна
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.FILLINGS_TAB));
        mainPage.clickFillingsTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Начинки' не активна!");

        // Шаг 9: Нажимаем на вкладку "Булки" и проверяем, что она активна
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.BUNS_TAB));
        mainPage.clickBunsTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна!");

        // Шаг 10: Переход обратно в Личный кабинет
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 11: Переход по клику на логотип и проверка активности вкладки "Булки"
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.LogotypeButton));
        mainPage.clickLogotypeTab();
        wait.until((webDriver) -> mainPage.isTabActive());
        assertTrue(mainPage.isTabActive(), "Вкладка 'Булки' не активна после клика на логотип!");

        // Шаг 12: Переход обратно в Личный кабинет
        wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.PERSONAL_ACCOUNT_BUTTON));
        mainPage.clickPersonalAccountButton();

        // Шаг 13: Выход из Личного кабинета
        ProfilePage profilePage = new ProfilePage(DriverManager.getDriver());
        profilePage.logOut();

        // Шаг 14: Проверка отображения кнопки "Войти" после выхода
        wait.until(ExpectedConditions.visibilityOfElementLocated(AuthorizationPage.LOGIN_BUTTON));
    }

    @AfterEach
    public void tearDown() {
        deleteRegisteredUser(); // Удаляем пользователя через API
        DriverManager.quitDriver(); // Закрываем браузер
    }

}