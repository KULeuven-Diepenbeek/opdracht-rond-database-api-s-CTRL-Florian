package be.kuleuven;

import java.sql.Connection;
import java.util.List;
import java.sql.SQLException;

import java.lang.String;

public class SpelerRepositoryJDBCimpl implements SpelerRepository {
  private Connection connection;

  // Constructor
  SpelerRepositoryJDBCimpl(Connection connection) {
    // TODO: Is dit correct?
    this.connection = connection;
  }

  @Override
  public void addSpelerToDb(Speler speler) {
    // TODO: Is dit correct?
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
      throw new RuntimeException("Databasefout bij toevoegen speler", e);
    }
  }

  @Override
  public Speler getSpelerByTennisvlaanderenId(int tennisvlaanderenId) {
    // TODO: verwijder de "throw new UnsupportedOperationException" en schrijf de code die de gewenste methode op de juiste manier implementeerd zodat de testen slagen.
    // throw new UnsupportedOperationException("Unimplemented method 'getSpelerByTennisvlaanderenId'");
    try {
      String sql = "SELECT * FROM speler WHERE tennisvlaanderenid = " + tennisvlaanderenId;
      var s = connection.createStatement();
      var result = s.executeQuery(sql);

      if (result.next()) {
        // TODO: [DEBUG] verwijder voor indienen.
        String printThis = String.format(
          "fromDB: %d '%s' %d",
          result.getInt("tennisvlaanderenid"),
          result.getString("naam"),
          result.getInt("punten")
        );
        System.out.println(printThis);
        return new Speler(
          result.getInt("tennisvlaanderenid"),
          result.getString("naam"),
          result.getInt("punten")
        );
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Databasefout bij ophalen speler", e);
    }
  }

  @Override
  public List<Speler> getAllSpelers() {
    // TODO: verwijder de "throw new UnsupportedOperationException" en schrijf de code die de gewenste methode op de juiste manier implementeerd zodat de testen slagen.
    throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
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
