import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceRegressionTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/main.html";

    @BeforeEach
    void setup() {
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- PAGE OBJECTS ---
    public static class LoginPage {
        @FindBy(id = "username")
        WebElement usernameInput;
        @FindBy(id = "password")
        WebElement passwordInput;
        @FindBy(id = "loginBtn")
        WebElement loginBtn;
        @FindBy(id = "login-section")
        WebElement loginSection;
        @FindBy(id = "menu-section")
        WebElement menuSection;

        public LoginPage(WebDriver driver) {
            PageFactory.initElements(driver, this);
        }
        public void login(String user, String pass) {
            usernameInput.clear();
            usernameInput.sendKeys(user);
            passwordInput.clear();
            passwordInput.sendKeys(pass);
            loginBtn.click();
        }
        public boolean isMenuVisible() {
            return menuSection.isDisplayed();
        }
        public boolean isLoginVisible() {
            return loginSection.isDisplayed();
        }
    }

    public static class MenuPage {
        @FindBy(xpath = "//button[text()='Crear usuario']")
        WebElement btnCrearUsuario;
        @FindBy(xpath = "//button[text()='Buscar usuario por email']")
        WebElement btnBuscarUsuario;
        @FindBy(xpath = "//button[text()='Actualizar nombre']")
        WebElement btnActualizarNombre;
        @FindBy(xpath = "//button[text()='Eliminar usuario']")
        WebElement btnEliminarUsuario;

        public MenuPage(WebDriver driver) {
            PageFactory.initElements(driver, this);
        }
        public void goToCrearUsuario() {
            btnCrearUsuario.click();
        }
        public void goToBuscarUsuario() {
            btnBuscarUsuario.click();
        }
        public void goToActualizarNombre() {
            btnActualizarNombre.click();
        }
        public void goToEliminarUsuario() {
            btnEliminarUsuario.click();
        }
    }

    public static class CrearUsuarioPage {
        @FindBy(id = "create_email")
        WebElement emailInput;
        @FindBy(id = "create_name")
        WebElement nameInput;
        @FindBy(id = "btnCreate")
        WebElement btnCreate;

        public CrearUsuarioPage(WebDriver driver) {
            PageFactory.initElements(driver, this);
        }
        public void crearUsuario(String email, String nombre) {
            emailInput.clear();
            emailInput.sendKeys(email);
            nameInput.clear();
            nameInput.sendKeys(nombre);
            btnCreate.click();
        }
    }

    public static class BuscarUsuarioPage {
        @FindBy(id = "search_email")
        WebElement emailInput;
        @FindBy(id = "btnSearch")
        WebElement btnSearch;
        @FindBy(id = "search_result")
        WebElement searchResult;

        public BuscarUsuarioPage(WebDriver driver) {
            PageFactory.initElements(driver, this);
        }
        public void buscarUsuario(String email) {
            emailInput.clear();
            emailInput.sendKeys(email);
            btnSearch.click();
        }
        public String getResultado() {
            return searchResult.getText();
        }
    }

    public static class ActualizarUsuarioPage {
        @FindBy(id = "update_id")
        WebElement idInput;
        @FindBy(id = "update_name")
        WebElement nameInput;
        @FindBy(id = "btnUpdate")
        WebElement btnUpdate;

        public ActualizarUsuarioPage(WebDriver driver) {
            PageFactory.initElements(driver, this);
        }
        public void actualizarUsuario(String id, String nombre) {
            idInput.clear();
            idInput.sendKeys(id);
            nameInput.clear();
            nameInput.sendKeys(nombre);
            btnUpdate.click();
        }
    }

    public static class EliminarUsuarioPage {
        @FindBy(id = "delete_id")
        WebElement idInput;
        @FindBy(id = "btnDelete")
        WebElement btnDelete;

        public EliminarUsuarioPage(WebDriver driver) {
            PageFactory.initElements(driver, this);
        }
        public void eliminarUsuario(String id) {
            idInput.clear();
            idInput.sendKeys(id);
            btnDelete.click();
        }
    }

    // --- TESTS ---

    @Test
    void testLoginExitoso() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOf(login.menuSection));
        assertTrue(login.isMenuVisible(), "El menú principal debe ser visible tras login exitoso");
    }

    @Test
    void testLoginFallido() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "claveIncorrecta");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Credenciales incorrectas", alert.getText(), "Debe mostrar alerta de credenciales incorrectas");
        alert.accept();
        assertTrue(login.isLoginVisible(), "Debe permanecer en la pantalla de login tras fallo");
    }

    @Test
    void testCrearUsuarioExitoso() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Crear usuario']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToCrearUsuario();
        CrearUsuarioPage crear = new CrearUsuarioPage(driver);
        String email = "testuser" + System.currentTimeMillis() + "@mail.com";
        crear.crearUsuario(email, "Test User");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Usuario creado exitosamente", alert.getText(), "Debe mostrar mensaje de éxito al crear usuario");
        alert.accept();
    }

    @Test
    void testCrearUsuarioEmailDuplicado() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Crear usuario']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToCrearUsuario();
        CrearUsuarioPage crear = new CrearUsuarioPage(driver);
        String email = "duplicado@mail.com";
        crear.crearUsuario(email, "Usuario Uno");
        wait.until(ExpectedConditions.alertIsPresent()).accept(); // Primer usuario creado
        menu.goToCrearUsuario();
        crear.crearUsuario(email, "Usuario Dos");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("El email ya existe", alert.getText(), "Debe mostrar error de email duplicado");
        alert.accept();
    }

    @Test
    void testCrearUsuarioCamposVacios() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Crear usuario']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToCrearUsuario();
        CrearUsuarioPage crear = new CrearUsuarioPage(driver);
        crear.crearUsuario("", "");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Email y nombre son obligatorios", alert.getText(), "Debe mostrar error por campos vacíos");
        alert.accept();
    }

    @Test
    void testBuscarUsuarioExistente() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Buscar usuario por email']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToBuscarUsuario();
        BuscarUsuarioPage buscar = new BuscarUsuarioPage(driver);
        String email = "buscaruser" + System.currentTimeMillis() + "@mail.com";
        // Crear usuario primero
        menu.goToCrearUsuario();
        CrearUsuarioPage crear = new CrearUsuarioPage(driver);
        crear.crearUsuario(email, "Buscar User");
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        menu.goToBuscarUsuario();
        buscar.buscarUsuario(email);
        wait.until(ExpectedConditions.textToBePresentInElement(buscar.searchResult, email));
        assertTrue(buscar.getResultado().contains(email), "El resultado debe contener el email buscado");
    }

    @Test
    void testBuscarUsuarioNoExistente() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Buscar usuario por email']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToBuscarUsuario();
        BuscarUsuarioPage buscar = new BuscarUsuarioPage(driver);
        String email = "noexiste" + System.currentTimeMillis() + "@mail.com";
        buscar.buscarUsuario(email);
        wait.until(ExpectedConditions.textToBePresentInElement(buscar.searchResult, "Usuario no encontrado"));
        assertEquals("Usuario no encontrado", buscar.getResultado(), "Debe mostrar mensaje de usuario no encontrado");
    }

    @Test
    void testBuscarUsuarioCampoVacio() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Buscar usuario por email']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToBuscarUsuario();
        BuscarUsuarioPage buscar = new BuscarUsuarioPage(driver);
        buscar.buscarUsuario("");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("El email es obligatorio", alert.getText(), "Debe mostrar error por email vacío");
        alert.accept();
    }

    @Test
    void testActualizarUsuarioExitoso() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Actualizar nombre']")));
        MenuPage menu = new MenuPage(driver);
        // Crear usuario primero
        menu.goToCrearUsuario();
        CrearUsuarioPage crear = new CrearUsuarioPage(driver);
        String email = "updateuser" + System.currentTimeMillis() + "@mail.com";
        crear.crearUsuario(email, "Nombre Antiguo");
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        // Buscar ID del usuario creado
        menu.goToBuscarUsuario();
        BuscarUsuarioPage buscar = new BuscarUsuarioPage(driver);
        buscar.buscarUsuario(email);
        wait.until(ExpectedConditions.textToBePresentInElement(buscar.searchResult, email));
        String resultado = buscar.getResultado();
        String id = resultado.replaceAll(".*ID: (\\d+).*", "$1");
        // Actualizar nombre
        menu.goToActualizarNombre();
        ActualizarUsuarioPage actualizar = new ActualizarUsuarioPage(driver);
        actualizar.actualizarUsuario(id, "Nombre Nuevo");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Usuario actualizado exitosamente", alert.getText(), "Debe mostrar mensaje de éxito al actualizar");
        alert.accept();
    }

    @Test
    void testActualizarUsuarioNoExistente() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Actualizar nombre']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToActualizarNombre();
        ActualizarUsuarioPage actualizar = new ActualizarUsuarioPage(driver);
        actualizar.actualizarUsuario("999999", "Nombre Nuevo");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Usuario no encontrado", alert.getText(), "Debe mostrar error si el usuario no existe");
        alert.accept();
    }

    @Test
    void testActualizarUsuarioCamposVacios() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Actualizar nombre']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToActualizarNombre();
        ActualizarUsuarioPage actualizar = new ActualizarUsuarioPage(driver);
        actualizar.actualizarUsuario("", "");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("ID y nombre son obligatorios", alert.getText(), "Debe mostrar error por campos vacíos");
        alert.accept();
    }

    @Test
    void testEliminarUsuarioExitoso() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Eliminar usuario']")));
        MenuPage menu = new MenuPage(driver);
        // Crear usuario primero
        menu.goToCrearUsuario();
        CrearUsuarioPage crear = new CrearUsuarioPage(driver);
        String email = "deleteuser" + System.currentTimeMillis() + "@mail.com";
        crear.crearUsuario(email, "Delete User");
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        // Buscar ID del usuario creado
        menu.goToBuscarUsuario();
        BuscarUsuarioPage buscar = new BuscarUsuarioPage(driver);
        buscar.buscarUsuario(email);
        wait.until(ExpectedConditions.textToBePresentInElement(buscar.searchResult, email));
        String resultado = buscar.getResultado();
        String id = resultado.replaceAll(".*ID: (\\d+).*", "$1");
        // Eliminar usuario
        menu.goToEliminarUsuario();
        EliminarUsuarioPage eliminar = new EliminarUsuarioPage(driver);
        eliminar.eliminarUsuario(id);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Usuario eliminado exitosamente", alert.getText(), "Debe mostrar mensaje de éxito al eliminar");
        alert.accept();
    }

    @Test
    void testEliminarUsuarioNoExistente() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Eliminar usuario']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToEliminarUsuario();
        EliminarUsuarioPage eliminar = new EliminarUsuarioPage(driver);
        eliminar.eliminarUsuario("999999");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Usuario no encontrado", alert.getText(), "Debe mostrar error si el usuario no existe");
        alert.accept();
    }

    @Test
    void testEliminarUsuarioCampoVacio() {
        LoginPage login = new LoginPage(driver);
        login.login("admin", "admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Eliminar usuario']")));
        MenuPage menu = new MenuPage(driver);
        menu.goToEliminarUsuario();
        EliminarUsuarioPage eliminar = new EliminarUsuarioPage(driver);
        eliminar.eliminarUsuario("");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("El ID es obligatorio", alert.getText(), "Debe mostrar error por ID vacío");
        alert.accept();
    }
}
