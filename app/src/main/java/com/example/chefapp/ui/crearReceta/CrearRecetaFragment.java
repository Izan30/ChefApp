package com.example.chefapp.ui.crearReceta;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.chefapp.MainActivity;
import com.example.chefapp.R;
import com.example.chefapp.databinding.FragmentCrearRecetaBinding;

public class CrearRecetaFragment extends Fragment {

    private FragmentCrearRecetaBinding binding;
    private View root;
    private Button btnSeleccionarFoto;
    private ImageView fotoReceta;
    private EditText nombre;
    private EditText descripcion;
    private EditText dificultad;
    private EditText numComensales;
    private EditText numMinutos;
    private EditText caracteristicasAdicionales;
    private EditText ingredientes;
    private EditText pasos;
    private Button btnCrearReceta;
    private ActivityResultLauncher<Intent> imagenPickerLauncher;
    private int imagenSeleccionada;
    private String textoNombreReceta;
    private String textoDescripcionReceta;
    private String textoDificultadReceta;
    private int numeroComensalesReceta;
    private int numeroMinutosReceta;
    private String textoCaracteristicasAdicionalesReceta;
    private String textoIngredientesReceta;
    private String textoPasosReceta;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCrearRecetaBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        findElementosVisuales();
        montarImagenPicker();
        seleccionarFoto();
        crearReceta();

