package com.example.chefapp.ui.cronometro;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.text.InputType.TYPE_CLASS_NUMBER;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.chefapp.R;
import com.example.chefapp.databinding.FragmentCronometroBinding;

import java.util.Locale;

public class CronometroFragment extends Fragment {

    private FragmentCronometroBinding binding;
    private View root;
    private TextView cronometro;
    private ImageButton botonIniciar;
    private ImageButton botonPausar;
    private ImageButton botonReiniciar;
    private Button botonCambiarTiempo;
    private Handler handler;
    private boolean cronometroActivo;
    private long tiempoInicio, tiempoRestanteEnMilisegundos, tiempoFinal;
    private int tiempoInicial;
    private static final String CHANNEL_ID = "cronometro_channel";
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCronometroBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        findElementosVisuales();
        mostrarDialogoMinutosParaCronometro();

        handler = new Handler();
        cronometroActivo = false;

        clickBotonesCronometro();
        clickBotonCambiarTiempo();
        comprobarPermisoNotificaciones();
        crearCanalNotificaciones();

        return root;
    }

    /**
     * Método que busca los elementos visuales en el layout.
     */
    private void findElementosVisuales() {
        cronometro = root.findViewById(R.id.cronometro);
        botonIniciar = root.findViewById(R.id.botonIniciarCronometro);
        botonPausar = root.findViewById(R.id.botonPausarCronometro);
        botonReiniciar = root.findViewById(R.id.botonDetenerCronometro);
        botonCambiarTiempo = root.findViewById(R.id.botonCambiarTiempo);
    }

    /**
     * Método que muestra un diálogo para introducir el tiempo en minutos para el cronómetro.
     */
    private void mostrarDialogoMinutosParaCronometro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom);
        builder.setTitle("Tiempo del cronómetro");
        builder.setMessage("Introduce el tiempo en minutos para el cronómetro:");

        EditText inputMinutos = new EditText(getContext());
        inputMinutos.setInputType(TYPE_CLASS_NUMBER);
        inputMinutos.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        builder.setView(inputMinutos);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String minutos = inputMinutos.getText().toString();

            if (!minutos.isEmpty()) {
                tiempoInicial = Integer.parseInt(minutos);
                tiempoRestanteEnMilisegundos = (long) tiempoInicial * 60 * 1000;
                actualizarCronometro(tiempoRestanteEnMilisegundos);
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Método que actualiza el cronómetro con el tiempo restante en horas, minutos y segundos.
     *
     * @param tiempoRestanteEnMilisegundos Tiempo restante en milisegundos.
     */
    private void actualizarCronometro(long tiempoRestanteEnMilisegundos) {
        int segundos = (int) (tiempoRestanteEnMilisegundos / 1000);
        int minutos = segundos / 60;
        int horas = minutos / 60;
        segundos = segundos % 60;
        minutos = minutos % 60;

        cronometro.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segundos));
    }

    /**
     * Runnable que se ejecuta cada segundo para actualizar el cronómetro.
     */
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tiempoRestanteEnMilisegundos = tiempoFinal - System.currentTimeMillis();

            if (tiempoRestanteEnMilisegundos > 0) {
                actualizarCronometro(tiempoRestanteEnMilisegundos);
                handler.postDelayed(this, 1000);
            } else {
                tiempoRestanteEnMilisegundos = (long) tiempoInicial * 60 * 1000;
                actualizarCronometro(tiempoRestanteEnMilisegundos);
                cronometroActivo = false;
                mostrarDialogoRecetaLista();
                mostrarNotificacionRecetaLista();
            }
        }
    };

    /**
     * Método que muestra un diálogo cuando la receta está lista.
     */
    private void mostrarDialogoRecetaLista() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom);
        builder.setTitle("Receta lista");
        builder.setMessage("Tu receta está lista, ¡comprueba el horno!");
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            botonIniciar.setVisibility(View.VISIBLE);
            botonPausar.setVisibility(View.GONE);
            botonReiniciar.setVisibility(View.VISIBLE);
            botonCambiarTiempo.setVisibility(View.VISIBLE);
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Método que gestiona los eventos de los botones del cronómetro.
     */
    @SuppressLint("SetTextI18n")
    private void clickBotonesCronometro() {
        botonIniciar.setOnClickListener(v -> {
            if (!cronometroActivo) {
                tiempoInicio = System.currentTimeMillis();
                tiempoFinal = tiempoInicio + tiempoRestanteEnMilisegundos;
                handler.postDelayed(runnable, 0);
                cronometroActivo = true;
                botonIniciar.setVisibility(View.GONE);
                botonPausar.setVisibility(View.VISIBLE);
                botonReiniciar.setVisibility(View.GONE);
                botonCambiarTiempo.setVisibility(View.GONE);
            }
        });

        botonPausar.setOnClickListener(v -> {
            if (cronometroActivo) {
                handler.removeCallbacks(runnable);
                tiempoRestanteEnMilisegundos = tiempoFinal - System.currentTimeMillis();
                cronometroActivo = false;
                botonIniciar.setVisibility(View.VISIBLE);
                botonPausar.setVisibility(View.GONE);
                botonReiniciar.setVisibility(View.VISIBLE);
                botonCambiarTiempo.setVisibility(View.VISIBLE);
            }
        });

        botonReiniciar.setOnClickListener(v -> {
            handler.removeCallbacks(runnable);
            cronometroActivo = false;
            tiempoRestanteEnMilisegundos = (long) tiempoInicial * 60 * 1000;
            actualizarCronometro(tiempoRestanteEnMilisegundos);
            botonIniciar.setVisibility(View.VISIBLE);
            botonPausar.setVisibility(View.GONE);
        });
    }

    /**
     * Método que gestiona el evento del botón para cambiar el tiempo del cronómetro.
     */
    private void clickBotonCambiarTiempo() {
        botonCambiarTiempo.setOnClickListener(v -> mostrarDialogoMinutosParaCronometro());
    }

    /**
     * Método que comprueba si la aplicación tiene permiso para mostrar notificaciones.
     */
    private void comprobarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrarNotificacionRecetaLista();
            }
        }
    }

    /**
     * Método que crea un canal de notificaciones para el cronómetro.
     */
    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Cronometro";
            String description = "Canal para notificaciones del cronometro";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Método que muestra una notificación cuando la receta está lista.
     */
    private void mostrarNotificacionRecetaLista() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_chef_app)
                .setContentTitle("Receta lista")
                .setContentText("Tu receta está lista, ¡comprueba el horno!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(requireContext(), CronometroFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(requireContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        requireActivity();
        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}