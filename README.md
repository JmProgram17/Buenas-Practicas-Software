# 🧾 Informe de Calidad y Refactorización de Código - `UserManager.java`

## 🧠 Autor: Julián Mateo Castellanos Cuervo 
**Fecha:** 2025-05-02  
**Versión original del código:** Código inicial con estructura estática y mínima validación.  
**Objetivo:** Evaluar, documentar y mejorar la calidad de un componente de gestión de usuarios en Java.

---

## 🧩 Tabla de Contenido

1. [Resumen del Código Original](#1-resumen-del-código-original)
2. [Problemas Detectados](#2-problemas-detectados)
3. [Análisis del Impacto](#3-análisis-del-impacto)
4. [Propuestas de Solución](#4-propuestas-de-solución)
5. [Implementación de Mejoras](#5-implementación-de-mejoras)
6. [Código Refactorizado Final](#6-código-refactorizado-final)
7. [Conclusiones](#7-conclusiones)

---

## 1. 📄 Resumen del Código Original

El código original implementa una clase `UserManager` en Java que permite agregar usuarios (hasta 10) y listarlos por consola. Sin embargo, presenta múltiples problemas de calidad relacionados con **legibilidad, escalabilidad, encapsulamiento, validación y manejo de errores**.

---

## 2. 🛑 Problemas Detectados

| Nº | Problema | Descripción Técnica |
|----|----------|---------------------|
| 1 | **Nombres no significativos** | Métodos como `a()` y `p()` no comunican su propósito, lo que daña la legibilidad. |
| 2 | **Estructura de datos rígida (`String[]`)** | El uso de un arreglo estático limita la escalabilidad y requiere lógica manual para manejo de índice. |
| 3 | **Campos públicos (`public static`)** | Exponer `users` y `userCount` viola principios de encapsulamiento y seguridad del estado. |
| 4 | **Validación de entrada mínima** | Solo se valida que el nombre no sea `null` o vacío, pero no se controlan duplicados ni espacios. |
| 5 | **Errores sin contexto** | Mensajes de error genéricos (`System.out.println("Error")`) impiden diagnóstico efectivo. |
| 6 | **Falta de persistencia** | Los datos se pierden al cerrar el programa; no hay almacenamiento permanente. |
| 7 | **No se registra la actividad del sistema** | No se auditan acciones del usuario ni errores. |
| 8 | **No se maneja sensibilidad a mayúsculas** | Permite duplicados como `Carlos` y `carlos`, lo cual es inconsistente. |

---

## 3. 📉 Análisis del Impacto

| Problema | Impacto en la Calidad |
|---------|------------------------|
| 1. Nombres no significativos | Reduce la mantenibilidad, dificulta la colaboración. |
| 2. Estructura rígida | Dificulta adaptación a futuros cambios (ej. usuarios ilimitados). |
| 3. Campos públicos | Permite modificación no controlada desde fuera de la clase. Riesgo de errores. |
| 4. Validación mínima | Riesgo de datos duplicados o inválidos en el sistema. |
| 5. Errores genéricos | Dificulta la depuración y soporte técnico. |
| 6. Falta de persistencia | Inutiliza el sistema para casos de uso reales donde se requiere mantener datos. |
| 7. Sin auditoría | Falta de trazabilidad, incumplimiento de requisitos de seguridad o auditoría. |
| 8. Sensibilidad a mayúsculas | Inconsistencias en la experiencia de usuario y duplicados ocultos. |

---

## 4. 🛠️ Propuestas de Solución

| Problema | Solución Propuesta |
|---------|---------------------|
| 1. Nombres | Renombrar métodos a `addUser()` y `printUsers()`. |
| 2. Arreglo fijo | Usar `ArrayList<String>` para manejar usuarios dinámicamente. |
| 3. Encapsulamiento | Hacer los campos `private` y finales. |
| 4. Validación | Añadir validación de duplicados, espacios en blanco y longitud. |
| 5. Errores | Usar mensajes claros y auditables. |
| 6. Persistencia | Guardar usuarios en `users.txt` y leer al iniciar. |
| 7. Auditoría | Registrar acciones importantes en `log.txt`. |
| 8. Mayúsculas | Convertir entradas a minúsculas de forma uniforme. |

---

## 5. 💡 Implementación de Mejoras

Las mejoras incluyen:

- Validación exhaustiva del input.
- Gestión dinámica de la colección de usuarios.
- Persistencia mediante archivos de texto.
- Auditoría de acciones para rastrear el uso del sistema.
- Estilo de código claro, limpio y documentado.

---

## 6. ✅ Código Refactorizado Final

```java
import java.io.*;
import java.util.*;

public class UserManager {

    private static final int MAX_USERS = 10;
    private static final List<String> users = new ArrayList<>();
    private static final String LOG_FILE = "log.txt";
    private static final String USERS_FILE = "users.txt";

    static {
        loadUsersFromFile();
    }

    public static boolean addUser(String user) {
        if (user == null || user.trim().isEmpty()) {
            log("Attempted to add invalid user: " + user);
            System.out.println("Invalid user name.");
            return false;
        }

        user = user.trim().toLowerCase();

        if (users.contains(user)) {
            log("Attempted to add duplicate user: " + user);
            System.out.println("User already exists: " + user);
            return false;
        }

        if (users.size() >= MAX_USERS) {
            log("Attempted to add user but capacity is full.");
            System.out.println("Cannot add more users: capacity full.");
            return false;
        }

        users.add(user);
        log("User added: " + user);
        saveUsersToFile();
        System.out.println("User added: " + user);
        return true;
    }

    public static void printUsers() {
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (String user : users) {
                System.out.println(user);
            }
        }
    }

    private static void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("No previous user data found.");
        }
    }

    private static void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (String user : users) {
                writer.write(user);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users to file.");
        }
    }

    private static void log(String message) {
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            logWriter.write(new Date() + " - " + message);
            logWriter.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to log file.");
        }
    }
}
