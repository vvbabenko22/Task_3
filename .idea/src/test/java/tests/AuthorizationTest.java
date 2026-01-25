package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.pages.AuthorizationPage;
import ru.yandex.practicum.pages.MainPage;
import ru.yandex.practicum.pages.ProfilePage;
import ru.yandex.practicum.pages.RegistrationPage;
import ru.yandex.practicum.pages.RecoverPasswordPage;
import ru.yandex.practicum.api.user.User;
import ru.yandex.practicum.api.user.UserSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static ru.yandex.practicum.config.RestConfig.HOST;

public class AuthorizationTest extends BaseTest {

    private MainPage mainPage;
    private RegistrationPage registrationPage;
    private AuthorizationPage authorizationPage;
    private ProfilePage profilePage;

    private RecoverPasswordPage recoverPasswordPage;

    private User user;
    private final UserSteps userSteps = new UserSteps();

    @Before
    public void setUp() {
        super.setUp();

        Faker faker = new Faker();

        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 12);

        user = new User(email, password, name, "");

        ValidatableResponse createResponse = userSteps.createUser(user);
        createResponse.assertThat().statusCode(SC_OK);

        String accessToken = userSteps.extractAccessToken(createResponse);
        user.setAccessToken(accessToken);

        driver.get(HOST);
        mainPage = new MainPage(driver);
    }

    @Test
    @DisplayName("Вход через кнопку «Войти в аккаунт» на главной — позитивный сценарий")
    @Description("Регистрация пользователя через API, вход через кнопку «Войти в аккаунт», проверка email в профиле")
    public void enterAccountButtonTest() {
        // Ожидаем и кликаем кнопку входа на главной
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();

        // Инициализируем страницу авторизации (конструктор ждёт загрузки)
        authorizationPage = new AuthorizationPage(driver);

        // Вводим данные и логинимся
        authorizationPage.enterUserDetails(user.getEmail(), user.getPassword());
        authorizationPage.clickEnterButton();

        // Ждём, что на главной появится кнопка оформления заказа (индикатор успешного логина)
        mainPage.waitForCheckoutButton();

        // Переходим в личный кабинет и проверяем профиль
        mainPage.clickPersonalAccountButton();
        profilePage = new ProfilePage(driver);
        profilePage.waitProfilePageLoad();

        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profilePage.getEmailText().toLowerCase());
    }

    @Test
    @DisplayName("Вход через кнопку «Личный кабинет» — позитивный сценарий")
    @Description("Регистрация через API, клик по 'Личный кабинет', логин, проверка email в профиле")
    public void enterProfileButtonTest() {
        // Сразу кликаем кнопку "Личный кабинет" на шапке
        mainPage.waitForPersonalAccountButton();
        mainPage.clickPersonalAccountButton();

        // На странице авторизации вводим данные и логинимся
        authorizationPage = new AuthorizationPage(driver);
        authorizationPage.enterUserDetails(user.getEmail(), user.getPassword());
        authorizationPage.clickEnterButton();

        // Подтверждение успешного входа и проверка профиля
        mainPage.waitForCheckoutButton();
        mainPage.clickPersonalAccountButton();
        profilePage = new ProfilePage(driver);
        profilePage.waitProfilePageLoad();

        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profilePage.getEmailText().toLowerCase());
    }

    @Test
    @DisplayName("Вход через ссылку в форме регистрации")
    @Description("Проверка, что из регистрации можно перейти на форму входа и залогиниться")
    public void enterLinkRegistrationFormTest() {
        // Открываем форму входа
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();

        authorizationPage = new AuthorizationPage(driver);

        // Переходим в форму регистрации и затем возвращаемся по ссылке "Уже зарегистрированы? Войти"
        authorizationPage.clickRegistration();
        registrationPage = new RegistrationPage(driver);
        registrationPage.waitForPageLoad();
        registrationPage.clickAlreadyRegisteredLink();

        // Конструктор AuthorizationPage уже проверит загрузку страницы
        authorizationPage = new AuthorizationPage(driver);

        // Логинимся и проверяем профиль
        authorizationPage.enterUserDetails(user.getEmail(), user.getPassword());
        authorizationPage.clickEnterButton();

        mainPage.waitForCheckoutButton();
        mainPage.clickPersonalAccountButton();
        profilePage = new ProfilePage(driver);
        profilePage.waitProfilePageLoad();

        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profilePage.getEmailText().toLowerCase());
    }

    @Test
    @DisplayName("Вход через ссылку в форме восстановления пароля")
    @Description("Переход из восстановления пароля на форму входа и последующий логин")
    public void enterLinkRecoverPasswordTest() {
        // Открываем форму входа
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();

        authorizationPage = new AuthorizationPage(driver);

        // Переходим на страницу восстановления пароля и возвращаемся по ссылке "Войти"
        authorizationPage.clickRecoverPassword();
        recoverPasswordPage = new RecoverPasswordPage(driver);
        recoverPasswordPage.waitForPageLoad();
        recoverPasswordPage.clickRememberedPassword();

        // Обратно на форме входа — логинимся
        authorizationPage = new AuthorizationPage(driver);
        authorizationPage.enterUserDetails(user.getEmail(), user.getPassword());
        authorizationPage.clickEnterButton();

        mainPage.waitForCheckoutButton();
        mainPage.clickPersonalAccountButton();
        profilePage = new ProfilePage(driver);
        profilePage.waitProfilePageLoad();

        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profilePage.getEmailText().toLowerCase());
    }

    @After
    public void tearDown() {
        if (user != null && user.getAccessToken() != null) {
            userSteps.deleteUser(user.getAccessToken());
        }
        super.tearDown();
    }
}