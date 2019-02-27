package server.multusession;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;
    public DataBase()throws ClassNotFoundException,SQLException{
//    Conn();
//    CreateDB();
//    WriteDB();
//    ReadDB();
//    CloseDB();
    }
    private static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:ChatDataBase.s3db");

        System.out.println("База Подключена!");
    }
    private static void CreateDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'password' text);");

        System.out.println("Таблица создана или уже существует.");
    }
    private static void WriteDB(String name,String pass) throws SQLException
    {
        String a ="INSERT INTO 'users' ('name', 'password') VALUES ('"+name+"', '"+pass+"'); ";
        statmt.execute(a);


        System.out.println("Таблица заполнена");
    }
    private static void ReadDB() throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users");

        while(resSet.next())
        {
            int id = resSet.getInt("id");
            String  name = resSet.getString("name");
            String  phone = resSet.getString("password");
            System.out.println( "ID = " + id );
            System.out.println( "name = " + name );
            System.out.println( "password = " + phone );
            System.out.println();
        }

        System.out.println("Таблица выведена");

    }

    public static String NameOK(String name,String pass)throws SQLException,ClassNotFoundException{
        String a="";
        Conn();
        CreateDB();
        //String selectTableSQL = "SELECT id, name, password from ChatDataBase";
        boolean findName = false;
        boolean eqlPass = false;
        try {
            // выбираем данные с БД
            resSet = statmt.executeQuery("SELECT * FROM users");

            // И если что то было получено то цикл while сработает
            while (resSet.next()&&!findName) {
                //String userid = resSet.getString("id");
                String username = resSet.getString("name");
                String password = resSet.getString("password");


                System.out.println("userid : " + username);
                System.out.println("userpass : " + password);
                if(username.compareToIgnoreCase(name)==0){
                    findName=true;
                    System.out.println(pass+" "+password+" "+name+" "+username);
                    if(pass.compareTo(password)==0){
                        eqlPass=true;
                        System.out.println("tt");
                    }
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if(eqlPass&&findName){
            a="Good";
        }
        if(!eqlPass&&!findName){
            a="Good";

            WriteDB(name, pass);
        }
        if(findName&&!eqlPass){
            a="WrongAnswer";
            System.out.println("OK"+name+pass);
        }

        CloseDB();

        return a;

    }
    private static void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();
        System.out.println("Соединения закрыты");
    }
}
