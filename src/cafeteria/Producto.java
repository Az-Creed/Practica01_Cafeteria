package cafeteria;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    private static final String ARCHIVO_PRODUCTOS = "producto.txt";

    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public void reponerStock(int cantidad) {
        if (cantidad > 0) {
            this.stock += cantidad;
        }
    }

    public boolean reducirStock(int cantidad) {
        if (cantidad > 0 && this.stock >= cantidad) {
            this.stock -= cantidad;
            return true;
        }
        return false;
    }

    public String toFormatoArchivo() {
        return id + "," + nombre + "," + precio + "," + stock;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %-15s | Precio: $%.2f | Stock: %d", id, nombre, precio, stock);
    }

    public static ArrayList<Producto> cargarProductos() {
        ArrayList<Producto> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO_PRODUCTOS);
        if (archivo.exists()) {
            try (Scanner lector = new Scanner(archivo)) {
                while (lector.hasNextLine()) {
                    String linea = lector.nextLine().trim();
                    if (!linea.isEmpty()) {
                        String[] datos = linea.split(",");
                        int id = Integer.parseInt(datos[0]);
                        String nombre = datos[1];
                        double precio = Double.parseDouble(datos[2]);
                        int stock = Integer.parseInt(datos[3]);
                        lista.add(new Producto(id, nombre, precio, stock));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al cargar producto.txt: " + e.getMessage());
            }
        }
        return lista;
    }

    public static void guardarProductos(ArrayList<Producto> lista) {
        File archivo = new File(ARCHIVO_PRODUCTOS);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (Producto p : lista) {
                bw.write(p.toFormatoArchivo());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar producto.txt: " + e.getMessage());
        }
    }
}
