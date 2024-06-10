package com.example.chefapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.chefapp.databinding.ActivityMainBinding;
import com.example.chefapp.ui.inicio.InicioFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase dbRecetas;
    public static int fotoReceta;
    public static String nombreReceta;
    public static String dificultadReceta;
    public static int comensalesReceta;
    public static int tiempoReceta;
    public static String descripcionReceta;
    public static String caracteristicasAdicionalesReceta;
    public static String ingredientesReceta;
    public static String pasosRealizacionReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cambiar el color de la barra de navegación (Barra inferior de Android)
        getWindow().setNavigationBarColor(getResources().getColor(R.color.selective_yellow, null));

        crearBDRecetas();
        insertarRecetasEnBD();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Controlar la navegación hacia atrás
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Si no estamos en la pantalla de inicio o estamos en la pantalla de inicio y estamos buscando recetas, navegamos a la pantalla de inicio
                if (Objects.requireNonNull(Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main).getCurrentDestination()).getId() != R.id.navigation_inicio || InicioFragment.buscar) {
                    Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_inicio);
                } else {
                    crearDialogoSalida();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(callback);
    }

    /**
     * Método para crear la base de datos de recetas si no existe.
     */
    private void crearBDRecetas() {
        dbRecetas = openOrCreateDatabase("chefApp", MODE_PRIVATE, null);
        dbRecetas.execSQL("CREATE TABLE IF NOT EXISTS recetas (id INTEGER PRIMARY KEY AUTOINCREMENT, foto BLOB, nombreReceta TEXT, dificultad TEXT, comensales INTEGER, tiempo INTEGER, " +
                "descripcion TEXT, caracteristicasAdicionales TEXT, ingredientes TEXT, pasosRealizacion TEXT)");
    }

    /**
     * Método para insertar las recetas en la base de datos si no existen.
     */
    private void insertarRecetasEnBD() {
        Cursor cursor = dbRecetas.rawQuery("SELECT * FROM recetas", null);

        if (cursor.getCount() == 0) {
            dbRecetas.execSQL("INSERT INTO recetas (foto, nombreReceta, dificultad, comensales, tiempo, descripcion, caracteristicasAdicionales, ingredientes, pasosRealizacion) VALUES " +
                    "(" + R.drawable.ensalada_cesar + ", 'Ensalada César', 'Dificultad fácil', 4, 20, 'Una ensalada clásica con pollo, lechuga romana, parmesano y aderezo César.', 'Ideal para una comida ligera.', 'Lechuga romana, pechuga de pollo, queso parmesano, crutones, aderezo César', '1. Cocinar la pechuga de pollo a la parrilla y cortar en tiras. 2. Lavar y cortar la lechuga romana en trozos grandes. 3. Mezclar la lechuga con el aderezo César en un bol grande. 4. Añadir las tiras de pollo, el queso parmesano rallado y los crutones a la ensalada. 5. Servir inmediatamente.'), " +
                    "(" + R.drawable.paella + ", 'Paella', 'Dificultad media', 6, 90, 'Plato tradicional español con arroz, mariscos y azafrán.', 'Perfecto para ocasiones especiales.', 'Arroz, mariscos, pollo, pimiento rojo, guisantes, azafrán, caldo de pescado', '1. En una paellera, calentar aceite de oliva y sofreír el pollo troceado hasta que esté dorado. 2. Añadir los mariscos (gambas, mejillones, calamares) y cocinar durante unos minutos. 3. Agregar el pimiento rojo cortado en tiras y los guisantes. 4. Añadir el arroz y mezclar bien con los ingredientes. 5. Verter el caldo de pescado caliente con el azafrán disuelto. 6. Cocinar a fuego medio hasta que el arroz esté en su punto y se haya absorbido todo el líquido. 7. Dejar reposar unos minutos antes de servir.'), " +
                    "(" + R.drawable.tacos_al_pastor + ", 'Tacos al Pastor', 'Dificultad fácil', 4, 60, 'Tacos de cerdo marinados con piña y especias.', 'Deliciosos y llenos de sabor.', 'Carne de cerdo, piña, tortillas de maíz, cebolla, cilantro, salsa', '1. Marinar la carne de cerdo en una mezcla de achiote, jugo de piña, vinagre, ajo y especias durante al menos 2 horas. 2. Cocinar la carne en una sartén grande a fuego medio hasta que esté bien cocida y ligeramente dorada. 3. Calentar las tortillas de maíz en una sartén. 4. Picar finamente la cebolla y el cilantro. 5. Servir la carne sobre las tortillas calientes y añadir piña asada, cebolla, cilantro y salsa al gusto.'), " +
                    "(" + R.drawable.lasania + ", 'Lasaña', 'Dificultad media', 8, 120, 'Plato italiano con capas de pasta, carne y queso.', 'Perfecta para una comida familiar.', 'Pasta de lasagna, carne molida, salsa de tomate, queso ricotta, queso mozzarella', '1. Precalentar el horno a 180°C. 2. Cocinar la carne molida en una sartén grande hasta que esté dorada. 3. Añadir la salsa de tomate a la carne y cocinar a fuego lento durante 15 minutos. 4. Hervir las láminas de pasta de lasagna según las instrucciones del paquete. 5. En una bandeja para horno, colocar una capa de salsa de carne, seguida de una capa de pasta, una capa de queso ricotta y una capa de queso mozzarella rallado. 6. Repetir las capas hasta que se acaben los ingredientes, terminando con una capa de queso mozzarella. 7. Hornear durante 45 minutos o hasta que el queso esté dorado y burbujeante. 8. Dejar reposar 10 minutos antes de servir.'), " +
                    "(" + R.drawable.shusi + ", 'Sushi', 'Dificultad difícil', 4, 60, 'Rollos de sushi con pescado fresco y arroz.', 'Ideal para una cena elegante.', 'Arroz para sushi, pescado fresco, algas nori, vegetales', '1. Cocinar el arroz para sushi y dejar enfriar ligeramente. 2. Colocar una hoja de alga nori sobre una esterilla de bambú. 3. Extender una capa fina de arroz sobre el nori, dejando un borde libre en la parte superior. 4. Colocar tiras de pescado fresco y vegetales (como pepino y aguacate) en el centro del arroz. 5. Enrollar el sushi firmemente con la ayuda de la esterilla de bambú. 6. Cortar el rollo en porciones con un cuchillo afilado y mojado. 7. Servir con salsa de soja, jengibre encurtido y wasabi.'), " +
                    "(" + R.drawable.gazpacho + ", 'Gazpacho', 'Dificultad fácil', 4, 30, 'Sopa fría de tomate y vegetales.', 'Refrescante en verano.', 'Tomates, pepino, pimiento, cebolla, ajo, aceite de oliva, vinagre', '1. Lavar y cortar los tomates, el pepino, el pimiento, la cebolla y el ajo. 2. Colocar todos los vegetales en una licuadora y añadir aceite de oliva, vinagre, sal y pimienta al gusto. 3. Licuar hasta obtener una mezcla homogénea. 4. Pasar la mezcla por un colador para eliminar las pieles y semillas. 5. Enfriar en el refrigerador durante al menos 1 hora. 6. Servir el gazpacho bien frío, decorado con trocitos de pepino, pimiento y un chorrito de aceite de oliva.'), " +
                    "(" + R.drawable.ceviche + ", 'Ceviche', 'Dificultad media', 4, 45, 'Plato de mariscos marinados en cítricos.', 'Ideal para una entrada.', 'Pescado blanco, jugo de limón, cebolla, cilantro, tomate, ají', '1. Cortar el pescado blanco en cubos pequeños. 2. Exprimir suficiente jugo de limón para cubrir completamente el pescado. 3. Marinar el pescado en el jugo de limón durante 20-30 minutos, hasta que el pescado esté opaco. 4. Picar finamente la cebolla, el cilantro, el tomate y el ají. 5. Mezclar el pescado marinado con los vegetales picados. 6. Añadir sal y pimienta al gusto. 7. Servir inmediatamente con chips de tortilla o tostadas.'), " +
                    "(" + R.drawable.pizza_4_estaciones + ", 'Pizza 4 Estaciones', 'Dificultad media', 4, 45, 'Pizza italiana con cuatro ingredientes diferentes en cada cuarto.', 'Una deliciosa variedad de sabores en una sola pizza', 'Masa de pizza, salsa de tomate, queso mozzarella, champiñones, jamón, atún en lata, aceitunas negras.', '1. Precalentar el horno a 220°C. 2. Estirar la masa de pizza sobre una superficie enharinada y transferirla a una bandeja para hornear. 3. Extender una capa uniforme de salsa de tomate sobre la masa. 4. Distribuir el queso mozzarella rallado por toda la pizza. 5. Dividir la pizza en cuatro secciones imaginarias. 6. En la primera sección, colocar los champiñones laminados. 7. En la segunda sección, colocar las rodajas de jamón. 8. En la tercera sección, colocar el atún escurrido de la lata. 9. En la cuarta sección, colocar las aceitunas negras. 10. Hornear durante 15-20 minutos, o hasta que la masa esté dorada y el queso derretido y burbujeante. 11. Retirar del horno y dejar enfriar unos minutos antes de cortar y servir.'), " +
                    "(" + R.drawable.brownies + ", 'Brownies', 'Dificultad media', 16, 50, 'Densos y chocolatosos.', 'Perfectos para los amantes del chocolate.', 'Chocolate, mantequilla, azúcar, huevos, harina, nueces', '1. Precalentar el horno a 180°C. 2. Derretir el chocolate y la mantequilla juntos en un bol grande. 3. Añadir el azúcar y mezclar bien. 4. Incorporar los huevos uno a uno, batiendo bien después de cada adición. 5. Añadir la harina y mezclar hasta que esté bien combinada. 6. Agregar las nueces picadas. 7. Verter la mezcla en un molde engrasado y hornear durante 25-30 minutos, hasta que un palillo insertado en el centro salga limpio. 8. Dejar enfriar completamente antes de cortar en cuadrados.'), " +
                    "(" + R.drawable.chilli_con_carne + ", 'Chili con Carne', 'Dificultad media', 6, 120, 'Un plato picante y reconfortante de carne y frijoles.', 'Ideal para los días fríos.', 'Carne molida, frijoles rojos, tomates, cebolla, ajo, chile en polvo, comino, pimentón', '1. En una olla grande, calentar aceite y sofreír la cebolla y el ajo picados hasta que estén tiernos. 2. Añadir la carne molida y cocinar hasta que esté dorada. 3. Agregar los tomates triturados, los frijoles rojos, el chile en polvo, el comino y el pimentón. 4. Cocinar a fuego lento durante al menos 1 hora, removiendo de vez en cuando. 5. Ajustar el sazón con sal y pimienta al gusto. 6. Servir caliente con arroz, pan de maíz o tortillas.')");
        }

        cursor.close();
    }

    /**
     * Método para crear un diálogo de confirmación de salida de la aplicación.
     */
    private void crearDialogoSalida() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
        builder.setMessage("¿Estás seguro de que quieres salir de ChefApp?");
        builder.setPositiveButton("Sí", (dialog, which) -> finish());
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}