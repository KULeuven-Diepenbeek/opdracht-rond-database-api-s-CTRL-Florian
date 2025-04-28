package be.kuleuven;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
      String sql = "INSERT INTO speler (tennisvlaanderenid, naam, punten) VALUES (?, ?, ?)";

      PreparedStatement prepared = connection.prepareStatement(sql);
      prepared.setInt(1, speler.getTennisvlaanderenid());
      prepared.setString(2, speler.getNaam());
      prepared.setInt(3, speler.getPunten());
      prepared.executeUpdate();
      prepared.close();

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
      String sql = "SELECT * FROM speler WHERE tennisvlaanderenid = ?";

      PreparedStatement prepared = connection.prepareStatement(sql);
      prepared.setInt(1, tennisvlaanderenId);
      var queryResult = prepared.executeQuery();

      if (queryResult.next()) {
        // [DEBUG]: verwijder voor indienen.
        String printThis = String.format(
          "fromDB: %d '%s' %d",
          queryResult.getInt("tennisvlaanderenid"),
          queryResult.getString("naam"),
          queryResult.getInt("punten")
        );
        System.out.println(printThis);
        // [QUESTION]: Waar moet close statement?
        //prepared.close();
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

    PreparedStatement prepared = connection.prepareStatement(sql);
    var queryResult = prepared.executeQuery();

    while (queryResult.next()){
      int tennisvlaanderenId = queryResult.getInt("tennisvlaanderenid");
      String naam = queryResult.getString("naam");
      int punten = queryResult.getInt("punten");

      // [DEBUG]: verwijder voor indienen.
      String printThis = String.format(
        "fromDB: %d '%s' %d",
        tennisvlaanderenId,
        naam,
        punten
      );
      System.out.println(printThis);

      result.add(new Speler(tennisvlaanderenId, naam, punten));
    }

    // [QUESTION]: Waar moet close statement?
    prepared.close();

    return result;

    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij ophalen alle spelers" + e.getMessage(), e);
    }
  }

  @Override
  public void updateSpelerInDb(Speler speler) {
    //throw new UnsupportedOperationException("Unimplemented method 'updateSpelerInDb'");
    try {
      String sqlCheck = "SELECT * FROM speler WHERE tennisvlaanderenid = ?";

      PreparedStatement preparedCheck = connection.prepareStatement(sqlCheck);
      preparedCheck.setInt(1, speler.getTennisvlaanderenid());
      var queryResult = preparedCheck.executeQuery();

      if(queryResult.next()){
        String sqlUpdate = "UPDATE speler SET naam = ?, punten = ? WHERE tennisvlaanderenid = ?";

        PreparedStatement preparedUpdate = connection.prepareStatement(sqlUpdate);
        preparedUpdate.setString(1, speler.getNaam());
        preparedUpdate.setInt(2, speler.getPunten());
        preparedUpdate.setInt(3, speler.getTennisvlaanderenid());
        preparedUpdate.executeUpdate();
        preparedUpdate.close();

      } else {
        // [DEBUG]: verwijder voor indienen.
        System.err.println("[DEBUG] Invalid Speler met identification: " + speler.getTennisvlaanderenid());
        throw new RuntimeException("Invalid Speler met identification: " + speler.getTennisvlaanderenid());
      }

    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij updaten speler" + e.getMessage(), e);
    }
  }

  @Override
  public void deleteSpelerInDb(int tennisvlaanderenid) {
    // throw new UnsupportedOperationException("Unimplemented method 'deleteSpelerInDb'");
    try {
      String sqlCheck = "SELECT * FROM speler WHERE tennisvlaanderenid = ?";

      PreparedStatement preparedCheck = connection.prepareStatement(sqlCheck);
      preparedCheck.setInt(1, tennisvlaanderenid);
      var queryResult = preparedCheck.executeQuery();

      if(queryResult.next()){
        String sqlDelete = "DELETE FROM speler WHERE tennisvlaanderenid = ?";

        PreparedStatement preparedDelete = connection.prepareStatement(sqlDelete);
        preparedDelete.setInt(1, tennisvlaanderenid);
        preparedDelete.executeUpdate();
        preparedDelete.close();
      } else {
        // [DEBUG]: verwijder voor indienen.
        System.err.println("[DEBUG] Invalid Speler met identification: " + tennisvlaanderenid);
        throw new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenid);
      }
    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij verwijderen speler" + e.getMessage(), e);
    }
  }

  @Override
  public String getHoogsteRankingVanSpeler(int tennisvlaanderenid) {
    // throw new UnsupportedOperationException("Unimplemented method 'getHoogsteRankingVanSpeler'");
    /*
     *  ! Info over JOIN (en meer) hier gevonden: https://www.w3schools.com/sql/sql_join.asp
     *  ! Info over ORDER BY (en meer) hier gevonden: https://www.w3schools.com/sql/sql_orderby.asp
     */
    try {
      String result = "Geen tornooi en plaats gevonden";

      String sql = "SELECT tornooi.clubnaam, wedstrijd.finale, wedstrijd.winnaar FROM wedstrijd " +   // clubnaam, finalenr. en winaar ophalen.
                   "JOIN tornooi ON wedstrijd.tornooi = tornooi.id " +                                // JOIN zodat clubnaam uit tornooi gehaald kan worden.
                                                                                                      // Het tornooi ID moet overeenkomen met de int "tornooi" in de tabel wedstrijd.
                                                                                                      // "tornooi" in wedstrijd is foreign key naar een tornaaiID.
                   "WHERE (wedstrijd.speler1 = ? OR wedstrijd.speler2 = ?) " +                        // Gegeven tennisvlaanderenid moet overeenkomen met speler1 / speler2 van een wedstrijd.
                   "ORDER BY wedstrijd.finale ASC, wedstrijd.winnaar = ? DESC";                                // Eerst laagste finalenr. dan sorteren welke boolean het hoogste is.
                                                                                                      // Boolean is true (1) als wedstrijd.winnaar = tennisvlaanderenid.

      PreparedStatement prepared = connection.prepareStatement(sql);
      prepared.setInt(1, tennisvlaanderenid);
      prepared.setInt(2, tennisvlaanderenid);
      prepared.setInt(3, tennisvlaanderenid);
      var queryResult = prepared.executeQuery();

      if (queryResult.next()) {
        String clubnaam = queryResult.getString("clubnaam");
        int finale = queryResult.getInt("finale");
        int winnaar = queryResult.getInt("winnaar");

        String plaats;
        switch (finale) {
          case 1:
            if (winnaar == tennisvlaanderenid){
              plaats = "winst";
            } else {
              plaats = "finale";
            }
            break;
          case 2:
            plaats = "halve finale";
            break;
          case 3:
            plaats = "kwart finale";
            break;
          case 4:
            plaats = "achtste finale";
            break;
          default:
            plaats = "n-finale";
        }
        result = "Hoogst geplaatst in het tornooi van " + clubnaam + " met plaats in de " + plaats;
      }
      return result;

    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij opzoeken beste ranking speler" + e.getMessage(), e);
    }
  }

  @Override
  public void addSpelerToTornooi(int tornooiId, int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'addSpelerToTornooi'");
    try {
      String sql = "INSERT INTO speler_speelt_tornooi (speler, tornooi) VALUES (?, ?)";

      PreparedStatement prepared = connection.prepareStatement(sql);
      prepared.setInt(1, tennisvlaanderenId);
      prepared.setInt(2, tornooiId);
      prepared.executeUpdate();
      prepared.close();
      connection.commit();

    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij toevoegen speler aan tornooi" + e.getMessage(), e);
    }
  }

  @Override
  public void removeSpelerFromTornooi(int tornooiId, int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'removeSpelerFromTornooi'");
    try {
      String sqlCheck = "SELECT * FROM speler_speelt_tornooi WHERE speler = ? AND tornooi = ?";

      PreparedStatement preparedCheck = connection.prepareStatement(sqlCheck);
      preparedCheck.setInt(1, tennisvlaanderenId);
      preparedCheck.setInt(2, tornooiId);
      var queryResult = preparedCheck.executeQuery();

      if(queryResult.next()){
        String sqlDelete = "DELETE FROM speler_speelt_tornooi WHERE speler = ? AND tornooi = ?";

        PreparedStatement preparedDelete = connection.prepareStatement(sqlDelete);
        preparedDelete.setInt(1, tennisvlaanderenId);
        preparedDelete.setInt(2, tornooiId);
        preparedDelete.executeUpdate();
        preparedDelete.close();
        connection.commit();
      } else {
        // [DEBUG]: verwijder voor indienen.
        System.err.println("[DEBUG] Invalid Speler met identification:");
        throw new RuntimeException("Invalid Speler met identification");
      }
    } catch (SQLException e) {
      // [DEBUG]: verwijder voor indienen.
      System.err.println("[DEBUG] " + e.getMessage());
      throw new RuntimeException("Databasefout bij verwijderen speler van tornooi" + e.getMessage(), e);
    }
  }
}
