package com.example.chefapp.ui.inicio.detallesReceta;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chefapp.MainActivity;
import com.example.chefapp.R;

public class DetallesReceta extends AppCompatActivity {

    private ImageView imagenReceta;
    private TextView nombreReceta;
    private TextView descripcionReceta;
    private TextView dificultadReceta;
    private TextView comensalesReceta;
    private TextView tiempoReceta;
    private TextView caracteristicasAdicionalesReceta;
    private TextView ingredientesReceta;
    private TextView pasosReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles_receta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detallesRecetaActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cambiar color de la barra de estado y de navegación (Barras inferior y superior (Noticaciones, batería, etc.))
        getWindow().setStatusBarColor(getResources().getColor(R.color.selective_yellow, null));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.selective_yellow, null));

        findElementosVisuales();
        formatearCamposReceta();
        rellenarCamposReceta();
    }

    /**
     * Busca los elementos visuales en el layout.
     */
    private void findElementosVisuales() {
        imagenReceta = findViewById(R.id.imagenReceta);
        nombreReceta = findViewById(R.id.nombreReceta);
        descripcionReceta = findViewById(R.id.descripcionReceta);
        dificultadReceta = findViewById(R.id.dificultadReceta);
        comensalesReceta = findViewById(R.id.numComensales);
        tiempoReceta = findViewById(R.id.numTiempoReceta);
        caracteristicasAdicionalesReceta = findViewById(R.id.listaCaracteristicasAdicionalesReceta);
        ingredientesReceta = findViewById(R.id.listaIngredientesReceta);
        pasosReceta = findViewById(R.id.listaPasosReceta);
    }

    /**
     * Formatea los campos de la receta.
     */
    private void formatearCamposReceta() {
        formatIngredientes();
        formatPasos();
    }

    /**
     * Rellena los campos de la receta.
     */
    private void rellenarCamposReceta() {
        imagenReceta.setImageResource(MainActivity.fotoReceta);
        nombreReceta.setText(MainActivity.nombreReceta);
        descripcionReceta.setText(MainActivity.descripcionReceta);
        dificultadReceta.setText(MainActivity.dificultadReceta);
        comensalesReceta.setText(String.valueOf(MainActivity.comensalesReceta));
        tiempoReceta.setText(String.valueOf(MainActivity.tiempoReceta));
        caracteristicasAdicionalesReceta.setText(MainActivity.caracteristicasAdicionalesReceta);
    }

    /**
     * Formatea los ingredientes de la receta.
     */
    private void formatIngredientes() {
        String[] arrayIngredientes = MainActivity.ingredientesReceta.split(", ");
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < arrayIngredientes.length; i++) {
            String ingrediente = arrayIngredientes[i];
            String ingredienteMayuscula = ingrediente.substring(0, 1).toUpperCase() + ingrediente.substring(1);
            stringBuilder.append("• ").append(ingredienteMayuscula);

            if (i < arrayIngredientes.length - 1) {
                stringBuilder.append("\n");
            }
        }

        ingredientesReceta.setText(stringBuilder.toString());
    }

    /**
     * Formatea los pasos de la receta.
     */
    private void formatPasos() {
        String[] arrayPasos = MainActivity.pasosRealizacionReceta.split("(?=\\d+\\.\\s)");
        StringBuilder stringBuilder = new StringBuilder();

        for (String paso : arrayPasos) {
            if (!paso.trim().isEmpty()) {
                stringBuilder.append(paso.trim()).append("\n\n");
            }
        }

        String pasosQuitarUltimoSaltoLinea = stringBuilder.toString().trim();
        pasosReceta.setText(pasosQuitarUltimoSaltoLinea);
    }
}