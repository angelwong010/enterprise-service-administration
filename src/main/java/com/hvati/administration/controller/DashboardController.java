package com.hvati.administration.controller;

import com.hvati.administration.repository.ClientRepository;
import com.hvati.administration.repository.QuotationRepository;
import com.hvati.administration.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final SaleRepository saleRepository;
    private final QuotationRepository quotationRepository;
    private final ClientRepository clientRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        long salesCount = saleRepository.count();
        long quotationsCount = quotationRepository.count();
        long clientsCount = clientRepository.count();

        long pendingSales = saleRepository.countByPaymentStatus("PENDING");
        long paidSales = saleRepository.countByPaymentStatus("PAID");
        long draftQuotations = quotationRepository.countByStatus("draft");
        long acceptedQuotations = quotationRepository.countByStatus("accepted");

        Map<String, Object> root = new HashMap<>();

        // --- Github issues: usamos ventas/cotizaciones reales en los totales,
        // pero mantenemos la forma original de Fuse para las gráficas ---
        Map<String, Object> githubIssues = new HashMap<>();
        Map<String, Object> githubOverview = new HashMap<>();

        Map<String, Object> thisWeekOverview = new HashMap<>();
        thisWeekOverview.put("new-issues", salesCount);          // Ventas
        thisWeekOverview.put("closed-issues", quotationsCount);  // Cotizaciones
        thisWeekOverview.put("fixed", Math.min(salesCount, quotationsCount)); // Aproximación
        thisWeekOverview.put("wont-fix", Math.max(0L, quotationsCount - salesCount));
        thisWeekOverview.put("re-opened", 0);
        thisWeekOverview.put("needs-triage", 0);

        Map<String, Object> lastWeekOverview = new HashMap<>(thisWeekOverview);

        githubOverview.put("this-week", thisWeekOverview);
        githubOverview.put("last-week", lastWeekOverview);

        githubIssues.put("overview", githubOverview);
        githubIssues.put("labels", List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));

        Map<String, Object> githubSeries = new HashMap<>();
        List<Map<String, Object>> thisWeekSeries = List.of(
                Map.of("name", "Sales", "type", "line", "data", List.of(8, 12, 6, 14, 10, 8, 11)),
                Map.of("name", "Quotations", "type", "column", "data", List.of(3, 5, 2, 6, 4, 3, 4))
        );
        List<Map<String, Object>> lastWeekSeries = List.of(
                Map.of("name", "Sales", "type", "line", "data", List.of(7, 10, 9, 11, 8, 12, 9)),
                Map.of("name", "Quotations", "type", "column", "data", List.of(2, 4, 3, 5, 4, 5, 3))
        );
        githubSeries.put("this-week", thisWeekSeries);
        githubSeries.put("last-week", lastWeekSeries);
        githubIssues.put("series", githubSeries);

        root.put("githubIssues", githubIssues);

        // --- Task distribution: distribución simple por área estática (puedes
        // reemplazar luego con canales/sucursales reales) ---
        Map<String, Object> taskDistribution = new HashMap<>();
        taskDistribution.put("overview", Map.of(
                "this-week", Map.of("new", 58, "completed", 42),
                "last-week", Map.of("new", 52, "completed", 38)
        ));
        taskDistribution.put("labels", List.of("Ventas", "Cotizaciones", "Caja", "Clientes"));
        taskDistribution.put("series", Map.of(
                "this-week", List.of(22, 18, 35, 25),
                "last-week", List.of(20, 20, 32, 28)
        ));
        root.put("taskDistribution", taskDistribution);

        // --- Schedule: agenda de ejemplo (puedes sustituir por tareas reales) ---
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("today", List.of(
                Map.of("title", "Venta registrada", "time", "09:15", "location", "Caja 1"),
                Map.of("title", "Cotización enviada", "time", "10:30"),
                Map.of("title", "Nuevo cliente creado", "time", "12:10")
        ));
        schedule.put("tomorrow", List.of(
                Map.of("title", "Revisión de inventario", "time", "09:00"),
                Map.of("title", "Entrega programada", "time", "11:00", "location", "Zona centro")
        ));
        root.put("schedule", schedule);

        // --- Budget distribution y expenses: datos estáticos tipo demo Fuse ---
        Map<String, Object> budgetDistribution = new HashMap<>();
        budgetDistribution.put("categories", List.of("Ventas", "Cotizaciones", "Caja", "Clientes", "Marketing"));
        budgetDistribution.put("series", List.of(
                Map.of("name", "Budget", "data", List.of(85, 70, 90, 60, 75))
        ));
        root.put("budgetDistribution", budgetDistribution);

        Map<String, Object> weeklyExpenses = new HashMap<>();
        weeklyExpenses.put("amount", 186420);
        weeklyExpenses.put("labels", List.of("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6"));
        weeklyExpenses.put("series", List.of(
                Map.of("name", "Sales", "data", List.of(42100, 45200, 38900, 31220, 0, 0))
        ));
        root.put("weeklyExpenses", weeklyExpenses);

        Map<String, Object> monthlyExpenses = new HashMap<>();
        monthlyExpenses.put("amount", 742150);
        monthlyExpenses.put("labels", List.of("Week 1", "Week 2", "Week 3", "Week 4"));
        monthlyExpenses.put("series", List.of(
                Map.of("name", "Sales", "data", List.of(168200, 195400, 189550, 189000))
        ));
        root.put("monthlyExpenses", monthlyExpenses);

        Map<String, Object> yearlyExpenses = new HashMap<>();
        yearlyExpenses.put("amount", 5850000);
        yearlyExpenses.put("labels", List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct"));
        yearlyExpenses.put("series", List.of(
                Map.of("name", "Sales", "data", List.of(420000, 480000, 510000, 490000, 520000, 550000, 530000, 560000, 580000, 610000))
        ));
        root.put("yearlyExpenses", yearlyExpenses);

        // --- Budget table ---
        Map<String, Object> budgetDetails = new HashMap<>();
        budgetDetails.put("columns", List.of("type", "total", "expensesAmount", "expensesPercentage", "remainingAmount", "remainingPercentage"));
        budgetDetails.put("rows", List.of(
                Map.of("id", 1, "type", "Ventas", "total", 850000, "expensesAmount", 742150, "expensesPercentage", 87.32, "remainingAmount", 107850, "remainingPercentage", 12.68),
                Map.of("id", 2, "type", "Cotizaciones", "total", 120000, "expensesAmount", 89000, "expensesPercentage", 74.17, "remainingAmount", 31000, "remainingPercentage", 25.83),
                Map.of("id", 3, "type", "Caja", "total", 500000, "expensesAmount", 420000, "expensesPercentage", 84.0, "remainingAmount", 80000, "remainingPercentage", 16.0),
                Map.of("id", 4, "type", "Clientes", "total", 80000, "expensesAmount", 45000, "expensesPercentage", 56.25, "remainingAmount", 35000, "remainingPercentage", 43.75),
                Map.of("id", 5, "type", "Marketing", "total", 200000, "expensesAmount", 120000, "expensesPercentage", 60.0, "remainingAmount", 80000, "remainingPercentage", 40.0)
        ));
        root.put("budgetDetails", budgetDetails);

        // --- Team: puedes remplazar por usuarios/empleados reales ---
        root.put("teamMembers", List.of(
                Map.of("id", "1", "avatar", "images/avatars/female-10.jpg", "name", "Nadia Mcknight", "email", "nadiamcknight@mail.com", "phone", "+1-943-511-2203", "title", "Project Director"),
                Map.of("id", "2", "avatar", "images/avatars/male-19.jpg", "name", "Best Blackburn", "email", "blackburn.best@beadzza.me", "phone", "+1-814-498-3701", "title", "Senior Developer"),
                Map.of("id", "3", "avatar", "images/avatars/male-14.jpg", "name", "Duncan Carver", "email", "duncancarver@mail.info", "phone", "+1-968-547-2111", "title", "Senior Developer"),
                Map.of("id", "4", "avatar", "images/avatars/male-01.jpg", "name", "Martin Richards", "email", "martinrichards@mail.biz", "phone", "+1-902-500-2668", "title", "Junior Developer")
        ));

        // Información adicional de alto nivel (para el Home del dashboard)
        root.put("summary", Map.of(
                "totalSales", salesCount,
                "pendingSales", pendingSales,
                "paidSales", paidSales,
                "totalQuotations", quotationsCount,
                "draftQuotations", draftQuotations,
                "acceptedQuotations", acceptedQuotations,
                "clientsCount", clientsCount
        ));

        return ResponseEntity.ok(root);
    }
}

