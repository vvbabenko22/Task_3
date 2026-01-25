package tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit5.AllureJunit5;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.RegistrationPage;
import utils.BrowserType;
import utils.DriverManager;
import utils.TestConfiguration;

import static config.RestConfig.HOST;

@ExtendWith(AllureJunit5.class)
public class SimpleRegistrationUITest {

    private RegistrationPage registrationPage;
    private Faker faker;

    @BeforeEach
    public void setUp() {
        // Создаем драйвер на основании текущей конфигурации
        DriverManager.getDriver().get(HOST + "/register");
        registrationPage = new RegistrationPage(DriverManager.getDriver());
        faker = new Faker();
    }

    @Test
    @DisplayName("UI-тест регистрации в Chrome")
    @Description("Регистрация нового пользователя в Chrome")
    public void testRegistrationInChrome() {
        // Устанавливаем браузер Chrome
        TestConfiguration.setBrowserType(BrowserType.CHROME);

        // Генерация случайных данных пользователя
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 12);

        // Регистрация пользователя
        registrationPage.register(name, email, password);
    }

    @Test
    @DisplayName("UI-тест регистрации в Яндекс.Браузере")
    @Description("Регистрация нового пользователя в Яндекс.Браузере")
    public void testRegistrationInYandexBrowser() {
        // Устанавливаем браузер Яндекс
        TestConfiguration.setBrowserType(BrowserType.YANDEX_BROWSER);

        // Генерация случайных данных пользователя
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 12);

        // Регистрация пользователя
        registrationPage.register(name, email, password);
    }

    @AfterEach
    public void tearDown() {
        // Закрываем драйвер после каждого теста
        DriverManager.quitDriver();
    }
}