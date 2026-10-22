package cafeteria;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static ArrayList<Producto> catalogo;
    private static ArrayList<Pedido> pedidos;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        catalogo = Producto.cargarProductos();
        pedidos = Pedido.cargarPedidos();

        int opcion = 0;
        do {
            System.out.println("\n=== CAFETERIA EL BUEN SABOR ===");
            System.out.println("1. Registrar nuevo producto");
            System.out.println("2. Listar catalogo de productos");
            System.out.println("3. Reponer stock de producto");
            System.out.println("4. Registrar nuevo pedido");
            System.out.println("5. Reporte de ventas y mayor demanda");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = 0;
            }

            switch (opcion) {
                case 1:
                    registrarProducto();
                    break;
                case 2:
                    listarCatalogo();
                    break;
                case 3:
                    reponerStock();
                    break;
                case 4:
                    registrarPedido();
                    break;
                case 5:
                    generarReporteVentas();
                    break;
                case 6:
                    Producto.guardarProductos(catalogo);
                    Pedido.guardarPedidos(pedidos);
                    System.out.println("Datos guardados en producto.txt y pedidos.txt. Saliendo...");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
            }
        } while (opcion != 6);
    }

    private static void registrarProducto() {
        try {
            System.out.print("ID del producto: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            if (buscarProductoPorId(id) != null) {
                System.out.println("Error: Ya existe un producto con ese ID.");
                return;
            }

            System.out.print("Nombre del producto: ");
            String nombre = sc.nextLine().trim();

            System.out.print("Precio unitario: ");
            double precio = Double.parseDouble(sc.nextLine().trim());

            System.out.print("Stock inicial: ");
            int stock = Integer.parseInt(sc.nextLine().trim());

            if (precio < 0 || stock < 0) {
                System.out.println("Error: Precio y stock deben ser valores positivos.");
                return;
            }

            catalogo.add(new Producto(id, nombre, precio, stock));
            Producto.guardarProductos(catalogo);
            System.out.println("Producto registrado correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Entrada numerica invalida.");
        }
    }

    private static void listarCatalogo() {
        System.out.println("\n--- CATALOGO DE PRODUCTOS ---");
        if (catalogo.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        for (Producto p : catalogo) {
            System.out.println(p);
        }
    }

    private static void reponerStock() {
        try {
            System.out.print("Ingrese el ID del producto a reponer: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Producto p = buscarProductoPorId(id);

            if (p == null) {
                System.out.println("Error: Producto no encontrado.");
                return;
            }

            System.out.print("Cantidad a agregar al stock: ");
            int cantidad = Integer.parseInt(sc.nextLine().trim());

            if (cantidad <= 0) {
                System.out.println("Error: La cantidad debe ser mayor a 0.");
                return;
            }

            p.reponerStock(cantidad);
            Producto.guardarProductos(catalogo);
            System.out.println("Stock actualizado. Nuevo stock de " + p.getNombre() + ": " + p.getStock());
        } catch (NumberFormatException e) {
            System.out.println("Error: Entrada numerica invalida.");
        }
    }

    private static void registrarPedido() {
        if (catalogo.isEmpty()) {
            System.out.println("No hay productos en el catalogo para realizar pedidos.");
            return;
        }

        System.out.print("Nombre del cliente: ");
        String cliente = sc.nextLine().trim();
        int idPedido = pedidos.size() + 1;
        Pedido nuevoPedido = new Pedido(idPedido, cliente);

        String continuar = "s";
        while (continuar.equalsIgnoreCase("s")) {
            listarCatalogo();
            try {
                System.out.print("Ingrese ID del producto a comprar: ");
                int idProd = Integer.parseInt(sc.nextLine().trim());
                Producto prod = buscarProductoPorId(idProd);

                if (prod == null) {
                    System.out.println("Producto no encontrado.");
                } else if (prod.getStock() == 0) {
                    System.out.println("Alerta: Producto agotado (Stock 0).");
                } else {
                    System.out.print("Cantidad solicitada (Disponible: " + prod.getStock() + "): ");
                    int cant = Integer.parseInt(sc.nextLine().trim());

                    if (prod.reducirStock(cant)) {
                        nuevoPedido.agregarProducto(prod, cant);
                        System.out.println("Producto agregado al pedido.");
                    } else {
                        System.out.println("Alerta: Stock insuficiente o cantidad invalida.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un numero valido.");
            }

            System.out.print("Desea agregar otro producto al pedido? (s/n): ");
            continuar = sc.nextLine().trim();
        }

        if (!nuevoPedido.getProductos().isEmpty()) {
            pedidos.add(nuevoPedido);
            Producto.guardarProductos(catalogo);
            Pedido.guardarPedidos(pedidos);
            nuevoPedido.mostrarTicket();
        } else {
            System.out.println("Pedido cancelado por no contener productos.");
        }
    }

    private static void generarReporteVentas() {
        System.out.println("\n--- REPORTE DE VENTAS DEL DIA ---");
        if (pedidos.isEmpty()) {
            System.out.println("Aun no se han registrado ventas.");
            return;
        }

        double totalRecaudado = 0;
        ArrayList<String> nombresProd = new ArrayList<>();
        ArrayList<Integer> cantidadesVendidas = new ArrayList<>();

        for (Pedido ped : pedidos) {
            totalRecaudado += ped.getTotal();
            for (int i = 0; i < ped.getProductos().size(); i++) {
                String nombre = ped.getProductos().get(i).getNombre();
                int cant = ped.getCantidades().get(i);

                int idx = nombresProd.indexOf(nombre);
                if (idx == -1) {
                    nombresProd.add(nombre);
                    cantidadesVendidas.add(cant);
                } else {
                    cantidadesVendidas.set(idx, cantidadesVendidas.get(idx) + cant);
                }
            }
        }

        System.out.println("Total de pedidos atendidos: " + pedidos.size());
        System.out.printf("Total vendido en el dia: $%.2f%n", totalRecaudado);

        System.out.println("\nDemanda por producto:");
        String prodMasVendido = "";
        int maxUnidades = 0;

        for (int i = 0; i < nombresProd.size(); i++) {
            String nom = nombresProd.get(i);
            int cant = cantidadesVendidas.get(i);
            System.out.println("- " + nom + ": " + cant + " unidades vendidas");
            if (cant > maxUnidades) {
                maxUnidades = cant;
                prodMasVendido = nom;
            }
        }

        System.out.println("Producto de mayor demanda: " + prodMasVendido + " (" + maxUnidades + " unidades)");
    }

    private static Producto buscarProductoPorId(int id) {
        for (Producto p : catalogo) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
