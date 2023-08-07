package com.alura.jdbc.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alura.jdbc.factory.ConnectionFactory;

public class ProductoController {
	
	public int modificar(String nombre, String descripcion, Integer cantidad, Integer id) throws SQLException {
		//Modificar
		//Connection con = new ConnectionFactory().recuperaConexion(); //crea la conexion
		
		ConnectionFactory factory = new ConnectionFactory();
	    Connection con = factory.recuperaConexion();
	    
	  //Evitando SQL Injection
	    PreparedStatement statement = con.prepareStatement("UPDATE PRODUCTO SET "
	            + " NOMBRE = ? "
	            + ", DESCRIPCION = ? "
	            + ", CANTIDAD = ? "
	            + " WHERE ID = ? ");
	    		    
	    statement.setString(1, nombre);
	    statement.setString(2, descripcion);
	    statement.setInt(3, cantidad);
	    statement.setInt(4, id);
	    
	    statement.execute();
	 /*   
	  //Metodo vulnerable a SQL Injection
	    	//Logica de Update con Statement
		Statement statement = con.createStatement();
		
		statement.execute("UPDATE PRODUCTO SET "
	            + " NOMBRE = '" + nombre + "'"
	            + ", DESCRIPCION = '" + descripcion + "'"
	            + ", CANTIDAD = " + cantidad
	            + " WHERE ID = " + id);
		*/
	    
		int updateCount = statement.getUpdateCount();
		con.close();
		return updateCount;
			
		//return statement.getUpdateCount();
			
	}

	
	public int eliminar(Integer id) throws SQLException {
		// Eliminar el producto
		Connection con = new ConnectionFactory().recuperaConexion(); //crea la conexion
		
		//Evitando SQL Injection
		PreparedStatement statement = con.prepareStatement("DELETE FROM PRODUCTO WHERE ID = ?");
		statement.setInt(1, id);
		statement.execute();
		
	/*	//Metodo vulnerable a SQL Injection
		//Logica de Delete con Statement
		Statement statement = con.createStatement();
		
		statement.execute("DELETE FROM PRODUCTO WHERE ID = " + id);
		
		// int updateCount = statement.getUpdateCount(); //devuelve un int el cual nos dice cuantas filas fueron modificadas. -- usado cuando el metodo es void
		
		return statement.getUpdateCount(); //devuelve un int el cual nos dice cuantas filas fueron modificadas. -- usado cuando el metodo es int y tiene return
		
		*/
		
		int updateCount = statement.getUpdateCount();
		con.close();
		return updateCount;
		
	}

	public List<Map<String, String>> listar() throws SQLException {
		
		Connection con = new ConnectionFactory().recuperaConexion();
		
		//Evitando SQL Injection
		PreparedStatement statement = con.prepareStatement("SELECT ID, NOMBRE, DESCRIPCION, CANTIDAD FROM PRODUCTO");
				statement.execute();
		
				
	/*	//Metodo vulnerable a SQL Injection
		Statement statement = con.createStatement(); //con este objeto podremos declarar el query para la consulta a la BD
		
		statement.execute("SELECT id, nombre, descripcion, cantidad FROM producto");
		//execute devuelve un boolean  para indicar si el resultado del statement es un listado como el select devuelve un true, 
		// si no retorna un false que seria un update, delete o insert.     
	 */
		
		ResultSet resultSet = statement.getResultSet(); //Estado de resultado
		
		List<Map<String, String>> resultado = new ArrayList<>();
		
		while(resultSet.next()) {//Para poder ver el contenido para agregarlo al listado del resultado
			Map<String, String> fila = new HashMap<>();
			fila.put("ID", String.valueOf(resultSet.getInt("ID")));
			fila.put("NOMBRE", resultSet.getString("NOMBRE"));
			fila.put("DESCRIPCION", resultSet.getString("DESCRIPCION"));
			fila.put("CANTIDAD", String.valueOf(resultSet.getInt("CANTIDAD")));
			
			resultado.add(fila);
		}
		
		
		con.close(); //cerramos la conexion
		
		return resultado; // Devolvemos la informacion
	}
	

    public void guardar(Map<String, String> producto) throws SQLException {
		Connection con = new ConnectionFactory().recuperaConexion();
			
		//Opcion de Insert Para evitar SQL Injection utilizando PreparedStatement: ingresar:  Mouse'  con el signo '  genera error por en en SQL las comillas simples '' señalan un String 
		//COn esto pasamos la seguridad al JDBC
		//El PreparedStatement se encarga de normalizar el texto ingresado como texto común. SI se agrega un ' el lo agrega asi.
		PreparedStatement statement = con.prepareStatement("INSERT INTO PRODUCTO (nombre, descripcion, cantidad)"
					+ " VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, producto.get("NOMBRE"));
					statement.setString(2, producto.get("DESCRIPCION"));
					statement.setInt(3, Integer.valueOf(producto.get("CANTIDAD")));
					
					statement.execute();
					
		
		/* //Opcion: no segura para insert vulnerable a SQL Injection
		 
		  //Logica de Insert con Statement	
		Statement statement = con.createStatement();
		
		String sqlInsert = "INSERT INTO PRODUCTO(nombre, descripcion, cantidad) " //No se escribe el id porque es auto_incremente en la BD
				+ " VALUES('" + producto.get("NOMBRE") + "', '" //se agrego comillas simples debido a que es un String el campo nombre, comillas simples '' en BD señala un String y en java son las comillas dobles "" para el String
				+ producto.get("DESCRIPCION") + "', "
				+ producto.get("CANTIDAD") + ")";
		
		System.out.println(sqlInsert);
		statement.execute(
				sqlInsert,
				Statement.RETURN_GENERATED_KEYS);
		*/
		
		/*//Opcion: no segura para insert vulnerable a SQL Injection
		statement.execute("INSERT INTO PRODUCTO(nombre, descripcion, cantidad) " //No se escribe el id porque es auto_incremente en la BD
					+ " VALUES('" + producto.get("NOMBRE") + "', '" //se agrego comillas simples debido a que es un String el campo nombre, comillas simples '' en BD señala un String y en java son las comillas dobles "" para el String
					+ producto.get("DESCRIPCION") + "', "
					+ producto.get("CANTIDAD") + ")", Statement.RETURN_GENERATED_KEYS); //")", cerrar el parentesis de la Query //Statement.RETURN_GENERATED_KEYS - Cuando se ejecuta un insert (se incrementa el id) obtiene el id generado con insert 
		*/
				
		ResultSet resultSet = statement.getGeneratedKeys(); // Resultado de los id generados con la ejecucion de la Query
		
		while(resultSet.next()) {//while para obtener el valor del id generado
				System.out.println(
						String.format(
							"Fue insertado el producto de ID %d",
							 resultSet.getInt(1)));//posicion 1 para saber cual id fue generado
										
		}
    } 
}


