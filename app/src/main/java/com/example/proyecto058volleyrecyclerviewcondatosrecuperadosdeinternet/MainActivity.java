package com.example.proyecto058volleyrecyclerviewcondatosrecuperadosdeinternet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Usuario> listaUsuario;
    private RequestQueue rq;

    private RecyclerView recyclerView;

    private AdaptadorUsuario adaptadorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaUsuario = new ArrayList<>();
        rq = Volley.newRequestQueue(this);
        for (int i=0;i<100;i++)
        cargarPersonas();
        recyclerView = findViewById(R.id.rv1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adaptadorUsuario = new AdaptadorUsuario();
        recyclerView.setAdapter(adaptadorUsuario);
    }

    private void cargarPersonas() {
        String url = "https://randomuser.me/api/";
        JsonObjectRequest requerimiento = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String valor = response.get("results").toString();
                        JSONArray arreglo = new JSONArray(valor);
                        JSONObject objeto = new JSONObject(arreglo.get(0).toString());
                        String email = objeto.getString("email");
                        String nombre = objeto.getJSONObject("name").getString("last");
                        String foto = objeto.getJSONObject("picture").getString("large");
                        Usuario usuario = new Usuario(nombre, email,foto);
                        listaUsuario.add(usuario);
                        adaptadorUsuario.notifyItemRangeInserted(listaUsuario.size(),1);
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(requerimiento);
    }

    private class AdaptadorUsuario extends RecyclerView.Adapter<AdaptadorUsuario.AdaptadorUsuarioHolder>{

        @NonNull
        @Override
        public AdaptadorUsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdaptadorUsuarioHolder(getLayoutInflater().inflate(R.layout.layout_tarjeta,parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorUsuarioHolder holder, int position) {
            holder.imprimir(position);
        }

        @Override
        public int getItemCount() {
            return listaUsuario.size();
        }

        class AdaptadorUsuarioHolder extends RecyclerView.ViewHolder{

            TextView tvNombre;
            TextView tvEmail;
            ImageView ivFoto;
            public AdaptadorUsuarioHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tv_nombre);
                tvEmail = itemView.findViewById(R.id.tv_mail);
                ivFoto = itemView.findViewById(R.id.iv_foto);
            }

            public void imprimir(int position) {
                tvNombre.setText("Nombre: " + listaUsuario.get(position).getNombre());
                tvEmail.setText("Mail: " + listaUsuario.get(position).getMail());
                recuperarImagen(listaUsuario.get(position).getFoto(), ivFoto);
            }

            private void recuperarImagen(String foto, ImageView ivFoto) {
                ImageRequest peticion = new ImageRequest(foto, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ivFoto.setImageBitmap(response);
                    }
                }, 0, 0, null, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                rq.add(peticion);
            }
        }
    }
}