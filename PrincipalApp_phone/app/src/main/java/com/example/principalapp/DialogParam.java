package com.example.principalapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DialogParam extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private EditText editTextporteur;
    private EditText editPorteur;
    private  Spinner spinner;
    private  DialogListener listener;

    private  String server1="tcp://farmer.cloudmqtt.com:12425";
    private  String usr1= "trapccpv";
    private  String pwd1 = "9710kh6VgsVe";

    private  String server2="tcp://farmer.cloudmqtt.com:11995";
    private  String usr2= "gxbiudao";
    private  String pwd2 = "lrRx6avOfYFa";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_param, null);

        spinner = view.findViewById(R.id.spinner);
        editTextporteur = view.findViewById(R.id.edit_porteur);
        editPorteur = view.findViewById(R.id.porteurName);

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.JacketList));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        builder.setView(view)
                .setTitle("Settings")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!spinner.getSelectedItem().toString().equalsIgnoreCase("Select a jacket...")){
                            Toast.makeText(getActivity(),
                                    spinner.getSelectedItem().toString(),
                                    Toast.LENGTH_SHORT).show();
                            String porteur_name = editTextporteur.getText().toString();
                            if (spinner.getSelectedItem().toString()== "Jacket n°1"){
                                System.out.println("je suis là!!!!!!!!!!!!!!!!!!!!!!");
                                listener.applyTexts(porteur_name, server1, usr1, pwd1);
                            }
                            else if (spinner.getSelectedItem().toString()== "Jacket n°2"){
                                listener.applyTexts(porteur_name, server2, usr2, pwd2);
                            }
                            else{
                                listener.applyTexts(porteur_name, server1, usr1, pwd1);
                            }
                        }

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e){
            throw  new ClassCastException(context.toString()+ "must implement a DialogListener");
        }
    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT);

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }





    public  interface DialogListener{
        void applyTexts(String porteur_name, String server, String usr, String pwd);

    }


}
