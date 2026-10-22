package cafeteria;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Pedido {
    private int idPedido;
    private String cliente;
    private ArrayList<Producto> productos;
    private ArrayList<Integer> cantidades;
    private double total;

    private static final String ARCHIVO_PEDIDOS = "pedidos.txt";

    public Pedido(int idPedido, String cliente) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.productos = new ArrayList<>();
        this.cantidades = new ArrayList<>();
        this.total = 0.0;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public String getCliente() {
        return cliente;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public ArrayList<Integer> getCantidades() {
        return cantidades;
    }

    public double getTotal() {
        return total;
    }

    public void agregarProducto(Producto prod, int cantidad) {
        productos.add(prod);
        cantidades.add(cantidad);
        total += prod.getPrecio() * cantidad;
    }

    public String toFormatoArchivo() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            int cant = cantidades.get(i);
            if (i > 0) {
                sb.append(";");
            }
            sb.append(p.getId()).append(":")
              .append(p.getNombre()).append(":")
              .append(p.getPrecio()).append(":")
              .append(cant);
        }
        return idPedido + "," + cliente + "," + total + "," + sb.toString();
    }

    public void mostrarTicket() {
        System.out.println("\n--- TICKET DE PEDIDO #" + idPedido + " ---");
        System.out.println("Cliente: " + cliente);
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            int cant = cantidades.get(i);
            double subtotal = p.getPrecio() * cant;
            System.out.printf("- %s x%d ($%.2f c/u) = $%.2f%n", p.getNombre(), cant, p.getPrecio(), subtotal);
        }
        System.out.printf("TOTAL A PAGAR: $%.2f%n", total);
        System.out.println("------------------------------");
    }

    public static ArrayList<Pedido> cargarPedidos() {
        ArrayList<Pedido> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO_PEDIDOS);
        if (archivo.exists()) {
            try (Scanner lector = new Scanner(archivo)) {
                while (lector.hasNextLine()) {
                    String linea = lector.nextLine().trim();
                    if (!linea.isEmpty()) {
                        String[] datos = linea.split(",");
                        int idPed = Integer.parseInt(datos[0]);
                        String cli = datos[1];
                        Pedido ped = new Pedido(idPed, cli);
                        if (datos.length > 3 && !datos[3].isEmpty()) {
                            String[] items = datos[3].split(";");
                            for (String item : items) {
                                String[] det = item.split(":");
                                int idP = Integer.parseInt(det[0]);
                                String nomP = det[1];
                                double preP = Double.parseDouble(det[2]);
                                int cantP = Integer.parseInt(det[3]);
                                ped.agregarProducto(new Producto(idP, nomP, preP, 0), cantP);
                            }
                        }
                        lista.add(ped);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al cargar pedidos.txt: " + e.getMessage());
            }
        }
        return lista;
    }

    public static void guardarPedidos(ArrayList<Pedido> lista) {
        File archivo = new File(ARCHIVO_PEDIDOS);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (Pedido p : lista) {
                bw.write(p.toFormatoArchivo());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar pedidos.txt: " + e.getMessage());
        }
    }
}