        return root;
    }

    /**
     * Busca los elementos visuales en el layout.
     */
    private void findElementosVisuales() {
        btnSeleccionarFoto = root.findViewById(R.id.botonSeleccionarImagen);
        fotoReceta = root.findViewById(R.id.imagenRecetaSeleccionada);
        nombre = root.findViewById(R.id.nombreReceta);
        descripcion = root.findViewById(R.id.descripcionReceta);
        dificultad = root.findViewById(R.id.dificultadReceta);
        numComensales = root.findViewById(R.id.comensalesReceta);
        numMinutos = root.findViewById(R.id.tiempoReceta);
        caracteristicasAdicionales = root.findViewById(R.id.caracteristicasAdicionalesReceta);
        ingredientes = root.findViewById(R.id.ingredientesReceta);
        pasos = root.findViewById(R.id.pasosReceta);
        btnCrearReceta = root.findViewById(R.id.botonCrearReceta);
    }

    /**
     * Monta el selector de imágenes.
     */
    private void montarImagenPicker() {
        imagenPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();

                if (data != null) {
                    Uri imageUri = data.getData();
                    fotoReceta.setImageURI(imageUri);
                    fotoReceta.setVisibility(View.VISIBLE);

                    imagenSeleccionada = getResources().getIdentifier(obtenerRutaImagen(imageUri), null, requireActivity().getPackageName());

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnSeleccionarFoto.getLayoutParams();
                    params.setMargins(50, -10, 50, 20);
                    btnSeleccionarFoto.setLayoutParams(params);
                }
            }
        });
    }

    /**
     * Obtiene la ruta de la imagen seleccionada.
     *
     * @param uri Uri de la imagen seleccionada.
     * @return Ruta de la imagen seleccionada.
     */
    private String obtenerRutaImagen(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String ruta = cursor.getString(columnIndex);
            cursor.close();
            return ruta;
        }

        return null;
    }

    /**
     * Selecciona la foto de la receta.
     */
    private void seleccionarFoto() {
        btnSeleccionarFoto.setOnClickListener(v -> abrirImagenPicker());
    }

    /**
     * Abre el selector de imágenes.
     */
    private void abrirImagenPicker() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagenPickerLauncher.launch(pickPhoto);
    }

    /**
     * Crea la receta.
     */
    private void crearReceta() {
        btnCrearReceta.setOnClickListener(v -> {
            if (validarCampos()) {
                obtenerDatosReceta();
                guardarRecetaBD();
                mostrarDialogoRecetaCreada();
            }
        });
    }

    /**
     * Valida los campos de la receta.
     *
     * @return true si los campos son válidos, false en caso contrario.
     */
    private boolean validarCampos() {
        boolean camposValidos = true;

        if (fotoReceta.getDrawable() == null) {
            mostrarDialogoImagenNoSeleccionada();
            camposValidos = false;
        }

        if (nombre.getText().toString().isEmpty()) {
            nombre.setError("El nombre de la receta es obligatorio");
            camposValidos = false;
        }

        if (descripcion.getText().toString().isEmpty()) {
            descripcion.setError("La descripción de la receta es obligatoria");
            camposValidos = false;
        }

        if (dificultad.getText().toString().isEmpty()) {
            dificultad.setError("La dificultad de la receta es obligatoria");
            camposValidos = false;
        }

        if (numComensales.getText().toString().isEmpty()) {
            numComensales.setError("El número de comensales es obligatorio");
            camposValidos = false;
        }

        if (numMinutos.getText().toString().isEmpty()) {
            numMinutos.setError("El tiempo de preparación es obligatorio");
            camposValidos = false;
        }

        if (ingredientes.getText().toString().isEmpty()) {
            ingredientes.setError("Los ingredientes son obligatorios");
            camposValidos = false;
        }

        if (pasos.getText().toString().isEmpty()) {
            pasos.setError("Los pasos son obligatorios");
            camposValidos = false;
        }

        return camposValidos;
    }

    /**
     * Muestra un diálogo informando de que no se ha seleccionado ninguna imagen.
     */
    private void mostrarDialogoImagenNoSeleccionada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom);
        builder.setTitle("Imagen no seleccionada");
        builder.setMessage("Por favor, selecciona una imagen para la receta");
        builder.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Obtiene los datos de la receta.
     */
    private void obtenerDatosReceta() {
        //imagenSeleccionada = fotoReceta.getDrawable();
        textoNombreReceta = nombre.getText().toString();
        textoDescripcionReceta = descripcion.getText().toString();
        textoDificultadReceta = dificultad.getText().toString();
        numeroComensalesReceta = Integer.parseInt(numComensales.getText().toString());
        numeroMinutosReceta = Integer.parseInt(numMinutos.getText().toString());
        textoCaracteristicasAdicionalesReceta = caracteristicasAdicionales.getText().toString();
        textoIngredientesReceta = ingredientes.getText().toString();
        textoPasosReceta = pasos.getText().toString();
    }

    /**
     * Guarda la receta en la base de datos.
     */
    private void guardarRecetaBD() {
        MainActivity.dbRecetas.execSQL("INSERT INTO recetas (foto, nombreReceta, dificultad, comensales, tiempo, descripcion, caracteristicasAdicionales, ingredientes, pasosRealizacion) VALUES " +
                "(" + imagenSeleccionada + ", '" + textoNombreReceta + "', '" + textoDificultadReceta + "', " + numeroComensalesReceta + ", " + numeroMinutosReceta + ", '" + textoDescripcionReceta + "', '" + textoCaracteristicasAdicionalesReceta + "', '" + textoIngredientesReceta + "', '" + textoPasosReceta + "')");
    }

    /**
     * Muestra un diálogo informando de que la receta se ha creado correctamente.
     */
    private void mostrarDialogoRecetaCreada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom);
        builder.setTitle("Receta creada");
        builder.setMessage("¡La receta se ha creado correctamente!");
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            dialog.dismiss();
            limpiarCampos();
        });
        builder.show();
    }

    /**
     * Limpia los campos de la receta.
     */
    private void limpiarCampos() {
        fotoReceta.setImageDrawable(null);
        fotoReceta.setVisibility(View.GONE);
        btnSeleccionarFoto.setVisibility(View.VISIBLE);
        nombre.setText("");
        descripcion.setText("");
        dificultad.setText("");
        numComensales.setText("");
        numMinutos.setText("");
        caracteristicasAdicionales.setText("");
        ingredientes.setText("");
        pasos.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}