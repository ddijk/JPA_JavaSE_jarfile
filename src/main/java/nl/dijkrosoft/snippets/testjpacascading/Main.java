package nl.dijkrosoft.snippets.testjpacascading;

import app.owf.i18n.SimpleInternationalisationService;
import app.owf.inject.DependencyContainer;
import app.owf.inject.DependencyType;
import app.owf.service.IInternationalisationService;
import app.owf.service.IUserDetailsService;
import app.owf.validation.IObjectValidator;
import com.ortecfinance.opal.model.AbstractFinancialItem;
import com.ortecfinance.opal.model.advice.AdviceCapitalGoal;
import com.ortecfinance.opal.model.client.Client;
import com.ortecfinance.opal.model.client.ClientCapitalGoal;
import com.ortecfinance.opal.model.financialitem.CheckingAccountItem;
import com.ortecfinance.opal.model.financialitem.FinancialItem;
import com.ortecfinance.opal.model.goal.GoalCategory;
import com.ortecfinance.opal.service.validation.FinancialItemValidator;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import owf.web.jsf.inject.FacesUserDetailsService;

public class Main {

    public static void main(String[] args) {
        DependencyContainer container = DependencyContainer.getDefaultContainer();
        container.registerType(IInternationalisationService.class, SimpleInternationalisationService.class);
        container.registerType(new DependencyType<IObjectValidator<FinancialItem>>() {
        }, FinancialItemValidator.class);
        container.registerEagerSingleton(IUserDetailsService.class, new FacesUserDetailsService());
        System.out.println("Here we go");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU2");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        //  createGoal(em);
        //   createClientCapitalGoal(em);
        deleteGoal(em, 700L);

        em.getTransaction().commit();

        em.close();

        emf.close();
        System.out.println("Going down");

    }

    private static void createGoal(EntityManager em) {

        GoalCategory cat = em.find(GoalCategory.class, 1L);
        System.out.println("cat is " + cat.getCode());

        AdviceCapitalGoal g = new AdviceCapitalGoal();
        g.setCategory(cat);
        g.setPriority(999);

        em.persist(g);

        System.out.println("Goals saved with id " + g.getId());

    }

    private static void createClientCapitalGoal(EntityManager em) {

        ClientCapitalGoal ccg = new ClientCapitalGoal();
        Client client = em.find(Client.class, 5426L);

        ccg.setClient(client);
        List<AbstractFinancialItem<?>> items = new ArrayList<>();

        CheckingAccountItem ca = new CheckingAccountItem();
        ca.getAccountHolders().add(client);

        items.add(ca);

        ccg.setItems(items);
        ca.getGoals().add(ccg);
        em.persist(ca);
        System.out.println("Saved CheckingAccountItem with id " + ca.getId());
    }

    private static void deleteGoal(EntityManager em, long id) {
        ClientCapitalGoal ccg = em.find(ClientCapitalGoal.class, id);
        final List<AbstractFinancialItem<?>> items = ccg.getItems();
        items.get(0).getGoals().clear();

        System.out.println("Aantal items " + items.size());

        ccg.getItems().clear();

        em.persist(ccg);

    }
}
