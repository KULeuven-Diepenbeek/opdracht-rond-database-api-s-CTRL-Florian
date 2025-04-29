package be.kuleuven;

import java.util.Comparator;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class SpelerRepositoryJPAimpl implements SpelerRepository {
  private final EntityManager em;
  public static final String PERSISTANCE_UNIT_NAME = "be.kuleuven.spelerhibernateTest";

  // Constructor
  SpelerRepositoryJPAimpl(EntityManager entityManager) {
    // throw new UnsupportedOperationException("Unimplemented constructor");
    this.em = entityManager;
  }

  @Override
  public void addSpelerToDb(Speler speler) {
    // throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
    try {
      em.getTransaction().begin();
      em.persist(speler);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw new RuntimeException(" A PRIMARY KEY constraint failed" + e);
    }
  }

  @Override
  public Speler getSpelerByTennisvlaanderenId(int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'getSpelerByTennisvlaanderenId'");
    try {
      var criteriaBuilder = em.getCriteriaBuilder();
      var query = criteriaBuilder.createQuery(Speler.class);
      var root = query.from(Speler.class);

      query.where(criteriaBuilder.equal(root.get("tennisvlaanderenId"), tennisvlaanderenId));
      
      Speler speler = em.createQuery(query).getSingleResult();

      return speler;

    } catch (NoResultException e) {
      throw new InvalidSpelerException("Invalid Speler met identification: " + tennisvlaanderenId);
    } catch (Exception e) {
      throw new InvalidSpelerException("Invalid Speler met identification: " + tennisvlaanderenId);
    }
  }

  @Override
  public List<Speler> getAllSpelers() {
    // throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
    try {
      var criteriaBuilder = em.getCriteriaBuilder();
      var query = criteriaBuilder.createQuery(Speler.class);
      var root = query.from(Speler.class);

      query.select(root);

      List<Speler> result = em.createQuery(query).getResultList();

      return result;
    } catch (Exception e) {
      throw new RuntimeException("Databasefout bij ophalen alle spelers" + e.getMessage(), e);
    }
    
  }

  @Override
  public void updateSpelerInDb(Speler speler) {
    // throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
      getSpelerByTennisvlaanderenId(speler.getTennisvlaanderenId());  // Handelt InvalidSpelerException.

      em.getTransaction().begin();
      em.merge(speler);
      em.getTransaction().commit();
  }

  @Override
  public void deleteSpelerInDb(int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
    em.getTransaction().begin();

    getSpelerByTennisvlaanderenId(tennisvlaanderenId);  // Handelt InvalidSpelerException.

    var criteriaBuilder = em.getCriteriaBuilder();
    var query = criteriaBuilder.createCriteriaDelete(Speler.class);
    var root = query.from(Speler.class);

    query.where(criteriaBuilder.equal(root.get("tennisvlaanderenId"), tennisvlaanderenId));
      
    em.createQuery(query).executeUpdate();

    em.getTransaction().commit();
  }

  @Override
  public String getHoogsteRankingVanSpeler(int tennisvlaanderenId) {
    // throw new UnsupportedOperationException("Unimplemented method 'getAllSpelers'");
    /*
     *  ! Ik heb gebrobeerd de query uit de JDBC en JDBI implementatie na te bouwen.
     *    Hiervoor heb ik info gevonden op de links tussen de code. 
     *    Met de link op de cursussite heb ik ook heel wat gevonden:
     *      https://www.initgrep.com/posts/java/jpa/create-programmatic-queries-using-criteria-api
     */
    var criteriaBuilder = em.getCriteriaBuilder();
    var query = criteriaBuilder.createQuery(Object[].class); // --> Resultaat is geen classe op zich!!!
    var wedstrijd = query.from(Wedstrijd.class);
    var tornooi = query.from(Tornooi.class);

      // https://www.initgrep.com/posts/java/jpa/select-values-in-criteria-queries multiselect:
    query.multiselect(
      tornooi.get("clubnaam"),
      wedstrijd.get("finale"),
      wedstrijd.get("winnaarId")
    );

    query.where(
      criteriaBuilder.equal(
        wedstrijd.get("tornooiId"), tornooi.get("id")
      ),
      criteriaBuilder.or(
        criteriaBuilder.equal(wedstrijd.get("speler1Id"), tennisvlaanderenId),
        criteriaBuilder.equal(wedstrijd.get("speler2Id"), tennisvlaanderenId)
      )
    );

    // var isWinnaar = criteriaBuilder.selectCase()
    //   .when(criteriaBuilder.equal(wedstrijd.get("winnaarId"), tennisvlaanderenId), 1)
    //   .otherwise(0);
      // https://www.baeldung.com/jpa-sort order by:
    query.orderBy(
      criteriaBuilder.asc(wedstrijd.get("finale")),
        // !  Geen boolean maar integer gebruiken zodat men hierop kan sorteren
        //    Elimineert de kans, dat als een speler 2 keer dezelfde finale speelde maar de ene won en de andere verloor, niet het beste wordt gebruikt.
      criteriaBuilder.desc(criteriaBuilder.selectCase().when(criteriaBuilder.equal(wedstrijd.get("winnaarId"), tennisvlaanderenId), 1).otherwise(0))
    );

      /*  https://stackoverflow.com/questions/63935473/hibernate-query-to-select-values-from-multiple-tables-using-criteriaquery
       *    --> zie eerste antwoord: lijkt op deze functie, men en sorteert of multiselect echter niet.
       *        Deze functie werkt ook niet met een tupple als query resultaat.
       */

       // var ipv Object[] ter vergemakkelijking.
    var resultList = em.createQuery(query)
      .setMaxResults(1)
      .getResultList();

    if (resultList.isEmpty()){
      return "Geen tornooi en plaats gevonden";
    }

      // var ipv Object[] ter vergemakkelijking.
    var result = resultList.get(0);
    String clubnaam = (String) result[0];
    int finale = (int) result[1];
    int winnaar = (int) result[2];

    String plaats;
    switch (finale) {
      case 1:
        if (winnaar == tennisvlaanderenId){
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
    String resultString = "Hoogst geplaatst in het tornooi van " + clubnaam + " met plaats in de " + plaats;
    return resultString;
  }

  @Override
  public void addSpelerToTornooi(int tornooiId, int tennisvlaanderenId) {
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      Speler speler = em.find(Speler.class, tennisvlaanderenId);
      Tornooi tornooi = em.find(Tornooi.class, tornooiId);

      speler.getTornooien().add(tornooi);
      em.merge(speler);

      tx.commit();
    } catch (Exception e) {
      if (tx.isActive())
        tx.rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  @Override
  public void removeSpelerFromTornooi(int tornooiId, int tennisvlaanderenId) {
    EntityTransaction tx = em.getTransaction();

    try {
      tx.begin();

      Speler speler = em.find(Speler.class, tennisvlaanderenId);
      Tornooi tornooi = em.find(Tornooi.class, tornooiId);

      if (speler == null || tornooi == null) {
        throw new IllegalArgumentException("Speler or Tornooi not found");
      }

      speler.getTornooien().remove(tornooi);
      em.merge(speler);

      tx.commit();
    } catch (Exception e) {
      if (tx.isActive())
        tx.rollback();
      throw e;
    } finally {
      em.close();
    }
  }
}
