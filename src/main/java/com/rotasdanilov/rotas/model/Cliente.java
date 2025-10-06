package com.rotasdanilov.rotas.model;

import java.util.Objects;
import java.util.UUID;

public class Cliente {

    private final String id;
    private final String nome;
    private final String endereco;
    private final double latitude;
    private final double longitude;

    public Cliente(String nome, String endereco, double latitude, double longitude) {
        this(UUID.randomUUID().toString(), nome, endereco, latitude, longitude);
    }

    public Cliente(String id, String nome, String endereco, double latitude, double longitude) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Cliente withId(String novoId) {
        return new Cliente(novoId, nome, endereco, latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
