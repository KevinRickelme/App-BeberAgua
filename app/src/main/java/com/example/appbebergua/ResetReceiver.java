package com.example.appbebergua;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import DAO.PessoaDAO;


public class ResetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PessoaDAO pessoaDAO = new PessoaDAO(context);
        if(pessoaDAO.hasData()) {
            pessoaDAO.resetQtdIngerida();
        }
    }

}
