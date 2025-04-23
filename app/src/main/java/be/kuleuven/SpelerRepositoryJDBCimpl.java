package be.kuleuven;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

import java.lang.String;

public class SpelerRepositoryJDBCimpl implements SpelerRepository {
  private Connection connection;

  // Constructor
  SpelerRepositoryJDBCimpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addSpelerToDb(Speler speler) {
    try {
      String sql = String.format(
        "INSERT INTO speler (tennisvlaanderenid, naam, punten) VALUES (%d, '%s', %d)",
        speler.getTennisvlaanderenid(),
        speler.getNaam(),
        speler.getPunten()
      );
      var s = connection.createStatement(); 
      s.executeUpdate(sql);
      //connection.commit();
      s.close();
    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij toevoegen speler" + e.getMessage(), e);
    }
  }

  @Override
  public Speler getSpelerByTennisvlaanderenId(int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'getSpelerByTennisvlaanderenId'");
    try {
      String sql = "SELECT * FROM speler WHERE tennisvlaanderenid = " + tennisvlaanderenId;
      var s = connection.createStatement();
      var queryResult = s.executeQuery(sql);

      if (queryResult.next()) {
        // [DEBUG]: verwijder voor indienen.
        String printThis = String.format(
          "fromDB: %d '%s' %d",
          queryResult.getInt("tennisvlaanderenid"),
          queryResult.getString("naam"),
          queryResult.getInt("punten")
        );
        System.out.println(printThis);
        return new Speler(
          queryResult.getInt("tennisvlaanderenid"),
          queryResult.getString("naam"),
          queryResult.getInt("punten")
        );
      } else {
        // [DEBUG]: verwijder voor indienen.
        System.err.println("[DEBUG] Invalid Speler met identification: " + tennisvlaanderenId);
        throw new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenId);
      }
    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij ophalen speler" + e.getMessage(), e);
    }
  }

  @Override
  public List<Speler> getAllSpelers() {
    // throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
    try{
    ArrayList<Speler> result = new ArrayList<>();
    String sql = "SELECT tennisvlaanderenid, naam, punten FROM speler";
    var s = connection.createStatement();
    var queryResult = s.executeQuery(sql);

    while (queryResult.next()){
      int tennisvlaanderenId = queryResult.getInt("tennisvlaanderenid");
      String naam = queryResult.getString("naam");
      int punten = queryResult.getInt("punten");

      result.add(new Speler(tennisvlaanderenId, naam, punten));
    }

    return result;

    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij ophalen alle spelers" + e.getMessage(), e);
    }
  }

  @Override
  public void updateSpelerInDb(Speler speler) {
    // TODO: verwijder de "throw new UnsupportedOperationException" en schrijf de code die de gewenste methode op de juiste manier implementeerd zodat de testen slagen.
    throw new UnsupportedOperationException("Unimplemented method 'updateSpelerInDb'");
  }

  @Override
  public void deleteSpelerInDb(int tennisvlaanderenid) {
    // TODO: verwijder de "throw new UnsupportedOperationException" en schrijf de code die de gewenste methode op de juiste manier implementeerd zodat de testen slagen.
    throw new UnsupportedOperationException("Unimplemented method 'deleteSpelerInDb'");
  }

  @Override
  public String getHoogsteRankingVanSpeler(int tennisvlaanderenid) {
    // TODO: verwijder de "throw new UnsupportedOperationException" en schrijf de code die de gewenste methode op de juiste manier implementeerd zodat de testen slagen.
    throw new UnsupportedOperationException("Unimplemented method 'getHoogsteRankingVanSpeler'");
  }

  @Override
  public void addSpelerToTornooi(int tornooiId, int tennisvlaanderenId) {
    // TODO: verwijder de "throw new UnsupportedOperationException" en schrijf de code die de gewenste methode op de juiste manier implementeerd zodat de testen slagen.
    throw new UnsupportedOperationException("Unimplemented method 'addSpelerToTornooi'");
  }

  @Override
  public void removeSpelerFromTornooi(int tornooiId, int tennisvlaanderenId) {
    // TODO: verwijder de "throw new UnsupportedOperationException" en schrijf de code die de gewenste methode op de juiste manier implementeerd zodat de testen slagen.
    throw new UnsupportedOperationException("Unimplemented method 'removeSpelerFromTornooi'");
  }
}
