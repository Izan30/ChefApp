package com.example.chefapp.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Receta {
    private long id;
    private int foto;
    private String nombreReceta;
    private String dificultad;
    private int comensales;
    private int tiempo;
    private String descripcion;
    private String caracteristicasAdicionales;
    private String ingredientes;
    private String pasosRealizacion;
}
