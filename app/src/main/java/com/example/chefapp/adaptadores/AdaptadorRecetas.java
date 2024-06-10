package com.example.chefapp.adaptadores;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chefapp.MainActivity;
import com.example.chefapp.R;
import com.example.chefapp.modelo.Receta;
import com.example.chefapp.ui.inicio.detallesReceta.DetallesReceta;

import java.util.ArrayList;

public class AdaptadorRecetas extends RecyclerView.Adapter<AdaptadorRecetas.ViewHolder> {
    private final ArrayList<Receta> recetas;

    /**
     * Constructor de la clase.
     *
     * @param recetas Lista de recetas a mostrar.
     */
    public AdaptadorRecetas(ArrayList<Receta> recetas) {
        this.recetas = recetas;
    }

    @NonNull
    @Override
    public AdaptadorRecetas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recetas_card_view, parent, false);
        return new AdaptadorRecetas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorRecetas.ViewHolder holder, int position) {
        Receta receta = recetas.get(position);
        holder.foto.setImageResource(receta.getFoto());
        holder.nombreReceta.setText(receta.getNombreReceta());
        holder.dificultad.setText(receta.getDificultad());
        holder.comensales.setText(String.valueOf(receta.getComensales()));
        holder.tiempo.setText(String.valueOf(receta.getTiempo()));
        holder.descripcion.setText(receta.getDescripcion());

        holder.itemView.setOnClickListener(v -> irAPantallaDetallesReceta(receta, v));
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    /**
     * Clase que se encarga de gestionar los elementos visuales de la vista.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView foto;
        private final TextView nombreReceta;
        private final TextView dificultad;
        private final TextView comensales;
        private final TextView tiempo;
        private final TextView descripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imagenReceta);
            nombreReceta = itemView.findViewById(R.id.nombreReceta);
            dificultad = itemView.findViewById(R.id.dificultadReceta);
            comensales = itemView.findViewById(R.id.numComensales);
            tiempo = itemView.findViewById(R.id.tiempoReceta);
            descripcion = itemView.findViewById(R.id.descripcionReceta);
        }
    }

    /**
     * Método que se encarga de llevar al usuario a la pantalla de detalles de la receta.
     *
     * @param receta Receta a mostrar.
     * @param v      Vista actual.
     */
    private void irAPantallaDetallesReceta(Receta receta, View v) {
        MainActivity.fotoReceta = receta.getFoto();
        MainActivity.nombreReceta = receta.getNombreReceta();
        MainActivity.dificultadReceta = receta.getDificultad();
        MainActivity.comensalesReceta = receta.getComensales();
        MainActivity.tiempoReceta = receta.getTiempo();
        MainActivity.descripcionReceta = receta.getDescripcion();
        MainActivity.caracteristicasAdicionalesReceta = receta.getCaracteristicasAdicionales();
        MainActivity.ingredientesReceta = receta.getIngredientes();
        MainActivity.pasosRealizacionReceta = receta.getPasosRealizacion();

        Intent intent = new Intent(v.getContext(), DetallesReceta.class);
        v.getContext().startActivity(intent);
    }
}
