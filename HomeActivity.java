package com.example.splash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splash.Models.Users;
import com.example.splash.OpenHelper.SQLite_OpenHelper;

public class HomeActivity extends AppCompatActivity {
 Button consultar;
 Button agenda;
 Button cupones;
 EditText nombre, edad;
 EditText ids;
 TextView resultado;
 SQLite_OpenHelper sql;
 SQLiteDatabase db;
 private final static String CHANNEL_ID = "NOTIFICACION";
 private final static int NOTIFICATION_ID=0;
 private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sql=new SQLite_OpenHelper(getApplicationContext());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        cupones=findViewById(R.id.cupones);
        nombre=findViewById(R.id.txt_nombre);
        edad=findViewById(R.id.txt_edad);
        resultado=findViewById(R.id.txt_resultado);
        consultar=findViewById(R.id.consultar);
        ids=findViewById(R.id.recupera);
        agenda=findViewById(R.id.agenda);
        consultar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) { consultarUsuario();
        }
    });

        cupones.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                crearUsuario(UiToUsers());
            }
        });


    }

    public Users UiToUsers()
    {
        return new Users(Integer.parseInt(edad.getText().toString()),nombre.getText().toString());
    }
    public void crearUsuario(Users u)
    {
        db = sql.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", u.getNombre());
        values.put("edad",u.getEdad());
        long id =db.insert("users",null, values);
        resultado.setText(id+"");
    }
    public void consultarUsuario()
    {
        Users usuarios = new Users();
        String campos[] = new String[] {"id","nombre", "edad"};
        String columnas[] = new String[] {ids.getText().toString()};
        db = sql.getReadableDatabase();
        Cursor c = db.query("users",campos,"id=?",columnas,null,null,null);
        if (c.moveToFirst()) {
            do {
                usuarios.setNombre(c.getString(1));
                usuarios.setEdad(c.getInt(2));

            } while (c.moveToNext());
            nombre.setText(usuarios.getNombre());
            edad.setText(usuarios.getEdad() + "");
            //resultado.setText(usuarios);
        }else{
            Toast.makeText(this,"No se han encontrado resultados", Toast.LENGTH_SHORT).show();
        }
    }
    public void showToast(){
        Toast.makeText(HomeActivity.this,"no tienes cupones", Toast.LENGTH_LONG).show();
    }
    public void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("sin cupones")
                .setMessage("no hay cupones")
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(HomeActivity.this, cupones.class);
                        startActivity(i);

                    }

                }).setNegativeButton("no",null).show();
    }
    public void lauchNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name ="notificacion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("notificacion android");
        builder.setContentText("quieres mas cupones");
        builder.setColor(Color.RED);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.YELLOW, 1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }
    public void Agenda ( View v){
        Intent Agenda = new Intent(this,Activiti_agenda.class);
        startActivity(Agenda);
    }
}

