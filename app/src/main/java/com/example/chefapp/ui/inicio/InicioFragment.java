package com.example.chefapp.ui.inicio;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chefapp.MainActivity;
import com.example.chefapp.R;
import com.example.chefapp.adaptadores.AdaptadorRecetas;
import com.example.chefapp.databinding.FragmentInicioBinding;
import com.example.chefapp.modelo.Receta;

import java.util.ArrayList;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private View root;
    private final ArrayList<Receta> recetas = new ArrayList<>();
    private ImageButton btnLupaBuscarReceta;
    private TextView nombreApp;
    private EditText barraBuscarReceta;
    private Cursor cursor;
    public static boolean buscar = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        findElementosVisuales();
        obtenerRecetas(null);
        iniciarRecyclerView();
        buscarRecetas();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Busca los elementos visuales en el layout.
     */
    private void findElementosVisuales() {
        btnLupaBuscarReceta = root.findViewById(R.id.lupaBuscarReceta);
        nombreApp = root.findViewById(R.id.nombreApp);
        barraBuscarReceta = root.findViewById(R.id.barraBuscarReceta);
    }

    /**
     * Obtiene las recetas de la base de datos.
     *
     * @param s Texto a buscar.
     */
    public void obtenerRecetas(CharSequence s) {
        recetas.clear();

        if (buscar) {
            if (s != null && !s.toString().isEmpty()) {
                cursor = MainActivity.dbRecetas.rawQuery("SELECT * FROM recetas WHERE nombreReceta LIKE '%" + s + "%'", null);
            }
        } else {
            cursor = MainActivity.dbRecetas.rawQuery("SELECT * FROM recetas", null);
        }

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            int foto = cursor.getInt(1);
            String nombreReceta = cursor.getString(2);
            String dificultad = cursor.getString(3);
            int comensales = cursor.getInt(4);
            int tiempo = cursor.getInt(5);
            String descripcion = cursor.getString(6);
            String caracteristicasAdicionales = cursor.getString(7);
            String ingredientes = cursor.getString(8);
            String pasosRealizacion = cursor.getString(9);

            recetas.add(new Receta(id, foto, nombreReceta, dificultad, comensales, tiempo, descripcion, caracteristicasAdicionales, ingredientes, pasosRealizacion));
        }

        cursor.close();
    }

    /**
     * Inicia el RecyclerView con las recetas obtenidas.
     */
    private void iniciarRecyclerView() {
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewRecetas);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter<AdaptadorRecetas.ViewHolder> myAdapter = new AdaptadorRecetas(recetas);
        recyclerView.setAdapter(myAdapter);
    }

    /**
     * Busca recetas en la base de datos mediante la barra de búsqueda.
     */
    private void buscarRecetas() {
        btnLupaBuscarReceta.setOnClickListener(v -> clickLupaBuscarReceta());

        barraBuscarReceta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                obtenerRecetas(s);
                iniciarRecyclerView();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita
            }
        });
    }

    /**
     * Cambia el estado de la barra de búsqueda.
     */
    private void clickLupaBuscarReceta() {
        if (buscar) {
            buscarFalse();
        } else {
            buscarTrue();
        }
    }

    /**
     * Activa la barra de búsqueda.
     */
    private void buscarTrue() {
        buscar = true;
        recetas.clear();
        iniciarRecyclerView();
        nombreApp.setVisibility(View.GONE);
        barraBuscarReceta.setVisibility(View.VISIBLE);
        btnLupaBuscarReceta.setBackgroundResource(R.drawable.icono_cerrar_buscar);
    }

    /**
     * Desactiva la barra de búsqueda.
     */
    public void buscarFalse() {
        buscar = false;
        recetas.clear();
        barraBuscarReceta.setText("");
        obtenerRecetas(null);
        iniciarRecyclerView();
        nombreApp.setVisibility(View.VISIBLE);
        barraBuscarReceta.setVisibility(View.GONE);
        btnLupaBuscarReceta.setBackgroundResource(R.drawable.icono_buscar_receta);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (buscar) {
            buscarFalse();
        }
    }
}