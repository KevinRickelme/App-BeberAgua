package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import kotlin.NotImplementedError;
import model.Pessoa;
import util.ConnectionFactory;

public class PessoaDAO {
    private final ConnectionFactory banco;
    private SQLiteDatabase db;

    public PessoaDAO(Context context){
        banco = new ConnectionFactory(context);
    }

    public long insert(Pessoa pessoa){
        db = banco.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("Nome", pessoa.Nome);
        values.put("Peso", pessoa.Peso);
        values.put("PraticaExercicio", pessoa.PraticaExercicio);
        values.put("MetaDiaria", pessoa.MetaDiaria);
        values.put("QtdIngerida", pessoa.QtdIngerida);

        long result = db.insert("Pessoa", null, values);
        db.close();
        return result;
    }

    public long update(Pessoa pessoa){

        db = banco.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Nome", pessoa.Nome);
        values.put("Peso", pessoa.Peso);
        values.put("PraticaExercicio", pessoa.PraticaExercicio);
        values.put("MetaDiaria", pessoa.MetaDiaria);
        values.put("QtdIngerida", pessoa.QtdIngerida);
        String where = "Id=1";
        long result = db.update("Pessoa", values, where, null);
        db.close();
        return result;
    }

    public Pessoa getPessoaFromDb(){
        String[] campos = {"Nome","Peso","PraticaExercicio","MetaDiaria","QtdIngerida"};
        db = banco.getReadableDatabase();
        Cursor cursor;
        Pessoa pessoa = new Pessoa();

        try {
            cursor = db.query(ConnectionFactory.TABELA, campos, null, null,null,null,null,"1");
            if(cursor != null && cursor.moveToLast()) {
                pessoa.Nome = cursor.getString(cursor.getColumnIndexOrThrow("Nome"));
                pessoa.Peso = cursor.getDouble(cursor.getColumnIndexOrThrow("Peso"));
                pessoa.PraticaExercicio = Boolean.valueOf((cursor.getString(cursor.getColumnIndexOrThrow("PraticaExercicio"))));
                pessoa.MetaDiaria = cursor.getDouble(cursor.getColumnIndexOrThrow("MetaDiaria"));
                pessoa.QtdIngerida = cursor.getDouble(cursor.getColumnIndexOrThrow("QtdIngerida"));
            }
            db.close();
            return pessoa;

        }
        catch(Exception ex){
            db.close();
            return pessoa;
        }

    }

    //verifica se já existo um cadastro
    public boolean hasData(){
        int contagem = 0;
        db = banco.getReadableDatabase();
        String[] CountId = {"Count(Id) as contagem"};
        Cursor cursor;
        cursor = db.query("Pessoa", CountId, null,null, null, null, null, "1");
        if(cursor != null && cursor.moveToLast()) {
            contagem = cursor.getInt(cursor.getColumnIndexOrThrow("contagem"));
        }
        if(contagem > 0) {
            db.close();
            return true;
        }
        else {
            db.close();
            return false;
        }
    }

    //atualizar a quantidade ingerida de água
    public void atualizaQtdIngerida(double valorIngerido){
        Pessoa p = new Pessoa();
        p = this.getPessoaFromDb();
        db = banco.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("QtdIngerida",(p.QtdIngerida + valorIngerido));
        try {
            db.update("Pessoa", values, null, null);
        }
        catch(Exception ex){
            int id=0;
        }
    }

    public void resetQtdIngerida(){
        Pessoa p = new Pessoa();
        p = this.getPessoaFromDb();
        db = banco.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("QtdIngerida",(0));
        try {
            db.update("Pessoa", values, null, null);
        }
        catch(Exception ex){
            int id=0;
        }
    }



}
