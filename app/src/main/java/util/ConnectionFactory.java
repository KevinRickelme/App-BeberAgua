package util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectionFactory extends SQLiteOpenHelper {
    public static final String NAME =  "drinkWater.db";
    public static final int VERSION = 1;
    public static final String TABELA = "Pessoa";
    public static final String ID = "_id";
    public static final String NOME = "Nome";
    public static final String PESO = "Peso";
    public static final String PRATICAEXERCICIO = "PraticaExercicio";
    public static final String METADIARIA = "MetaDiaria";



    public ConnectionFactory(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Pessoa(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nome VARCHAR(250)," +
                "Peso DOUBLE," +
                "PraticaExercicio Boolean," +
                "MetaDiaria Double," +
                "QtdIngerida Double)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS pessoa";
        db.execSQL(sql);
        onCreate(db);

    }
}
