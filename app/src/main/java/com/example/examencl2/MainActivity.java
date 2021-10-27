package com.example.examencl2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.service.autofill.Validators;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

   //FECHA
    EditText etDate;
    DatePickerDialog.OnDateSetListener setListener;

   //CRUD
   EditText etId,etNombre,etApellido,etFecha,etDNI;
   Button btnIns, btnSel, btnUpd, btnDel;
   Spinner spSexo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FECHA
        etDate = findViewById(R.id.fecha);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        MainActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth
                               ,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;
                String date = year+"/"+month+"/"+day;
                etDate.setText(date);
            }
        };

        //CRUD

        etId = findViewById(R.id.idd);
        etNombre = findViewById(R.id.nombre);
        etApellido = findViewById(R.id.apellido);
        etFecha = findViewById(R.id.fecha);
        spSexo = findViewById(R.id.sexo);
        etDNI = findViewById(R.id.dni);

        btnIns = findViewById(R.id.btnR);
        btnSel = findViewById(R.id.btnB);
        btnUpd = findViewById(R.id.btnM);
        btnDel = findViewById(R.id.btnE);

        btnIns.setOnClickListener(this);
        btnSel.setOnClickListener(this);
        btnUpd.setOnClickListener(this);
        btnDel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnR:
                Registrar(v);
                break;
            case R.id.btnB:
                Buscar(v);
                break;
            case R.id.btnE:
                Eliminar(v);
                break;
            case R.id.btnM:
                Modificar(v);
                break;
        }
    }

    //Método para insertar docente:
    public void Registrar(View view){
        AdminSQL admin= new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String codigo=etId.getText().toString();
        String nombre=etNombre.getText().toString();
        String apellido=etApellido.getText().toString();
        String sexo= String.valueOf(spSexo.getSelectedItem());
        String fecha=etFecha.getText().toString();
        String dni=etDNI.getText().toString();
        if (etNombre.getText().toString().length() < 4) {
            etNombre.setError("4 caracteres minimo!");
        }
       else if(etApellido.getText().toString().length() < 4){
            etApellido.setError("4 caracteres minimo!");
        }
       else if(etDNI.getText().toString().length() < 8 ){
            etDNI.setError("DNI de 8 digitos");
        }
        else if (!codigo.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty() && !sexo.isEmpty() && !fecha.isEmpty() && !dni.isEmpty()){
            ContentValues registro=new ContentValues();
            registro.put("codigo",codigo);
            registro.put("nombre",nombre);
            registro.put("apellido",apellido);
            registro.put("sexo",sexo);
            registro.put("fecha",fecha);
            registro.put("dni",dni);
            BaseDeDatos.insert("docente",null,registro);
            BaseDeDatos.close();
            etId.setText("");
            etNombre.setText("");
            etApellido.setText("");
            spSexo.setSelection(0);
            etFecha.setText("");
            etDNI.setText("");
            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(this,"Debes llenar todos los campos",Toast.LENGTH_SHORT).show();}
    }

    //MÉTODO PARA CONSULTAR UN DOCENTE
    public void Buscar(View view){
       AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos= admin.getWritableDatabase();
        String codigo=etId.getText().toString();
        if (!codigo.isEmpty()){
            Cursor fila = BaseDeDatos.rawQuery("select nombre,apellido,sexo,fecha,dni from docente where codigo="+codigo,null);
            if(fila.moveToFirst()){
                etNombre.setText(fila.getString(0));
                etApellido.setText(fila.getString(1));
                SeleccionaItem(spSexo,fila.getString(2));
                etFecha.setText(fila.getString(3));
                etDNI.setText(fila.getString(4));
                BaseDeDatos.close();
            }else {
                Toast.makeText(this,"No existe el docente",Toast.LENGTH_SHORT).show();
                BaseDeDatos.close();
            }
        }else{
            Toast.makeText(this,"Debes introducir el código del docente",Toast.LENGTH_SHORT).show();
        }
    }
    //Llamado al spinner a la hora de buscar
    private void SeleccionaItem(Spinner spSexo, String value) {
        for (int i = 0; i < spSexo.getCount(); i++) {
            if (spSexo.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spSexo.setSelection(i);
            }
        }
    }

    //METODO PARA ELIMINAR UN DOCENTE

    public void Eliminar(View view){

        AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos=admin.getWritableDatabase();
        String codigo= etId.getText().toString();

        if (!codigo.isEmpty()){
            int cantidad=BaseDeDatos.delete("docente","codigo="+codigo,null);
            BaseDeDatos.close();
            etId.setText("");
            etNombre.setText("");
            etApellido.setText("");
            spSexo.setSelection(0);
            etFecha.setText("");
            etDNI.setText("");

            if(cantidad==1){
                Toast.makeText(this,"Docente eliminado",Toast.LENGTH_SHORT).show();
            }else {Toast.makeText(this,"El docente no existe",Toast.LENGTH_SHORT).show();}
        }else {Toast.makeText(this,"Debes introducir el código del docente",Toast.LENGTH_SHORT).show();}
    }

    //METODO PARA MODIFICAR UN DOCENTE
    public void Modificar(View view){
        AdminSQL admin=new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos=admin.getWritableDatabase();
        String codigo=etId.getText().toString();
        String nombre=etNombre.getText().toString();
        String apellido=etApellido.getText().toString();
        String sexo= String.valueOf(spSexo.getSelectedItem());
        String fecha=etFecha.getText().toString();
        String dni=etDNI.getText().toString();
        if (etNombre.getText().toString().length() < 4) {
            etNombre.setError("4 caracteres minimo!");
        }
        else if(etApellido.getText().toString().length() < 4){
            etApellido.setError("4 caracteres minimo!");
        }
        else if(etDNI.getText().toString().length() < 8 ){
            etDNI.setError("DNI de 8 digitos");
        }
        else if (!codigo.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty() && !sexo.isEmpty() && !fecha.isEmpty() && !dni.isEmpty()){
            ContentValues registro=new ContentValues();
            registro.put("codigo",codigo);
            registro.put("nombre",nombre);
            registro.put("apellido",apellido);
            registro.put("sexo",sexo);
            registro.put("fecha",fecha);
            registro.put("dni",dni);
            int cantidad=BaseDeDatos.update("docente",registro,"codigo="+codigo,null);
            BaseDeDatos.close();
            etId.setText("");
            etNombre.setText("");
            etApellido.setText("");
            spSexo.setSelection(0);
            etFecha.setText("");
            etDNI.setText("");
            if (cantidad==1)
                Toast.makeText(this,"Docente modificado correctamente",Toast.LENGTH_SHORT).show();
            else Toast.makeText(this,"El docente no existe",Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this,"Debes llenar todos los campos",Toast.LENGTH_SHORT).show();
    }
}