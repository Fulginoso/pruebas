package com.example.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal del sistema.
 * Esta clase simula un sistema básico de usuarios donde es posible:
 * - Registrar usuarios
 * - Iniciar sesión
 * - Consultar información de perfil
 * - Realizar operaciones simples como transferencias de saldo
 *
 * Los comentarios están diseñados para ser interpretados por el Swarm.
 */
public class UserService {

    private Map<String, User> users = new HashMap<>();

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Reglas funcionales:
     * - No se puede registrar un usuario con un nombre ya existente.
     * - La contraseña debe tener al menos 6 caracteres.
     * - El usuario comienza con un saldo inicial de 100.
     *
     * @param username nombre del usuario
     * @param password contraseña del usuario
     * @return true si el registro fue exitoso, false si hubo error
     */
    public boolean registerUser(String username, String password) {
        // Verifica si el usuario ya existe
        if (users.containsKey(username)) {
            return false;
        }

        // Verifica longitud mínima de contraseña
        if (password.length() < 6) {
            return false;
        }

        users.put(username, new User(username, password, 100.0));
        return true;
    }

    /**
     * Inicia sesión en el sistema.
     *
     * Reglas funcionales:
     * - El usuario debe existir.
     * - La contraseña debe coincidir.
     * - Retorna un objeto User si el login es válido.
     *
     * @param username nombre del usuario
     * @param password contraseña del usuario
     * @return el usuario autenticado o null si falla
     */
    public User login(String username, String password) {
        // Comprueba la existencia del usuario
        if (!users.containsKey(username)) {
            return null;
        }

        User user = users.get(username);

        // Validación de contraseña
        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    /**
     * Obtiene información del perfil de un usuario.
     *
     * Reglas funcionales:
     * - Solo se puede consultar un usuario existente.
     * - Retorna nombre y saldo actual.
     *
     * @param username nombre del usuario
     * @return cadena con información del usuario o mensaje de error
     */
    public String getProfile(String username) {
        if (!users.containsKey(username)) {
            return "Usuario no encontrado.";
        }

        User user = users.get(username);
        return "Usuario: " + user.getUsername() + " | Saldo: " + user.getBalance();
    }

    /**
     * Realiza una transferencia de dinero entre usuarios.
     *
     * Reglas funcionales:
     * - Ambos usuarios deben existir.
     * - El usuario origen debe tener suficiente dinero.
     * - La cantidad debe ser mayor que 0.
     *
     * Flujo normal:
     * 1. Validar datos.
     * 2. Restar saldo del origen.
     * 3. Sumar saldo al destino.
     * 4. Retornar éxito.
     *
     * Flujo alterno:
     * - Saldo insuficiente.
     * - Usuario inexistente.
     * - Monto inválido.
     *
     * @param from usuario que envía
     * @param to usuario que recibe
     * @param amount cantidad a transferir
     * @return mensaje indicando éxito o el tipo de error
     */
    public String transfer(String from, String to, double amount) {

        if (!users.containsKey(from) || !users.containsKey(to)) {
            return "Uno de los usuarios no existe.";
        }

        if (amount <= 0) {
            return "La cantidad debe ser mayor que cero.";
        }

        User userFrom = users.get(from);
        User userTo = users.get(to);

        // Validar saldo
        if (userFrom.getBalance() < amount) {
            return "Saldo insuficiente.";
        }

        // Realizar transferencia
        userFrom.setBalance(userFrom.getBalance() - amount);
        userTo.setBalance(userTo.getBalance() + amount);

        return "Transferencia completada con éxito.";
    }

    public static void main(String[] args) {
        UserService service = new UserService();

        /*
         * Escenario de prueba básico.
         * Estas acciones permitirán al Swarm generar:
         * - Historias de usuario basadas en comentarios
         * - Documentación funcional
         * - Casos de prueba
         * - Scripts Selenium
         */

        service.registerUser("juan", "password123");
        service.registerUser("maria", "secret789");

        User logged = service.login("juan", "password123");

        if (logged != null) {
            System.out.println(service.getProfile("juan"));
            System.out.println(service.transfer("juan", "maria", 50));
            System.out.println(service.getProfile("juan"));
            System.out.println(service.getProfile("maria"));
        }
    }
}

/**
 * Clase interna User.
 * Representa un usuario del sistema.
 *
 * Atributos principales:
 * - username: nombre único del usuario.
 * - password: texto plano (solo para demo, no usar en producción).
 * - balance: monto disponible del usuario.
 */
class User {
    private String username;
    private String password;
    private double balance;

    public User(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
