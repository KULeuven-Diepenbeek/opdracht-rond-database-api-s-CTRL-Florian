package be.kuleuven;

import java.util.List;

import javax.persistence.NoResultException;

import org.jdbi.v3.core.Jdbi;

public class SpelerRepositoryJDBIimpl implements SpelerRepository {
  private final Jdbi jdbi;

  // Constructor
  SpelerRepositoryJDBIimpl(String connectionString, String user, String pwd) {
    // this.jdbi = null;
    this.jdbi = Jdbi.create(connectionString, user, pwd);
  }

  @Override
  public void addSpelerToDb(Speler speler) {
    // throw new UnsupportedOperationException("Unimplemented method 'addSpelerToDb'");
    try {
      String sql = "INSERT INTO speler (tennisvlaanderenid, naam, punten) VALUES (?, ?, ?)";

      jdbi.withHandle(handle -> {
        return handle.execute(sql, 
          speler.getTennisvlaanderenId(), speler.getNaam(), speler.getPunten());
      });
    } catch (Exception e) {
      throw new RuntimeException("Databasefout bij toevoegen speler" + e.getMessage(), e);
    }
  }

  @Override
  public Speler getSpelerByTennisvlaanderenId(int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'getSpelerByTennisvlaanderenId'");
    try {
      String sql = "SELECT * FROM speler WHERE tennisvlaanderenid = :tennisvlaanderenid";

      Speler speler = jdbi.withHandle(handle -> {
        return handle.createQuery(sql)
          .bind("tennisvlaanderenid", tennisvlaanderenId)
          .mapToBean(Speler.class)
          .first();       // !  Als er geen speler gevonden is gooit .first() een IllegalStateException.
      });

      if (speler == null) {
        throw new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenId);
      }
      return speler;

    } catch (IllegalStateException e) {
      throw new InvalidSpelerException("Invalid Speler met identification: " + tennisvlaanderenId);
    } catch (Exception e) {
      throw new InvalidSpelerException("Invalid Speler met identification: " + tennisvlaanderenId);
    }
  }

  @Override
  public List<Speler> getAllSpelers() {
    // throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
    try {
      String sql = "SELECT * FROM speler";

      return jdbi.withHandle(handle -> {
        return handle.createQuery(sql)
          .mapToBean(Speler.class)
          .list();
      });

    } catch (Exception e) {
      throw new RuntimeException("Databasefout bij ophalen alle spelers" + e.getMessage(), e);
    }
  }

  @Override
  public void updateSpelerInDb(Speler speler) {
    // throw new UnsupportedOperationException("Unimplemented method 'updateSpelerInDb'");
    getSpelerByTennisvlaanderenId(speler.getTennisvlaanderenId());

    String sql = "UPDATE speler SET naam = ?, punten = ? WHERE tennisvlaanderenid = ?";

    jdbi.withHandle(handle -> {
      return handle.execute(sql, 
        speler.getNaam(), speler.getPunten(), speler.getTennisvlaanderenId());
    });
  }

  @Override
  public void deleteSpelerInDb(int tennisvlaanderenid) {
    // throw new UnsupportedOperationException("Unimplemented method 'deleteSpelerInDb'");
    getSpelerByTennisvlaanderenId(tennisvlaanderenid);  // Handelt InvalidSpelerException.

    String sql = "DELETE FROM speler WHERE tennisvlaanderenid = ?";

    jdbi.withHandle(handle -> {
      return handle.execute(sql,
         tennisvlaanderenid);
    });
  }

  @Override
  public String getHoogsteRankingVanSpeler(int tennisvlaanderenid) {
    // throw new UnsupportedOperationException("Unimplemented method 'getHoogsteRankingVanSpeler'");
    /*
     *  ! Info over JOIN (en meer) hier gevonden: https://www.w3schools.com/sql/sql_join.asp
     *  ! Info over ORDER BY (en meer) hier gevonden: https://www.w3schools.com/sql/sql_orderby.asp
     */
    try {
      String sql = "SELECT tornooi.clubnaam, wedstrijd.finale, wedstrijd.winnaar FROM wedstrijd " +   // clubnaam, finalenr. en winaar ophalen.
                   "JOIN tornooi ON wedstrijd.tornooi = tornooi.id " +                                // JOIN zodat clubnaam uit tornooi gehaald kan worden.
                                                                                                      // Het tornooi ID moet overeenkomen met de int "tornooi" in de tabel wedstrijd.
                                                                                                      // "tornooi" in wedstrijd is foreign key naar een tornaaiID.
                   "WHERE (wedstrijd.speler1 = :id OR wedstrijd.speler2 = :id) " +                    // Gegeven tennisvlaanderenid moet overeenkomen met speler1 / speler2 van een wedstrijd.
                   "ORDER BY wedstrijd.finale ASC, wedstrijd.winnaar = :id DESC";                     // Eerst laagste finalenr. dan sorteren welke boolean het hoogste is.
                                                                                                      // Boolean is true (1) als wedstrijd.winnaar = tennisvlaanderenid.

      return jdbi.withHandle(handle -> {
        return handle.createQuery(sql)
          .bind("id", tennisvlaanderenid)
              /* 
                https://jdbi.org 7.1 Row Swappers:
                  Resultaat van de query wordt gemapt in een resultset en context (!?).

              */
          .map((rs, ctx) -> {
            String clubnaam = rs.getString("clubnaam");
            int finale = rs.getInt("finale");
            int winnaar = rs.getInt("Winnaar");

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

            String result = "Hoogst geplaatst in het tornooi van " + clubnaam + " met plaats in de " + plaats;
            return result;
          })
          .findFirst()
            // https://jdbi.org .orElse():
          .orElse("Geen tornooi en plaats gevonden");
      });

    } catch (Exception e) {
      throw new RuntimeException("Databasefout bij opzoeken beste ranking speler" + e.getMessage(), e);
    }
  }

  @Override
  public void addSpelerToTornooi(int tornooiId, int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'addSpelerToTornooi'");
    try {
      String sql = "INSERT INTO speler_speelt_tornooi (speler, tornooi) VALUES (?, ?)";

      jdbi.withHandle(handle -> {
        return handle.execute(sql,
          tennisvlaanderenId, tornooiId);
      });

    } catch (Exception e) {
      throw new RuntimeException("Databasefout bij opzoeken beste ranking speler" + e.getMessage(), e);
    }
  }

  @Override
  public void removeSpelerFromTornooi(int tornooiId, int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'removeSpelerFromTornooi'");
    try {
      String sql = "DELETE FROM speler_speelt_tornooi WHERE speler = ? AND tornooi = ?";

      jdbi.withHandle(handle -> {
        return handle.execute(sql,
          tennisvlaanderenId, tornooiId);
      });
      
    } catch (Exception e) {
      throw new RuntimeException("Databasefout bij opzoeken beste ranking speler" + e.getMessage(), e);
    }
  }
}
