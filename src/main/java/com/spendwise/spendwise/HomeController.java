
package com.spendwise.spendwise;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;

@Controller
public class HomeController {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public HomeController(
            ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    private Budget getOrCreateBudget() {
        return budgetRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Budget newBudget = new Budget();
                    newBudget.setMonthlyBudget(6000.0);
                    return budgetRepository.save(newBudget);
                });
    }

    @GetMapping("/")
    public String home(Model model) {

        Budget budget = getOrCreateBudget();
        double monthlyBudget = budget.getMonthlyBudget();

        String currentMonth = YearMonth.now().toString();

        double totalSpent = expenseRepository.findAll()
                .stream()
                .filter(expense ->
                        expense.getDate() != null
                        && expense.getDate().startsWith(currentMonth))
                .mapToDouble(Expense::getAmount)
                .sum();

        double remaining = monthlyBudget - totalSpent;

        int progress = monthlyBudget > 0
                ? (int) Math.min(100,
                    Math.round(totalSpent / monthlyBudget * 100))
                : 0;

        model.addAttribute("appName", "SpendWise");
        model.addAttribute("monthlyBudget", monthlyBudget);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("remaining", remaining);
        model.addAttribute("progress", progress);
        model.addAttribute("overBudget", totalSpent > monthlyBudget);

        return "index";
    }

    @GetMapping("/budget")
    public String showBudgetForm(Model model) {
        model.addAttribute("budget", getOrCreateBudget());
        return "budget-form";
    }

    @PostMapping("/budget")
    public String saveBudget(
            @ModelAttribute Budget budget,
            RedirectAttributes redirectAttributes) {

        Budget savedBudget = getOrCreateBudget();
        savedBudget.setMonthlyBudget(budget.getMonthlyBudget());
        budgetRepository.save(savedBudget);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Monthly budget saved successfully!");

        return "redirect:/";
    }
}
